package org.nasdanika.models.app.graph.drawio;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.models.app.Label;

/**
 * Used to pass role with label.
 */
public class ModelElementAdapter implements Adapter {

	private Notifier target;
	private String role;
	private ModelElement<?> modelElement;
	private String sortKey;
	
	public ModelElementAdapter(
			Label target, 
			ModelElement<?> modelElement,
			String role,
			String sortKey) {
		this.target = target;
		this.modelElement = modelElement;		
		this.role = role;
		this.sortKey = sortKey;
	}
	
	public String getRole() {
		return role;
	}
	
	public ModelElement<?> getModelElement() {
		return modelElement;
	}
	
	public String getSortKey() {
		return sortKey;
	}

	@Override
	public void notifyChanged(Notification notification) {
		// NOP
	}

	@Override
	public Notifier getTarget() {
		return target;
	}

	@Override
	public void setTarget(Notifier newTarget) {
		this.target = newTarget;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return type == ModelElementAdapter.class;
	}

}
