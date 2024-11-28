import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.app.util.AppEPackageResourceSetCapabilityFactory;
import org.nasdanika.models.app.util.LabelConfiguratorCapabilityFactory;

module org.nasdanika.models.app {
		
	requires transitive org.nasdanika.models.bootstrap;
	requires org.nasdanika.mapping;
	
	exports org.nasdanika.models.app;
	exports org.nasdanika.models.app.impl;
	exports org.nasdanika.models.app.util;	
	
	provides CapabilityFactory with 
		AppEPackageResourceSetCapabilityFactory,
		LabelConfiguratorCapabilityFactory;
	
}
	
