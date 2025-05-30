package org.nasdanika.models.app.cli;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.capability.CapabilityLoader;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
		description = "Generates HTML site",
		versionProvider = ModuleVersionProvider.class,		
		mixinStandardHelpOptions = true,
		name = "site")
public class SiteCommand extends AbstractSiteCommand {
	
	public SiteCommand() {
		
	}
	
	public SiteCommand(CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
	}
		
	@Parameters(
		index =  "0",	
		description = {  
			"Model URI, resolved relative",
			"to the current directory"
		})
	private String model;
	
	@Override
	protected URI getModelURI(URI contextURI) {
		 return URI.createURI(model).resolve(contextURI);	
	}
	
	@Parameters(index =  "1", description = "Output directory")
	private String output;
	
	@Override
	protected String getOutput() {
		return output;
	}

}
