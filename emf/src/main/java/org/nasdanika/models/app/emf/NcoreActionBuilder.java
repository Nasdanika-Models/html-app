package org.nasdanika.models.app.emf;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Hex;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.nasdanika.common.Context;
import org.nasdanika.common.MutableContext;
import org.nasdanika.common.NasdanikaException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.PropertyComputer;
import org.nasdanika.common.Status;
import org.nasdanika.common.Util;
import org.nasdanika.drawio.Document;
import org.nasdanika.emf.persistence.TextResourceFactory;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.Label;
import org.nasdanika.ncore.Documented;
import org.nasdanika.ncore.Marker;
import org.nasdanika.ncore.ModelElement;
import org.nasdanika.ncore.NamedElement;
import org.nasdanika.ncore.util.NcoreUtil;
import org.nasdanika.ncore.util.NcoreUtil.ContainmentPath;
import org.nasdanika.ncore.util.SemanticInfo;
import org.xml.sax.SAXException;

/**
 * Handles Ncore-specific cases - {@link NamedElement} and {@link Documented}
 * @author Pavel
 *
 * @param <T>
 */
public class NcoreActionBuilder<T extends EObject> extends EObjectActionBuilder<T> {
		
	public static final String TARGET_URI_KEY = "target-uri";
	public static final String ACTION_URI_KEY = "action-uri";
	public static final String LINK_UUID_KEY = "link-uuid";
	public static final String SEMANTIC_UUID_KEY = "semantic-uuid";
	private static final String URI_BASE_SUFFIX = "-base";
	
	public NcoreActionBuilder(T target, Context context) {
		super(target, context);		
	}
	
	@Override
	protected Action buildAction(
			Action action,
			BiConsumer<EObject,Action> registry, 
			java.util.function.Consumer<org.nasdanika.common.Consumer<org.nasdanika.models.app.emf.EObjectActionResolver.Context>> resolveConsumer, 
			ProgressMonitor progressMonitor) {
		Action ret = super.buildAction(action, registry, resolveConsumer, progressMonitor);		
		
		T semanticElement = getTarget();
		
		List<URI> uris = NcoreUtil.getIdentifiers(semanticElement);
		String id = uris.isEmpty() ? null : uris.get(0).toString();
		
		if (semanticElement instanceof ModelElement) {
			ModelElement modelElement = (ModelElement) semanticElement;
			ret.setTooltip(modelElement.getDescription()); // Escape?
			
			if (id == null) {
				id = modelElement.getUuid();
			}
		}

		if (!Util.isBlank(id)) {
			try {
				String digest = Hex.encodeHexString(MessageDigest.getInstance("SHA-256").digest(id.getBytes(StandardCharsets.UTF_8)));
				ret.setId(digest);
			} catch (NoSuchAlgorithmException e) {
	 		}
		}
		
		ContainmentPath cPath = NcoreUtil.containmentPath(semanticElement);
		if (cPath == null || Util.isBlank(cPath.path())) {
			ret.setLocation("${base-uri}index." + getHtmlExtension());
		} else {
			ret.setLocation(cPath.path() + "/index." + getHtmlExtension());
		}		
		
		if (semanticElement instanceof NamedElement) {
			ret.setText(((NamedElement) semanticElement).getName()); // Escape?
		}
		
		new SemanticInfo(semanticElement).annotate(ret);
		return ret;
	}
	
	@Override
	protected void resolve(Action action, org.nasdanika.models.app.emf.EObjectActionResolver.Context context,	ProgressMonitor progressMonitor) {
		super.resolve(action, context, progressMonitor);
		
		T semanticElement = getTarget();
		if (semanticElement instanceof Documented) {
			List<EObject> documentation = ((Documented) semanticElement).getDocumentation();
			action.getContent().addAll(EcoreUtil.copyAll(documentation)); // TODO - wrap into a group in order to inject uri's properties, optionally save into its own resource.
		}
		
		if (semanticElement instanceof ModelElement) {
			for (Entry<String, String> semanticRepresentationEntry: ((ModelElement) semanticElement).getRepresentations()) {
				String actionRepresentation = processRepresentation(semanticRepresentationEntry.getValue(), action, this::resolveSemanticElementLink, context, progressMonitor);
				if (!Util.isBlank(actionRepresentation)) {
					action.getRepresentations().put(semanticRepresentationEntry.getKey(), actionRepresentation);
				}
			}
		}
	}
	
