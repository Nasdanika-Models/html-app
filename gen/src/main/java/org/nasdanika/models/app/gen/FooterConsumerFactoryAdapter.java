package org.nasdanika.models.app.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Context;
import org.nasdanika.common.Function;
import org.nasdanika.common.ListCompoundSupplierFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.emf.EObjectAdaptable;
import org.nasdanika.html.HTMLElement;
import org.nasdanika.html.Tag;
import org.nasdanika.html.bootstrap.BootstrapFactory;
import org.nasdanika.html.bootstrap.Navs;
import org.nasdanika.models.app.Footer;

public class FooterConsumerFactoryAdapter extends PagePartConsumerFactoryAdapter<Footer> {

	protected FooterConsumerFactoryAdapter(Footer footer, AdapterFactory adapterFactory) {
		super(footer, adapterFactory);
	}
	
	private Function<Supplier.FunctionResult<HTMLElement<?>, List<Object>>, HTMLElement<?>> createNavsFunction(Context context) {
		return new Function<Supplier.FunctionResult<HTMLElement<?>,List<Object>>, HTMLElement<?>>() {
			
			@Override
			public double size() {
				return 1;
			}
			
			@Override
			public String name() {
				return "Navs";
			}
			
			@Override
			public HTMLElement<?> execute(Supplier.FunctionResult<HTMLElement<?>, List<Object>> input, ProgressMonitor progressMonitor) {
				Tag ret = (Tag) input.argument();				
				List<Object> items = new ArrayList<>();
				BootstrapFactory bootstrapFactory = context.get(BootstrapFactory.class, BootstrapFactory.INSTANCE);
				boolean isFirst = true;
				for (Object item: input.result()) {
					if (isFirst) {
						isFirst = false;
					} else {
						items.add(bootstrapFactory.getHTMLFactory().span("&nbsp;&bull;&nbsp;"));
					}
					items.add(item);
				}
				Tag navs = Util.navs(items, bootstrapFactory);
				navs.addClass("nsd-app-footer-navs");
				ret.content(navs);				
				return ret;
			}
		};
	}
	
	/**
	 * Adapts items to suppliers, builds {@link Navs} from items and adds them to the header.
	 */
	@Override
	protected Function<HTMLElement<?>, HTMLElement<?>> createConfigureFunction(Context context) {
		EList<EObject> items = getTarget().getItems();
		if (items.isEmpty()) {
			return super.createConfigureFunction(context);
		}
		Function<HTMLElement<?>, Supplier.FunctionResult<HTMLElement<?>, List<Object>>> itemsFunction = new ListCompoundSupplierFactory<>("Items", EObjectAdaptable.adaptToSupplierFactoryNonNull(items, Object.class)).<HTMLElement<?>>asFunctionFactory().create(context);
		return super.createConfigureFunction(context).then(itemsFunction).then(createNavsFunction(context));
	}

}
