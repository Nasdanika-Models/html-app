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
import org.nasdanika.models.ecore.graph.processors.EClassNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;
import org.nasdanika.models.ecore.graph.processors.EReferenceNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EStructuralFeatureNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.LINK)
public class LinkProcessorFactory extends LabelProcessorFactory {
	
	public LinkProcessorFactory(Context context) {
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
			java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
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
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__LOCATION,
		description = "Link URL relative to the ancestor URL or base-uri",
		documentation = 
		        """
				Link URL relative to the ancestor URL or ``base-uri``.

				``${{{{base-uri}}}}`` token can be used in the activator to define the uri relative to the base generation URI (output folder)
				instead of the parent URI. 
				It might be useful it the parent URI is an absolute external URI.
				``${{{{base-uri}}}}`` ends with a slash, so there is no need to add a slash. E.g. ``${{{{base-uri}}}}index.html``.
				""")
	public EReferenceNodeProcessor createIconProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__SCRIPT,
		description = "Script to execute on link click (activation)")
	public EReferenceNodeProcessor createLinkProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__MODAL,
		description = "Modal dialog which opens on link activation")
	public EReferenceNodeProcessor createModalProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__NAME,
		description = "Link name attribute is used for referencing sections if not blank")
	public EReferenceNodeProcessor createNameProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__CONFIRMATION,
		description = "Confirmation to display in a confirmation dialog before action activation",
		documentation = 
		        """
				Confirmation to display in a confirmation dialog before action activation to give the user an opportunity to cancel the action.
				E.g. confirmation of deletion.
				""")
	public EReferenceNodeProcessor createConfirmationProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__TARGET,
		description = "Link target")
	public EReferenceNodeProcessor createTargetProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}
	
	@EStructuralFeatureNodeProcessorFactory(
		nsURI = AppPackage.eNS_URI,
		classID = AppPackage.LINK,
		featureID = AppPackage.LINK__ACTION,
		description = "Link's target action",
		documentation = 
		        """
				A link can point to an action.
				""")
	public EReferenceNodeProcessor createActionProcessor(
		NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
		java.util.function.BiFunction<EObject, ProgressMonitor, Action> prototypeProvider,
		BiConsumer<Label, ProgressMonitor> labelConfigurator,
		ProgressMonitor progressMonitor) {		
		return new EReferenceNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}

} // Link
