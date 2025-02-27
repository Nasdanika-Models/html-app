package org.nasdanika.models.app.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.cli.Description;
import org.nasdanika.cli.ParentCommands;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.Util;
import org.nasdanika.drawio.Document;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.models.app.graph.drawio.DrawioHtmlAppGenerator;
import org.nasdanika.models.app.graph.drawio.RepresentationElementFilter;

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
	
	public DrawioHtmlAppGeneratorCommand(CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
	}
	
	@ParentCommand
	private Document.Supplier documentSupplier;

	protected Consumer<Diagnostic> createDiagnosticConsumer(ProgressMonitor progressMonitor) {		
		return d -> progressMonitor.worked(d.getStatus(), 1, "Diagnostic: " + d.getMessage(), d);
	}

	protected DrawioHtmlAppGenerator createDrawioActionGenerator(Collection<RepresentationElementFilter> representationElementFilters) {
		return new DrawioHtmlAppGenerator() {
			
			@Override
			protected URI getBaseURI() {
				URI baseURI = super.getBaseURI();
				if (Util.isBlank(base)) {
					return baseURI;
				}
				
				URI bURI = URI.createURI(base);
				if (bURI.isRelative()) {
					bURI = bURI.resolve(baseURI);
				}
				return bURI;
			}
			
			@Override
			protected URI getRefBaseURI(URI docURI) {
				if (Util.isBlank(refBase)) {
					return super.getRefBaseURI(docURI);
				}
				return URI.createURI(refBase).resolve(docURI);
			}
			
			@Override
			protected String getIndexName() {
				return DrawioHtmlAppGeneratorCommand.this.indexName;
			}
			
			@Override
			public void filterRepresentationElement(
					ModelElement sourceElement, 
					ModelElement representationElement,
					Map<Element, ProcessorInfo<WidgetFactory>> registry, ProgressMonitor progressMonitor) {

				for (RepresentationElementFilter ref: representationElementFilters) {
					ref.filterRepresentationElement(sourceElement, representationElement, registry, progressMonitor);
				}
			}
			
		};
	}
	
	@Option(
		names = {"-b", "--base-uri"},
		description = "Base URI. E.g. 'pages/'")
	private String base;
		
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

	@Override
	protected Collection<Label> getLabels(ProgressMonitor progressMonitor) {
		Document document = documentSupplier.getDocument(progressMonitor); 
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
		
		DrawioHtmlAppGenerator actionGenerator = createDrawioActionGenerator(refs);
		Supplier<Collection<Label>> labelSupplier = actionGenerator.createLabelsSupplier(document, progressMonitor);
		Consumer<Diagnostic> diagnosticConsumer = createDiagnosticConsumer(progressMonitor);
		return labelSupplier.call(progressMonitor, diagnosticConsumer);
	}		

}