	/**
	 * Resolves links to external semantic elements.
	 * @param uri
	 * @return
	 */
	protected EObject resolveSemanticElementLink(URI uri) {
		return null;
	}
	
	/**
	 * Processes semantic element representations in order to add them to the action.  
	 * @return Processed representation or null if the representation shall not be stored at the action.
	 */
	protected String processRepresentation(
			String representation, 
			Action action, 
			java.util.function.Function<URI, EObject> semanticLinkResolver,
			org.nasdanika.models.app.emf.EObjectActionResolver.Context context, 
			ProgressMonitor progressMonitor) {
		
		// Processing drawio data URI's
		URI representationURI = URI.createURI(representation);
		if (Document.isDataURI(representationURI)) {
			try {
				Document document = Document.load(representationURI);				
				Document processedDocument = processDrawioRepresentation(document, action, semanticLinkResolver, context, progressMonitor);
				RepresentationProcessor representationProcessor = this.context.get(RepresentationProcessor.class);
				if (representationProcessor != null) {
					processedDocument = representationProcessor.processDrawioRepresentation(processedDocument, action, semanticLinkResolver, context, progressMonitor);
				}
				if (processedDocument == null) {
					return null;
				}
				return processedDocument.toDataURI(true).toString();
			} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
				throw new NasdanikaException("Error loading drawio document: " + e, e);
			}
		}
		
