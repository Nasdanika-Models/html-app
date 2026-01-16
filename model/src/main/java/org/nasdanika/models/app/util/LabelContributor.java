package org.nasdanika.models.app.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.mapping.AbstractMappingFactory;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.Link;

public class LabelContributor implements AbstractMappingFactory.Contributor<ModelElement<?>, Label> {
	
	private AbstractMappingFactory<Object, EObject> factory;

	public LabelContributor(AbstractMappingFactory<Object,EObject> factory) {
		this.factory = factory;
	}

	@Override
	public boolean canHandle(Object source, EObject target) {
		return source instanceof ModelElement && target instanceof Label;
	}
	
	@Override
	public void initialize(
			ModelElement<?> obj, 
			Label target,
			BiConsumer<ModelElement<?>, BiConsumer<Label, ProgressMonitor>> elementProvider,
			Consumer<BiConsumer<Map<ModelElement<?>, Label>, ProgressMonitor>> registry,
			ProgressMonitor progressMonitor) {
		
		if (target instanceof Link) {
			obj.setProperty("link-uuid", target.getUuid());
		}		
	}
	
	@Override
	public void configure(
			ModelElement<?> obj, 
			Collection<EObject> documentation, 
			Label target,
			Map<ModelElement<?>, Label> registry, 
			boolean isPrototype, 
			ProgressMonitor progressMonitor) {
		
		if (org.nasdanika.common.Util.isBlank(target.getText())) {
			String name = factory.getContentProvider().getName(obj);
			target.setText(name);
		}
		if (org.nasdanika.common.Util.isBlank(target.getTooltip())) {
			target.setTooltip(obj.getTooltip());
		}
		
		if (target instanceof Action && documentation != null) {
			((Action) target).getContent().addAll(documentation);
		}
	}

}
