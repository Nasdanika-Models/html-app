package org.nasdanika.models.app.processors;

import java.util.function.BiConsumer;

import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Reflector.Factory;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.html.model.app.Action;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.app.Label;
import org.nasdanika.html.model.app.graph.WidgetFactory;
import org.nasdanika.models.ecore.graph.processors.EPackageNodeProcessor;
import org.nasdanika.models.ecore.graph.processors.EPackageNodeProcessorFactory;

@EPackageNodeProcessorFactory(nsURI = AppPackage.eNS_URI)
public class EcoreGenAppProcessorsFactory {

	private Context context;
	
	@Factory
	public final ActionProcessorFactory actionProcessorFactory;
	
	@Factory
	public final ActionReferenceProcessorFactory actionReferenceProcessorFactory;
	
	@Factory
	public final ContentPanelProcessorFactory contentPanelProcessorFactory;
	
	@Factory
	public final FooterProcessorFactory footerProcessorFactory;
	
	@Factory
	public final HeaderProcessorFactory headerProcessorFactory;
	
	@Factory
	public final LabelProcessorFactory labelProcessorFactory;
	
	@Factory
	public final LinkProcessorFactory linkProcessorFactory;
	
	@Factory
	public final NavigationBarProcessorFactory navigationBarProcessorFactory;
	
	@Factory
	public final NavigationPanelProcessorFactory navigationPanelProcessorFactory;
	
	@Factory
	public final NavigationPanelStyleProcessorFactory navigationPanelStyleProcessorFactory;
	
	@Factory
	public final PageProcessorFactory pageProcessorFactory;
	
	@Factory
	public final PagePartProcessorFactory pagePartProcessorFactory;
	
	@Factory
	public final SectionStyleProcessorFactory sectionStyleProcessorFactory;	
	
	public EcoreGenAppProcessorsFactory(Context context) {
		this.context = context; 
		actionProcessorFactory = new ActionProcessorFactory(context);
		actionReferenceProcessorFactory = new ActionReferenceProcessorFactory(context);
		contentPanelProcessorFactory = new ContentPanelProcessorFactory(context);
		footerProcessorFactory = new FooterProcessorFactory(context);
		headerProcessorFactory = new HeaderProcessorFactory(context);
		labelProcessorFactory = new LabelProcessorFactory(context);
		linkProcessorFactory = new LinkProcessorFactory(context);
		navigationBarProcessorFactory = new NavigationBarProcessorFactory(context);
		navigationPanelProcessorFactory = new NavigationPanelProcessorFactory(context);
		navigationPanelStyleProcessorFactory = new NavigationPanelStyleProcessorFactory(context);
		pageProcessorFactory = new PageProcessorFactory(context);
		pagePartProcessorFactory = new PagePartProcessorFactory(context);
		sectionStyleProcessorFactory = new SectionStyleProcessorFactory(context);	
	}

	@EPackageNodeProcessorFactory(
			label = "HTML Application Model",
			icon = "https://img.icons8.com/fluency/48/application.png",
			description = "A model for building HTML applications",
			actionPrototype = """
                    app-action:
                      content:
                        content-markdown:
                          source:
                            content-resource:
                              location: app.md
					"""
	)
	public EPackageNodeProcessor createEPackageProcessor(
			NodeProcessorConfig<WidgetFactory, WidgetFactory> config, 
			java.util.function.Function<ProgressMonitor, Action> prototypeProvider,
			BiConsumer<Label, ProgressMonitor> labelConfigurator,
			ProgressMonitor progressMonitor) {		
		return new EPackageNodeProcessor(config, context, prototypeProvider) {
			
			@Override
			public void configureLabel(Object source, Label label, ProgressMonitor progressMonitor) {
				super.configureLabel(source, label, progressMonitor);
				if (labelConfigurator != null) {
					labelConfigurator.accept(label, progressMonitor);
				}
			}
			
		};
	}	

}
