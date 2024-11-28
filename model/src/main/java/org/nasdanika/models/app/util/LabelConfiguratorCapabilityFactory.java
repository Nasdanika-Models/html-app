package org.nasdanika.models.app.util;

import java.util.concurrent.CompletionStage;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.emf.ConfigurationLoadingDrawioFactory;
import org.nasdanika.mapping.AbstractMappingFactory;
import org.nasdanika.mapping.AbstractMappingFactory.TargetConfigurator;

public class LabelConfiguratorCapabilityFactory extends ServiceCapabilityFactory<ConfigurationLoadingDrawioFactory<EObject>, AbstractMappingFactory.TargetConfigurator<ConfigurationLoadingDrawioFactory<EObject>, EObject>> {

	@Override
	public boolean isFor(Class<?> type, Object serviceRequirement) {
		return type == AbstractMappingFactory.TargetConfigurator.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected CompletionStage<Iterable<CapabilityProvider<TargetConfigurator<ConfigurationLoadingDrawioFactory<EObject>, EObject>>>> createService(
			Class<TargetConfigurator<ConfigurationLoadingDrawioFactory<EObject>, EObject>> serviceType,
			ConfigurationLoadingDrawioFactory<EObject> serviceRequirement, 
			Loader loader,
			ProgressMonitor progressMonitor) {
		
		return wrap((AbstractMappingFactory.TargetConfigurator) new LabelConfigurator());
	}

}
