package org.nasdanika.models.app.util;

import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.nasdanika.common.Context;
import org.nasdanika.models.bootstrap.util.BootstrapObjectLoaderExecutionParticipant;
import org.nasdanika.models.app.AppPackage;

/**
 * Registers {@link AppPackage}
 * @author Pavel
 *
 */
public abstract class AppObjectLoaderExecutionParticipant extends BootstrapObjectLoaderExecutionParticipant {

	public AppObjectLoaderExecutionParticipant(Context context) {
		super(context);
	}

	@Override
	protected Collection<EPackage> getEPackages() {
		Collection<EPackage> ret = super.getEPackages(); 
		ret.add(AppPackage.eINSTANCE);
		return ret;
	}
	
}
