package org.nasdanika.models.app.graph.drawio;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.common.MapCompoundSupplier;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.drawio.Layer;
import org.nasdanika.drawio.Root;
import org.nasdanika.graph.processor.ChildProcessors;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;

public class RootProcessor extends BaseProcessor<Root> {
	
	public RootProcessor(DrawioProcessorFactory factory) {
		super(factory);
	}

	@ChildProcessors
	public Map<Layer, ProcessorInfo<WidgetFactory,WidgetFactory,WidgetFactory>> layerProcessorInfos;
	
	@SuppressWarnings("resource")
	@Override
	public Supplier<Collection<Label>> createLabelsSupplier() {
		MapCompoundSupplier<Layer, Collection<Label>> childLabelsSupplier = new MapCompoundSupplier<>("Layer labels supplier");
		for (Entry<Layer, ProcessorInfo<WidgetFactory,WidgetFactory,WidgetFactory>> ce: layerProcessorInfos.entrySet()) {
			if (configuration.test(ce.getKey())) {
				childLabelsSupplier.put(ce.getKey(), ce.getValue().getProcessor().createLabelsSupplier());
			}
		}
		
		return childLabelsSupplier.then(this::createRootLabels);
	}
	
	protected Collection<Label> createRootLabels(Map<Layer, Collection<Label>> childLabels, ProgressMonitor progressMonitor) {
		return childLabels.values().stream().flatMap(Collection::stream).toList();
	}
	
	@Override
	public void resolve(URI base, ProgressMonitor progressMonitor) {
		super.resolve(base, progressMonitor);
		for (ProcessorInfo<WidgetFactory,WidgetFactory,WidgetFactory> cpi: layerProcessorInfos.values()) {
			cpi.getProcessor().resolve(base, progressMonitor);
		}
	}

	@Override
	public URI getActionURI(ProgressMonitor progressMonitor) {
		throw new UnsupportedOperationException();
	}

}
