package org.nasdanika.models.app.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.nasdanika.capability.emf.EPackageCapabilityFactory;
import org.nasdanika.models.app.AppPackage;

public class AppEPackageResourceSetCapabilityFactory extends EPackageCapabilityFactory {

	@Override
	protected EPackage getEPackage() {
		return AppPackage.eINSTANCE;
	}

	@Override
	protected URI getDocumentationURI() {
		return URI.createURI("https://html-app.models.nasdanika.org/");
	}

}
