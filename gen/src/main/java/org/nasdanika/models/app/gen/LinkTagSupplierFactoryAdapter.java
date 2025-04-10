package org.nasdanika.models.app.gen;

import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.nasdanika.common.Context;
import org.nasdanika.common.ExecutionException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.common.Util;
import org.nasdanika.emf.EObjectAdaptable;
import org.nasdanika.html.Event;
import org.nasdanika.html.HTMLFactory;
import org.nasdanika.html.Tag;
import org.nasdanika.html.TagName;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Link;
import org.nasdanika.models.html.gen.ContentConsumer;

public class LinkTagSupplierFactoryAdapter<M extends Link> extends LabelTagSupplierFactoryAdapter<M> {
	
	public LinkTagSupplierFactoryAdapter(M link, AdapterFactory adapterFactory) {
		super(link, adapterFactory);
		if (link instanceof Action) {
			throw new IllegalArgumentException("Actions must be converted to links first");
		}
	}
	
	@Override
	protected Tag createTextAndIconTag(Tag icon, String text, Tag modal, Context context, ProgressMonitor progressMonitor) {
		M semanticElement = getTarget();
		HTMLFactory htmlFactory = context.get(HTMLFactory.class, HTMLFactory.INSTANCE);

		Tag anchor = htmlFactory.tag(TagName.a);
		if (icon != null) {
			icon.addClass("nsd-app-label-icon");
			anchor.accept(icon);
		}
		if (!Util.isBlank(text)) {
			anchor.accept(text);
		}
		
		if (!semanticElement.isDisabled()) {
			if (modal != null) {
				if (modal.getId() == null) {
					modal.id(htmlFactory.nextId());
				}
				anchor.attribute("data-toggle", "modal");
				anchor.attribute("data-target", "#" + modal.getId());
				anchor.attribute("href", "#");
	
				@SuppressWarnings("unchecked")
				List<Object> pageBody = context.get(org.nasdanika.models.html.gen.PageSupplierFactoryAdapter.PAGE_BODY_PROPERTY, List.class);
				if (pageBody != null) {
					pageBody.add(modal);
				} else {
					ContentConsumer contentConsumer = context.get(ContentConsumer.class);
					if (contentConsumer == null) {
						throw new ExecutionException("Cannot contribute a modal - there is neither page body nor content consumer in the context");
					}
					contentConsumer.accept(modal);
				}
			} else {	
				String name = context.interpolateToString(semanticElement.getName());
				if (!Util.isBlank(name)) {
					anchor.attribute("name", name);
				} else {				
					String location = context.interpolateToString(semanticElement.getLocation());
					String confirmation = context.interpolateToString(semanticElement.getConfirmation());
					if (!Util.isBlank(location)) {
						anchor.attribute("href", location);					
						if (!Util.isBlank(confirmation)) {
							anchor.on(Event.click, "return confirm('" + confirmation + "');");
						}
						String target = context.interpolateToString(semanticElement.getTarget());
						if (!Util.isBlank(target)) {
							anchor.attribute("target", target);
						}
					} else { 
						String script = context.interpolateToString(semanticElement.getScript());
						if (!Util.isBlank(script)) {
							if (!Util.isBlank(confirmation)) {
								script = "if (confirm('" + confirmation + "')) { "+ script +" } return false;";
							}
							anchor.on(Event.click, script);
							anchor.style("cursor", "pointer");
							anchor.attribute("href", "#");
						} 
					}
				}
			}
		}
		
		return anchor;
	}

	@Override
	protected SupplierFactory<Tag> getModalFactory() {
		M semanticElement = getTarget();
		org.nasdanika.models.bootstrap.Modal modal = semanticElement.getModal();
		SupplierFactory<Tag> modalFactory = modal == null ? SupplierFactory.empty() : EObjectAdaptable.adaptToSupplierFactoryNonNull(modal, Tag.class);
		return modalFactory;
	}
	
}
