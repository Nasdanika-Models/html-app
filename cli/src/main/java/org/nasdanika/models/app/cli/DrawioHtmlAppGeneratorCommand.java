package org.nasdanika.models.app.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.cli.ParentCommands;
import org.nasdanika.common.Context;
import org.nasdanika.common.Description;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Util;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.drawio.Page;
import org.nasdanika.drawio.Tag;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.models.app.graph.drawio.DrawioHtmlAppGenerator;
import org.nasdanika.models.app.graph.drawio.RepresentationElementFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
		description = "Generates html application model from a drawio document",
		versionProvider = ModuleVersionProvider.class,		
		mixinStandardHelpOptions = true,
		name = "html-app")
@ParentCommands(Document.Supplier.class)
@Description(icon = "https://docs.nasdanika.org/images/html-application.svg")
public class DrawioHtmlAppGeneratorCommand extends AbstractHtmlAppGeneratorCommand {
	
	private static Logger LOGGER = LoggerFactory.getLogger(DrawioHtmlAppGeneratorCommand.class);
	
	public DrawioHtmlAppGeneratorCommand(CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
	}
	
	@ParentCommand
	private Document.Supplier documentSupplier;

	protected Consumer<Diagnostic> createDiagnosticConsumer(ProgressMonitor progressMonitor) {		
		return d -> progressMonitor.worked(d.getStatus(), 1, "Diagnostic: " + d.getMessage(), d);
	}
		

	protected DrawioHtmlAppGenerator createDrawioActionGenerator(URI baseURI, Collection<RepresentationElementFilter> representationElementFilters) {
		return new DrawioHtmlAppGenerator() {
			
			@Override
			protected URI getBaseURI() {
				return baseURI;
			}
			
			@Override
			public URI getRefBaseURI(URI docURI) {
				if (Util.isBlank(refBase)) {
					return super.getRefBaseURI(docURI);
				}
				return URI.createURI(refBase).resolve(docURI);
			}
			
			@Override
			public String getIndexName() {
				return DrawioHtmlAppGeneratorCommand.this.indexName;
			}
			
			@Override
			public boolean isUseTooltipIfDocIsEmpty() {
				return DrawioHtmlAppGeneratorCommand.this.tooltip;
			}
			
			@Override
			public void filterRepresentationElement(
					ModelElement sourceElement, 
					ModelElement representationElement,
					Map<Element, ProcessorInfo<WidgetFactory>> registry, ProgressMonitor progressMonitor) {

				for (RepresentationElementFilter ref: representationElementFilters) {
					ref.filterRepresentationElement(sourceElement, representationElement, registry, progressMonitor);
				}
				
				if (representationFilterPropery != null) {
					for (Entry<String, String> ppe: predicatePropery.entrySet()) {
						String propVal = sourceElement.getProperty(ppe.getValue());
						if (!Util.isBlank(propVal)) {
							ScriptEngineManager sem = new ScriptEngineManager();
							ScriptEngine scriptEngine = sem.getEngineByName(ppe.getKey());
							try {
								scriptEngine.put("sourceElement", sourceElement);
								scriptEngine.put("representationElement", representationElement);
								scriptEngine.eval(propVal);
							} catch (ScriptException e) {
								throw new CommandLine.ExecutionException(spec.commandLine(), "Error evaluating predicate: " + e, e);
							}
						}
					}
				}
			}
			
			@Override
			public boolean test(org.nasdanika.graph.Element element) {
				if (predicatePropery != null && element instanceof ModelElement) {
					for (Entry<String, String> ppe: predicatePropery.entrySet()) {
						String propVal = ((ModelElement) element).getProperty(ppe.getValue());
						if (!Util.isBlank(propVal)) {
							if ("spel".equals(ppe.getKey())) {
								Boolean result = element.evaluate(propVal, Boolean.class); // TODO - variables from context?
								if (Boolean.FALSE.equals(result)) {
									return false;
								}
							} else {
								ScriptEngineManager sem = new ScriptEngineManager();
								ScriptEngine scriptEngine = sem.getEngineByName(ppe.getKey());
								try {
									scriptEngine.put("element", element);
									Object result = scriptEngine.eval(propVal);
									if (Boolean.FALSE.equals(result)) {
										return false;
									}
								} catch (ScriptException e) {
									throw new CommandLine.ExecutionException(spec.commandLine(), "Error evaluating predicate: " + e, e);
								}
							}
						}
					}
				}
				
				return super.test(element);
			};
			
		};
	}
			
	@Option(
		names = {"--tooltip"},
		description = {
			"Use tooltip as documentation",
			"if documentation is empty.",
			"Default: true"
		},
		negatable = true)
	private boolean tooltip;	
	
	@Option(
		names = {"-b", "--base-uri"},
		description = "Base URI. E.g. 'pages/'")
	private String base;
	
	@Option(
		names = "--predicate",
		description = {
				"SpEL expression for filtering",
				"diagram elements"
		})
	private String predicate;
	
	@Option(
		names = "--tag",
		description = "Tag(s) to filter diagram elements")
	private List<String> tag;
		
	@Option(
		names = "--ref-base-uri",
		description = { 
				"Base URI for resolving documentation",
				"and prototype references. Resolved",
				"relative to the document URI"				
		})
	private String refBase;	
	
