import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.app.util.AppEPackageResourceSetCapabilityFactory;

module org.nasdanika.html.model.app {
		
	requires transitive org.nasdanika.html.model.bootstrap;
	
	exports org.nasdanika.models.app;
	exports org.nasdanika.models.app.impl;
	exports org.nasdanika.models.app.util;	
	
	provides CapabilityFactory with AppEPackageResourceSetCapabilityFactory;
	
}
	
