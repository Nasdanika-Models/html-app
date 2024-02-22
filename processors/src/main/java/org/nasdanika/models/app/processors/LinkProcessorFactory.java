/**
 */
package org.nasdanika.models.app.processors;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.common.Context;
import org.nasdanika.common.Util;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.bootstrap.Modal;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.LINK)
public class LinkProcessorFactory extends LabelProcessorFactory {
	
	public LinkProcessorFactory(Context context) {
		super(context);
	}

//	String getLocation();
//	String getScript();
//	Modal getModal();
//	String getConfirmation();
//	String getName();
//	String getTarget();
//	ActionProcessorFactory getAction();

} // Link
