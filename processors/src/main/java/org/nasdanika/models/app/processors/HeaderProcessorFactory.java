/**
 */
package org.nasdanika.models.app.processors;

import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.HEADER)
public class HeaderProcessorFactory extends PagePartProcessorFactory {

	public HeaderProcessorFactory(Context context) {
		super(context);
	}

//	LabelProcessorFactory getTitle();

} // Header
