package org.nasdanika.models.app.gen;

import org.nasdanika.common.ConsumerFactory;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.emf.FunctionAdapterFactory;
import org.nasdanika.html.HTMLElement;
import org.nasdanika.models.app.AppPackage;
import org.nasdanika.models.app.ContentPanel;
import org.nasdanika.models.app.Footer;
import org.nasdanika.models.app.Header;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.Link;
import org.nasdanika.models.app.NavigationBar;
import org.nasdanika.models.app.NavigationPanel;
import org.nasdanika.models.app.Page;
import org.nasdanika.models.bootstrap.gen.BootstrapAdapterFactory;

/**
 * @author Pavel
 *
 */
public class AppAdapterFactory extends BootstrapAdapterFactory {
	
	public static AppAdapterFactory INSTANCE = new AppAdapterFactory();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AppAdapterFactory() {
		ClassLoader classLoader = getClassLoader();
		
		registerAdapterFactory(
				new FunctionAdapterFactory<SupplierFactory<HTMLElement<?>>, Page>(
					AppPackage.Literals.PAGE, 
					(Class) SupplierFactory.class, 
					classLoader, 
					e -> new PageSupplierFactoryAdapter(e, this)));
		
		registerAdapterFactory(
				new FunctionAdapterFactory<SupplierFactory.Provider, Label>(
					AppPackage.Literals.LABEL, 
					SupplierFactory.Provider.class, 
					classLoader, 
					e -> new LabelSupplierFactoryProviderAdapter(e, this)));
		
		registerAdapterFactory(
				new FunctionAdapterFactory<SupplierFactory.Provider, Link>(
					AppPackage.Literals.LINK, 
					SupplierFactory.Provider.class, 
					classLoader, 
					e -> new LinkSupplierFactoryProviderAdapter(e, this)));
		
		registerAdapterFactory(
				new FunctionAdapterFactory<ConsumerFactory<HTMLElement<?>>, ContentPanel>(
					AppPackage.Literals.CONTENT_PANEL, 
					(Class) ConsumerFactory.class, 
					classLoader, 
					e -> new ContentPanelConsumerFactoryAdapter(e, this)));		
		
		registerAdapterFactory(
				new FunctionAdapterFactory<ConsumerFactory<HTMLElement<?>>, Footer>(
					AppPackage.Literals.FOOTER, 
					(Class) ConsumerFactory.class, 
					classLoader, 
					e -> new FooterConsumerFactoryAdapter(e, this)));		
		
		registerAdapterFactory(
				new FunctionAdapterFactory<ConsumerFactory<HTMLElement<?>>, Header>(
					AppPackage.Literals.HEADER, 
					(Class) ConsumerFactory.class, 
					classLoader, 
					e -> new HeaderConsumerFactoryAdapter(e, this)));		
		
		registerAdapterFactory(
				new FunctionAdapterFactory<ConsumerFactory<HTMLElement<?>>, NavigationBar>(
					AppPackage.Literals.NAVIGATION_BAR, 
					(Class) ConsumerFactory.class, 
					classLoader, 
					e -> new NavigationBarConsumerFactoryAdapter(e, this)));		
		
		registerAdapterFactory(
				new FunctionAdapterFactory<ConsumerFactory<HTMLElement<?>>, NavigationPanel>(
					AppPackage.Literals.NAVIGATION_PANEL, 
					(Class) ConsumerFactory.class, 
					classLoader, 
					e -> new NavigationPanelConsumerFactoryAdapter(e, this)));	
		
		// Lower-level factories - through inheritance
//		registerAdapterFactory(new ExecutionParticpantAdapterFactory());
//		registerAdapterFactory(new HtmlAdapterFactory());
//		registerAdapterFactory(new BootstrapAdapterFactory());				
	}
	
}
