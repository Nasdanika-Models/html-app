package org.nasdanika.models.app.cli;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.common.Context;
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
public class HelpSiteCommand extends AbstractSiteCommand {
	
	public HelpSiteCommand() {
		
	}
	
	public HelpSiteCommand(CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
	}
	
	// TODO - root action for header/icon
	
	@Parameters(index =  "0", description = "Output directory")
	private String output;
	
	@Override
	protected String getOutput() {
		return output;
	}
	
	private CommandLine rootCommand;

	public HelpSiteCommand(CommandLine rootCommand) {
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
		
		Action rootCommandAction = ActionHelpMixIn.createCommandLineAction(rootCommand);
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
