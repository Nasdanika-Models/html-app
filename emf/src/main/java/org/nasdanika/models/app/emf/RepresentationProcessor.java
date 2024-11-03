package org.nasdanika.models.app.emf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Document;
import org.nasdanika.models.app.Action;

public interface RepresentationProcessor {
	
	default Document processDrawioRepresentation(
			Document document, 
			Action action, 
			java.util.function.Function<URI, EObject> semanticLinkResolver,
			org.nasdanika.models.app.emf.EObjectActionResolver.Context context, 
			ProgressMonitor progressMonitor) {
		
		return document;
	}

}
