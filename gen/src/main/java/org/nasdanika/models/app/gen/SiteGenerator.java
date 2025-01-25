package org.nasdanika.models.app.gen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.nasdanika.common.Context;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.DiagnosticException;
import org.nasdanika.common.NasdanikaException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Status;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.exec.content.Text;
import org.nasdanika.exec.resources.Container;
import org.nasdanika.exec.resources.ReconcileAction;
import org.nasdanika.exec.resources.ResourcesFactory;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.Link;
import org.nasdanika.models.app.emf.NcoreActionBuilder;
import org.nasdanika.models.app.emf.ResolutionListener;
import org.nasdanika.models.app.gen.ActionContentProvider.Factory;
import org.nasdanika.models.app.gen.Util.HTMLProcessor;
import org.nasdanika.ncore.ModelElement;
import org.nasdanika.ncore.NcorePackage;
import org.nasdanika.ncore.util.ContainerInfo;
import org.nasdanika.ncore.util.SemanticInfo;
import org.nasdanika.resources.FileSystemContainer;

import com.redfin.sitemapgenerator.ChangeFreq;

public class SiteGenerator {

	/**
	 * Override to return contributors.
	 * 
	 * @return A collection of {@link SiteGeneratorContributor}s.
	 */
	protected Collection<SiteGeneratorContributor> getContributors() {
		return Collections.emptyList();
	}

	/**
	 * Creates and configures a resource set for loading models. Override to
	 * customize, e.g. register {@link EPackage}'s and adapter factories.
	 * 
	 * @param progressMonitor
	 * @return
	 */
	protected ResourceSet createResourceSet(Context context, ProgressMonitor progressMonitor) {
		ResourceSet resourceSet = Util.createResourceSet(context, progressMonitor);
		for (SiteGeneratorContributor contributor : getContributors()) {
			contributor.configureResourceSet(resourceSet, context, progressMonitor);
		}
		return resourceSet;
	}

	/**
	 * Creates a resource set for loading resource model. Override to customize,
	 * e.g. register {@link EPackage}'s and adapter factories.
	 * 
	 * @param progressMonitor
	 * @return
	 */
	protected ResourceSet createResourceModelResourceSet(Context context, ProgressMonitor progressMonitor) {
		ResourceSet resourceSet = createResourceSet(context, progressMonitor);
		for (SiteGeneratorContributor contributor : getContributors()) {
			contributor.configureResourceModelResourceSet(resourceSet, context, progressMonitor);
		}
		return resourceSet;
	}

	/**
	 * Creates a resource set for loading action model. Override to customize, e.g.
	 * register {@link EPackage}'s and adapter factories.
	 * 
	 * @param progressMonitor
	 * @return
	 */
	protected ResourceSet createActionModelResourceSet(Context context, ProgressMonitor progressMonitor) {
		ResourceSet resourceSet = createResourceSet(context, progressMonitor);
		for (SiteGeneratorContributor contributor : getContributors()) {
			contributor.configureActionModelResourceSet(resourceSet, context, progressMonitor);
		}
		return resourceSet;
	}

	/**
	 * Generates a resource model from an action model.
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	protected Resource generateResourceModel(
			Label root, 
			Iterable<Map.Entry<SemanticInfo, ?>> semanticInfoSource,
			org.nasdanika.models.bootstrap.Page pageTemplate, 
			URI resourceURI, 
			String containerName,
			File resourceWorkDir, 
			ResolutionListener resolutionListener, 
			Context context,
			ProgressMonitor progressMonitor) throws IOException {

		for (SiteGeneratorContributor contributor : getContributors()) {
			contributor.processRoot(root, context, progressMonitor);
		}

		java.util.function.Consumer<Diagnostic> diagnosticConsumer = diagnostic -> {
			if (diagnostic.getStatus() == Status.FAIL || diagnostic.getStatus() == Status.ERROR) {
				System.err.println("***********************");
				System.err.println("*      Diagnostic     *");
				System.err.println("***********************");
				diagnostic.dump(System.err, 4, Status.FAIL, Status.ERROR);
			}
			if (diagnostic.getStatus() != Status.SUCCESS) {
				throw new DiagnosticException(diagnostic);
			}
			;
		};

		Container container = ResourcesFactory.eINSTANCE.createContainer();
		container.setName(containerName);
		container.setReconcileAction(ReconcileAction.OVERWRITE);

		ResourceSet resourceSet = createResourceModelResourceSet(context, progressMonitor);
		Resource modelResource = resourceSet.createResource(resourceURI);
		modelResource.getContents().add(container);

		// Generating content file from action content

		File pagesDir = new File(resourceWorkDir, "pages");
		pagesDir.mkdirs();

		JSONArray semanticInfo = semanticInfo(root, semanticInfoSource, context);
		if (semanticInfo != null) {
			org.nasdanika.exec.resources.File semanticMapFile = container.getFile("semantic-info.json");
			Text semanticMapText = ContentFactory.eINSTANCE.createText();
			semanticMapText.setContent(semanticInfo.toString(4));
			semanticMapFile.getContents().add(semanticMapText);
		}

		Factory actionContentProviderFactory = contentProviderContext -> (cAction, uriResolver, pMonitor) -> {
			ActionContentGenerator actionContentGenerator = new ActionContentGenerator(
					cAction, 
					getExtension(), 
					semanticInfoURIResolver(uriResolver), 
					semanticInfoSource, 
					resolutionListener, 
					context) {
				
				/**
				 * @param action
				 * @return
				 */
				@Override
				protected SemanticInfo getSemanticInfoAnnotation(Action action) {
					return SiteGenerator.this.getSemanticInfoAnnotation(action);
				}	
				
