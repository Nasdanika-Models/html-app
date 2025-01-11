package org.nasdanika.models.app.graph.drawio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.Context;
import org.nasdanika.common.DiagnosticException;
import org.nasdanika.common.DocumentationFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Status;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.ConnectionProcessorConfig;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.graph.processor.Processor;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.ncore.util.NcoreUtil;
import org.nasdanika.persistence.ConfigurationException;

public class DrawioProcessorFactory extends Configuration {
	
	protected CapabilityLoader capabilityLoader;
	private ResourceSet resourceSet;
	
	public DrawioProcessorFactory(ProgressMonitor progressMonitor) {
		this(new CapabilityLoader(), progressMonitor);
	}
	
	public DrawioProcessorFactory(CapabilityLoader capabilityLoader, ProgressMonitor progressMonitor) {
		this.capabilityLoader = capabilityLoader;
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
	}
	
	public CapabilityLoader getCapabilityLoader() {
		return capabilityLoader;
	}
	
	public ResourceSet getResourceSet() {
		return resourceSet;
	}
	
	protected String getEPackageName(EPackage ePackage) {
		return NcoreUtil.getNasdanikaAnnotationDetail(ePackage, NcoreUtil.LOAD_KEY, ePackage.getName());
	}
	
	protected Map<String, EPackage> getEPackages() {
		Map<String, EPackage> ret = new LinkedHashMap<>();
		for (Object ep: resourceSet.getPackageRegistry().values()) {
			EPackage ePackage = (EPackage) ep;
			ret.put(getEPackageName(ePackage), ePackage);
		}
		return ret;
	}
		
	public EClassifier getType(String type, ModelElement source) {
		return NcoreUtil.getType(type, getEPackages(), msg -> new ConfigurationException(msg, source));
	}
	
	private Collection<DocumentationFactory> documentationFactories;
	
	protected Collection<DocumentationFactory> getDocumentationFactories(ProgressMonitor progressMonitor) {
		if (documentationFactories == null) {
			documentationFactories = new ArrayList<>();
			if (capabilityLoader != null) {
				Requirement<Object, DocumentationFactory> requirement = ServiceCapabilityFactory.createRequirement(DocumentationFactory.class, null, new DocumentationFactory.Requirement(true));
				Iterable<CapabilityProvider<Object>> cpi = capabilityLoader.load(requirement, progressMonitor);
				for (CapabilityProvider<Object> cp: cpi) {				
					cp.getPublisher().subscribe(df -> documentationFactories.add((DocumentationFactory) df));
				}
			}
		}
		return documentationFactories;
	}
				
	@Processor(type = org.nasdanika.drawio.Document.class)
	public WidgetFactory createDocumentProcessor(
		ProcessorConfig config, 
		boolean parallel, 
		BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
		Function<ProgressMonitor, Object> next,		
		ProgressMonitor progressMonitor) {
		
		DocumentProcessor processor = new DocumentProcessor(this);
		return filter(config, processor, infoProvider, progressMonitor);
	}
				
	@Processor(type = org.nasdanika.drawio.Page.class)
	public WidgetFactory createPageProcessor(
		ProcessorConfig config, 
		boolean parallel, 
		BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
		Function<ProgressMonitor, Object> next,		
		ProgressMonitor progressMonitor) {
		
		PageProcessor processor = new PageProcessor(this);
		return filter(config, processor, infoProvider, progressMonitor);
	}
				
	@Processor(type = org.nasdanika.drawio.Root.class)
	public WidgetFactory createRootProcessor(
		ProcessorConfig config, 
		boolean parallel, 
		BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
		Function<ProgressMonitor, Object> next,		
		ProgressMonitor progressMonitor) {
		
		RootProcessor processor = new RootProcessor(this);
		return filter(config, processor, infoProvider, progressMonitor);
	}
				
	@Processor(type = org.nasdanika.drawio.Layer.class)
	public WidgetFactory createLayerProcessor(
		ProcessorConfig config, 
		boolean parallel, 
		BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
		Function<ProgressMonitor, Object> next,		
		ProgressMonitor progressMonitor) {
		
		LayerProcessor processor = new LayerProcessor(this);
		return filter(config, processor, infoProvider, progressMonitor);
	}
	
	@Processor(type = org.nasdanika.drawio.Node.class)
	public WidgetFactory createNodeProcessor(
		NodeProcessorConfig<?,?> config, 
		boolean parallel, 
		BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
		Function<ProgressMonitor, Object> next,		
		ProgressMonitor progressMonitor) {
		
		org.nasdanika.drawio.Node node = (org.nasdanika.drawio.Node) config.getElement();
		if (node.isTargetLink() && node.getLinkTarget() instanceof ModelElement) {
			return null;			
		}
		NodeProcessor processor = new NodeProcessor(this);
		return filter(config, processor, infoProvider, progressMonitor);
	}
	
	@Processor(type = org.nasdanika.drawio.Connection.class)
	public WidgetFactory createConnectionProcessor(
		ConnectionProcessorConfig<?,?> config, 
		boolean parallel, 
		BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
		Function<ProgressMonitor, Object> next,		
		ProgressMonitor progressMonitor) {
		
		org.nasdanika.drawio.Connection connection = (org.nasdanika.drawio.Connection) config.getElement();
		if (connection.isTargetLink() && connection.getLinkTarget() instanceof ModelElement) {
			return null;			
		}
		
		ConnectionProcessor processor = new ConnectionProcessor(this);
		return filter(config, processor, infoProvider, progressMonitor);
	}

	public Context getContext() {
		return Context.EMPTY_CONTEXT;
	}
	
	public void onDiagnostic(org.nasdanika.common.Diagnostic diagnostic) {
		if (diagnostic.getStatus() == Status.FAIL || diagnostic.getStatus() == Status.ERROR) {
			System.err.println("***********************");
			System.err.println("*      Diagnostic     *");
			System.err.println("***********************");
			diagnostic.dump(System.err, 4, Status.FAIL, Status.ERROR);
		}
		if (diagnostic.getStatus() != Status.SUCCESS) {
			throw new DiagnosticException(diagnostic);
		};
	}
	
}
