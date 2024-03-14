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
import org.nasdanika.models.ecore.graph.processors.EClassNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.ACTION)
public class ActionProcessorFactory extends LinkProcessorFactory {
	
	public ActionProcessorFactory(Context context) {
		super(context);
	}
	
	@EClassifierNodeProcessorFactory(
			description = "",
			documentation = 
                    """
					
                    """
	)
	public EClassNodeProcessor createEClassifierProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
			java.util.function.Function<ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EClassNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}	
			
		};
	}
		
//	int getSectionColumns();
//	@EStructuralFeatureNodeProcessorFactory(
//			nsURI = AppPackage.eNS_URI,
//			classID = AppPackage.,
//			featureID = AppPackage.,
//			description = ""
//	)
//	public EReferenceNodeProcessor create Processor(
//			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
//			java.util.function.Function<ProgressMonitor, Action> prototypeProvider,
//			BiConsumer<Label, ProgressMonitor> labelConfigurator,
//			ProgressMonitor progressMonitor) {		
//		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
//			
//			@Override
//			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
//				super.configureLabel(source, label, progressMonitor);
//				if (labelConfigurator != null) {
//					labelConfigurator.accept(label, progressMonitor);
//				}
//			}
//			
//		};
//	}
	
//	SectionStyleProcessorFactory getSectionStyle();
//	EList<ActionProcessorFactory> getSections();
//	EList<EObject> getNavigation();
//	NavigationPanelProcessorFactory getLeftNavigation();
//	NavigationPanelProcessorFactory getRightNavigation();
//	NavigationPanelProcessorFactory getFloatLeftNavigation();
//	NavigationPanelProcessorFactory getFloatRightNavigation();
//	EList<ActionProcessorFactory> getAnonymous();
//	EList<Resource> getResources();
//	boolean isInline();
//	boolean isModalActivator();
//	LinkProcessorFactory createLink();

} // Action
