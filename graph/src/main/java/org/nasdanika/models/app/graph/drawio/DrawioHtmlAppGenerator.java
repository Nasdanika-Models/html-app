package org.nasdanika.models.app.graph.drawio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Transformer;
import org.nasdanika.drawio.ConnectionBase;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.LinkTarget;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.graph.Connection;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.NopEndpointProcessorConfigFactory;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.graph.processor.ReflectiveProcessorFactoryProvider;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;

public class DrawioHtmlAppGenerator extends Configuration {

	protected CapabilityLoader capabilityLoader;
		
	public DrawioHtmlAppGenerator() {
		this(new CapabilityLoader());
	}
	
	public DrawioHtmlAppGenerator(CapabilityLoader capabilityLoader) {
		this.capabilityLoader = capabilityLoader;
	}
	
	@SuppressWarnings("unchecked")
	public Supplier<Collection<Label>> createLabelsSupplier(Document document, ProgressMonitor progressMonitor) {
		NopEndpointProcessorConfigFactory<WidgetFactory> processorConfigFactory = new NopEndpointProcessorConfigFactory<WidgetFactory>() {
			
			@Override
			protected boolean isPassThrough(Connection incomingConnection) {
				return false;
			}
			
		};
		
		Transformer<Element,ProcessorConfig> processorConfigTransformer = new Transformer<>(processorConfigFactory);				
		
		Collection<Element> elements = new ArrayList<>();
		Consumer<org.nasdanika.drawio.Element> consumer = org.nasdanika.drawio.Util.withLinkTargets(elements::add, ConnectionBase.SOURCE);
		document.accept(consumer, null);
		Map<Element, ProcessorConfig> configs = processorConfigTransformer.transform(elements, false, progressMonitor);
		
		DrawioProcessorFactory processorFactory = createProcessorFactory(progressMonitor);
		ReflectiveProcessorFactoryProvider<WidgetFactory, WidgetFactory, WidgetFactory> rpfp = new ReflectiveProcessorFactoryProvider<>(processorFactory);
		Map<Element, ProcessorInfo<WidgetFactory>> processors = rpfp.getFactory().createProcessors(configs.values(), false, progressMonitor);
		
		processors
			.keySet()
			.stream()
			.filter(ModelElement.class::isInstance)
			.map(ModelElement.class::cast)
			.filter(ModelElement::isTargetLink)
			.map(DrawioHtmlAppGenerator::getLinkTargetRecursive)
			.forEach(entry ->  ((LinkTargetProcessor<LinkTarget>) processors.get(entry.getValue()).getProcessor()).addReferrer(entry.getKey()));
		
		DocumentProcessor docProcessor = (DocumentProcessor) processors.get(document).getProcessor();
				
		URI baseURI = getBaseURI();
		docProcessor.resolve(baseURI, progressMonitor);
		
		Supplier<Collection<Label>> labelsSupplier = docProcessor.createLabelsSupplier();
		return labelsSupplier.then(labels -> {
			for (Label label: labels) {
				label.rebase(null, baseURI);
			}
			
			return labels;
		});
	}
	
	protected URI baseURI = URI.createURI("tmp://" + UUID.randomUUID() + "/" + UUID.randomUUID() + "/");
	
	/**
	 * Base URI for resolving and link locations.
	 * @return
	 */
	protected URI getBaseURI() {
		return baseURI;
	}
	
	/**
	 * URI for rebasing labels to make locations relative.
	 * This method returns the base URI. Override to, for example, add prefix to all locations. 
	 * @return
	 */
	protected URI getRebaseURI() {
		return baseURI;
	}
	
	@Override
	public URI getRefBaseURI(URI docURI) {
		return super.getRefBaseURI(docURI);
	}

