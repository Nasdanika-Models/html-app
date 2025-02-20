package org.nasdanika.models.app.gen;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.nasdanika.common.Context;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.util.AppObjectLoaderExecutionParticipant;
import org.nasdanika.persistence.ObjectLoaderResourceFactory;

/**
 * {@link YamlLoadingSupplier} for Engineering {@link EPackage}s.
 * Registers exec- loader. 
 * @author Pavel
 *
 */
public abstract class AppGenObjectLoaderExecutionParticipant extends AppObjectLoaderExecutionParticipant {

	public AppGenObjectLoaderExecutionParticipant(Context context) {
		super(context);
	}
	
	@Override
	protected ObjectLoaderResourceFactory createObjectLoaderResorceFactory(ResourceSet resourceSet, ProgressMonitor progressMonitor) {
		resourceSet.getAdapterFactories().add(new AppAdapterFactory());
		return super.createObjectLoaderResorceFactory(resourceSet, progressMonitor);
	}
	
}
