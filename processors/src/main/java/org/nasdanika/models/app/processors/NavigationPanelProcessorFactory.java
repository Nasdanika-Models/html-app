/**
 */
package org.nasdanika.models.app.processors;

import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.NAVIGATION_PANEL)
public class NavigationPanelProcessorFactory extends PagePartProcessorFactory {
	
	public NavigationPanelProcessorFactory(Context context) {
		super(context);
	}

//	NavigationPanelStyleProcessorFactory getStyle();
//	String getId();
//	int getLabelTrimLength();
//	boolean isCollapsible();
//	int getJsTreeSearchThreshold();

} // NavigationPanel
