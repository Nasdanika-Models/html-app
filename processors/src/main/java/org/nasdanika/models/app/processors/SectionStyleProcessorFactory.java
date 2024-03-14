/**
 */
package org.nasdanika.models.app.processors;

import java.util.function.BiConsumer;

import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.html.model.app.Action;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.app.Label;
import org.nasdanika.html.model.app.graph.WidgetFactory;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;
import org.nasdanika.models.ecore.graph.processors.EEnumNodeProcessor;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.SECTION_STYLE)
public class SectionStyleProcessorFactory {
	
	private Context context;

	public SectionStyleProcessorFactory(Context context) {
		this.context = context;
	}
	
	@EClassifierNodeProcessorFactory(
			label = "Section Style",
			description = "",
			documentation = 
                    """
					
                    """
	)
	public EEnumNodeProcessor createEClassifierProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
			java.util.function.Function<ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EEnumNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}	
			
		};
	}

//	AUTO(0, "Auto", "Auto"),	
//	ACTION_GROUP(1, "ActionGroup", "ActionGroup"),
//	CARD(2, "Card", "Card"),
//	CARD_PILL(3, "CardPill", "CardPill"),
//	CARD_TAB(4, "CardTab", "CardTab"),
//	HEADER(5, "Header", "Header"),
//	PILL(6, "Pill", "Pill"),
//	TAB(7, "Tab", "Tab"),
//	TABLE(8, "Table", "Table");
	
} //SectionStyle
