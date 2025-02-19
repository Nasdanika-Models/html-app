import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.app.cli.ActionHelpMixInFactory;
import org.nasdanika.models.app.cli.AppCommandFactory;
import org.nasdanika.models.app.cli.DrawioHtmlAppGeneratorCommandFactory;
import org.nasdanika.models.app.cli.HelpSiteCommandFactory;
import org.nasdanika.models.app.cli.HtmlAppGeneratorCommandFactory;
import org.nasdanika.models.app.cli.SiteGeneratorCommandFactory;

module org.nasdanika.models.app.cli {
	
	exports org.nasdanika.models.app.cli;

	requires transitive org.nasdanika.models.app.gen;		
	requires transitive org.nasdanika.cli;
	requires spring.core;
	requires org.eclipse.emf.ecore.xmi;
	requires org.nasdanika.models.app.graph;
	requires org.apache.commons.text;
	
	opens org.nasdanika.models.app.cli to info.picocli;
	
	provides CapabilityFactory with 
		AppCommandFactory, 
		HelpSiteCommandFactory,
		ActionHelpMixInFactory,
		HtmlAppGeneratorCommandFactory,
		DrawioHtmlAppGeneratorCommandFactory,
		SiteGeneratorCommandFactory;

}
