package org.nasdanika.models.app.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.common.Context;
import org.nasdanika.common.Description;
import org.nasdanika.common.DocumentationFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;
import org.nasdanika.models.app.Label;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;

@Command(
		description = "Generates help HTML site",
		mixinStandardHelpOptions = true,
		name = "site")
@Description(
		icon = "https://img.icons8.com/material-two-tone/20/web.png",
		value = """
				## Example
				
				```
				help site --page-template="page-template.yml#/" --root-action-icon=https://docs.nasdanika.org/images/nasdanika-logo.png --root-action-location=https://github.com/Nasdanika-Demos --root-action-text="Nasdanika Demos" target/doc-site
				```
								
				"""
		)
public class HelpSiteCommand extends AbstractSiteCommand {
	
	// TODO - root action for header/icon
	
	@Parameters(index =  "0", description = "Output directory")
	private String output;
	
	@Override
	protected String getOutput() {
		return output;
	}
	
	private CommandLine rootCommand;
	private ActionHelpMixIn.Contributor contributor;

	public HelpSiteCommand(
			CommandLine rootCommand,
			ActionHelpMixIn.Contributor contributor,
			CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
		this.rootCommand = rootCommand;
		this.contributor = contributor;
	}
	
	private URI modelURI;
	
	@Override
	protected URI getModelURI(URI contextURI) {
		return modelURI;
	}
		
	@Option(names = "--root-action-icon", description = "Root action icon")
	private String rootActionIcon;
	
	@Option(names = "--root-action-text", description = "Root action text")
	private String rootActionText;
	
	@Option(names = "--root-action-location", description = "Root action location")
	private String rootActionLocation;
	
	@Option(
			names = "--command-path", 
			split = ",",
			description = {
				"Comma-separated list of command names",
				"help is generated for the last command",
				"in the path"
			})
	@Description(
			"""
			Use this option if you want to generate help not for the root
			command, but for a sub-command.			
			""")
	private String[] commandPath;
	
	@Override
	protected int generate(Context context, ProgressMonitor progressMonitor) throws IOException, DiagnosticException {
		Action rootAction = AppFactory.eINSTANCE.createAction();
		rootAction.setIcon(rootActionIcon);
		rootAction.setText(rootActionText);
		rootAction.setLocation(rootActionLocation);
		
		ArrayList<DocumentationFactory> documentationFactories = new ArrayList<>();
		if (capabilityLoader != null) {
			Requirement<Object, DocumentationFactory> requirement = ServiceCapabilityFactory.createRequirement(DocumentationFactory.class, null, new DocumentationFactory.Requirement(true));
			Iterable<CapabilityProvider<Object>> cpi = capabilityLoader.load(requirement, progressMonitor);
			for (CapabilityProvider<Object> cp: cpi) {				
				cp.getPublisher().subscribe(df -> documentationFactories.add((DocumentationFactory) df));
			}
		}		
				
		CommandLine command = rootCommand;
		if (commandPath != null) {
			for (String cmdName: commandPath) {
				Map<String, CommandLine> subCommands = command.getSubcommands();
				if (subCommands == null) {
					throw new ParameterException(command, command.getCommandName() + " has no subcommands");
				} else {
					CommandLine subCommand = subCommands.get(cmdName);
					if (subCommand == null) {
						throw new ParameterException(command, command.getCommandName() + " does not have " + cmdName + " subcommand");						
					}
					command = subCommand;
				}
			}
		}
		Label rootCommandLabel = ActionHelpMixIn.createCommandLineLabel(
				command, 
				documentationFactories,
				contributor,
				progressMonitor);
		
		if (rootCommandLabel instanceof Action) {
			((Action) rootCommandLabel).setLocation("${base-uri}index.html");
		}
		rootAction.getChildren().add(rootCommandLabel);
		ResourceSet actionModelsResourceSet = new ResourceSetImpl();
		actionModelsResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		
		URI actionModelResourceURI = URI.createFileURI(File.createTempFile("command-action-model-", ".xml").getAbsolutePath());		
		Resource actionModelResource = actionModelsResourceSet.createResource(actionModelResourceURI);
		actionModelResource.getContents().add(rootAction);
		actionModelResource.save(null);
		modelURI = actionModelResourceURI.appendFragment("/");
		return super.generate(context, progressMonitor);
	}

}
