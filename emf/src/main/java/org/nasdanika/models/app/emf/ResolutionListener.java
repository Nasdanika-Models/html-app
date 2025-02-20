package org.nasdanika.models.app.emf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.Composable;
import org.nasdanika.common.Context;
import org.nasdanika.drawio.Document;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;
import org.nasdanika.ncore.ModelElement;

/**
 * Receives notifications about successful and unsuccessful resolution of target-uri, action-uri, action-uuid properties and semantic-link semantic-ref tokens.
 * to report resolution error and also to build back-links. 
 * Instances of this interface are obtained as {@link Context} services.
 * @author Pavel
 *
 */
public interface ResolutionListener extends Composable<ResolutionListener> {

	/**
	 * Called on resolution of "target-uri" property of a diagram element.
	 * If target is null then resolution was unsuccessful.
	 * @param element
	 * @param action
	 * @param targetUriPropertyValue
	 * @param target
	 */
	default void onTargetURIResolution(
			Action action, 
			org.nasdanika.drawio.ModelElement modelElement, 
			String targetUriPropertyValue, 
			EObject target) {
		
	}

	/**
	 * Called on resoulution of semantic-uuid property.
	 * @param element
	 * @param action
	 * @param semanticUUID
	 * @param semanticModelElement If null then there is no element with a given UUID.
	 * @param semanticModelElementAction If null and the previous argument is not null, then there is a semantic element, but there is not action for it.
	 */
	default void onSemanticUUIDResolution(
			Action action, 
			org.nasdanika.drawio.ModelElement modelElement, 
			String semanticUUID, 
			ModelElement semanticModelElement,
			Label semanticModelElementLabel) {
		
	}

	/**
	 * Called on resolution of action URI
	 * @param action Source action
	 * @param modelElement Source model element if resolution is done for diagram element properties
	 * @param targetURIStr Target URI string resolved relative to action URI's.
	 * @param target Target, null if not found.
	 * @param targetURI Target URI relative to the source action. Null of target is not found or doesn't have a URI.
	 * @param backLinkURI Backlink from the target action to the source action.
	 */
	default void onActionURIResolution(
			Action action, 
			org.nasdanika.drawio.ModelElement modelElement, 
			String targetURIStr, 
			Label target, 
			URI targetURI, 
			URI backLinkURI) {
		
	}

	/**
	 * Called on resolution of action UUID
	 * @param action
	 * @param modelElement
	 * @param actionUUID
	 * @param target
	 * @param targetURI
	 * @param backLinkURI
	 */
	default void onActionUUIDResolution(
			Action action, 
			org.nasdanika.drawio.ModelElement modelElement, 
			String actionUUID,
			Label target,
			URI targetURI,
			URI backLinkURI) {
		
	}

	/**
	 * Called on resolution of semantic reference or link
	 * @param action
	 * @param targetURIStr
	 * @param target
	 * @param targetURI
	 * @param backLinkURI
	 */
	default void onSemanticReferenceResolution(
			Action action, 
			String targetURIStr, 
			Label target, 
			URI targetURI, 
			URI backLinkURI) {
		
	}
	
	/**
	 * Called on resolution of representation context property.
	 * @param action
	 * @param key Representation key
	 * @param document Representation document, can be null if representation with a given key does not exist.
	 */
	default void onComputingDrawioRepresentation(
			Action action,
			String key,
			org.nasdanika.drawio.Document document) {
		
	}

	@Override
	default ResolutionListener compose(ResolutionListener other) {
		if (other == null) {
			return this;
		}
		
		return new ResolutionListener() {
			/**
			 * Called on resolution of "target-uri" property of a diagram element.
			 * If target is null then resolution was unsuccessful.
			 * @param element
			 * @param action
			 * @param targetUriPropertyValue
			 * @param target
			 */
			@Override
			public void onTargetURIResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement, 
					String targetUriPropertyValue, 
					EObject target) {
				
				ResolutionListener.this.onTargetURIResolution(action, modelElement, targetUriPropertyValue, target);
				other.onTargetURIResolution(action, modelElement, targetUriPropertyValue, target);
			}
			
			@Override
			public void onSemanticUUIDResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement, 
					String semanticUUID, 
					ModelElement semanticModelElement,
					Label semanticModelElementLabel) {			
				ResolutionListener.this.onSemanticUUIDResolution(action, modelElement, semanticUUID, semanticModelElement, semanticModelElementLabel);
				other.onSemanticUUIDResolution(action, modelElement, semanticUUID, semanticModelElement, semanticModelElementLabel);
			}

			/**
			 * Called on resolution of action URI
			 * @param action Source action
			 * @param modelElement Source model element if resolution is done for diagram element properties
			 * @param targetURIStr Target URI string resolved relative to action URI's.
			 * @param target Target, null if not found.
			 * @param targetURI Target URI relative to the source action. Null of target is not found or doesn't have a URI.
			 * @param backLinkURI Backlink from the target action to the source action.
			 */
			@Override
			public void onActionURIResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement, 
					String targetURIStr, 
					Label target, 
					URI targetURI, 
					URI backLinkURI) {
				ResolutionListener.this.onActionURIResolution(action, modelElement, targetURIStr, target, targetURI, backLinkURI);
				other.onActionURIResolution(action, modelElement, targetURIStr, target, targetURI, backLinkURI);
			}

			/**
			 * Called on resolution of action UUID
			 * @param action
			 * @param modelElement
			 * @param actionUUID
			 * @param target
			 * @param targetURI
			 * @param backLinkURI
			 */
			@Override
			public void onActionUUIDResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement, 
					String actionUUID,
					Label target,
					URI targetURI,
					URI backLinkURI) {
				ResolutionListener.this.onActionUUIDResolution(action, modelElement, actionUUID, target, targetURI, backLinkURI);
				other.onActionUUIDResolution(action, modelElement, actionUUID, target, targetURI, backLinkURI);
			}

			/**
			 * Called on resolution of semantic reference or link
			 * @param action
			 * @param targetURIStr
			 * @param target
			 * @param targetURI
			 * @param backLinkURI
			 */
			@Override
			public void onSemanticReferenceResolution(
					Action action, 
					String targetURIStr, 
					Label target, 
					URI targetURI, 
					URI backLinkURI) {
				ResolutionListener.this.onSemanticReferenceResolution(action, targetURIStr, target, targetURI, backLinkURI);
				other.onSemanticReferenceResolution(action, targetURIStr, target, targetURI, backLinkURI);
			}
			
			@Override
			public void onComputingDrawioRepresentation(Action action, String key, Document document) {
				ResolutionListener.this.onComputingDrawioRepresentation(action, key, document);
				other.onComputingDrawioRepresentation(action, key, document);
			}
			
		};
	}	

}
