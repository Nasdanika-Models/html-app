package org.nasdanika.models.app.gen;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Context;
import org.nasdanika.common.Function;
import org.nasdanika.common.ListCompoundSupplierFactory;
import org.nasdanika.common.MapCompoundSupplierFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.emf.EObjectAdaptable;
import org.nasdanika.html.HTMLElement;
import org.nasdanika.html.Tag;
import org.nasdanika.html.bootstrap.BootstrapFactory;
import org.nasdanika.html.bootstrap.Navs;
import org.nasdanika.models.app.AppPackage;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.NavigationBar;

public class NavigationBarConsumerFactoryAdapter extends PagePartConsumerFactoryAdapter<NavigationBar> {

	protected NavigationBarConsumerFactoryAdapter(NavigationBar navigationBar, AdapterFactory adapterFactory) {
		super(navigationBar, adapterFactory);
	}
	
	private Function<Supplier.FunctionResult<HTMLElement<?>, Map<EStructuralFeature, Object>>, HTMLElement<?>> createNavbarFunction(Context context) {
		return new Function<Supplier.FunctionResult<HTMLElement<?>,Map<EStructuralFeature, Object>>, HTMLElement<?>>() {
			
			@Override
			public double size() {
				return 1;
			}
			
			@Override
			public String name() {
				return "Navs";
			}
			
			@Override
			public HTMLElement<?> execute(Supplier.FunctionResult<HTMLElement<?>, Map<EStructuralFeature, Object>> input, ProgressMonitor progressMonitor) {
				Tag ret = (Tag) input.argument();
				Tag brand = (Tag) input.result().get(AppPackage.Literals.NAVIGATION_BAR__BRAND);
				@SuppressWarnings("unchecked")
				List<Object> items = (List<Object>) input.result().get(AppPackage.Literals.PAGE_PART__ITEMS);	
				NavigationBar semanticElement = getTarget();
				ret.content(Util.navbar(
						brand, 
						items, 
						semanticElement.getExpand(), 
						semanticElement.isDark(),
						semanticElement.getBackground(),						
						context.get(BootstrapFactory.class, BootstrapFactory.INSTANCE)));	
				
				return ret;
			}
		};
	}
	
	/**
	 * Adapts items to suppliers, builds {@link Navs} from items and adds them to the header.
	 */
	@Override
	protected Function<HTMLElement<?>, HTMLElement<?>> createConfigureFunction(Context context) {
		Label brand = getTarget().getBrand();
		EList<EObject> items = getTarget().getItems();
		if (brand == null && items.isEmpty()) {
			return super.createConfigureFunction(context);
		}
		
		MapCompoundSupplierFactory<EStructuralFeature, Object> featuresSupplierFactory = new MapCompoundSupplierFactory<>("Brand and items");
		
		if (brand != null) {
			featuresSupplierFactory.put(AppPackage.Literals.NAVIGATION_BAR__BRAND, EObjectAdaptable.adaptToSupplierFactoryNonNull(brand, Object.class));
		}
		
		if (!items.isEmpty()) {
			featuresSupplierFactory.put(AppPackage.Literals.PAGE_PART__ITEMS, new ListCompoundSupplierFactory<>("Items", EObjectAdaptable.adaptToSupplierFactoryNonNull(items, Object.class)));
		}
		
		Function<HTMLElement<?>, Supplier.FunctionResult<HTMLElement<?>, Map<EStructuralFeature, Object>>> featuresFunction = featuresSupplierFactory.<HTMLElement<?>>asFunctionFactory().create(context);			
		return super.createConfigureFunction(context).then(featuresFunction).then(createNavbarFunction(context));
	}
	
}
