package org.nasdanika.models.app.gen;

import java.util.function.BiFunction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ContextualFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;

/**
 * Allows to process action content during site generation 
 * @author Pavel
 *
 */
public interface ActionContentProvider {
	
	interface Factory extends ContextualFactory<ActionContentProvider> {
		
	};
	
	EList<EObject> getActionContent(Action action, BiFunction<Label, URI, URI> uriResolver, ProgressMonitor progressMonitor);

}
