package org.nasdanika.models.app.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import org.nasdanika.cli.Description;
import org.nasdanika.common.Context;
import org.nasdanika.common.DocumentationFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
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

	public HelpSiteCommand(
			CommandLine rootCommand,
			CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
		this.rootCommand = rootCommand;
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
				
		Action rootCommandAction = ActionHelpMixIn.createCommandLineAction(
				rootCommand, 
				documentationFactories,
				progressMonitor);
		
		rootCommandAction.setLocation("${base-uri}index.html");
		rootAction.getChildren().add(rootCommandAction);
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
