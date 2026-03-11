package org.nasdanika.models.app.cli;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.cli.ParentCommands;
import org.nasdanika.common.EObjectSupplier;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Label;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
		description = "Filters label model elements",
		versionProvider = ModuleVersionProvider.class,		
		mixinStandardHelpOptions = true,
		name = "labels")
@ParentCommands(EObjectSupplier.class)
public class LabelsCommand extends AbstractHtmlAppGeneratorCommand {
	
	@ParentCommand
	EObjectSupplier<EObject> eObjectSupplier;
	
	@Override
	protected Collection<Label> getLabels(ProgressMonitor progressMonitor) {
		return eObjectSupplier.getEObjects(progressMonitor).stream()
				.filter(Label.class::isInstance)
				.map(Label.class::cast)
				.toList(); 
	}

}
