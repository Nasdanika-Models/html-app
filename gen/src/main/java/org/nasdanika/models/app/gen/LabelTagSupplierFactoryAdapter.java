package org.nasdanika.models.app.gen;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.nasdanika.common.Context;
import org.nasdanika.common.Function;
import org.nasdanika.common.ListCompoundSupplierFactory;
import org.nasdanika.common.MapCompoundSupplierFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.common.Util;
import org.nasdanika.emf.EObjectAdaptable;
import org.nasdanika.html.HTMLElement;
import org.nasdanika.html.HTMLFactory;
import org.nasdanika.html.Tag;
import org.nasdanika.html.TagName;
import org.nasdanika.html.bootstrap.BootstrapElement;
import org.nasdanika.html.bootstrap.BootstrapFactory;
import org.nasdanika.html.bootstrap.Color;
import org.nasdanika.models.app.AppPackage;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.NavigationPanel;
import org.nasdanika.models.bootstrap.gen.BootstrapElementSupplierFactoryAdapter;

public class LabelTagSupplierFactoryAdapter<M extends Label> extends BootstrapElementSupplierFactoryAdapter<M, BootstrapElement<?,?>> {
	
	public LabelTagSupplierFactoryAdapter(M label, AdapterFactory adapterFactory) {
		super(label, adapterFactory);
	}
		
	@Override
	public Supplier<HTMLElement<?>> createHTMLElementSupplier(Context context) {
		Function<Map<EStructuralFeature, Object>, HTMLElement<?>> labelFunction = new Function<Map<EStructuralFeature, Object>, HTMLElement<?>>() {

			@Override
			public double size() {
				return 1;
			}

			@Override
			public String name() {
				return "Label";
			}

			@Override
			public HTMLElement<?> execute(Map<EStructuralFeature, Object> features, ProgressMonitor progressMonitor) {
				BootstrapFactory bootstrapFactory = context.get(BootstrapFactory.class, BootstrapFactory.INSTANCE);
					
				Tag ret = (Tag) features.get(AppPackage.Literals.LABEL__TEXT);
				
				Label semanticElement = getTarget();
				Color color = semanticElement.getColor();								
				String notification = semanticElement.getNotification();
				if (!Util.isBlank(notification)) {
					Color notificationColor = Color.PRIMARY;
					if (color == Color.PRIMARY) {
						notificationColor = Color.SECONDARY;
					} else if (color == Color.DANGER) {
						notificationColor = Color.DANGER;				
					} else if (color == Color.WARNING) {
						notificationColor = Color.WARNING;				
					} 
					Tag badge = bootstrapFactory.badge(true, notificationColor, notification);
					badge.addClass("nsd-label-notification");
					ret.accept(badge);
				}
				
				String tooltip = semanticElement.getTooltip();
				if (!Util.isBlank(tooltip)) {
					ret.attribute("title", tooltip);
				}				
				
				Object decorator = features.get(AppPackage.Literals.LABEL__DECORATOR);
				HTMLFactory htmlFactory = bootstrapFactory.getHTMLFactory();
				if (decorator != null) {					
					if (TagName.span.name().equalsIgnoreCase(ret.getTagName())) {
						ret.accept(decorator);
					} else {
						ret = htmlFactory.span(ret, decorator);
					}
				}
								
				@SuppressWarnings("unchecked")
				List<Object> children = (List<Object>) features.get(AppPackage.Literals.LABEL__CHILDREN);
				if (children == null || semanticElement.eContainer() instanceof Label) {				
					setData(ret, semanticElement, features);
					return ret;
				}
				
				// Navigation drop-down
				Tag dropdown = htmlFactory.tag(TagName.li).addClass("dropdown");
				ret
					.addClass("nav-link", "dropdown-toggle")
					.attribute("role", "button")
					.attribute("data-toggle", "dropdown");
				
				dropdown.accept(ret);
								
				Tag dropdownMenu = htmlFactory.div().addClass("dropdown-menu");
				dropdown.accept(dropdownMenu);				
				addDropdownItems(dropdownMenu, children);
				
				setData(dropdown, semanticElement, features);				
				return dropdown;
			}

			protected void setData(Tag ret, Label semanticElement, Map<EStructuralFeature, Object> features) {
				ret.setData(semanticElement);
				
				/**
				 * Storing feature data to use downstream.
				 */
				for (Entry<EStructuralFeature, Object> fe: features.entrySet()) {
					ret.setData(fe.getKey(), fe.getValue());
				}
			}
			
			@SuppressWarnings("unchecked")
			private void addDropdownItems(Tag dropdownMenu, List<Object> items) {
				for (Object item: items) {					
					dropdownMenu.accept(item);
					if (item instanceof Tag) {
						Tag itemTag = (Tag) item;
						switch (itemTag.getTagName().toLowerCase()) {
						case "span":
							itemTag.addClass("active");  // Kinda hackish - assuming that the only span in a dropdow is the active item. Shall carry down some flag, perhaps using Tag properties
						case "a": 
							itemTag.addClass("dropdown-item");
							break;
						case "div": 
							itemTag.addClass("dropdown-divider");
							break;
						case "h6": 
							itemTag.addClass("dropdown-header");
							break;
						}
						List<Object> children = (List<Object>) itemTag.getData(AppPackage.Literals.LABEL__CHILDREN);
						if (children != null) {
							addDropdownItems(dropdownMenu, children);
						}					
					}
				}
			}
			
		};
		
		MapCompoundSupplierFactory<EStructuralFeature, Object> featuresFactory = new MapCompoundSupplierFactory<>("Features");
		
		M semanticElement = getTarget();
		Label decorator = semanticElement.getDecorator();
		if (decorator != null) {
			featuresFactory.put(AppPackage.Literals.LABEL__DECORATOR, EObjectAdaptable.adaptToSupplierFactoryNonNull(decorator, Object.class));
		}
		
		List<EObject> children = semanticElement.getChildren();
		if (!children.isEmpty()) {
			featuresFactory.put(AppPackage.Literals.LABEL__CHILDREN, new ListCompoundSupplierFactory<>("Children", EObjectAdaptable.adaptToSupplierFactoryNonNull(children, Object.class)));			
		}
		
		SupplierFactory<Tag> textSupplierFactory = this::createTextAndIconSupplier;
		featuresFactory.put(AppPackage.Literals.LABEL__TEXT, textSupplierFactory);
		
		return featuresFactory.create(context).then(labelFunction);
	}
	
