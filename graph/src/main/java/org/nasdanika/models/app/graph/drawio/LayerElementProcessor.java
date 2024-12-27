package org.nasdanika.models.app.graph.drawio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jsoup.Jsoup;
import org.nasdanika.common.MapCompoundSupplier;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Util;
import org.nasdanika.drawio.Connection;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.LayerElement;
import org.nasdanika.drawio.LinkTarget;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.drawio.Node;
import org.nasdanika.drawio.Page;
import org.nasdanika.drawio.Root;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.exec.content.Text;
import org.nasdanika.graph.processor.NodeProcessorInfo;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;

public class LayerElementProcessor<T extends LayerElement> extends LinkTargetProcessor<T> {
	
	protected Map<ModelElement, ProcessorInfo<WidgetFactory>> childInfos = new ConcurrentHashMap<>();
	
	protected Map<Connection, CompletableFuture<ConnectionProcessor>> outgoingEndpoints = new ConcurrentHashMap<>();	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addReferrer(ModelElement referrer) {
		super.addReferrer(referrer);		
		for (Element child: referrer.getChildren()) {
			if (child instanceof ModelElement) {
				ProcessorInfo<WidgetFactory> ci = registry.get(child);
				if (ci != null /* && ci.getProcessor() != null */) {
					childInfos.put((ModelElement) child, ci);
				}
			}
		}		
		
		ProcessorInfo<WidgetFactory> referrerInfo = registry.get(referrer);
		if (referrerInfo instanceof NodeProcessorInfo) {
			NodeProcessorInfo<WidgetFactory, WidgetFactory, WidgetFactory> npi = (NodeProcessorInfo<WidgetFactory, WidgetFactory, WidgetFactory>) referrerInfo;
			outgoingEndpoints.putAll((Map) npi.getOutgoingEndpoints());			
		}
	}
	
	public LayerElementProcessor(DrawioProcessorFactory factory) {
		super(factory);
	}

	public Collection<ModelElement> referrers = new ArrayList<>();	

	/**
	 * Has documentation or has a page link (which implies having documentation)
	 */
	@Override
	public URI getActionURI(ProgressMonitor progressMonitor) {
		LinkTarget linkTarget = element.getLinkTarget();
		if (linkTarget instanceof Page) {
			ProcessorInfo<WidgetFactory> ppi = registry.get(linkTarget);
			if (ppi != null) {
				PageProcessor pageProcessor = (PageProcessor) ppi.getProcessor();
				if (pageProcessor != null) {
					return uri;
				}
			}
		}
		
		if (super.getDocumentation(progressMonitor).isEmpty()) {
			return null;
		}
		return uri;
	}	
	
	@Override
	public void resolve(URI base, ProgressMonitor progressMonitor) {
		super.resolve(base, progressMonitor);
		for (Entry<ModelElement, ProcessorInfo<WidgetFactory>> cpe: childInfos.entrySet()) {
			if (cpe.getKey() instanceof Node || isLogicalChildConnection(cpe.getKey())) {
				cpe.getValue().getProcessor().resolve(uri, progressMonitor);
			}
		}		
		for (Entry<Connection, CompletableFuture<ConnectionProcessor>> oe: outgoingEndpoints.entrySet()) {
			oe.getValue().thenAccept(cp -> cp.resolve(uri, progressMonitor));
		}	
		if (element.isTargetLink()) {
			LinkTarget linkTarget = element.getLinkTarget();
			if (linkTarget instanceof Page) {
				ProcessorInfo<WidgetFactory> ppi = registry.get(linkTarget);
				if (ppi != null) {
					ppi.getProcessor().resolve(uri, progressMonitor);
				}
			}
		}
	}	
	
	@Override
	protected Collection<EObject> getDocumentation(ProgressMonitor progressMonitor) {
		List<EObject> ret = new ArrayList<>();		
		LinkTarget linkTarget = element.getLinkTarget();
		if (linkTarget instanceof Page) {
			ProcessorInfo<WidgetFactory> ppi = registry.get(linkTarget);
			if (ppi != null) {
				PageProcessor pageProcessor = (PageProcessor) ppi.getProcessor();
				Text representationText = ContentFactory.eINSTANCE.createText(); // Interpolate with element properties?
				try {
					Document representation = pageProcessor.createRepresentation(progressMonitor);
					representationText.setContent(representation.toHtml(true, factory.getViewer()));
					ret.addAll(factory.createRepresentationContent(representation, registry, progressMonitor));
				} catch (TransformerException | IOException | ParserConfigurationException e) {
					representationText.setContent("<div class=\"alert alert-danger\" role=\"alert\">Error creating representation:" + e + "</div>");
				}
				ret.add(representationText);
			}
		}
				
		ret.addAll(super.getDocumentation(progressMonitor));
		
		// linked documentation (root)
		if (linkTarget instanceof Page) {
			Root root = ((Page) linkTarget).getModel().getRoot();
			ProcessorInfo<WidgetFactory> rpi = registry.get(root);
			RootProcessor rootProcessor = (RootProcessor) rpi.getProcessor();
			ret.addAll(rootProcessor.getDocumentation(progressMonitor));			
		}
		
		return ret;
	}
	