	protected DrawioProcessorFactory createProcessorFactory(ProgressMonitor progressMonitor) {
		return new DrawioProcessorFactory(capabilityLoader, progressMonitor) {
			
			@Override
			public String getIconProperty() {
				return DrawioHtmlAppGenerator.this.getIconProperty();
			}	
			
			@Override
			public String getTitleProperty() {
				return DrawioHtmlAppGenerator.this.getTitleProperty();
			}	
				
			@Override
			public String getDocumentationProperty() {
				return DrawioHtmlAppGenerator.this.getDocumentationProperty();
			}	
				
			@Override
			public String getDocRefProperty() {
				return DrawioHtmlAppGenerator.this.getDocRefProperty();
			}	
			
			@Override
			public String getDocFormatProperty() {
				return DrawioHtmlAppGenerator.this.getDocFormatProperty();
			}		
			
			@Override
			public URI getRefBaseURI(URI docURI) {
				return DrawioHtmlAppGenerator.this.getRefBaseURI(docURI);
			}
			
			@Override
			public boolean isUseTooltipIfDocIsEmpty() {
				return DrawioHtmlAppGenerator.this.isUseTooltipIfDocIsEmpty();
			}
			
			/**
			 * Override to implement filtering of representation elements
			 * @param representationElement
			 * @param registry
			 * @param progressMonitor
			 */
			@Override
			public void filterRepresentationElement(
					ModelElement sourceElement,
					ModelElement representationElement,
					Map<org.nasdanika.drawio.Element, ProcessorInfo<WidgetFactory>> registry,
					ProgressMonitor progressMonitor) {
				
				DrawioHtmlAppGenerator.this.filterRepresentationElement(sourceElement, representationElement, registry, progressMonitor);				
			}

			/**
			 * Override to customize viewer.
			 * @return
			 */
			@Override
			public String getViewer() {
				return DrawioHtmlAppGenerator.this.getViewer();
			}
				
			/**
			 * Application base for resolving relative image URL's. 
			 * This implementation returns DEFAULT_APP_BASE. 
			 * Override to customize for different (e.g. intranet) installations.
			 * App sources - https://github.com/jgraph/drawio/tree/dev/src/main/webapp.
			 * For the purposes of serving images and a diagram editor the web app can be deployed as a static site.
			 * It can also be deployed as a Docker container - https://www.drawio.com/blog/diagrams-docker-app, https://hub.docker.com/r/jgraph/drawio 
			 * @return
			 */
			@Override
			public URI getAppBase() {
				return DrawioHtmlAppGenerator.this.getAppBase();
			}
			
			/**
			 * This implementation returns the argument. 
			 * Override to rewrite URL's before conversion to icons. For example, read representations from a file system and convert to data URL's.
			 * @param imageRepr
			 * @return
			 */
			@Override
			public String rewriteImage(String imageRepr, ProgressMonitor progressMonitor) {
				return DrawioHtmlAppGenerator.this.rewriteImage(imageRepr, progressMonitor);
			}
			
			/**
			 * Icon size to scale image representations to
			 * @return
			 */
			public int getIconSize() {
				return DrawioHtmlAppGenerator.this.getIconSize();
			}
			
			@Override
			public String getIndexName() {
				return DrawioHtmlAppGenerator.this.getIndexName();
			}

			/**
			 * Override to create additional content from a representation (page).
			 * For example, aria for screen readers and AI explaining the diagrams.
			 * This implementation returns an empty collection.
			 * @param representation
			 * @param registry
			 * @param progressMonitor
			 * @return
			 */
			public Collection<? extends EObject> createRepresentationContent(
					Document representation,
					Map<org.nasdanika.drawio.Element, ProcessorInfo<WidgetFactory>> registry,
					ProgressMonitor progressMonitor) {
				
				return DrawioHtmlAppGenerator.this.createRepresentationContent(representation, registry, progressMonitor);
			}
			
			@Override
			public <T extends WidgetFactory> T filter(
					ProcessorConfig config, 
					T processor,
					BiConsumer<Element, BiConsumer<ProcessorInfo<Object>, ProgressMonitor>> infoProvider,
					ProgressMonitor progressMonitor) {
				return DrawioHtmlAppGenerator.this.filter(config, processor, infoProvider, progressMonitor);
			}
			
		};
	}

	private static Map.Entry<ModelElement, LinkTarget> getLinkTargetRecursive(ModelElement source) {				
		// Preventing infinite loops
		HashSet<ModelElement> tracker = new HashSet<ModelElement>();
		ModelElement modelElement = source; 
		while (tracker.add(modelElement) && modelElement.isTargetLink() && modelElement.getLinkTarget() instanceof ModelElement) { 
			modelElement = (ModelElement) modelElement.getLinkTarget();			
		}
		
		if (source != modelElement) {
			return Map.entry(source, modelElement); // Not going to pages
		}
		
		if (source.isTargetLink()) {
			return Map.entry(source, source.getLinkTarget());
		}
		
		return null;
	}
				
}
