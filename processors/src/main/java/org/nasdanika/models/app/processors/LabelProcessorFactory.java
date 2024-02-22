/**
 */
package org.nasdanika.models.app.processors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.bootstrap.BootstrapElement;
import org.nasdanika.html.model.bootstrap.Item;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.LABEL)
public class LabelProcessorFactory {
	
	private Context context;

	public LabelProcessorFactory(Context context) {
		this.context = context;
	}

//	String getId();
//	String getText();
//	String getIcon();
//	String getTooltip();
//	boolean isOutline();
//	String getNotification();
//	void setNotification(String value);
//	EList<EObject> getChildren();
//	LabelProcessorFactory getDecorator();

} // Label
