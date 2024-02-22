/**
 */
package org.nasdanika.models.app.processors;

import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.bootstrap.Appearance;
import org.nasdanika.html.model.bootstrap.BootstrapElement;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.PAGE)
public class PageProcessorFactory {
	
	private Context context;

	public PageProcessorFactory(Context context) {
		this.context = context;
	}

//	boolean isFluid();
//	HeaderProcessorFactory getHeader();
//	NavigationBarProcessorFactory getNavigationBar();
//	NavigationPanelProcessorFactory getNavigationPanel();
//	ContentPanelProcessorFactory getContentPanel();
//	FooterProcessorFactory getFooter();
//	Appearance getContentRowAppearance();

} // Page
