package org.nasdanika.models.app.gen;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.nasdanika.html.bootstrap.BootstrapElement;
import org.nasdanika.models.app.PagePart;
import org.nasdanika.models.bootstrap.gen.BootstrapElementConsumerFactoryAdapter;

public class PagePartConsumerFactoryAdapter<M extends PagePart> extends BootstrapElementConsumerFactoryAdapter<M, BootstrapElement<?,?>> {

	protected PagePartConsumerFactoryAdapter(M pagePart, AdapterFactory adapterFactory) {
		super(pagePart, adapterFactory);
	}

}