	/**
	 * Override in Link to create a link.
	 * @param context
	 * @param progressMonitor
	 * @return
	 */
	protected Supplier<Tag> createTextAndIconSupplier(Context context) {
		Function<Tag,Tag> textAndIconFunction = new Function<Tag, Tag>() {
				
			@Override
			public double size() {
				return 1;
			}

			@Override
			public String name() {
				return "Label text and icon";
			}

			@Override
			public Tag execute(Tag modal, ProgressMonitor progressMonitor) {
				BootstrapFactory bootstrapFactory = context.get(BootstrapFactory.class, BootstrapFactory.INSTANCE);
				HTMLFactory htmlFactory = context.get(HTMLFactory.class, HTMLFactory.INSTANCE);
				Label semanticElement = getTarget();
				
				String text = context.interpolateToString(getTarget().getText());
				for (EObject semanticAncestor = semanticElement.eContainer(); semanticAncestor != null; semanticAncestor = semanticAncestor.eContainer()) {
					if (semanticAncestor instanceof NavigationPanel) {
						int labelTrimLength = ((NavigationPanel) semanticAncestor).getLabelTrimLength();
						if (labelTrimLength > 0 && !Util.isBlank(text) && text.length() > labelTrimLength) {
							text = text.substring(0, labelTrimLength - 3) + "...";
						}
						break;
					}						
				}
				
				String icon = semanticElement.getIcon();
				Tag iconTag = null;
				if (icon != null) {
					if (icon.contains("/")) {
						// Image
						iconTag = htmlFactory.tag(TagName.img).attribute("src", icon); //.style().height("1em");
					} else {
						// Class
						iconTag = htmlFactory.span().addClass(icon);
					}
				}
				
				Tag ret = createTextAndIconTag(iconTag, text, modal, context, progressMonitor);

				Color color = semanticElement.getColor();				
				if (color != null) {
					bootstrapFactory.wrap(ret).text().color(color);
				}	
												
				return ret;
			}
			
		};
		
		return getModalFactory().create(context).then(textAndIconFunction);				
	}
	
	/**
	 * @param icon Icon
	 * @param text text
	 * @param modal Modal for binding to links, not applicalbe to labels.
	 * @param context
	 * @param progressMonitor
	 * @return
	 */
	protected Tag createTextAndIconTag(Tag icon, String text, Tag modal, Context context, ProgressMonitor progressMonitor) {
		HTMLFactory htmlFactory = context.get(HTMLFactory.class, HTMLFactory.INSTANCE);
		TagName tagName;
		if (getTarget().getChildren().isEmpty()) {
			tagName = TagName.span; // A regular label.
		} else {
			// Drop-down activator or drop-down header if contained in a label
			tagName = getTarget().eContainer() instanceof Label ? TagName.h6 : TagName.a;
		}
		
		if (Util.isBlank(text)) {
			if (icon == null) {
				// Drop-down divider
				return htmlFactory.div();
			}
			
			// Wrap into a header for drop-down headers
			return tagName == TagName.h6 ? htmlFactory.tag(tagName, icon) : icon;
		}
		if (icon == null) {
			return htmlFactory.tag(tagName, text);
		}
		
		icon.addClass("nsd-app-label-icon");
		return htmlFactory.tag(tagName, icon, text);	
	}
	
	/**
	 * For links. Placing here to simplify code. This implementation returns empty supplier.
	 * @return
	 */
	protected SupplierFactory<Tag> getModalFactory() {
		return SupplierFactory.empty();
	}
		
}
