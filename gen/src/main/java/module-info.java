import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.app.gen.AppAdapterCapabilityFactory;

module org.nasdanika.models.app.gen {
		
	requires transitive org.nasdanika.models.bootstrap.gen;
	requires transitive org.nasdanika.html.jstree;
	requires transitive org.nasdanika.models.app.emf;
	requires transitive sitemapgen4j;
	requires org.apache.commons.codec;
	requires org.apache.commons.text;
	requires transitive org.jsoup;
	requires org.eclipse.emf.ecore.xmi;
	requires org.eclipse.emf.ecore;
	
	exports org.nasdanika.models.app.gen;
	opens org.nasdanika.models.app.gen;
	
	provides CapabilityFactory with AppAdapterCapabilityFactory;
}