	protected Collection<Label> addPageLabels(Collection<Label> labels, Collection<Label> pageLabels) {
		for (Label label: labels) {
			label.getChildren().addAll(pageLabels);
			if (label instanceof Action) {
				pageLabels.forEach(pageLabel -> pageLabel.rebase(null, uri));		
			}
		}
		return labels;
	}
		
	@SuppressWarnings("resource")
	@Override
	public Supplier<Collection<Label>> createLabelsSupplier() {
		MapCompoundSupplier<ModelElement, Collection<Label>> childLabelsSupplier = new MapCompoundSupplier<>("Child labels supplier");

		String parentProperty = factory.getParentProperty();
		String targetKey = factory.getTargetKey();
		String sourceKey = factory.getSourceKey();

		// Own child nodes not linked to other nodes
		C: for (Entry<ModelElement, ProcessorInfo<WidgetFactory>> ce: childInfos.entrySet()) {
			ModelElement child = ce.getKey();
			if (child instanceof Connection && ((Connection) child).getSource() != null) {
				continue;
			}
			if (child instanceof Node) {
				Node childNode = (Node) child;
				if (!Util.isBlank(parentProperty)) {
					if (!Util.isBlank(targetKey)) {
						for (Connection cnoc: childNode.getOutgoingConnections()) {
							String cParent = cnoc.getProperty(parentProperty);
							if (targetKey.equals(cParent)) {
								continue C; // Logical child of connection's target
							}
						}
					}
					if (!Util.isBlank(sourceKey)) {
						for (Connection cnoc: childNode.getIncomingConnections()) {
							String cParent = cnoc.getProperty(parentProperty);
							if (sourceKey.equals(cParent)) {
								continue C; // Logical child of connection's source
							}
						}
					}
				}
				
			}
			childLabelsSupplier.put(child, ce.getValue().getProcessor().createLabelsSupplier());
		}
		
		// Connections without parent property
		for (Entry<Connection, CompletableFuture<ConnectionProcessor>> ce: outgoingEndpoints.entrySet()) {
			Connection conn = ce.getKey();
			if (!Util.isBlank(parentProperty) &&  !Util.isBlank(conn.getProperty(parentProperty))) {
				continue;
			}
			if (isLogicalChildConnection(conn)) {
				childLabelsSupplier.put(ce.getKey(), ce.getValue().join().createLabelsSupplier());
			}
		}
		
		// Nodes linked by connections with parent property
		if (!Util.isBlank(parentProperty) && element instanceof Node) {
			Node node = (Node) element;
			if (!Util.isBlank(sourceKey)) {
				for (Connection oc: node.getOutgoingConnections()) {
					Node target = oc.getTarget();
					if (sourceKey.equals(oc.getProperty(parentProperty)) && target != null) {
						ProcessorInfo<WidgetFactory> childInfo = registry.get(target);
						if (childInfo != null) {
							WidgetFactory processor = childInfo.getProcessor();
							if (processor != null) {
								childLabelsSupplier.put(target, processor.createLabelsSupplier());
							}
						}
					}
				}
			}
			if (!Util.isBlank(targetKey)) {
				for (Connection ic: node.getIncomingConnections()) {
					Node source = ic.getSource();
					if (targetKey.equals(ic.getProperty(parentProperty)) && source != null) {
						ProcessorInfo<WidgetFactory> childInfo = registry.get(source);
						if (childInfo != null) {
							WidgetFactory processor = childInfo.getProcessor();
							if (processor != null) {
								childLabelsSupplier.put(source, processor.createLabelsSupplier());
							}
						}
					}
				}
			}			
		}
						
		Supplier<Collection<Label>> labelSupplier = childLabelsSupplier.then(this::createLayerElementLabels);
		
		if (element.isTargetLink()) {
			LinkTarget linkTarget = element.getLinkTarget();
			if (linkTarget instanceof Page) {
				ProcessorInfo<WidgetFactory> ppi = registry.get(linkTarget);
				Supplier<Collection<Label>> pageLabelSupplier = ppi.getProcessor().createLabelsSupplier();
				return labelSupplier.then(pageLabelSupplier.asFunction(this::addPageLabels));
			}
		}		
		
		return labelSupplier;
	}
	
	protected String getRole(ModelElement modelElement) {
		String roleProperty = factory.getRoleProperty();
		if (!Util.isBlank(roleProperty)) {
			String role = modelElement.getProperty(roleProperty);
			if (!Util.isBlank(role)) {
				return role;
			}
		}
		
		return modelElement instanceof Connection ? factory.getAnonymousRole() : factory.getChildRole();
	}
	