	@Option(
		names = {"-x", "--index"},
		description = "Index file name, defaults to ${DEFAULT-VALUE}")
	@Description(
			"""
			You may change the file extension if you need to generate server pages (PHP, JSP, ASPX) with additional functionality such as authentication.
			For example, you may change the extension to ``php`` to add authentication and other dynamic behavior with ``-x index.php`` option. 
			[Internet Banking System PHP demo](https://github.com/Nasdanika-Demos/internet-banking-system-php) shows how do so.
			If you use generated search, also add ``-x <extension>`` option to the [site](https://docs.nasdanika.org/nsd-cli/nsd/drawio/html-app/site/index.html) command so the generated pages are included into the search index, e.g. ``-x php``.
			
			To add contents before the ``<html>`` opening tag use ``prolog`` configuration key in page template. E.g.:
			
			```yml
			  prolog:  
			    content.Text: |
			      <?php
			        ... php code here ...
			      ?>
			```
			
			You can also use ``epilog`` to add content after the ``</html>`` closing tag.
			
			If you use search and glossary, change their extensions too.
			
			This technique can be used to publish generated sites to SharePoint - change the extension to ``aspx``.			
			""")
	private String indexName = "index.html";
		
	@Option(			
			names = {"-F", "--representation-element-filter"},
			paramLabel = "Invocable URI",
			description = {
					"Invocable URI of a representation element filter",
					"URIs are resolved relative to the current directory"
			})
	@Description(icon = "https://docs.nasdanika.org/images/filter.svg")
	private List<String> representationElementFilters = new ArrayList<>();
		
	@Option(			
			names = {"--page"},
			paramLabel = "Page name",
			description = {
					"If provided, actions are generated for the page",
					"with matching name"
			})
	private String pageName;
	
	@Option(			
			names = {"--predicate-property"},
			paramLabel = "Predicate property",
			description = {
					"Mapping of a script language name",
					"to the property name containing",
					"predicate script in this language",
					"'spel' is reserved for SpEL"
			})
	private Map<String,String> predicatePropery;	
	
	@Option(			
			names = {"--representation-filter-property"},
			paramLabel = "Predicate property",
			description = {
					"Mapping of a script language name",
					"to the property name containing",
					"representation filter script",
					"in this language"
			})
	private Map<String,String> representationFilterPropery;	

	@Override
	protected Collection<Label> getLabels(ProgressMonitor progressMonitor) {
		URI actionGeneratorBaseURI = URI.createURI("tmp://" + UUID.randomUUID() + "/" + UUID.randomUUID() + "/");
		if (!Util.isBlank(base)) {
			URI bURI = URI.createURI(base);
			if (bURI.isRelative()) {
				bURI = bURI.resolve(actionGeneratorBaseURI);
			}
			actionGeneratorBaseURI = bURI;
		}				
		
		Map<String,String> properties = Map.of(Context.BASE_URI_PROPERTY, actionGeneratorBaseURI.toString());
		
		Document document = documentSupplier.getDocument(
				null,
				properties::get, 
				progressMonitor); 
		Collection<RepresentationElementFilter> refs = new ArrayList<>();		
		File currentDir = new File(".");
		URI baseURI = URI.createFileURI(currentDir.getAbsolutePath()).appendSegment("");		
		for (String refStr: representationElementFilters) {
			URI requirement = URI.createURI(refStr).resolve(baseURI);
			Invocable invocable = capabilityLoader.loadOne(
					ServiceCapabilityFactory.createRequirement(Invocable.class, null, requirement),
					progressMonitor);
			RepresentationElementFilter ref = invocable.invoke();
			refs.add(ref);			
		}		
		
		DrawioHtmlAppGenerator actionGenerator = createDrawioActionGenerator(actionGeneratorBaseURI, refs);
		org.nasdanika.drawio.Element root = document;
		if (!Util.isBlank(pageName)) {
			Optional<Page> pageOpt = document.getPages().stream().filter(p -> pageName.equals(p.getName())).findFirst();
			if (pageOpt.isEmpty()) {
				throw new CommandLine.ParameterException(spec.commandLine(), "Page not found: " + pageName);
			}
			root = pageOpt.get();
		}
		Supplier<Collection<Label>> labelSupplier = actionGenerator.createLabelsSupplier(root, this::testElement, progressMonitor);
		Consumer<Diagnostic> diagnosticConsumer = createDiagnosticConsumer(progressMonitor);
		return labelSupplier.call(progressMonitor, diagnosticConsumer);
	}		
	
	protected boolean testElement(org.nasdanika.graph.Element element) {
		if (!Util.isBlank(predicate)) {
			try {
				return element.evaluate(predicate, Boolean.class);
			} catch (Exception e) {
				LOGGER.debug("Error evaluating predicate '" + predicate + "' in the context of '" + element + "': " + e, e);
			}
		}
		
		if (tag != null && !tag.isEmpty()) {
			if (element instanceof ModelElement) {
				Set<Tag> elementTags = ((ModelElement) element).getTags();
				if (elementTags.isEmpty()) {
					return true; // No tags 
				}
				for (Tag eTag: elementTags) {
					if (tag.contains(eTag.getName())) {
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

}
