package org.nasdanika.models.app.graph.drawio;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.jsoup.Jsoup;
import org.nasdanika.common.DocumentationFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.common.Util;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.emf.persistence.EObjectLoader;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.exec.content.Text;
import org.nasdanika.graph.processor.ProcessorElement;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.graph.processor.Registry;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.persistence.ConfigurationException;
import org.nasdanika.persistence.ObjectLoader;

/**
 * Base class for processors
 */
public class BaseProcessor<T extends Element> implements WidgetFactory {
	
	protected DrawioProcessorFactory factory;
	
	public BaseProcessor(DrawioProcessorFactory factory) {
		this.factory = factory;
	}
		
	protected T element;
	
	@ProcessorElement	
	public void setElement(T element) {
		this.element = element;
	}
	
	@Registry 
	public Map<Element, ProcessorInfo<WidgetFactory>> registry;
	
	protected URI uri;
	
	/**
	 * @return Action URI if this processor creates an action or null otherwise.
	 */
	public URI getActionURI(ProgressMonitor progressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object createLabel(ProgressMonitor progressMonitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String createLabelString(ProgressMonitor progressMonitor) {
		return null;
	}

	@Override
	public Object createLink(URI base, ProgressMonitor progressMonitor) {
		return null;
	}

	@Override
	public Label createHelpDecorator(URI base, ProgressMonitor progressMonitor) {
		return null;
	}

	@Override
	public String createLinkString(URI base, ProgressMonitor progressMonitor) {
		return null;
	}

	@Override
	public String selectString(Object selector, URI base, ProgressMonitor progressMonitor) {
		return null;
	}

	@Override
	public void resolve(URI base, ProgressMonitor progressMonitor) {
		if (uri != null && uri.isRelative() && base != null && base.isHierarchical() && !base.isRelative()) {
			uri = uri.resolve(base);
		}
	}

	@Override
	public Supplier<Collection<Label>> createLabelsSupplier() {
		return new Supplier<Collection<Label>>() {

			@Override
			public double size() {
				return 1;
			}

			@Override
			public String name() {
				return "Labels supplier for " + BaseProcessor.this;
			}

			@Override
			public Collection<Label> execute(ProgressMonitor progressMonitor) {
				return createLabels(progressMonitor);
			}
			
		};		
	}

	protected Collection<Label> createLabels(ProgressMonitor progressMonitor) {
		Object label = createLabel(progressMonitor);
		return label == null ? Collections.emptyList() : Collections.singleton((Label) label);
	}	
			
	protected Collection<EObject> getDocumentation(ProgressMonitor progressMonitor) {
		if (element instanceof ModelElement) {		
			try {
				ModelElement modelElement = (ModelElement) element;
				URI baseUri = modelElement.getModel().getPage().getDocument().getURI();
				String docProperty = factory.getDocumentationProperty();
				if (!Util.isBlank(docProperty)) {
					String doc = modelElement.getProperty(docProperty);
					if (!Util.isBlank(doc)) {
						String[] docFormatStr = { null };
						String docFormatProperty = factory.getDocFormatProperty();
						if (!Util.isBlank(docFormatProperty)) {
							docFormatStr[0] = modelElement.getProperty(docFormatProperty);
						}
						if (Util.isBlank(docFormatStr[0])) {
							docFormatStr[0] = "markdown";
						}
						
						Optional<DocumentationFactory> dfo = factory.getDocumentationFactories(progressMonitor)
							.stream()
							.filter(df -> df.canHandle(docFormatStr[0]))
							.findAny();
						
						if (dfo.isPresent()) {
							return dfo.get().createDocumentation(doc, baseUri, progressMonitor);
						}
						
						throw new ConfigurationException("Unsupported documentation format: '" + docFormatStr[0] + "'", modelElement);
					}
				}
				
				String docRefProperty = factory.getDocRefProperty();
				if (!Util.isBlank(docRefProperty)) {
					String docRefStr = modelElement.getProperty(docRefProperty);
					if (!Util.isBlank(docRefStr)) {
						String docFormatProperty = factory.getDocFormatProperty();
						DocumentationFactory docFactory = null;
						if (!Util.isBlank(docFormatProperty)) {
							String docFormatStr = modelElement.getProperty(docFormatProperty);
							if (!Util.isBlank(docFormatStr)) {
								Optional<DocumentationFactory> dfo = factory.getDocumentationFactories(progressMonitor)
										.stream()
										.filter(df -> df.canHandle(docFormatStr))
										.findAny();
								if (dfo.isPresent()) {
									docFactory = dfo.get();
								} else {
									throw new ConfigurationException("Unsupported documentation format: '" + docFormatStr + "'", modelElement);												
								}
							}
						}
						URI[] docRefURI = { URI.createURI(docRefStr) };
						if (baseUri != null && !baseUri.isRelative()) {
							docRefURI[0] = docRefURI[0].resolve(baseUri);
						}
						if (docFactory == null) {
							Optional<DocumentationFactory> dfo = factory.getDocumentationFactories(progressMonitor)
									.stream()
									.filter(df -> df.canHandle(docRefURI[0]))
									.findAny();		
							
							if (dfo.isPresent()) {
								docFactory = dfo.get();
							} else {
								throw new ConfigurationException("Unsupported documentation URI: " + docRefURI[0], modelElement);												
							}
						}
						
						return docFactory.createDocumentation(docRefURI[0], progressMonitor);				
					}
				}
			} catch (Exception e) {
				Text text = ContentFactory.eINSTANCE.createText(); // Interpolate with element properties?
				text.setContent("<div class=\"nsd-error\">" + e + "</div>");
				return Collections.singleton(text);
			}
		}
		return Collections.emptyList();		
	}
	
	/**
	 * Set icon, tooltip, ...
	 * @param label
	 * @param progressMonitor 
	 */
	public void configureLabel(Label label, ProgressMonitor progressMonitor) {
		if (element instanceof ModelElement) {
			ModelElement modelElement = (ModelElement) element;
			if (Util.isBlank(label.getIcon())) {
				String iconProperty = factory.getIconProperty();
				if (!Util.isBlank(iconProperty)) {
					label.setIcon(modelElement.getProperty(iconProperty));
				}
			}
			if (Util.isBlank(label.getTooltip())) {
				label.setTooltip(modelElement.getTooltip());
			}
			String titleProperty = factory.getTitleProperty();
			if (!Util.isBlank(titleProperty)) {
				String title = modelElement.getProperty(titleProperty);
				if (!Util.isBlank(title)) {
					label.setText(title);
				}
			}
			if (Util.isBlank(label.getText()) && element instanceof ModelElement) {
				label.setText(((ModelElement) element).getId());
			}
		}
	}
		
	protected int compareModelElementsBySortKeyAndLabel(ModelElement a, ModelElement b) {
		String sortKeyProperty = factory.getSortKeyProperty();
		if (!Util.isBlank(sortKeyProperty)) {
			String aKey = a.getProperty(sortKeyProperty);
			String bKey = b.getProperty(sortKeyProperty);
			if (Util.isBlank(aKey)) {
				if (!Util.isBlank(bKey)) {
					return 1;
				}
			} else if (Util.isBlank(bKey)) {
				if (!Util.isBlank(bKey)) {
					return -1;
				}				
			} else {
				int cmp = aKey.compareTo(bKey);
				if (cmp != 0) {
					return cmp;
				}
			}
				
		}
		if (Util.isBlank(a.getLabel())) {
			return Util.isBlank(b.getLabel()) ? a.hashCode() - b.hashCode() : 1;
		}
		if (Util.isBlank(b.getLabel())) {
			return -1;
		}
		
		return Jsoup.parse(a.getLabel()).text().compareTo(Jsoup.parse(b.getLabel()).text());
	}	

	@SuppressWarnings("unchecked")
	protected Label getPrototype(ProgressMonitor progressMonitor) {
		if (element instanceof ModelElement) {		
			try {
				EObjectLoader eObjectLoader = new EObjectLoader((ObjectLoader) null) {
					
					@Override
					public ResolutionResult resolveEClass(String type) {
						EClass eClass = (EClass) factory.getType(type, (ModelElement) element);
						return new ResolutionResult(eClass, null);
					}
					
					@Override
					public ResourceSet getResourceSet() {
						return factory.getResourceSet();
					}
					
				};
				
				ModelElement modelElement = (ModelElement) element;
				URI baseUri = modelElement.getModel().getPage().getDocument().getURI();
				String prototypeProperty = factory.getPrototypeProperty();
				if (!Util.isBlank(prototypeProperty)) {
					String prototypeSpec = modelElement.getProperty(prototypeProperty);
					if (!Util.isBlank(prototypeSpec)) {
						Object obj = eObjectLoader.loadYaml(
								prototypeSpec, 
								baseUri, 
								null, 
								progressMonitor);
						
						if (obj instanceof SupplierFactory) {
							obj = ((SupplierFactory<Object>) obj).create(factory.getContext()).call(progressMonitor, factory::onDiagnostic);
						} 
						
						return (Label) obj;
					}
				}
				
				String protoRefProperty = factory.getProtoRefProperty();
				if (!Util.isBlank(protoRefProperty)) {
					String protoRefStr = modelElement.getProperty(protoRefProperty);
					if (!Util.isBlank(protoRefStr)) {
						URI[] protoRefURI = { URI.createURI(protoRefStr) };
						if (baseUri != null && !baseUri.isRelative()) {
							protoRefURI[0] = protoRefURI[0].resolve(baseUri);
						}
						
						return (Label) factory.getResourceSet().getEObject(protoRefURI[0], true);
					}
				}
			} catch (Exception e) {
				Action errorAction = AppFactory.eINSTANCE.createAction();				
				Text text = ContentFactory.eINSTANCE.createText(); // Interpolate with element properties?
				text.setContent("<div class=\"nsd-error\">Error loading prototype: " + e + "</div>");
				errorAction.getContent().add(text);
				return errorAction;
			}
		}
		return null;		
	}	

}