	protected Collection<Label> createLayerElementLabels(
			Map<ModelElement, Collection<Label>> childLabels, 
			ProgressMonitor progressMonitor) {
				
		String label = element.getLabel();
		if (Util.isBlank(label)) {
			// No label - passing up sorted by label
			return childLabels.entrySet()
					.stream()
					.sorted((a,b) -> compareModelElementsBySortKeyAndLabel(a.getKey(), b.getKey()))
					.flatMap(e -> e.getValue().stream())
					.toList();		
		}
		
		Collection<EObject> documentation = getDocumentation(progressMonitor);
		int childLabelsSum = childLabels
				.values()
				.stream()
				.mapToInt(Collection::size)
				.sum();
		
		if (documentation.isEmpty() && childLabelsSum == 0) {
			// No child labels, no documentation
			return Collections.emptyList();
		}
				
		// Group by role and sort
		Map<String, List<Entry<ModelElement, Collection<Label>>>> groupedByRole = Util.groupBy(childLabels.entrySet(), e -> getRole(e.getKey()));
		groupedByRole.values().forEach(labels -> Collections.sort(labels, (a,b) -> compareModelElementsBySortKeyAndLabel(a.getKey(), b.getKey())));
		
//		List<Label> childNodesLabelsList = logicalChildLabels.entrySet()
//			.stream()
//			.filter(e -> e.getKey() instanceof Node)
//			.sorted((a,b) -> compareModelElementsByLabel(a.getKey(), b.getKey()))
//			.flatMap(e -> e.getValue().stream())
//			.toList();		
//		
//		List<Action> childConnectionsActionList = logicalChildLabels.entrySet()
//				.stream()
//				.filter(e -> isLogicalChildConnection(e.getKey()))
//				.sorted((a,b) -> compareModelElementsByLabel(a.getKey(), b.getKey()))
//				.flatMap(e -> e.getValue().stream())
//				.filter(Action.class::isInstance)
//				.map(Action.class::cast)
//				.toList();				
		
	
		String childRole = factory.getChildRole();
		Label prototype = getPrototype(progressMonitor);
		Label mLabel;
		if (prototype == null) {		
			boolean onlyChildRole = !Util.isBlank(childRole) 
					&& groupedByRole.size() == 1 
					&& childRole.equals(groupedByRole.keySet().iterator().next());
			
			mLabel = documentation.isEmpty() && onlyChildRole ? AppFactory.eINSTANCE.createLabel() : AppFactory.eINSTANCE.createAction();
		} else {
			mLabel = EcoreUtil.copy(prototype);
		}
		String labelText = Jsoup.parse(label).text();
		mLabel.setText(labelText);
		
		if (!Util.isBlank(childRole)) {
			List<Entry<ModelElement, Collection<Label>>> children = groupedByRole.remove(childRole);
			if (children != null) {
				children.forEach(e -> mLabel.getChildren().addAll(e.getValue()));				
			}
		}		
		
		configureLabel(mLabel, progressMonitor);
				
		if (mLabel instanceof Action) {
			Action mAction = (Action) mLabel;
			if (!documentation.isEmpty() ) {
				mAction.getContent().addAll(documentation);
			}
		
			String anonymousRole = factory.getAnonymousRole();
			if (!Util.isBlank(anonymousRole)) {
				List<Entry<ModelElement, Collection<Label>>> anonymous = groupedByRole.remove(anonymousRole);
				if (anonymous != null) {
					for (Entry<ModelElement, Collection<Label>> ae: anonymous) {
						for (Label ael: ae.getValue()) {
							if (ael instanceof Action) {
								mAction.getAnonymous().add((Action) ael);
							} else {
								addError(mAction, "Not an action, cannot add to anonymous: " + ael.getText());
							}
						}
					}
				}
			}
			
			String navigationRole = factory.getNavigationRole();
			if (!Util.isBlank(navigationRole)) {
				List<Entry<ModelElement, Collection<Label>>> navs = groupedByRole.remove(navigationRole);
				if (navs != null) {
					navs.forEach(e -> mAction.getNavigation().addAll(e.getValue()));				
				}
			}		
			
			String sectionRole = factory.getSectionRole();
			if (!Util.isBlank(sectionRole)) {
				List<Entry<ModelElement, Collection<Label>>> sections = groupedByRole.remove(sectionRole);
				if (sections != null) {
					for (Entry<ModelElement, Collection<Label>> se: sections) {
						for (Label sel: se.getValue()) {
							if (sel instanceof Action) {
								mAction.getSections().add((Action) sel);
							} else {
								addError(mAction, "Not an action, cannot add to sections: " + sel.getText());
							}
						}
					}
				}
			}
			
			mAction.setLocation(uri.toString());
			childLabels.values().stream().flatMap(Collection::stream).forEach(cl -> cl.rebase(null, uri));
			
			if (!groupedByRole.isEmpty()) {
				addError(mAction, "Unsupported roles: " + groupedByRole.keySet());				
			}
		}		
		
		return Collections.singleton(mLabel);			
	}

	protected void addError(Action action, String error) {
		Text errorText = ContentFactory.eINSTANCE.createText(); 
		errorText.setContent("<div class=\"alert alert-danger\" role=\"alert\">" + error + "</div>");
		action.getContent().add(errorText);
	}

}
