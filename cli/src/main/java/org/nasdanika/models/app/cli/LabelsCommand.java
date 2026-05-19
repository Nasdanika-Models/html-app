package org.nasdanika.models.app.cli;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.cli.CommandGroup;
import org.nasdanika.cli.ParentCommands;
import org.nasdanika.common.Description;
import org.nasdanika.common.EObjectSupplier;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.util.LabelSupplier;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(
		description = "Filters label model elements",
		versionProvider = ModuleVersionProvider.class,		
		mixinStandardHelpOptions = true,
		name = "labels")
@ParentCommands(EObjectSupplier.class)
@Description(icon = "https://docs.nasdanika.org/images/tags.svg")
public class LabelsCommand extends CommandGroup implements LabelSupplier {
	
	@ParentCommand
	EObjectSupplier<EObject> eObjectSupplier;

	@Override
	public Collection<Label> getEObjects(ProgressMonitor progressMonitor) {
		return eObjectSupplier.getEObjects(progressMonitor).stream()
				.filter(Label.class::isInstance)
				.map(Label.class::cast)
				.toList(); 
	}

}
