package org.nasdanika.models.app.graph.drawio;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Util;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.LayerElement;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.drawio.Page;
import org.nasdanika.drawio.Root;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.exec.content.Text;
import org.nasdanika.graph.processor.ProcessorElement;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.graph.processor.RegistryEntry;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;

/**
 * Page processor creates an action for top level pages, adds page diagram and root documentation.
 * For linked pages passes through child labels to be used by the linking element. 
 * Also generates diagram embedding widget to add to the linking element page. 
 */
public class PageProcessor extends LinkTargetProcessor<Page> {
	
	public PageProcessor(DrawioProcessorFactory factory) {
		super(factory);
	}
	
	@ProcessorElement
	@Override
	public void setElement(Page element) {
		super.setElement(element);
		uri = URI.createURI(getIndexName());
	}
	
	/**
	 * Forcing top-level page
	 */
	public boolean isTopLevelPage;
	
	@RegistryEntry("#element.model.root == #this")
	public RootProcessor rootProcessor;
	
	@Override
	public void resolve(URI base, ProgressMonitor progressMonitor) {
		super.resolve(base, progressMonitor);
		rootProcessor.resolve(base, progressMonitor);
	}
	
	@Override
	public Supplier<Collection<Label>> createLabelsSupplier() {
		if (isTopLevelPage || getReferrers().isEmpty()) {
			return rootProcessor.createLabelsSupplier().then(this::createPageLabels);			
		}
		return rootProcessor.createLabelsSupplier();
	}
	
	protected Collection<Label> createPageLabels(Collection<Label> rootLabels, ProgressMonitor progressMonitor) {
		Root root = element.getModel().getRoot();
		ProcessorInfo<WidgetFactory> rpi = registry.get(root);
		RootProcessor rootProcessor = (RootProcessor) rpi.getProcessor();
		Label prototype = rootProcessor.getPrototype(progressMonitor);
		Action action = prototype instanceof Action ? (Action) prototype : AppFactory.eINSTANCE.createAction();
		if (Util.isBlank(action.getText())) {
			action.setText(element.getName());
		}
		if (Util.isBlank(action.getLocation())) {
			action.setLocation(uri.toString());
		}

		addChildLabels(action, rootLabels, progressMonitor);		
		rootLabels.forEach(rl -> rl.rebase(null, uri));
		rootProcessor.configureLabel(action, progressMonitor);
		Text representationText = ContentFactory.eINSTANCE.createText(); // Interpolate with element properties?
		try {
			Document representation = createRepresentation(progressMonitor);
			representationText.setContent(representation.toHtml(true, configuration.getViewer()));
			action.getContent().addAll(configuration.createRepresentationContent(representation, registry, progressMonitor));
		} catch (TransformerException | IOException | ParserConfigurationException e) {
			representationText.setContent("<div class=\"nsd-error\">Error creating representation: " + e + "</div>");
		}
		action.getContent().add(representationText);
		action.getContent().addAll(rootProcessor.getDocumentation(progressMonitor));
		return Collections.singleton(action);
	}	
	
	/**
	 * Creates filtered representation
	 * @param semanticModelElement
	 * @param modelPage
	 * @param registry
	 * @param progressMonitor
	 * @return
	 * @throws ParserConfigurationException 
	 */
	public Document createRepresentation(ProgressMonitor progressMonitor) throws ParserConfigurationException {		
		Document representationDocument = Document.create(true, element.getDocument().getURI());
		representationDocument.getPages().add(element);
		representationDocument.getPages().get(0).accept(representationElement -> {
			if (representationElement instanceof ModelElement) {
				filterRepresentationElement(findElement(representationElement), (ModelElement) representationElement, progressMonitor);
			}
		});
		return representationDocument;
	}
	
	protected ModelElement findElement(org.nasdanika.graph.Element representationElement) {
		if (representationElement instanceof ModelElement) {
			String id = ((ModelElement) representationElement).getId();
			return element
					.stream()
					.filter(ModelElement.class::isInstance)
					.map(ModelElement.class::cast)
					.filter(me -> id.equals(me.getId()))
					.findAny()
					.orElse(null);
		}
		return null;
	}
	
	/**
	 * Resolves links and delegates to the factory to customize filtering.
	 */
	protected void filterRepresentationElement(
			ModelElement sourceElement,
			ModelElement representationElement, 
			ProgressMonitor progressMonitor) {
		
		// Links
		if (representationElement instanceof LayerElement) {
			if (sourceElement.isTargetLink()) {
				representationElement.setLink(null);
			}
			while (sourceElement.isTargetLink() && sourceElement.getLinkTarget() instanceof ModelElement) {
				sourceElement = (ModelElement) sourceElement.getLinkTarget();
			}						
			
			ProcessorInfo<WidgetFactory> sourceElementProcessorInfo = registry.get(sourceElement);
			if (sourceElementProcessorInfo != null) {
				WidgetFactory sourceElementProcessor = sourceElementProcessorInfo.getProcessor();
				if (sourceElementProcessor instanceof BaseProcessor) {
					BaseProcessor<?> baseProcessor = (BaseProcessor<?>) sourceElementProcessor;
					String role = baseProcessor.getRole();
					String sectionRole = configuration.getSectionRole();
					if (Util.isBlank(sectionRole) || !sectionRole.equals(role)) {
						URI sourceURI = baseProcessor.getActionURI(progressMonitor);
						if (sourceURI != null && uri != null) {
							URI linkURI = sourceURI.deresolve(uri, true, true, true);
							representationElement.setLink(linkURI.toString());
						}
					}
				}
			}
		}
			
		configuration.filterRepresentationElement(
				sourceElement,
				representationElement, 
				registry, 
				progressMonitor);
			
	}
	
}