		return representation;
	}
	
	protected Document processDrawioRepresentation(
			Document document, 
			Action action, 
			java.util.function.Function<URI, EObject> semanticLinkResolver,
			org.nasdanika.models.app.emf.EObjectActionResolver.Context context, 
			ProgressMonitor progressMonitor) {
		
		document.accept(element -> processDrawioElement(element, action, semanticLinkResolver, context, progressMonitor));
		
		return document;
	}

	protected void processDrawioElement(
			org.nasdanika.graph.Element element, 
			Action action, 
			java.util.function.Function<URI, EObject> semanticLinkResolver,
			org.nasdanika.models.app.emf.EObjectActionResolver.Context context, 
			ProgressMonitor progressMonitor) {
		
		ResolutionListener semanticLinkResolutionListener = this.context == null ? null : this.context.get(ResolutionListener.class);

		if (element instanceof org.nasdanika.drawio.ModelElement) {
			org.nasdanika.drawio.ModelElement modelElement = (org.nasdanika.drawio.ModelElement) element;
			String semanticUUID = modelElement.getProperty(SEMANTIC_UUID_KEY);
			if (Util.isBlank(semanticUUID)) {
				String targetUriPropertyValue = modelElement.getProperty(TARGET_URI_KEY);
				if (!Util.isBlank(targetUriPropertyValue)) {
					boolean found = false;
					for (URI tURI: resolveURIs(TARGET_URI_KEY, modelElement, NcoreUtil.getIdentifiers(action), false)) {
						EObject uriTarget = findByURI(tURI, getTarget(), semanticLinkResolver);
						if (uriTarget != null) {
							if (uriTarget != null) {
								Action uriTargetAction = context.getAction(uriTarget);
								if (uriTargetAction != null) {
									String uriTargetActionUUID = uriTargetAction.getUuid();
									if (!Util.isBlank(uriTargetActionUUID)) {
										modelElement.setProperty(LINK_UUID_KEY, uriTargetActionUUID);
									}
									if (Util.isBlank(modelElement.getTooltip())) {
										String uriTargetActionTooltip = uriTargetAction.getTooltip();
										if (!Util.isBlank(uriTargetActionTooltip)) {
											modelElement.setTooltip(uriTargetActionTooltip);
										}									
									}
									found = true;
									if (semanticLinkResolutionListener != null) {
										semanticLinkResolutionListener.onTargetURIResolution(action, modelElement, targetUriPropertyValue, uriTarget);
									}
									break;
								}
							}						
						}
					}				
					if (!found) {
						String message = "Action with URI " + targetUriPropertyValue + " not found";
						progressMonitor.worked(Status.ERROR, 1, message, modelElement);
						if (semanticLinkResolutionListener != null) {
							semanticLinkResolutionListener.onTargetURIResolution(action, modelElement, targetUriPropertyValue, null);
						}
						// TODO - some other form of reporting?
					}
				}
			} else {
				ModelElement semanticModelElement = findByUUID(semanticUUID, getTarget());
				if (semanticModelElement == null) {
					String message = "Semantic element with UUID " + semanticUUID + " not found";
					progressMonitor.worked(Status.ERROR, 1, message, modelElement);
					if (semanticLinkResolutionListener != null) {
						semanticLinkResolutionListener.onSemanticUUIDResolution(action, modelElement, semanticUUID, null, null);
					}
				} else {
					Action semanticModelElementAction = context.getAction(semanticModelElement);
					if (semanticModelElementAction == null) {
						String message = "Action for a semantic element with UUID " + semanticUUID + " not found: " + semanticModelElement;
						progressMonitor.worked(Status.ERROR, 1, message, modelElement);
						if (semanticLinkResolutionListener != null) {
							semanticLinkResolutionListener.onSemanticUUIDResolution(action, modelElement, semanticUUID, semanticModelElement, null);
						}
					} else {
						String semanticModelElementActionUUID = semanticModelElementAction.getUuid();
						if (!Util.isBlank(semanticModelElementActionUUID)) {
							modelElement.setProperty(LINK_UUID_KEY, semanticModelElementActionUUID);
						}
						if (Util.isBlank(modelElement.getTooltip())) {
							String semanticModelElementDescription = semanticModelElementAction.getDescription();
							if (!Util.isBlank(semanticModelElementDescription)) {
								modelElement.setTooltip(semanticModelElementDescription);
							}									
						}
						if (semanticLinkResolutionListener != null) {
							semanticLinkResolutionListener.onSemanticUUIDResolution(action, modelElement, semanticUUID, semanticModelElement, semanticModelElementAction);
						}
					}
				}
			}			
		}		
	}
	
	/**
	 * @return all contents iterator at the highest available level - resource set, resource, or the semantic element.
	 */
	private static TreeIterator<? extends Notifier> getAllContents(EObject semanticElement) {
		Resource resource = semanticElement.eResource();
		if (resource == null) {
			return semanticElement.eAllContents();
		}
		
		ResourceSet resourceSet = resource.getResourceSet();
		if (resourceSet == null) {
			return resource.getAllContents();
		}		
		
		return resourceSet.getAllContents();
	}
	
	private static ModelElement findByUUID(String uuid, EObject semanticElement) {
		if (Util.isBlank(uuid)) {
			return null;			
		}
		
		if (semanticElement instanceof ModelElement) {
			String semanticElementUUID = ((ModelElement) semanticElement).getUuid();
			if (uuid.equals(semanticElementUUID)) {
				return (ModelElement) semanticElement;
			}
		}
		
		TreeIterator<? extends Notifier> rscit = getAllContents(semanticElement);
		while (rscit.hasNext()) {
			Notifier next = rscit.next();
			if (next instanceof ModelElement) {
				String nextUUID = ((ModelElement) next).getUuid();
				if (uuid.equals(nextUUID)) {
					return (ModelElement) next;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Finds using semantic info annotation
	 * @param semanticUUID
	 * @param semanticElement
	 * @return
	 */
	private static ModelElement findBySemanticUUID(String semanticUUID, EObject element) {
		if (Util.isBlank(semanticUUID)) {
			return null;			
		}
		
		if (element instanceof ModelElement) {
			ModelElement semanticModelElement = (ModelElement) element;
			if (matchSemanticUUID(semanticModelElement, semanticUUID)) {
				return semanticModelElement;
			}
		}
		
		TreeIterator<? extends Notifier> rscit = getAllContents(element);
		while (rscit.hasNext()) {
			Notifier next = rscit.next();
			if (next instanceof ModelElement) {				
				ModelElement nextModelElement = (ModelElement) next;
				if (matchSemanticUUID(nextModelElement, semanticUUID)) {
					return nextModelElement;
				}
			}
		}
		
		return null;
	}
	
	private static boolean matchSemanticUUID(ModelElement modelElement, String semanticUUID) {
		if (Util.isBlank(semanticUUID)) {
			return false;			
		}
		
		URI semanticIdentifier = URI.createGenericURI("uuid", semanticUUID, null);
	
		SemanticInfo semanticInfo = SemanticInfo.getAnnotation(modelElement);
		if (semanticInfo != null) {
			for (URI identifier: semanticInfo.getIdentifiers()) {
				if (semanticIdentifier.equals(identifier)) {
					return true;
				}
			}
		}
		return false;
	}
	
		
	/**
	 * @param uriResolver returns a list of target URI's potentially resolving them relative to the semantic element URI.
	 * @param semanticElement
	 * @param semanticInfoSource 
	 * @return
	 */
	private static EObject findByURI(
			URI uri, 
			EObject semanticElement, 
			java.util.function.Function<URI,EObject> fallback) {
		
		List<URI> semanticURIs = NcoreUtil.getIdentifiers(semanticElement);
		
		Collection<URI> uris = new HashSet<>();
		if (uri.isRelative()) {
			for (URI semanticURI: semanticURIs) {
				if (semanticURI.isRelative() && semanticURI.isHierarchical()) {
					 uris.add(uri.resolve(semanticURI));
				}
			}
		} else {
			uris.add(uri);
		}
		
		for (URI semanticURI: semanticURIs) {
			for (URI u: uris) {
				if (Objects.equals(semanticURI, u)) {
					return semanticElement;
				}
			}
		}
		
		TreeIterator<? extends Notifier> rscit = getAllContents(semanticElement);
		while (rscit.hasNext()) {
			Notifier next = rscit.next();
			if (next instanceof EObject) {
				EObject nextEObject = (EObject) next;
				for (URI nextUri: NcoreUtil.getIdentifiers(nextEObject)) {
					if (nextUri != null) {
						for (URI u: uris) {
							if (nextUri.equals(u)) {
								return nextEObject;								
							}
						}
					}
				}
				if (next instanceof ModelElement) {
					String uuid = ((ModelElement) next).getUuid();
					if (!Util.isBlank(uuid)) {
						URI uuidUri = URI.createURI("uuid:" + uuid);
						for (URI u: uris) {
							if (uuidUri.equals(u)) {
								return nextEObject;
							}
						}						
					}
				}				
			}
		}
		
		if (fallback != null) {
			for (URI u: uris) {
				EObject ret = fallback.apply(u);
				if (ret != null) {
					return ret;
				}
			}
		}
						
		return null;
	}
	
	/**
	 * Constructs action identifying string for error reporting.
	 * @param action
	 * @return
	 */
	public static String actionMarker(Action action) {
		StringBuilder actionMarker = new StringBuilder("[Action] ");
		String text = action.getText();
		if (!org.nasdanika.common.Util.isBlank(text)) {
			actionMarker.append(text);
		}
		String location = action.getLocation();
		if (!org.nasdanika.common.Util.isBlank(location)) {
			if (!org.nasdanika.common.Util.isBlank(text)) {
				actionMarker.append(", ");
			}
			actionMarker.append("location=").append(location);
		}
		EList<Marker> markers = action.getMarkers();
		if (!markers.isEmpty()) {
			if (!org.nasdanika.common.Util.isBlank(text)) {
				actionMarker.append(", ");
			}
			actionMarker.append("markers=").append(markers);			
		}
		List<URI> uris = NcoreUtil.getIdentifiers(action);
		if (!uris.isEmpty()) {
			if (!org.nasdanika.common.Util.isBlank(text)) {
				actionMarker.append(", ");
			}
			actionMarker.append("uris=").append(markers);			
		}
		
		return actionMarker.toString();
	}	
	
	/**
	 * For drawio representations resolves and sets links for model elements with action-uuid or action-uri property and empty link. Returns a map of representation keys to corresponding drawio documents.
	 * @param action
	 * @param uriResolver
	 * @param progressMonitor
	 * @return
	 */
	public static Map<String, Object> resolveRepresentationLinks(
			Action action, 
			Iterable<Map.Entry<SemanticInfo,?>> semanticInfoSource,
			BiFunction<Label, URI, URI> uriResolver,
			ResolutionListener resolutionListener,			
			ProgressMonitor progressMonitor) {

		Map<String, Object> ret = new LinkedHashMap<>();
		
		for (Entry<String, String> actionRepresentationEntry: action.getRepresentations()) {
			URI representationURI = URI.createURI(actionRepresentationEntry.getValue());
			if (Document.isDataURI(representationURI)) {
				try {
					Document document = Document.load(representationURI);
					document.accept(element -> resolveLink(
							element, 
							action, 
							semanticInfoSource,
							uriResolver, 
							resolutionListener,  
							progressMonitor));
					ret.put(actionRepresentationEntry.getKey(), document);
				} catch (ParserConfigurationException | SAXException | IOException e) {
					throw new NasdanikaException("Error loading drawio document: " + e, e);
				}
			} else if (TextResourceFactory.isDataURI(representationURI)) {
				Resource textResource = new TextResourceFactory().createResource(representationURI);
				try {
					textResource.load(null);
				} catch (IOException e) {
					throw new NasdanikaException("Error loading text resource from data URI: " + e, e);
				}
				StringBuilder textBuilder = new StringBuilder();
				for (EObject eObj: textResource.getContents()) {
					if (eObj instanceof org.nasdanika.ncore.String) {
						String text = ((org.nasdanika.ncore.String) eObj).getValue();
						if (text != null) {
							textBuilder.append(text);
						}
					} else {
						throw new UnsupportedOperationException("Unsupported contents: " + eObj);
					}
				}
				
				MutableContext context = Context.EMPTY_CONTEXT.fork();
				
				PropertyComputer actionUUIDPropertyComputer = new PropertyComputer() {
					
					@SuppressWarnings("unchecked")
					@Override
					public <T> T compute(Context propertyComputerContext, String key, String path, Class<T> type) {
						if (type == null || type.isAssignableFrom(String.class)) {
							URI ret = resolveActionUUIDLink(
									path,
									action,
									semanticInfoSource,
									uriResolver,
									resolutionListener,  
									progressMonitor);
							return ret == null ? null : (T) ret.toString();
						}
						return null;
					}
				};
				
				context.put(LINK_UUID_KEY, actionUUIDPropertyComputer);
				
				PropertyComputer actionURIPropertyComputer = new PropertyComputer() {
					
					@SuppressWarnings("unchecked")
					@Override
					public <T> T compute(Context propertyComputerContext, String key, String path, Class<T> type) {
						if (type == null || type.isAssignableFrom(String.class)) {
							URI ret = resolveActionURILink(
									path, 
									action, 
									semanticInfoSource,
									uriResolver, 
									resolutionListener,  
									progressMonitor);
							return ret == null ? null : (T) ret.toString();
						}
						return null;
					}
				};
				
				context.put(ACTION_URI_KEY, actionURIPropertyComputer);								
				
				ret.put(actionRepresentationEntry.getKey(), context.computingContext().interpolateToString(textBuilder.toString()));
			} else {
				// Simple carry-over
				ret.put(actionRepresentationEntry.getKey(), actionRepresentationEntry.getValue());				
			}
		}
		return ret;
	}
	
	private static URI resolveActionUUIDLink(
			String actionUUID,
			Action action, 
			Iterable<Map.Entry<SemanticInfo,?>> semanticInfoSource,
			BiFunction<Label, URI, URI> uriResolver,
			ResolutionListener resolutionListener,
			ProgressMonitor progressMonitor) {

		if (!Util.isBlank(actionUUID)) {
			ModelElement targetAction = findByUUID(actionUUID, action);
			if (targetAction instanceof Label) {
				URI actionURI = uriResolver.apply(action, (URI) null);
				URI targetURI = uriResolver.apply((Label) targetAction, actionURI);
				if (targetURI != null) {
					return targetURI;
				}
			} else {
				return resolveActionURILink(
						"uuid:" + actionUUID,
						action,
						semanticInfoSource,
						uriResolver,
						resolutionListener,
						progressMonitor);
			}
		}
		
		return null;
	}
	
	public static EObject find(URI identifier, Iterable<Map.Entry<SemanticInfo,?>> semanticInfoSource) {
		if (semanticInfoSource != null) {
			for (Entry<SemanticInfo, ?> sie: semanticInfoSource) {
				if (sie != null) {
					SemanticInfo si = sie.getKey();
					Object value = sie.getValue();
					if (si != null && value instanceof EObject && si.getIdentifiers().contains(identifier)) {
						return (EObject) value;
					}
				}
			}
		}
		return null;
	}
		
	private static URI resolveActionURILink(
			String aURI,
			Action action, 
			Iterable<Map.Entry<SemanticInfo,?>> semanticInfoSource,
			BiFunction<Label, URI, URI> uriResolver,
			ResolutionListener resolutionListener,
			ProgressMonitor progressMonitor) {

		if (!Util.isBlank(aURI)) {
			EObject target = findByURI(URI.createURI(aURI), action, identifier -> find(identifier,semanticInfoSource));
			if (target instanceof Label) {
				URI actionURI = uriResolver.apply(action, (URI) null);
				URI targetURI = uriResolver.apply((Label) target, actionURI);
				if (resolutionListener != null) {
					URI rawTargetURI = uriResolver.apply((Label) target, null);
					URI backLinkURI = uriResolver.apply(action, rawTargetURI);
					resolutionListener.onActionURIResolution(action, null, aURI, (Label) target, targetURI, backLinkURI);
				}
				if (targetURI != null) {
					return targetURI;
				}
			} else {				
				String message = "Unresolved action URI: " + aURI;
				progressMonitor.worked(Status.ERROR, 1, message, action);
				if (resolutionListener != null) {
					resolutionListener.onActionURIResolution(action, null, aURI, null, null, null);
				}
			}
		}
		
		return null;
	}

	private static void resolveLink(
			org.nasdanika.graph.Element element, 
			Action action, 
			Iterable<Map.Entry<SemanticInfo,?>> semanticInfoSource,
			BiFunction<Label, URI, URI> uriResolver,
			ResolutionListener resolutionListener,
			ProgressMonitor progressMonitor) {
		if (element instanceof org.nasdanika.drawio.ModelElement) {
			org.nasdanika.drawio.ModelElement modelElement = (org.nasdanika.drawio.ModelElement) element;
			String actionUUID = modelElement.getProperty(LINK_UUID_KEY);			
			if (Util.isBlank(actionUUID)) {
				// For semantic mapping to actions 
				String semanticUUID =  modelElement.getProperty(SEMANTIC_UUID_KEY);
				if (!Util.isBlank(semanticUUID)) {
					ModelElement targetModelElement = findBySemanticUUID(semanticUUID, action);
					if (targetModelElement instanceof Label) {
						URI actionURI = uriResolver.apply(action, (URI) null);
						URI targetURI = uriResolver.apply((Label) targetModelElement, actionURI);
						if (resolutionListener != null) {
							resolutionListener.onSemanticUUIDResolution(
									action, 
									modelElement, 
									semanticUUID, 
									null,
									(Label) targetModelElement);
						}
						
						if (targetURI != null) {
							modelElement.setLink(targetURI.toString());
							if (Util.isBlank(modelElement.getTooltip())) {
								String actionTooltip = ((Label) targetModelElement).getTooltip();
								if (!Util.isBlank(actionTooltip)) {
									modelElement.setTooltip(actionTooltip);
								}
							}
						}
					} else {
						String message = "Action with semantic UUID " + semanticUUID + " not found";
						progressMonitor.worked(Status.ERROR, 1, message, modelElement);
						if (resolutionListener != null) {
							resolutionListener.onSemanticUUIDResolution(
									action, 
									modelElement, 
									semanticUUID, 
									null, 
									null);
						}
					}
				}
			} else {
				ModelElement targetModelElement = findByUUID(actionUUID, action);
				if (targetModelElement instanceof Label) {
					URI actionURI = uriResolver.apply(action, (URI) null);
					URI targetURI = uriResolver.apply((Label) targetModelElement, actionURI);
					if (resolutionListener != null) {
						URI rawTargetURI = uriResolver.apply((Label) targetModelElement, null);
						URI backLinkURI = uriResolver.apply(action, rawTargetURI);
						resolutionListener.onActionUUIDResolution(action, modelElement, actionUUID, (Label) targetModelElement, targetURI, backLinkURI);
					}
					
					if (targetURI != null) {
						modelElement.setLink(targetURI.toString());
						if (Util.isBlank(modelElement.getTooltip())) {
							String actionTooltip = ((Label) targetModelElement).getTooltip();
							if (!Util.isBlank(actionTooltip)) {
								modelElement.setTooltip(actionTooltip);
							}
						}
					}
				} else {
					String message = "Action with UUID " + actionUUID + " not found";
					progressMonitor.worked(Status.ERROR, 1, message, modelElement);
					if (resolutionListener != null) {
						resolutionListener.onActionUUIDResolution(action, modelElement, actionUUID, null, null, null);
					}
				}
			}

			String actionUriPropertyValue = modelElement.getProperty(ACTION_URI_KEY);
			if (!Util.isBlank(actionUriPropertyValue)) {
				boolean found = false;
				for (URI aURI: resolveURIs(ACTION_URI_KEY, modelElement, NcoreUtil.getIdentifiers(action), false)) {
					if (aURI != null) {
						EObject target = findByURI(aURI, action, identifier -> find(identifier,semanticInfoSource));
						if (target instanceof Label) {
							URI actionURI = uriResolver.apply(action, (URI) null);
							URI targetURI = uriResolver.apply((Label) target, actionURI);
							if (resolutionListener != null) {
								URI rawTargetURI = uriResolver.apply((Label) target, null);
								URI backLinkURI = uriResolver.apply(action, rawTargetURI);
								resolutionListener.onActionURIResolution(action, modelElement, actionUriPropertyValue, (Label) target, targetURI, backLinkURI);
							}
							if (targetURI != null) {
								modelElement.setLink(targetURI.toString());
								if (Util.isBlank(modelElement.getTooltip())) {
									String actionTooltip = ((Label) target).getTooltip();
									if (!Util.isBlank(actionTooltip)) {
										modelElement.setTooltip(actionTooltip);
									}
								}
							}
							found = true;
							break;
						}
					}
				}		
				if (!found) {
					String message = "Action with URI " + actionUriPropertyValue + " not found";
					progressMonitor.worked(Status.ERROR, 1, message, modelElement);
					if (resolutionListener != null) {
						resolutionListener.onActionURIResolution(action, modelElement, actionUriPropertyValue, null, null, null);
					}
				}
			}
		}		
	}
	
	/**
	 * Resolves model element URI specified in the property name relative to parents which have the same property and ultimately relative to the bases - uri's of the representation's containing semantic element.
	 * @param propertyName
	 * @param modelElement
	 * @param bases
	 * @return
	 */
	public static Collection<URI> resolveURIs(String propertyName, org.nasdanika.drawio.ModelElement modelElement, Collection<URI> bases, boolean asBase) {
		String aURI = modelElement.getProperty(propertyName);		
		if (Util.isBlank(aURI)) {
			if (asBase) {
				aURI = modelElement.getProperty(propertyName + URI_BASE_SUFFIX);						
			}
			if (Util.isBlank(aURI)) {
				return Collections.emptySet();
			}
		}
		URI actionURI = URI.createURI(aURI);
		Collection<URI> ret = new HashSet<>();
		ret.add(actionURI);
		if (actionURI.isRelative()) {
			for (URI actionBaseURI: resolveBaseURIs(propertyName, modelElement.getParent(), bases)) {			
				if (actionBaseURI != null && !actionBaseURI.isRelative() && actionBaseURI.isHierarchical()) {
					String cLastSegment = actionBaseURI.lastSegment();
					if (cLastSegment == null || cLastSegment.length() > 0) {
						actionBaseURI = actionBaseURI.appendSegment("");
					}
					
					ret.add(actionURI.resolve(actionBaseURI));
				}
			}
		}
		return ret;
	}
	
	private static Collection<URI> resolveBaseURIs(String propertyName, org.nasdanika.drawio.ModelElement modelElement, Collection<URI> bases) {
		if (modelElement == null) {
			return bases;
		}
		String aURI = modelElement.getProperty(propertyName);
		if (Util.isBlank(aURI)) {
			aURI = modelElement.getProperty(propertyName + URI_BASE_SUFFIX);						
		}
		return Util.isBlank(aURI) ? resolveBaseURIs(propertyName, modelElement.getParent(), bases) : resolveURIs(propertyName, modelElement, bases, true);
	}
	
}
