/**
 */
package org.nasdanika.models.app.processors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Context;
import org.nasdanika.exec.resources.Resource;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.ACTION)
public class ActionProcessorFactory extends LinkProcessorFactory {
	
	public ActionProcessorFactory(Context context) {
		super(context);
	}
	
//	int getSectionColumns();
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
