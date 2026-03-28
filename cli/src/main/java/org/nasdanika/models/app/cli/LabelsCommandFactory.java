package org.nasdanika.models.app.cli;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.nasdanika.cli.SubCommandCapabilityFactory;
import org.nasdanika.common.EObjectSupplier;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Util;
import org.nasdanika.models.app.util.LabelSupplier;

import picocli.CommandLine;

public class LabelsCommandFactory extends SubCommandCapabilityFactory<LabelsCommand> {

	@Override
	protected Class<LabelsCommand> getCommandType() {
		return LabelsCommand.class;
	}
	
	@Override
	protected CompletionStage<LabelsCommand> doCreateCommand(
			List<CommandLine> parentPath,
			Loader loader,
			ProgressMonitor progressMonitor) {
		
		// Do not bind to LabelSuppliers - would be an infinite loop
		if (!parentPath.isEmpty()) {
			for (CommandLine ancestor: parentPath) {
				Object userObject = ancestor.getCommandSpec().userObject();
				if (userObject instanceof LabelSupplier) {
					return null;
				}
			}
			
			CommandLine lastCommand = parentPath.get(parentPath.size() - 1);
			Object userObject = lastCommand.getCommandSpec().userObject();
			if (userObject instanceof EObjectSupplier) {
				// Check for sub-interfaces of EObjectSupplier
				Class<?> commandClass = userObject.getClass();
				for (Class<?> ancestor: Util.lineage(commandClass)) {
					if (ancestor.isInterface() && EObjectSupplier.class.isAssignableFrom(ancestor) && ancestor != EObjectSupplier.class) {
						return null;
					}
				}; 
			}
			
		}
			
		return CompletableFuture.completedStage(new LabelsCommand());
	}

}
