package org.nasdanika.models.app.graph.drawio;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.DiagramGenerator;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.graph.WidgetFactory;

/**
 * Base class for processor configuration/customization classes.
 */
public class Configuration implements RepresentationElementFilter {
	/**
	 * Icons size for UI generation - jsTree displays icons up to 24x24 pixels, leaving 4 pixes for padding
	 */
	public static final int ICON_SIZE = 20;
	/**
	 * Default base URI for the Drawio application to resolve library relative URL's.
	 */
	public static final URI DEFAULT_APP_BASE = URI.createURI("https://app.diagrams.net/");
	
	public static final String DOC_FORMAT_PROPERTY = "doc-format";
	public static final String DOC_REF_PROPERTY = "doc-ref";
	public static final String DOCUMENTATION_PROPERTY = "documentation";
	public static final String PROTOTYPE_PROPERTY = "prototype";
	public static final String PROTO_REF_PROPERTY = "proto-ref";
	public static final String TITLE_PROPERTY = "title";
	public static final String ICON_PROPERTY = "icon";
	public static final String ROLE_PROPERTY = "role";
	public static final String PARENT_PROPERTY = "parent";
	
	public static final String SOURCE_KEY = "source";
	public static final String TARGET_KEY = "target";

	public static final String ANONYMOUS_ROLE = "anonymous";
	public static final String CHILD_ROLE = "child";
	public static final String NAVIGATION_ROLE = "navigation";
	public static final String SECTION_ROLE = "section";
	
	public static final String SORT_KEY_PROPERTY = "sort-key";
	
	public static final String INDEX_NAME = "index.html";
	
	protected String getIconProperty() {
		return ICON_PROPERTY;
	}	
		
	protected String getSortKeyProperty() {
		return SORT_KEY_PROPERTY;
	}		
	
	protected String getRoleProperty() {
		return ROLE_PROPERTY;
	}	
	
	protected String getParentProperty() {
		return PARENT_PROPERTY;
	}	
	
	protected String getTitleProperty() {
		return TITLE_PROPERTY;
	}	
		
	protected String getDocumentationProperty() {
		return DOCUMENTATION_PROPERTY;
	}	
		
	protected String getDocRefProperty() {
		return DOC_REF_PROPERTY;
	}	
		
	protected String getPrototypeProperty() {
		return PROTOTYPE_PROPERTY;
	}	
		
	protected String getProtoRefProperty() {
		return PROTO_REF_PROPERTY;
	}	
	
	protected String getDocFormatProperty() {
		return DOC_FORMAT_PROPERTY; 
	}		
	
	protected String getSourceKey() {
		return SOURCE_KEY; 
	}		
	
	protected String getTargetKey() {
		return TARGET_KEY; 
	}		
	
	protected String getAnonymousRole() {
		return ANONYMOUS_ROLE; 
	}		
	
	protected String getChildRole() {
		return CHILD_ROLE; 
	}		
	
	protected String getNavigationRole() {
		return NAVIGATION_ROLE; 
	}		
	
	protected String getSectionRole() {
		return SECTION_ROLE; 
	}		
	
	protected String getIndexName() {
		return INDEX_NAME;
	}
	
	/**
	 * Override to implement filtering of representation elements
	 * @param representationElement
	 * @param registry
	 * @param progressMonitor
	 */
	@Override
	public void filterRepresentationElement(
			ModelElement sourceElement,
			ModelElement representationElement,
			Map<org.nasdanika.drawio.Element, ProcessorInfo<WidgetFactory>> registry,
			ProgressMonitor progressMonitor) {
		
	}

	/**
	 * Override to customize viewer.
	 * @return
	 */
	protected String getViewer() {
		return DiagramGenerator.JSDELIVR_DRAWIO_VIEWER;
	}
	
	/**
	 * Application base for resolving relative image URL's. 
	 * This implementation returns DEFAULT_APP_BASE. 
	 * Override to customize for different (e.g. intranet) installations.
	 * App sources - https://github.com/jgraph/drawio/tree/dev/src/main/webapp.
	 * For the purposes of serving images and a diagram editor the web app can be deployed as a static site.
	 * It can also be deployed as a Docker container - https://www.drawio.com/blog/diagrams-docker-app, https://hub.docker.com/r/jgraph/drawio 
	 * @return
	 */
	protected URI getAppBase() {
		return DrawioProcessorFactory.DEFAULT_APP_BASE;
	}
	
	/**
	 * This implementation returns the argument. 
	 * Override to rewrite URL's before conversion to icons. For example, read representations from a file system and convert to data URL's.
	 * @param imageRepr
	 * @return
	 */
	protected String rewriteImage(String imageRepr, ProgressMonitor progressMonitor) {
		return imageRepr;
	}
	
	/**
	 * Icon size to scale image representations to
	 * @return
	 */
	protected int getIconSize() {
		return DrawioProcessorFactory.ICON_SIZE;
	}

	/**
	 * Override to create additional content from a representation (page).
	 * For example, aria for screen readers and AI explaining the diagrams.
	 * This implementation returns an empty collection.
	 * @param representation
	 * @param registry
	 * @param progressMonitor
	 * @return
	 */
	protected Collection<? extends EObject> createRepresentationContent(
			Document representation,
			Map<org.nasdanika.drawio.Element, ProcessorInfo<WidgetFactory>> registry,
			ProgressMonitor progressMonitor) {
		return Collections.emptyList();
	}
	
	/**
	 * Filters processor. This implementation returns the processor AS-IS.
	 * @param <T>
	 * @param config
	 * @param processor
	 * @param infoProvider
	 * @param progressMonitor
	 */
	protected <T extends WidgetFactory> T filter(
			ProcessorConfig config, 
			T processor, 
			BiConsumer<Element,BiConsumer<ProcessorInfo<Object>,ProgressMonitor>> infoProvider,
			ProgressMonitor progressMonitor) {
		
		return processor;
	}	

}
