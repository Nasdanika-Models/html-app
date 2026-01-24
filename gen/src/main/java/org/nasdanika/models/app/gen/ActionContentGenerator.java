package org.nasdanika.models.app.gen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.nasdanika.common.Context;
import org.nasdanika.common.DefaultConverter;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.MutableContext;
import org.nasdanika.common.NasdanikaException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.PropertyComputer;
import org.nasdanika.common.Status;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.drawio.Connection;
import org.nasdanika.drawio.Layer;
import org.nasdanika.drawio.LayerElement;
import org.nasdanika.drawio.Model;
import org.nasdanika.drawio.Node;
import org.nasdanika.drawio.Page;
import org.nasdanika.drawio.Rectangle;
import org.nasdanika.drawio.Root;
import org.nasdanika.drawio.comparators.LabelModelElementComparator;
import org.nasdanika.emf.EObjectAdaptable;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.graph.Element;
import org.nasdanika.html.HTMLFactory;
import org.nasdanika.html.Tag;
import org.nasdanika.html.TagName;
import org.nasdanika.html.jstree.JsTreeFactory;
import org.nasdanika.html.jstree.JsTreeNode;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.Link;
import org.nasdanika.models.app.emf.NcoreActionBuilder;
import org.nasdanika.models.app.emf.ResolutionListener;
import org.nasdanika.models.html.gen.ContentConsumer;
import org.nasdanika.ncore.util.ContainerInfo;
import org.nasdanika.ncore.util.NcoreUtil;
import org.nasdanika.ncore.util.SemanticIdentity;
import org.nasdanika.ncore.util.SemanticInfo;
import org.nasdanika.ncore.util.SemanticMap;
import org.nasdanika.persistence.Marker;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class ActionContentGenerator {

	public static final String SEMANTIC_REF_KEY = "semantic-ref";
	private Action action;
	private String extension;
	private BiFunction<Label, URI, URI> uriResolver;
	private ResolutionListener resolutionListener;
	private Iterable<Entry<SemanticInfo, ?>> semanticInfoSource;
	private Context context;
	
	public ActionContentGenerator(
			Action action, 
			String extension,
			BiFunction<Label, URI, URI> uriResolver,
			Iterable<Map.Entry<SemanticInfo, ?>> semanticInfoSource, 
			ResolutionListener resolutionListener,
			Context context) {
		
		this.action = action;
		this.extension = extension;
		this.uriResolver = uriResolver;
		this.semanticInfoSource = semanticInfoSource;
		this.resolutionListener = resolutionListener;
		this.context = context;
	}

	/**
	 * {@link ActionContentProvider} method
	 * 
	 * @param action
	 * @param uriResolver
	 * @param progressMonitor
	 * @return
	 */
	public EList<EObject> getActionContent(
			File resourceWorkDir, 
			java.util.function.Consumer<Diagnostic> diagnosticConsumer, 
			ProgressMonitor progressMonitor) {

		List<Object> contentContributions = new ArrayList<>();
		Context actionContentContext = createActionContentContext((ContentConsumer) contentContributions::add, progressMonitor);

		File contentDir = new File(resourceWorkDir, "content");
		contentDir.mkdirs();

		String fileName = action.getUuid() + extension;
		SupplierFactory<InputStream> contentFactory = org.nasdanika.common.Util.asInputStreamSupplierFactory(action.getContent());
		try (InputStream contentStream = contentFactory.create(actionContentContext).call(progressMonitor, diagnosticConsumer, Status.FAIL, Status.ERROR)) {
			if (contentContributions.isEmpty()) {
				Files.copy(contentStream, new File(contentDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				Stream<InputStream> pageBodyContributionsStream = contentContributions.stream().filter(Objects::nonNull)
						.map(e -> {
							try {
								return DefaultConverter.INSTANCE.toInputStream(e.toString());
							} catch (IOException ex) {
								throw new NasdanikaException("Error converting element to InputStream: " + ex, ex);
							}
						});
				Stream<InputStream> concatenatedStream = Stream.concat(pageBodyContributionsStream,	Stream.of(contentStream));
				Files.copy(org.nasdanika.common.Util.join(concatenatedStream), new File(contentDir, fileName).toPath(),	StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (NasdanikaException e) {
			throw e;
		} catch (Exception e) {
			throw new NasdanikaException(e);
		}

		org.nasdanika.exec.content.Resource contentResource = ContentFactory.eINSTANCE.createResource();
		contentResource.setLocation("../content/" + fileName);
		progressMonitor.worked(1, "[Action content] " + action.getText() + " -> " + fileName);
		return ECollections.singletonEList(contentResource);
	}
	
	/**
	 * @param action
	 * @return
	 */
	protected SemanticInfo getSemanticInfoAnnotation(Action action) {
		return SemanticInfo.getAnnotation(action);
	}	
	
	/**
	 * Computes semantic links for external URI's. This implementation returns null;
	 * @param targetURI
	 * @param baseURI   Base URI to deresolve link's reference to make it relative.
	 * @param context
	 * @return
	 */
	protected String computeSemanticLink(
			URI targetURI, 
			URI baseURI, 
			Context context,
			ResolutionListener resolutionListener) {

		return null;
	}	
	
	/**
	 * Computes semantic link. This implementation uses the registry and
	 * uriResolver. Override to add support of resolving external semantic links,
	 * e.g. loaded from semantic maps of dependency sites.
	 * 
	 * @param context
	 * @param key
	 * @param path
	 * @param action
	 * @param baseSemanticURI
	 * @param uriResolver
	 * @param registry
	 * @return
	 */
	protected String computeSemanticLink(
			Context context, 
			String path, 
			Collection<URI> baseSemanticURIs,
			ProgressMonitor progressMonitor) {

		int spaceIdx = path.indexOf(' ');
		String targetUriStr = spaceIdx == -1 ? path : path.substring(0, spaceIdx);

		if (baseSemanticURIs == null || baseSemanticURIs.isEmpty()) {
			baseSemanticURIs = Collections.singleton(null);
		}
		for (URI baseSemanticURI : baseSemanticURIs) {
			URI targetURI = URI.createURI(targetUriStr);
			if (baseSemanticURI != null && targetURI.isRelative()) {
				targetURI = targetURI.resolve(baseSemanticURI.appendSegment(""));
			}
			URI bURI = uriResolver.apply(action, (URI) null);

			if (semanticInfoSource != null) {
				for (Entry<SemanticInfo, ?> entry : semanticInfoSource) {
					if (entry != null) {
						Object value = entry.getValue();
						if (value instanceof Label) {
							Label targetLabel = (Label) value;
							SemanticInfo semanticElementAnnotation = entry.getKey();
							if (semanticElementAnnotation != null) {
								for (URI semanticURI : semanticElementAnnotation.getIdentifiers()) {
									if (Objects.equals(targetURI, semanticURI)) {
										Label tLabel = Util.createLabel(targetLabel, action, uriResolver, null, null,
												false, false, false);
										SupplierFactory<Tag> tagSupplierFactory = EObjectAdaptable
												.adaptToSupplierFactory(tLabel, Tag.class, new AppAdapterFactory());
										Tag tag;
										if (tagSupplierFactory == null) {
											HTMLFactory htmlFactory = context.get(HTMLFactory.class,
													HTMLFactory.INSTANCE);
											URI targetActionURI = uriResolver.apply(targetLabel, bURI);
											tag = htmlFactory.tag(targetActionURI == null ? TagName.span : TagName.a,
													spaceIdx == -1 ? targetLabel.getText()
															: path.substring(spaceIdx + 1));
											String targetLabelTooltip = targetLabel.getTooltip();
											if (!org.nasdanika.common.Util.isBlank(targetLabelTooltip)) {
												tag.attribute("title", targetLabelTooltip);
											}
											if (targetActionURI != null) {
												tag.attribute("href", targetActionURI.toString());
											}
										} else {
											tag = tagSupplierFactory.create(context).execute(progressMonitor);
										}
										if (resolutionListener != null) {
											URI rawTargetURI = uriResolver.apply(targetLabel, null);
											URI backLinkURI = uriResolver.apply(action, rawTargetURI);
											resolutionListener.onSemanticReferenceResolution(action, path, targetLabel,
													targetURI, backLinkURI);
										}
										return tag.toString();
									}
								}
							}
						}
					}
				}
			}

			String semanticLink = computeSemanticLink(targetURI, bURI, context, resolutionListener);
			if (semanticLink != null) {
				return semanticLink;
			}
		}

		String message = "Unresolved semantic URI: " + targetUriStr;
		progressMonitor.worked(Status.ERROR, 1, message, action);
		if (resolutionListener != null) {
			resolutionListener.onSemanticReferenceResolution(action, targetUriStr, null, null, null);
		}

		return null;
	}
		
	/**
	 * Registers semantic-link and semantic-ref property computers
	 * 
	 * @param action
	 * @param uriResolver
	 * @param registry
	 * @param mctx
	 */
	protected Context createActionContentContext(ContentConsumer contentConsumer, ProgressMonitor progressMonitor) {

		MutableContext mctx = context.fork();
		mctx.put("nsd-site-map-tree-script", (Function<Context, String>) ctx -> computeSiteMapTreeScript(ctx, progressMonitor));

		if (contentConsumer != null) {
			mctx.register(ContentConsumer.class, contentConsumer);
		}

		SemanticInfo semanticInfo = getSemanticInfoAnnotation(action);
		Collection<URI> baseSemanticURIs = semanticInfo == null ? Collections.emptyList() : semanticInfo.getIdentifiers().stream().filter(u -> !u.isRelative() && u.isHierarchical()).toList();

		Map<String, Object> representations = NcoreActionBuilder.resolveRepresentationLinks(
				action, 
				semanticInfoSource,
				uriResolver, 
				resolutionListener, 
				progressMonitor);

		PropertyComputer representationsPropertyComputer = new PropertyComputer() {

			@SuppressWarnings("unchecked")
			@Override
			public <T> T compute(Context ctx, String key, String path, Class<T> type) {
				if (type == null || type.isAssignableFrom(String.class)) {
					return (T) computeRepresentation(
							representations, 
							ctx, 
							key, 
							path, 
							baseSemanticURIs,
							progressMonitor);
				}
				return null;
			}
		};

		mctx.put("representations", representationsPropertyComputer);

		PropertyComputer semanticLinkPropertyComputer = new PropertyComputer() {

			@SuppressWarnings("unchecked")
			@Override
			public <T> T compute(Context ctx, String key, String path, Class<T> type) {
				if (type == null || type.isAssignableFrom(String.class)) {
					return (T) computeSemanticLink(
							ctx, 
							path, 
							baseSemanticURIs, 
							progressMonitor);
				}
				return null;
			}
		};

		mctx.put("semantic-link", semanticLinkPropertyComputer);

		PropertyComputer semanticReferencePropertyComputer = new PropertyComputer() {

			@SuppressWarnings("unchecked")
			@Override
			public <T> T compute(Context propertyComputerContext, String key, String path, Class<T> type) {
				if (type == null || type.isAssignableFrom(String.class)) {
					URI sRef = computeSemanticReferfence(
							propertyComputerContext, 
							path, 
							baseSemanticURIs,
							progressMonitor);
					return sRef == null ? null : (T) sRef.toString();
				}
				return null;
			}
		};

		mctx.put(SEMANTIC_REF_KEY, semanticReferencePropertyComputer);

		return mctx;
	}
	

	/**
	 * @param context
	 * @param key
	 * @param path
	 * @param action
	 * @param baseSemanticURI
	 * @param uriResolver
	 * @param registry
	 * @return
	 */
	protected Object computeRepresentation(
			Map<String, Object> representations, 
			Context context, 
			String key,
			String path, 
			Collection<URI> baseSemanticURIs, 
			ProgressMonitor progressMonitor) {

		String[] pathSegments = path.split("/");
		if (pathSegments.length == 1) {
			// Non drawio representation, just a key
			return representations.get(path);
		}
		try {
			org.nasdanika.drawio.Document valueDocument = (org.nasdanika.drawio.Document) representations.get(pathSegments[0]);
			if (resolutionListener != null) {
				resolutionListener.onComputingDrawioRepresentation(action, pathSegments[0], valueDocument);
			}
			if (valueDocument == null) {
				valueDocument = org.nasdanika.drawio.Document.create(false, null);
				Page page = valueDocument.createPage();
				page.setName("Error");

				Model model = page.getModel();
				Root root = model.getRoot();
				List<Layer<?>> layers = root.getLayers();

				Node errorNode = layers.get(0).createNode();
				errorNode.setLabel("Representation not found: " + pathSegments[0]);
				errorNode.getStyle().put("fillColor", "#f8cecc");
				errorNode.getStyle().put("strokeColor", "#b85450");

				Rectangle errorGeometry = errorNode.getGeometry();
				errorGeometry.setX(10);
				errorGeometry.setY(10);
				errorGeometry.setWidth(300);
				errorGeometry.setHeight(30);
			} else {
				// Injecting links for elements with semantic-ref property
				valueDocument.accept(element -> processDrawioElement(
						element, 
						context, 
						key, 
						path, 
						baseSemanticURIs, 
						progressMonitor));
			}

			if (pathSegments.length == 2) {
				// Document
				if ("diagram".equals(pathSegments[1])) {
					return valueDocument.save(true);
				}

				if ("toc".equals(pathSegments[1])) {
					return String.valueOf(computeTableOfContents(valueDocument, context));
				}

				if ("info".equals(pathSegments[1])) {
					return computeInfo(action, valueDocument, context);
				}

				return null;
			}

			if (pathSegments.length == 3) {
				// Page
				for (Page page : valueDocument.getPages()) {
					if (pathSegments[1].equals(page.getName())) {
						org.nasdanika.drawio.Document pageDocument = org.nasdanika.drawio.Document.create(true,	valueDocument.getURI());
						pageDocument.getPages().add(page);
						if ("diagram".equals(pathSegments[2])) {
							return pageDocument.save(true);
						}

						if ("toc".equals(pathSegments[2])) {
							return String.valueOf(computeTableOfContents(pageDocument, context));
						}

						if ("info".equals(pathSegments[2])) {
							return computeInfo(action, pageDocument, context);
						}
					}
				}
			}
		} catch (TransformerException | IOException | ParserConfigurationException e) {
			throw new NasdanikaException(e);
		}

		return null;
	}

	/**
	 * Computes a info YAML dump for a Drawio element
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected String computeInfo(Action action, org.nasdanika.drawio.Element<?> element, Context context) {
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
		dumperOptions.setIndent(4);
		Map<String, Object> infoMap = new LinkedHashMap<>();
		Map<String, Object> actionMap = new LinkedHashMap<>();
		if (!org.nasdanika.common.Util.isBlank(action.getText())) {
			actionMap.put("text", action.getText());
		}
		List<URI> actionURIs = NcoreUtil.getIdentifiers(action);
		if (actionURIs.size() == 1) {
			actionMap.put("uri", actionURIs.get(0).toString());
		} else if (!actionURIs.isEmpty()) {
			actionMap.put("uris", actionURIs.stream().map(Object::toString).toList());
		}
		EList<org.nasdanika.ncore.Marker> actionMarkers = action.getMarkers();
		if (actionMarkers != null && !actionMarkers.isEmpty()) {
			actionMap.put("markers",
					actionMarkers.stream().map(am -> DefaultConverter.INSTANCE.toMap(am, null)).toArray());
		}
		infoMap.put("action", actionMap);
		Map<String, Object> elementInfo = element
				.accept((el, childInfo) -> this.elementInfo(el, childInfo, actionURIs));
		if (elementInfo != null) {
			infoMap.put("element", elementInfo);
		}
		StringWriter out = new StringWriter();
		try (out) {
			new Yaml(dumperOptions).dump(infoMap, out);
		} catch (Exception e) {
			return "Error computing representation info: " + e;
		}
		return out.toString();
	}

	private Map<String, Object> elementInfo(Element element, Map<? extends Element, Map<String, Object>> childInfo,
			List<URI> actionURIs) {
		Map<String, Object> info = new LinkedHashMap<>();
		info.put("type", element.getClass().getName());
		if (element instanceof org.nasdanika.drawio.Element) {
			org.nasdanika.drawio.Element<?> drawioElement = (org.nasdanika.drawio.Element<?>) element;
			URI uri = drawioElement.getURI();
			if (uri != null) {
				info.put("uri", uri.toString());
			}
			Collection<? extends Marker> markers = drawioElement.getMarkers();
			if (markers != null && !markers.isEmpty()) {
				info.put("markers",
						markers.stream()
								.map(em -> em instanceof EObject ? DefaultConverter.INSTANCE.toMap((EObject) em, null)
										: String.valueOf(em))
								.toArray());
			}
		}
		if (element instanceof org.nasdanika.drawio.Page) {
			org.nasdanika.drawio.Page page = (org.nasdanika.drawio.Page) element;
			String pageName = page.getName();
			if (!org.nasdanika.common.Util.isBlank(pageName)) {
				info.put("name", pageName);
			}
			String id = page.getId();
			if (!org.nasdanika.common.Util.isBlank(id)) {
				info.put("id", id);
			}
		}
		if (element instanceof org.nasdanika.drawio.ModelElement) {
			org.nasdanika.drawio.ModelElement<?> modelElement = (org.nasdanika.drawio.ModelElement<?>) element;
			String id = modelElement.getId();
			if (!org.nasdanika.common.Util.isBlank(id)) {
				info.put("id", id);
			}

			String label = modelElement.getLabel();
			if (!org.nasdanika.common.Util.isBlank(label)) {
				info.put("label", label);
			}

			String tooltip = modelElement.getTooltip();
			if (!org.nasdanika.common.Util.isBlank(tooltip)) {
				info.put("tooltip", tooltip);
			}

			if (modelElement.isTargetLink()) {
				info.put("linked-page", modelElement.getLinkTarget().toString());
			} else {
				String link = modelElement.getLink();
				if (!org.nasdanika.common.Util.isBlank(link)) {
					info.put("link", link);
				}
			}
			Map<String, String> style = modelElement.getStyle();
			if (style != null && !style.isEmpty()) {
				info.put("style", style);
			}

			Set<org.nasdanika.drawio.Tag> tags = modelElement.getTags();
			if (tags != null && !tags.isEmpty()) {
				info.put("tags", tags);
			}

			info.put("visible", modelElement.isVisible());

			// Properties
			NamedNodeMap attributes = modelElement.getElement().getAttributes();
			if (attributes != null) {
				Map<String, String> properties = new LinkedHashMap<>();
				for (int i = 0; i < attributes.getLength(); ++i) {
					org.w3c.dom.Node attribute = attributes.item(i);
					if (attribute instanceof Attr) {
						String propertyName = ((Attr) attribute).getName();
						String propertyValue = modelElement.getProperty(propertyName);
						if (!org.nasdanika.common.Util.isBlank(propertyValue)) {
							properties.put(propertyName, propertyValue);
						}
					}
				}
				if (!properties.isEmpty()) {
					info.put("properties", properties);
				}
			}

			Collection<URI> elementActionURIs = NcoreActionBuilder.resolveURIs(NcoreActionBuilder.ACTION_URI_KEY,
					modelElement, actionURIs, false);
			if (elementActionURIs != null && !elementActionURIs.isEmpty()) {
				info.put("action-uris", elementActionURIs.stream().map(Object::toString).toArray());
			}

			Collection<URI> elementActionUriBases = NcoreActionBuilder.resolveURIs(NcoreActionBuilder.ACTION_URI_KEY,
					modelElement, actionURIs, true);
			if (elementActionUriBases != null && !elementActionUriBases.isEmpty()) {
				info.put("action-uri-bases", elementActionUriBases.stream().map(Object::toString).toArray());
			}

			Collection<URI> elementTargetURIs = NcoreActionBuilder.resolveURIs(NcoreActionBuilder.TARGET_URI_KEY,
					modelElement, actionURIs, false);
			if (elementTargetURIs != null && !elementTargetURIs.isEmpty()) {
				info.put("target-uris", elementTargetURIs.stream().map(Object::toString).toArray());
			}

			Collection<URI> elementTargetUriBases = NcoreActionBuilder.resolveURIs(NcoreActionBuilder.TARGET_URI_KEY,
					modelElement, actionURIs, true);
			if (elementTargetUriBases != null && !elementTargetUriBases.isEmpty()) {
				info.put("target-uri-bases", elementTargetUriBases.stream().map(Object::toString).toArray());
			}

		}
		if (element instanceof Connection) {
			Node source = ((Connection) element).getSource();
			if (source != null) {
				info.put("source", source.toString());
			}
			Node target = ((Connection) element).getTarget();
			if (target != null) {
				info.put("target", target.toString());
			}
		}
		if (element instanceof Node) {
			List<Connection> incomingConnections = ((Node) element).getAllIncomingConnections();
			if (incomingConnections != null && !incomingConnections.isEmpty()) {
				info.put("incoming-connections", incomingConnections.stream().map(Object::toString).toArray());
			}
			List<Connection> outgoingConnections = ((Node) element).getAllOutgoingConnections();
			if (outgoingConnections != null && !outgoingConnections.isEmpty()) {
				info.put("outgoing-connections", outgoingConnections.stream().map(Object::toString).toArray());
			}
		}

		if (childInfo != null && !childInfo.isEmpty()) {
			info.put("children", childInfo.values().stream().toArray());
		}
		return info;
	}

	
	/**
	 * Computes a table of contents for a Drawio element
	 * 
	 * @param element
	 * @param context
	 * @return
	 */
	protected Object computeTableOfContents(org.nasdanika.drawio.Element<?> element, Context context) {
		HTMLFactory htmlFactory = context.get(HTMLFactory.class, HTMLFactory.INSTANCE);
		if (element instanceof org.nasdanika.drawio.Document) {
			List<Page> pages = ((org.nasdanika.drawio.Document) element).getPages();
			if (pages.size() == 1) {
				return computeTableOfContents(pages.get(0), context);
			}
			Tag ol = htmlFactory.tag(TagName.ol);
			for (Page page : pages) {
				Tag li = htmlFactory.tag(TagName.li, page.getName(), computeTableOfContents(page, context));
				ol.content(li);
			}
			return ol;
		}

		if (element instanceof Page) {
			List<Layer<?>> layers = new ArrayList<>(((Page) element).getModel().getRoot().getLayers());
			if (layers.size() == 1) {
				return computeTableOfContents(layers.get(0), context);
			}
			Collections.reverse(layers);
			Tag ol = htmlFactory.tag(TagName.ol);
			for (Layer<?> layer : layers) {
				if (org.nasdanika.common.Util.isBlank(layer.getLabel())) {
					ol.content(computeTableOfContents(layer, context));
				} else {
					Tag li = htmlFactory.tag(TagName.li,
							org.nasdanika.common.Util.isBlank(layer.getLink()) || layer.getLinkTarget() instanceof Page
									? layer.getLabel()
									: htmlFactory.tag(TagName.a, layer.getLabel()).attribute("href", layer.getLink()),
							org.nasdanika.common.Util.isBlank(layer.getTooltip()) ? ""
									: " - " + Jsoup.parse(layer.getTooltip()).text(),
							computeTableOfContents(layer, context));
					ol.content(li);
				}
			}
			return ol;
		}

		if (element instanceof Layer) { // Including nodes
			List<LayerElement<?>> layerElements = new ArrayList<>(((Layer<?>) element).getElements());
			Collections.sort(layerElements, new LabelModelElementComparator()); // TODO - use "sort" property
			if (element instanceof org.nasdanika.drawio.Node) {
				List<LayerElement<?>> outgoingConnections = new ArrayList<>(
						((org.nasdanika.drawio.Node) element).getAllOutgoingConnections());
				Collections.sort(outgoingConnections, new LabelModelElementComparator());
				layerElements.addAll(outgoingConnections);
			}

			Tag ol = htmlFactory.tag(TagName.ol);
			for (LayerElement<?> layerElement : layerElements) {
				if (layerElement instanceof org.nasdanika.drawio.Node
						|| (layerElement instanceof Connection && (((Connection) layerElement).getSource() == null
								|| ((Connection) layerElement).getSource() == element))) {
					if (org.nasdanika.common.Util.isBlank(layerElement.getLabel())) {
						ol.content(computeTableOfContents(layerElement, context));
					} else {
						Tag li = htmlFactory.tag(TagName.li, org.nasdanika.common.Util.isBlank(layerElement.getLink())
								|| layerElement.getLinkTarget() instanceof Page
										? Jsoup.parse(layerElement.getLabel()).text()
										: htmlFactory.tag(TagName.a, Jsoup.parse(layerElement.getLabel()).text())
												.attribute("href", layerElement.getLink()),
								org.nasdanika.common.Util.isBlank(layerElement.getTooltip()) ? ""
										: " - " + Jsoup.parse(layerElement.getTooltip()).text(),
								computeTableOfContents(layerElement, context));
						ol.content(li);
					}
				}
			}
			return ol;
		}

		return null;
	}

	public static record SemanticInfoRecord(URI location, String description) {
	}	

	protected void processDrawioElement(
			org.nasdanika.graph.Element element, 
			Context context, 
			String key, 
			String path,
			Collection<URI> baseSemanticURIs, 
			ProgressMonitor progressMonitor) {

//		ResolutionListener semanticLinkResolutionListener = this.context == null ? null : this.context.get(ResolutionListener.class);

		if (element instanceof org.nasdanika.drawio.ModelElement) {
			org.nasdanika.drawio.ModelElement<?> modelElement = (org.nasdanika.drawio.ModelElement<?>) element;
			String targetUriPropertyValue = modelElement.getProperty(NcoreActionBuilder.TARGET_URI_KEY);
			if (!org.nasdanika.common.Util.isBlank(targetUriPropertyValue)) {
				SemanticInfoRecord sRec = computeSemanticInfoRecord(
						context, 
						targetUriPropertyValue, 
						baseSemanticURIs, 
						progressMonitor);

				if (sRec != null) {
					modelElement.setLink(sRec.location().toString());
					if (org.nasdanika.common.Util.isBlank(modelElement.getTooltip())) {
						modelElement.setTooltip(sRec.description());
					}
				}
			}
		}
	}
	
	/**
	 * Computes semantic info - link and tooltip for drawio elements. This
	 * implementation uses the registry and uriResolver. Override to add support of
	 * resolving external semantic infos, e.g. loaded from semantic maps of
	 * dependency sites.
	 * 
	 * @param context
	 * @param key
	 * @param path
	 * @param action
	 * @param baseSemanticURI
	 * @param uriResolver
	 * @param registry
	 * @return
	 */
	protected SemanticInfoRecord computeSemanticInfoRecord(
			Context context, 
			String targetUriStr, 
			Collection<URI> baseSemanticURIs, 
			ProgressMonitor progressMonitor) {

		if (baseSemanticURIs == null || baseSemanticURIs.isEmpty()) {
			baseSemanticURIs = Collections.singleton(null);
		}
		for (URI baseSemanticURI : baseSemanticURIs) {
			URI targetURI = URI.createURI(targetUriStr);
			if (baseSemanticURI != null && targetURI.isRelative()) {
				targetURI = targetURI.resolve(baseSemanticURI.appendSegment(""));
			}
			URI bURI = uriResolver.apply(action, (URI) null);
			if (semanticInfoSource != null) {
				for (Entry<SemanticInfo, ?> entry : semanticInfoSource) {
					if (entry != null) {
						Object value = entry.getValue();
						if (value instanceof Label) {
							Label targetLabel = (Label) value;
							SemanticInfo semanticElementAnnotation = entry.getKey();
							if (semanticElementAnnotation != null) {
								for (URI semanticURI : semanticElementAnnotation.getIdentifiers()) {
									if (Objects.equals(targetURI, semanticURI)) {
										URI targetActionURI = uriResolver.apply(targetLabel, bURI);
										if (targetActionURI != null) {
											if (resolutionListener != null) {
												URI rawTargetURI = uriResolver.apply(targetLabel, null);
												URI backLinkURI = uriResolver.apply(action, rawTargetURI);
												resolutionListener.onSemanticReferenceResolution(action, targetUriStr,
														targetLabel, targetURI, backLinkURI);
											}

											String description = semanticElementAnnotation.getDescription();
											if (org.nasdanika.common.Util.isBlank(description)) {
												description = targetLabel.getTooltip();
											}
											return new SemanticInfoRecord(targetActionURI, description);
										}
									}
								}
							}
						}
					}
				}
			}

			SemanticInfoRecord rec = computeSemanticInfoRecord(targetURI, context);
			if (rec != null) {
				URI ref = rec.location();
				if (bURI != null) {
					ref = ref.deresolve(bURI, true, true, true);
				}
				if (resolutionListener != null) {
					resolutionListener.onSemanticReferenceResolution(action, targetUriStr, null, ref, null);
				}
				return new SemanticInfoRecord(ref, rec.description());
			}
		}

		String message = "Unresolved target URI: " + targetUriStr;
		progressMonitor.worked(Status.ERROR, 1, message, action);
		if (resolutionListener != null) {
			resolutionListener.onSemanticReferenceResolution(action, targetUriStr, null, null, null);
		}
		return null;
	}	
	
	/**
	 * Computes semantic reference. This implementation uses the registry and
	 * uriResolver. Override to add support of resolving external semantic
	 * references, e.g. loaded from semantic maps of dependency sites.
	 * 
	 * @param context
	 * @param key
	 * @param path
	 * @param action
	 * @param baseSemanticURI
	 * @param uriResolver
	 * @param registry
	 * @return
	 */
	protected URI computeSemanticReferfence(
			Context context, 
			String path, 
			Collection<URI> baseSemanticURIs, 
			ProgressMonitor progressMonitor) {

		SemanticInfoRecord rec = computeSemanticInfoRecord(
				context,
				path, 
				baseSemanticURIs, 
				progressMonitor);
		return rec == null ? null : rec.location();
	}

	/**
	 * Computes semantic links for external URI's. This implementation returns null;
	 * 
	 * @param targetURI
	 * @param context
	 * @return
	 */
	protected SemanticInfoRecord computeSemanticInfoRecord(URI targetURI, Context context) {
		return null;
	}
	
	/**
	 * Computes site map tree script - context property computer
	 * 
	 * @param context
	 * @return
	 */
	protected String computeSiteMapTreeScript(
			Context context, 
			ProgressMonitor progressMonitor) {
		// TODO - actions from action prototype, e.g. Ecore doc actions, to the tree.

		int maxLength = 50;
		SemanticMap<SemanticInfo, JsTreeNode> nodeMap = new SemanticMap<>();

		if (semanticInfoSource != null) {
			for (Entry<SemanticInfo, ?> entry : semanticInfoSource) {
				if (entry != null) {
					Object value = entry.getValue();
					if (value instanceof Label) {
						Label treeLabel = (Label) value;
						SemanticInfo semanticElementAnnotation = entry.getKey();
						if (semanticElementAnnotation != null) {
							Label label = treeLabel instanceof Link ? AppFactory.eINSTANCE.createLink()
									: AppFactory.eINSTANCE.createLabel();
							String treeLabelText = treeLabel.getText();
							if (org.nasdanika.common.Util.isBlank(treeLabelText)) {
								treeLabelText = "(blank)";
							}
							label.setText(
									treeLabelText.length() > maxLength ? treeLabelText.substring(0, maxLength) + "..."
											: treeLabelText);
							label.setIcon(treeLabel.getIcon());

							LabelJsTreeNodeSupplierFactoryAdapter<?> adapter;
							if (label instanceof Link) {
								URI bURI = uriResolver.apply(action, (URI) null);
								URI tURI = uriResolver.apply(treeLabel, bURI);
								if (tURI != null) {
									((Link) label).setLocation(tURI.toString());
								}
								adapter = new LinkJsTreeNodeSupplierFactoryAdapter<Link>((Link) label);
							} else {
								adapter = new LabelJsTreeNodeSupplierFactoryAdapter<>(label);
							}

							try {
								JsTreeNode jsTreeNode = adapter.create(context).execute(progressMonitor);
								jsTreeNode.attribute(Util.DATA_NSD_LABEL_UUID_ATTRIBUTE, treeLabel.getUuid());
								nodeMap.put(semanticElementAnnotation, jsTreeNode);
							} catch (Exception e) {
								throw new NasdanikaException(e);
							}
						}
					}
				}
			}
		}

		JsTreeFactory jsTreeFactory = context.get(JsTreeFactory.class, JsTreeFactory.INSTANCE);
		SemanticMap<ContainerInfo, List<Entry<SemanticInfo, JsTreeNode>>> groupedByContainer = new SemanticMap<>();
		org.nasdanika.common.Util.groupBy(nodeMap.entrySet(), sea -> ((SemanticInfo) sea.getKey()).getContainerInfo(),
				groupedByContainer);
		SemanticMap<ContainerInfo, List<Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>>>> groupedByContainerAndReferenceName = new SemanticMap<>();
		groupedByContainer.entrySet().stream()
				.map(e -> mapEntry(e.getKey(),
						org.nasdanika.common.Util.groupBy(e.getValue(),
								ve -> ActionContentGenerator.containmentRef(ve.getKey()))))
				.forEach(e -> groupedByContainerAndReferenceName.put(e.getKey(),
						createReferenceJsTreeNodes(e.getValue(), jsTreeFactory)));

		// Organizing JsTreeNodes into a tree - going over all SemanticInfo entries and
		// moving containerInfo's from groupedByContainerAndReferenceName under
		List<Entry<SemanticInfo, JsTreeNode>> allEntries = groupedByContainerAndReferenceName.values().stream()
				.flatMap(Collection::stream).flatMap(e -> e.getValue().stream()).toList();

		for (Entry<SemanticInfo, JsTreeNode> entry : allEntries) {
			@SuppressWarnings("unlikely-arg-type")
			List<Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>>> children = groupedByContainerAndReferenceName
					.remove(entry.getKey());
			if (children != null) {
				for (Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>> childEntry : children) {
					if (childEntry != null) {
						JsTreeNode refNode = childEntry.getKey();
						List<JsTreeNode> entryValueChildren = entry.getValue().children();
						if (refNode == null) {
							List<Entry<SemanticInfo, JsTreeNode>> grandChildren = childEntry.getValue();
							if (grandChildren != null) {
								for (Entry<SemanticInfo, JsTreeNode> grandChild : grandChildren) {
									entryValueChildren.add(grandChild.getValue());
								}
							}
						} else {
							entryValueChildren.add(refNode);
						}
					}
				}
			}
		}

		List<JsTreeNode> roots = new ArrayList<>();
		for (Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>> re : groupedByContainerAndReferenceName.values()
				.stream().flatMap(Collection::stream).toList()) {
			if (re != null) {
				JsTreeNode reKey = re.getKey();
				if (reKey == null) {
					for (Entry<SemanticInfo, JsTreeNode> childEntry : re.getValue()) {
						if (childEntry != null) {
							roots.add(childEntry.getValue());
						}
					}
				} else {
					roots.add(reKey);
				}
			}
		}

		JSONObject jsTree = jsTreeFactory.buildJsTree(roots);

		List<String> plugins = new ArrayList<>();
		plugins.add("state");
		plugins.add("search");
		JSONObject searchConfig = new JSONObject();
		searchConfig.put("show_only_matches", true);
		jsTree.put("search", searchConfig);
		jsTree.put("plugins", plugins);
		jsTree.put("state", Collections.singletonMap("key", "nsd-site-map-tree"));

		// Deletes selection from state
		String filter = NavigationPanelConsumerFactoryAdapter.CLEAR_STATE_FILTER
				+ " tree.search.search_callback = (results, node) => results.split(' ').includes(node.original['data-nsd-label-uuid']);";

		return jsTreeFactory.bind("#nsd-site-map-tree", jsTree, filter, null).toString();
	}
	

	private static <K, V> Map.Entry<K, V> mapEntry(K key, V value) {
		return new Entry<K, V>() {

			@Override
			public K getKey() {
				return key;
			}

			@Override
			public V getValue() {
				return value;
			}

			@Override
			public V setValue(V value) {
				throw new UnsupportedOperationException();
			}

		};
	}

	// Helper method
	private static String containmentRef(SemanticIdentity se) {
		if (se instanceof SemanticInfo) {
			ContainerInfo cInfo = ((SemanticInfo) se).getContainerInfo();
			if (cInfo != null) {
				return cInfo.getReference();
			}
		}
		return null;
	}

	private static List<Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>>> createReferenceJsTreeNodes(
			Map<String, List<Entry<SemanticInfo, JsTreeNode>>> groupedByReference, JsTreeFactory jsTreeFactory) {

		return groupedByReference.entrySet().stream().sorted(ActionContentGenerator::compareReferenceEntries)
				.map(e -> createReferenceJsTreeNode(e, jsTreeFactory)).toList();
	}

	private static int compareReferenceEntries(Entry<String, List<Entry<SemanticInfo, JsTreeNode>>> a,
			Entry<String, List<Entry<SemanticInfo, JsTreeNode>>> b) {
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return 1;
		}

		if (b == null) {
			return -1;
		}

		String aRef = a.getKey();
		String bRef = b.getKey();

		if (org.nasdanika.common.Util.isBlank(aRef) && org.nasdanika.common.Util.isBlank(bRef)) {
			return 0;
		}

		if (org.nasdanika.common.Util.isBlank(aRef)) {
			return 1;
		}

		if (org.nasdanika.common.Util.isBlank(bRef)) {
			return -1;
		}

		return aRef.compareTo(bRef);
	}

	/**
	 * Creates a {@link JsTreeNode} for a reference if refEntry is not null and
	 * reference name is not null. Links value nodes under the reference node
	 * 
	 * @param refEntry
	 * @param jsTreeFactory
	 * @return
	 */
	private static Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>> createReferenceJsTreeNode(
			Entry<String, List<Entry<SemanticInfo, JsTreeNode>>> refEntry, JsTreeFactory jsTreeFactory) {

		if (refEntry == null) {
			return null;
		}

		if (org.nasdanika.common.Util.isBlank(refEntry.getKey())) {
			return new Entry<JsTreeNode, List<Entry<SemanticInfo, JsTreeNode>>>() {

				@Override
				public List<Entry<SemanticInfo, JsTreeNode>> setValue(List<Entry<SemanticInfo, JsTreeNode>> value) {
					throw new UnsupportedOperationException();
				}

				@Override
				public List<Entry<SemanticInfo, JsTreeNode>> getValue() {
					return refEntry.getValue();
				}

				@Override
				public JsTreeNode getKey() {
					return null;
				}
			};
		}

		JsTreeNode refNode = jsTreeFactory.jsTreeNode();
		refNode.text(org.nasdanika.common.Util.nameToLabel(refEntry.getKey()));
		for (Entry<SemanticInfo, JsTreeNode> ve : refEntry.getValue()) {
			refNode.children().add(ve.getValue());
		}

		return Map.entry(refNode, refEntry.getValue());
	}	

}
