/**
 */
package org.nasdanika.models.app.processors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.bootstrap.BootstrapElement;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.PAGE_PART)
public class PagePartProcessorFactory {
	
	private Context context;

	public PagePartProcessorFactory(Context context) {
		this.context = context;
	}

//	EList<EObject> getItems();

} // PagePart
