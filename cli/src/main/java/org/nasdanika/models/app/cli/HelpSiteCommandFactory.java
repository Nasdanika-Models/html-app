package org.nasdanika.models.app.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.cli.HelpCommand;
import org.nasdanika.cli.SubCommandCapabilityFactory;
import org.nasdanika.common.ProgressMonitor;

import picocli.CommandLine;

/**
 * Sub-command for the help command to generate help site.
 */
public class HelpSiteCommandFactory extends SubCommandCapabilityFactory<HelpSiteCommand> {

	@Override
	protected CompletionStage<HelpSiteCommand> createCommand(
			List<CommandLine> parentPath, 
			Loader loader,
			ProgressMonitor progressMonitor) {
		if (parentPath != null && parentPath.size() > 1) {
			Object userObj = parentPath.get(parentPath.size() - 1).getCommandSpec().userObject();
			if (userObj instanceof HelpCommand) {
				Requirement<Void, ActionHelpMixIn.Contributor> contributorRequirement = ServiceCapabilityFactory.createRequirement(ActionHelpMixIn.Contributor.class);				
				CompletionStage<Iterable<CapabilityProvider<ActionHelpMixIn.Contributor>>> contributorsCS = loader.load(contributorRequirement, progressMonitor);
				return contributorsCS.thenApply(capabilityProviders -> {
					Collection<ActionHelpMixIn.Contributor> contributors = new ArrayList<>();
					for (CapabilityProvider<ActionHelpMixIn.Contributor> capabilityProvider: capabilityProviders) {
						contributors.addAll(capabilityProvider.getPublisher().collect(Collectors.toList()).block());
					}				
					return new HelpSiteCommand(
							((HelpCommand) userObj).getRoot(),
							ActionHelpMixIn.Contributor.of(contributors),
							loader.getCapabilityLoader());
				});
			}
		}
		return null;
	}

	@Override
	protected Class<HelpSiteCommand> getCommandType() {
		return HelpSiteCommand.class;
	}

}
