package org.nasdanika.models.app.util;

import java.util.concurrent.CompletionStage;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.mapping.AbstractMappingFactory;
import org.nasdanika.mapping.AbstractMappingFactory.Contributor;

public class LabelContributorCapabilityFactory extends ServiceCapabilityFactory<AbstractMappingFactory<Object, EObject>, AbstractMappingFactory.Contributor<AbstractMappingFactory<Object, EObject>, EObject>> {

	@Override
	public boolean isFor(Class<?> type, Object serviceRequirement) {
		return type == AbstractMappingFactory.Contributor.class && serviceRequirement instanceof AbstractMappingFactory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected CompletionStage<Iterable<CapabilityProvider<Contributor<AbstractMappingFactory<Object, EObject>, EObject>>>> createService(
			Class<Contributor<AbstractMappingFactory<Object, EObject>, EObject>> serviceType,
			AbstractMappingFactory<Object, EObject> serviceRequirement, 
			Loader loader,
			ProgressMonitor progressMonitor) {
		
		return wrap((AbstractMappingFactory.Contributor) new LabelContributor(serviceRequirement));
	}

}
