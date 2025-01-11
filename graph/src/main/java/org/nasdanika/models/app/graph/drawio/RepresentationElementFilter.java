package org.nasdanika.models.app.graph.drawio;

import java.util.Map;

import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.graph.WidgetFactory;

public interface RepresentationElementFilter {
	
	void filterRepresentationElement(
			ModelElement sourceElement, 
			ModelElement representationElement,
			Map<Element, ProcessorInfo<WidgetFactory>> registry, ProgressMonitor progressMonitor);	

}
