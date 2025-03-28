package org.nasdanika.models.app.graph.emf;

import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.emf.EObjectNode;
import org.nasdanika.graph.emf.EReferenceConnection;
import org.nasdanika.graph.processor.ConnectionProcessorConfig;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.graph.processor.NodeProcessorInfo;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorFactory;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.graph.WidgetFactory;

/**
 * Processor factory which uses EObjet URI's as identifiers and adapters to create node processors.
 * @author Pavel
 *
 */
public class EObjectProcessorFactory extends ProcessorFactory<Object> {
	
	@SuppressWarnings("unchecked")
	@Override
	protected ProcessorInfo<Object> createProcessor(
			ProcessorConfig config, 
			boolean parallel,
			BiConsumer<Element, BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
			Consumer<CompletionStage<?>> endpointWiringStageConsumer,
			ProgressMonitor progressMonitor) {
		
		if (config.getElement() instanceof EReferenceConnection) {
			return config.toInfo(new ConnectionProcessor((ConnectionProcessorConfig<WidgetFactory, WidgetFactory>) config, isCompactPath(config)));
		}
		
		if (config.getElement() instanceof EObjectNode) {
			Object adapter = EcoreUtil.getRegisteredAdapter(((EObjectNode) config.getElement()).get(), NodeProcessorInfo.Factory.class);
			if (adapter instanceof NodeProcessorInfo.Factory) {
				return ((NodeProcessorInfo.Factory<Object,WidgetFactory,WidgetFactory>) adapter).create((NodeProcessorConfig<WidgetFactory, WidgetFactory>) config, parallel, infoProvider, endpointWiringStageConsumer, getContext(), progressMonitor);
			}
		}
		
		return super.createProcessor(config, parallel, infoProvider, endpointWiringStageConsumer, progressMonitor);
	}
	
	protected Context getContext() {
		return Context.EMPTY_CONTEXT;
	}
	
	/**
	 * Override to return true for compact reference and operation paths
	 * @return
	 */
	protected boolean isCompactPath(ProcessorConfig config) {
		return false;
	}
		
}
