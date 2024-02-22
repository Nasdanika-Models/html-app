/**
 */
package org.nasdanika.models.app.processors;

import org.nasdanika.common.Context;
import org.nasdanika.html.bootstrap.Breakpoint;
import org.nasdanika.html.bootstrap.Color;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.NAVIGATION_BAR)
public class NavigationBarProcessorFactory extends PagePartProcessorFactory {
	
	public NavigationBarProcessorFactory(Context context) {
		super(context);
	}

//	LabelProcessorFactory getBrand();
//	boolean isDark();
//	Breakpoint getExpand();
//	Color getBackground();

} // NavigationBar
