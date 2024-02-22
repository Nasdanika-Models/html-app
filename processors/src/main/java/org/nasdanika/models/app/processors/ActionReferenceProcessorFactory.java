/**
 */
package org.nasdanika.models.app.processors;

import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.ACTION_REFERENCE)
public class ActionReferenceProcessorFactory {
	
	private Context context;

	public ActionReferenceProcessorFactory(Context context) {
		this.context = context;
	}

} // ActionReference
