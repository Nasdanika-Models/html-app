package org.nasdanika.models.app.gen;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.nasdanika.common.Context;
import org.nasdanika.common.FunctionFactory;
import org.nasdanika.common.MapCompoundConsumerFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.emf.EObjectAdaptable;
import org.nasdanika.html.HTMLElement;
import org.nasdanika.html.Tag;
import org.nasdanika.html.bootstrap.BootstrapElement;
import org.nasdanika.html.bootstrap.BootstrapFactory;
import org.nasdanika.html.bootstrap.Container;
import org.nasdanika.html.bootstrap.Container.Row;
import org.nasdanika.html.bootstrap.Container.Row.Col;
import org.nasdanika.models.app.AppPackage;
import org.nasdanika.models.app.ContentPanel;
import org.nasdanika.models.app.Footer;
import org.nasdanika.models.app.Header;
import org.nasdanika.models.app.NavigationBar;
import org.nasdanika.models.app.NavigationPanel;
import org.nasdanika.models.app.Page;
import org.nasdanika.models.bootstrap.Appearance;
import org.nasdanika.models.bootstrap.gen.BootstrapElementSupplierFactoryAdapter;

public class PageSupplierFactoryAdapter extends BootstrapElementSupplierFactoryAdapter<Page, BootstrapElement<?,?>> {
	
	public PageSupplierFactoryAdapter(Page page, AdapterFactory adapterFactory) {
		super(page, adapterFactory);
	}
		
	protected Supplier<Supplier.FunctionResult<Map<EStructuralFeature, HTMLElement<?>>, HTMLElement<?>>> createContainerSupplier(Context context) {
		return new Supplier<Supplier.FunctionResult<Map<EStructuralFeature, HTMLElement<?>>, HTMLElement<?>>>() {

			@Override
			public double size() {
				return 1;
			}

			@Override
			public String name() {
				return "Container";
			}

			@Override
			public Supplier.FunctionResult<Map<EStructuralFeature, HTMLElement<?>>, HTMLElement<?>> execute(ProgressMonitor progressMonitor) {
				BootstrapFactory bootstrapFactory = context.get(BootstrapFactory.class, BootstrapFactory.INSTANCE);
				Page semanticElement = getTarget();
				Container container = semanticElement.isFluid() ? bootstrapFactory.fluidContainer() : bootstrapFactory.container();
				
				Map<EStructuralFeature, HTMLElement<?>> parts = new LinkedHashMap<>();
				
				if (semanticElement.getHeader() != null) {
					Col header = container.row().col();				
					header.toHTMLElement().addClass("nsd-app-header");					
					parts.put(AppPackage.Literals.PAGE__HEADER, header.toHTMLElement());
				}
				
				if (semanticElement.getNavigationBar() != null) {
					Col navigationBar =  container.row().col();
					navigationBar.toHTMLElement().addClass("nsd-app-navbar");
					parts.put(AppPackage.Literals.PAGE__NAVIGATION_BAR, navigationBar.toHTMLElement());
				}

				if (semanticElement.getNavigationPanel() != null || semanticElement.getContentPanel() != null) {
					Row contentRow = container.row();
					contentRow.toHTMLElement().addClass("nsd-app-content-row");
					parts.put(AppPackage.Literals.PAGE__CONTENT_ROW_APPEARANCE, contentRow.toHTMLElement());

					if (semanticElement.getNavigationPanel() != null) {
						Col navigationPanel = contentRow.col();
						navigationPanel.toHTMLElement().addClass("nsd-app-navigation-panel");
						parts.put(AppPackage.Literals.PAGE__NAVIGATION_PANEL, navigationPanel.toHTMLElement());
					}
				
					if (semanticElement.getContentPanel() != null) {
						Col contentPanel = contentRow.col();
						contentPanel.toHTMLElement().addClass("nsd-app-content-panel");
						parts.put(AppPackage.Literals.PAGE__CONTENT_PANEL, contentPanel.toHTMLElement());
					}
				}

				if (semanticElement.getFooter() != null) {
					Col footer =  container.row().col();
					footer.toHTMLElement().addClass("nsd-app-footer");
					parts.put(AppPackage.Literals.PAGE__FOOTER, footer.toHTMLElement());
				}
				
				Tag containerTag = container.toHTMLElement();
				containerTag.addClass("nsd-app-container");
				
				return new Supplier.FunctionResult<Map<EStructuralFeature,HTMLElement<?>>, HTMLElement<?>>(parts, containerTag);			}
			
		};
	}
	
	@Override
	public Supplier<HTMLElement<?>> createHTMLElementSupplier(Context context) {
		MapCompoundConsumerFactory<EStructuralFeature, HTMLElement<?>> partsFactory = new MapCompoundConsumerFactory<>("Page parts");
		Page semanticElement = (Page) getTarget();
		
		Header header = semanticElement.getHeader();
		if (header != null) {
			partsFactory.put(AppPackage.Literals.PAGE__HEADER, EObjectAdaptable.adaptToConsumerFactoryNonNull(header, HTMLElement.class));			
		}
		NavigationBar navigationBar = semanticElement.getNavigationBar();
		if (navigationBar != null) {
			partsFactory.put(AppPackage.Literals.PAGE__NAVIGATION_BAR, EObjectAdaptable.adaptToConsumerFactoryNonNull(navigationBar, HTMLElement.class));			
		}
		Appearance conentRowAppearance = semanticElement.getContentRowAppearance();
		if (conentRowAppearance != null) {
			partsFactory.put(AppPackage.Literals.PAGE__CONTENT_ROW_APPEARANCE, EObjectAdaptable.adaptToConsumerFactoryNonNull(conentRowAppearance, HTMLElement.class));			
		}
		NavigationPanel navigationPanel = semanticElement.getNavigationPanel();
		if (navigationPanel != null) {
			partsFactory.put(AppPackage.Literals.PAGE__NAVIGATION_PANEL, EObjectAdaptable.adaptToConsumerFactoryNonNull(navigationPanel, HTMLElement.class));			
		}
		ContentPanel contentPanel = semanticElement.getContentPanel();
		if (contentPanel != null) {
			partsFactory.put(AppPackage.Literals.PAGE__CONTENT_PANEL, EObjectAdaptable.adaptToConsumerFactoryNonNull(contentPanel, HTMLElement.class));			
		}
		Footer footer = semanticElement.getFooter();
		if (footer != null) {
			partsFactory.put(AppPackage.Literals.PAGE__FOOTER, EObjectAdaptable.adaptToConsumerFactoryNonNull(footer, HTMLElement.class));			
		}	
		
		SupplierFactory<Supplier.FunctionResult<Map<EStructuralFeature,HTMLElement<?>>, HTMLElement<?>>> containerSupplierFactory = this::createContainerSupplier;		
		FunctionFactory<Supplier.FunctionResult<Map<EStructuralFeature, HTMLElement<?>>, HTMLElement<?>>, HTMLElement<?>> partsFunctionFactory = partsFactory.asResultFunctionFactory();
		return containerSupplierFactory.then(partsFunctionFactory).create(context);
	}	
	
}
