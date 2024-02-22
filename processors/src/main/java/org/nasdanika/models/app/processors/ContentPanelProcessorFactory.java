/**
 */
package org.nasdanika.models.app.processors;

import org.eclipse.emf.common.util.EList;
import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.CONTENT_PANEL)
public class ContentPanelProcessorFactory extends PagePartProcessorFactory {
	
	public ContentPanelProcessorFactory(Context context) {
		super(context);
	}

//	LabelProcessorFactory getTitle();
//	EList<LabelProcessorFactory> getBreadcrumb();
//	NavigationPanelProcessorFactory getLeftNavigation();
//	NavigationPanelProcessorFactory getRightNavigation();
//	NavigationPanelProcessorFactory getFloatLeftNavigation();
//	NavigationPanelProcessorFactory getFloatRightNavigation();
//	EList<ContentPanelProcessorFactory> getSections();
//	int getSectionColumns();
//	SectionStyleProcessorFactory getSectionStyle();

} // ContentPanel
