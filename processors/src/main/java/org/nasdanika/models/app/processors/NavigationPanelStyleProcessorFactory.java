/**
 */
package org.nasdanika.models.app.processors;

import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppPackage;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;
import org.nasdanika.models.ecore.graph.processors.EEnumNodeProcessor;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.NAVIGATION_PANEL_STYLE)
public class NavigationPanelStyleProcessorFactory {
	
	private Context context;

	public NavigationPanelStyleProcessorFactory(Context context) {
		this.context = context;
	}
	
	@EClassifierNodeProcessorFactory(
			label = "Navigation Panel Style",
			description = "",
			documentation = 
                    """
					
                    """
	)
	public EEnumNodeProcessor createEClassifierProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
			java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
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
//	CARDS(1, "Cards", "Cards"),
//	COLLAPSIBLE_CARDS(2, "CollapsibleCards", "CollapsibleCards"), 
//	TREE(3, "Tree", "Tree"), 
//	SEARCHABLE_TREE(4, "SearchableTree", "SearchableTree");
	
} //NavigationPanelStyle
