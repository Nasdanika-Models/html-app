package org.nasdanika.models.app.util;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.mapping.AbstractMappingFactory;
import org.nasdanika.mapping.ContentProvider;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;

public class LabelConfigurator implements AbstractMappingFactory.TargetConfigurator<ModelElement, Label> {

	@Override
	public boolean canHandle(Object source, EObject target) {
		return source instanceof ModelElement && target instanceof Label;
	}

	@Override
	public void configureTarget(
			ModelElement obj, 
			Label target, 
			ContentProvider<ModelElement> contentProvider,
			Map<ModelElement, Label> registry, 
			boolean isPrototype, 
			ProgressMonitor progressMonitor) {
		
		if (org.nasdanika.common.Util.isBlank(target.getText())) {
			target.setText(contentProvider.getName(obj));
		}
	}

}