				@Override
				protected String computeSemanticLink(
						URI targetURI, 
						URI baseURI, 
						Context context,
						ResolutionListener resolutionListener) {

					return SiteGenerator.this.computeSemanticLink(targetURI, baseURI, context, resolutionListener);
				}					
				
			};
			
			return actionContentGenerator.getActionContent(resourceWorkDir, diagnosticConsumer, pMonitor);
		};
		
		org.nasdanika.models.app.gen.PageContentProvider.Factory pageContentProviderFactory = contentProviderContext -> (page, baseURI, uriResolver, pMonitor) -> getPageContent(
				page, 
				baseURI,
				semanticInfoURIResolver(uriResolver), pagesDir, contentProviderContext, progressMonitor);
		
		Util.generateSite(
				root, 
				pageTemplate, 
				container,
				actionContentProviderFactory,
				pageContentProviderFactory,
				Context.singleton("semantic-info", semanticInfo.toString()).compose(context), progressMonitor);

		modelResource.save(null);

		// Page model to XML conversion
		ResourceSet pageResourceSet = new ResourceSetImpl();
		pageResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		pageResourceSet.getAdapterFactories().add(new AppAdapterFactory());
		for (File pageFile : pagesDir.listFiles(f -> f.getName().endsWith(".xml"))) {
			URI pageURI = URI.createFileURI(pageFile.getCanonicalPath());
			Resource pageEResource = pageResourceSet.getResource(pageURI, true);
			SupplierFactory<InputStream> contentFactory = org.nasdanika.common.Util
					.asInputStreamSupplierFactory(pageEResource.getContents());
			try (InputStream contentStream = contentFactory.create(context).call(progressMonitor, diagnosticConsumer,
					Status.FAIL, Status.ERROR)) {
				Files.copy(contentStream, new File(pageFile.getCanonicalPath().replace(".xml", getExtension())).toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				progressMonitor.worked(1, "[Page xml -> html] " + pageFile.getName());
			}
		}

		for (SiteGeneratorContributor contributor : getContributors()) {
			contributor.processResourcecModel(modelResource, context, progressMonitor);
		}

		return modelResource;
	}

	protected String getExtension() {
		return ".html";
	}

	protected BiFunction<Label, URI, URI> semanticInfoURIResolver(BiFunction<Label, URI, URI> uriResolver) {
		return (label, base) -> {
			URI ret = uriResolver.apply(label, base);
			return ret == null ? resolveSemanticInfo(label, base) : ret;
		};
	}

	protected boolean isSemanticInfoLink(Link link) {
		return false;
	}

	/**
	 * Resolves URI from a semantic info (external definitions)
	 * 
	 * @param label
	 * @param base
	 * @return
	 */
	protected URI resolveSemanticInfo(Label label, URI base) {
		if (label instanceof Link && isSemanticInfoLink((Link) label)) {
			String linkLocation = ((Link) label).getLocation();
			if (!org.nasdanika.common.Util.isBlank(linkLocation)) {
				URI locationURI = URI.createURI(linkLocation);
				return base == null || locationURI == null ? locationURI
						: locationURI.deresolve(base, true, true, true);
			}
		}
		return null;
	}

	/**
	 * Creates a semantic info array. Override to create aggregated semantic maps.
	 * 
	 * @param root
	 * @param registry
	 * @param context
	 * @return
	 */
	protected JSONArray semanticInfo(Label root, Iterable<Map.Entry<SemanticInfo, ?>> semanticInfoSource,
			Context context) {
		// Semantic info
		JSONArray semanticInfo = new JSONArray();
		URI baseURI = URI.createURI("temp://" + UUID.randomUUID() + "/" + UUID.randomUUID() + "/");
		Context semanticMapContext = Context.singleton(Context.BASE_URI_PROPERTY, baseURI).compose(context);
		BiFunction<Label, URI, URI> uriResolver = semanticInfoURIResolver(
				org.nasdanika.models.app.util.Util.uriResolver(root, semanticMapContext));

		if (semanticInfoSource != null) {
			for (Entry<SemanticInfo, ?> nextEntry : semanticInfoSource) {
				if (nextEntry != null) {
					Object nextValue = nextEntry.getValue();
					if (nextValue instanceof Label) {
						Label label = (Label) nextValue;
						SemanticInfo semanticElementAnnotation = nextEntry.getKey();
						if (semanticElementAnnotation != null) {
							JSONObject value = semanticElementAnnotation.toJSON();

							String labelText = label.getText();
							if (!org.nasdanika.common.Util.isBlank(labelText)) {
								value.put(ContainerInfo.NAME_KEY, labelText);
							}
							String labelTooltip = label.getTooltip();
							if (!org.nasdanika.common.Util.isBlank(labelTooltip)) {
								value.put(SemanticInfo.DESCRIPTION_KEY, labelTooltip);
							}
							String labelIcon = label.getIcon();
							if (!org.nasdanika.common.Util.isBlank(labelIcon)) {
								value.put(SemanticInfo.ICON_KEY, labelIcon);
							}
							URI linkURI = uriResolver.apply(label, baseURI);
							if (linkURI != null) {
								value.put(SemanticInfo.LOCATION_KEY, linkURI.toString());
							}

							semanticInfo.put(value);
						}
					}
				}
			}
		}
		return semanticInfo;
	}

	/**
	 * Creates a file with .xml extension containing page contents resource model.
	 * Creates and returns a resource with .html extension. A later processing step
	 * shall convert .xml to .html
	 * 
	 * @param page
	 * @param baseURI
	 * @param uriResolver
	 * @param pagesDir
	 * @param progressMonitor
	 * @return
	 */
	protected EList<EObject> getPageContent(org.nasdanika.models.bootstrap.Page page, URI baseURI,
			BiFunction<Label, URI, URI> uriResolver, File pagesDir, Context contentProviderContext,
			ProgressMonitor progressMonitor) {

		try {
			// Saving a page to .xml and creating a reference to .html; Pages shall be
			// processed from .xml to .html individually.
			page.setUuid(UUID.randomUUID().toString());

			ResourceSet pageResourceSet = new ResourceSetImpl();
			pageResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
					.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
			URI pageURI = URI.createFileURI(new File(pagesDir, page.getUuid() + ".xml").getCanonicalPath());
			Resource pageEResource = pageResourceSet.createResource(pageURI);
			pageEResource.getContents().add(page);
			pageEResource.save(null);

			org.nasdanika.exec.content.Resource pageResource = ContentFactory.eINSTANCE.createResource();
			pageResource.setLocation("pages/" + page.getUuid() + getExtension());
			progressMonitor.worked(1, "[Page content] " + page.getName() + " -> " + pageResource.getLocation());
			return ECollections.singletonEList(pageResource);
		} catch (IOException e) {
			throw new NasdanikaException(e);
		}
	}

	protected boolean isParentReference(EReference eReference) {
		return eReference.isContainment() || eReference == NcorePackage.Literals.REFERENCE__TARGET;
	}

	/**
	 * Computes semantic links for external URI's. This implementation returns null;
	 * 
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

	protected SemanticInfo getSemanticInfoAnnotation(Action action) {
		return SemanticInfo.getAnnotation(action);
	}
	
	// --- Utility methods

	/**
	 * Wraps iterator
	 * 
	 * @param <T>
	 * @param iterator
	 * @return
	 */
	public static <T, U> Iterator<U> wrap(Iterator<T> iterator, Function<? super T, U> mapper) {
		return new Iterator<U>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public U next() {
				return mapper.apply(iterator.next());
			}

			@Override
			public void remove() {
				iterator.remove();
			}

		};
	}

	/**
	 * @param object
	 * @return Semantic info or self info
	 */
	protected Map.Entry<SemanticInfo, Label> semanticInfo(Notifier object) {
		if (object instanceof Label) {
			Label label = (Label) object;
			SemanticInfo semanticInfo = SemanticInfo.getAnnotation(label);
			if (semanticInfo == null) {
				semanticInfo = new SemanticInfo(label);
			}
			return Map.entry(semanticInfo, (Label) object);
		}
		return null;
	}

	/**
	 * Override to load semantic info from external sources. This implementation
	 * iterates over the resource set and returns entries created by selfInfo
	 * 
	 * @param resourceSet
	 * @return
	 */
	protected Iterable<Map.Entry<SemanticInfo, ?>> semanticInfoSource(ResourceSet resourceSet) {
		return composeWithSemanticInfo(asIterable(resourceSet, this::semanticInfo));
	}

	/**
	 * @return External {@link SemanticInfo}'s for referencing.
	 */
	protected Iterable<SemanticInfo> getSemanticInfos() {
		return null;
	}

	protected Iterable<Entry<SemanticInfo, ?>> composeWithSemanticInfo(
			Iterable<Entry<SemanticInfo, ?>> semanticInfoSource) {
		Iterable<SemanticInfo> semanticInfos = getSemanticInfos();
		if (semanticInfos == null) {
			return semanticInfoSource;
		}
		return new Iterable<Entry<SemanticInfo, ?>>() {

			@Override
			public Iterator<Entry<SemanticInfo, ?>> iterator() {
				return new Iterator<Map.Entry<SemanticInfo, ?>>() {

					private Iterator<Map.Entry<SemanticInfo, ?>> firstIterator = semanticInfoSource.iterator();
					private Iterator<Map.Entry<SemanticInfo, ?>> secondIterator = wrap(semanticInfos.iterator(),
							this::mapToLabel);

					private Map.Entry<SemanticInfo, Label> mapToLabel(SemanticInfo semanticInfo) {
						URI location = semanticInfo.getLocation();
						Label label = location == null ? AppFactory.eINSTANCE.createLabel()
								: AppFactory.eINSTANCE.createLink();
						label.setText(semanticInfo.getName());
						label.setTooltip(semanticInfo.getDescription());
						label.setIcon(semanticInfo.getIcon());

						if (location != null) {
							((Link) label).setLocation(location.toString());
						}
						return Map.entry(semanticInfo, label);
					}

					@Override
					public Entry<SemanticInfo, ?> next() {
						if (firstIterator != null && firstIterator.hasNext()) {

						}
						return (firstIterator.hasNext() ? firstIterator : secondIterator).next();
					}

					@Override
					public boolean hasNext() {
						return firstIterator.hasNext() || secondIterator.hasNext();
					}
				};
			}

		};
	}

	/**
	 * Creates a resolution listener. Used by subclasses.
	 * 
	 * @param errorConsumer   location, error message
	 * @param context
	 * @param progressMonitor
	 * @return
	 */
	protected ResolutionListener createResolutionListener(
			BiConsumer<String, String> 
			errorConsumer, Context context,
			ProgressMonitor progressMonitor) {

		return new ResolutionListener() {
			/**
			 * Called on resolution of "target-uri" property of a diagram element. If target
			 * is null then resolution was unsuccessful.
			 * 
			 * @param element
			 * @param action
			 * @param targetUriPropertyValue
			 * @param target
			 */
			@Override
			public void onTargetURIResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement,
					String targetURI, 
					EObject target) {

				if (target == null) {
					String location = modelElement == null ? NcoreActionBuilder.actionMarker(action)
							: modelElement.toString() + " " + modelElement.getMarkers().toString();
					errorConsumer.accept(location, "Element with URI not found: " + targetURI);
				}
			}
			
			@Override
			public void onSemanticUUIDResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement,
					String semanticUUID, 
					ModelElement semanticModelElement, 
					Label semanticModelElementLabel) {

				if (semanticModelElementLabel== null) {
					String location = modelElement == null ? NcoreActionBuilder.actionMarker(action) : modelElement.toString() + " " + modelElement.getMarkers().toString();
					errorConsumer.accept(location, "Action for semantic UUID not found: " + semanticUUID);
				}
			}

			/**
			 * Called on resolution of action URI
			 * 
			 * @param action       Source action
			 * @param modelElement Source model element if resolution is done for diagram
			 *                     element properties
			 * @param targetURIStr Target URI string resolved relative to action URI's.
			 * @param target       Target, null if not found.
			 * @param targetURI    Target URI relative to the source action. Null of target
			 *                     is not found or doesn't have a URI.
			 * @param backLinkURI  Backlink from the target action to the source action.
			 */
			@Override
			public void onActionURIResolution(
					Action action, 
					org.nasdanika.drawio.ModelElement modelElement,
					String targetURIStr, 
					Label target, 
					URI targetURI, 
					URI backLinkURI) {

				if (target == null) {
					String location = modelElement == null ? NcoreActionBuilder.actionMarker(action)
							: modelElement.toString() + " " + modelElement.getMarkers().toString();
					errorConsumer.accept(location, "Action with URI not found: " + targetURIStr);
				}
			}

			/**
			 * Called on resolution of action UUID
			 * 
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

				if (target == null) {
					String location = modelElement == null ? NcoreActionBuilder.actionMarker(action)
							: modelElement.toString() + " " + modelElement.getMarkers().toString();
					errorConsumer.accept(location, "Action with UUID not found: " + actionUUID);
				}
			}

			/**
			 * Called on resolution of semantic reference or link
			 * 
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

				if (target == null) {
					errorConsumer.accept(NcoreActionBuilder.actionMarker(action),
							"Unresolved semantic reference: " + targetURIStr);
				}
			}

			@Override
			public void onComputingDrawioRepresentation(Action action, String key,
					org.nasdanika.drawio.Document document) {

				if (document == null) {
					errorConsumer.accept(NcoreActionBuilder.actionMarker(action), "Unresolved representation: " + key);
				}
			}

		};
	}

	/**
	 * Override to load semantic info from external sources. This implementation
	 * iterates over the resource set and returns entries created by selfInfo
	 * 
	 * @param resourceSet
	 * @return
	 */
	protected Iterable<Map.Entry<SemanticInfo, ?>> semanticInfoSource(Resource actionResource) {
		return composeWithSemanticInfo(asIterable(actionResource, this::semanticInfo));
	}

	public static <T> Iterable<T> asIterable(Resource resource, Function<? super EObject, T> mapper) {
		return () -> wrap(resource.getAllContents(), mapper);
	}

	public static <T> Iterable<T> asIterable(ResourceSet resourceSet, Function<? super Notifier, T> mapper) {
		return () -> wrap(resourceSet.getAllContents(), mapper);
	}
	

	/**
	 * Generates files from the previously generated resource model.
	 * 
	 * @throws org.eclipse.emf.common.util.DiagnosticException
	 * @throws IOException
	 * @throws Exception
	 */
	protected Map<String, Collection<String>> generateContainer(
			Resource resourceModel,
			// TODO backlinks info provided by the resolution listener
			File workDir, 
			File outputDir, 
			Predicate<String> cleanPredicate, 
			String siteMapDomain, 
			String containerName,
			Context context, 
			ProgressMonitor progressMonitor) throws org.eclipse.emf.common.util.DiagnosticException, IOException {
		Map<String, Collection<String>> errors = new TreeMap<>();

		Util.generateContainer(resourceModel, new FileSystemContainer(workDir), context, progressMonitor);

		org.nasdanika.common.Util.copy(new File(workDir, containerName), outputDir, true, cleanPredicate, null);

		Util.generateSitemapAndSearch(
				outputDir, 
				siteMapDomain, 
				this::isSiteMap, 
				getChangeFrequency(), 
				this::isSearch,
				(path, error) -> {
					progressMonitor.worked(Status.ERROR, 1, "[" + path + "] " + error);
					errors.computeIfAbsent(path, p -> new ArrayList<>()).add(error);
				});

		HTMLProcessor htmlProcessor = new HTMLProcessor() {
			
			@Override
			public boolean isHTML(File file, String path) {
				return file != null && file.isFile() && path != null && path.toLowerCase().endsWith(getExtension()); 
			}			

			@Override
			public boolean process(File file, String path, Document document) {
//				System.out.println(path + " -> " + file.getAbsolutePath());
				// TODO - Backlinks injection
				return false;
			}
		};

		Util.processHTML(htmlProcessor, outputDir.listFiles());

		return errors;
	}

	protected ChangeFreq getChangeFrequency() {
		return ChangeFreq.WEEKLY;
	}

	protected boolean isSiteMap(File file, String path) {
		return path.endsWith(getExtension());
	}

	/**
	 * Whether to include in search
	 * @param file
	 * @param path
	 * @return
	 */
	protected boolean isSearch(File file, String path) {
		return path.endsWith(getExtension()) && !("search" + getExtension()).equals(path) && !("glossary" + getExtension()).equals(path);
	}

}
