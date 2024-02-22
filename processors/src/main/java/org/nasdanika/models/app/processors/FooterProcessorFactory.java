/**
 */
package org.nasdanika.models.app.processors;

import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.FOOTER)
public class FooterProcessorFactory extends PagePartProcessorFactory {
	
	public FooterProcessorFactory(Context context) {
		super(context);
	}

} // Footer
