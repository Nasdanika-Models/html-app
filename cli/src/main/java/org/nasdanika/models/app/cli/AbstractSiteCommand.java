package org.nasdanika.models.app.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.cli.DelegatingCommand;
import org.nasdanika.common.Context;
import org.nasdanika.common.NasdanikaException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Status;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.html.bootstrap.Theme;
import org.nasdanika.models.app.gen.AppSiteGenerator;
import org.springframework.util.AntPathMatcher;

import picocli.CommandLine.Option;

/**
 * Base class for site generation commands
 * @deprecated Use {@link SiteGeneratorCommand}
 */
@Deprecated
public abstract class AbstractSiteCommand extends DelegatingCommand {
	
	public AbstractSiteCommand() {
		
	}
	
	public AbstractSiteCommand(CapabilityLoader capabilityLoader) {
		super(capabilityLoader);
	}
	
	protected abstract String getOutput();
	
	protected abstract URI getModelURI(URI contextURI);
		
	@Option(
			names = {"-e", "--exclude"},
			arity = "*",
			description = {
					"Output directory clean excludes",
					"Ant pattern"
				})
	private String[] excludes;
	
	@Option(
			names = {"-i", "--include"},
			arity = "*",
			description = {
					"Output directory clean includes",
					"Ant pattern"
				})
	private String[] includes;
	
	@Option(names = {"-m", "--domain"}, description = "Sitemap domain")
	private String domian;
	
	@Option(names = {"-w", "--work-dir"}, description = "Working directory")
	private File workDir;
	
	@Option(names = {"-b", "--base-dir"}, description = "Base directory")
	private File baseDir;
		
	@Option(
			names = {"-l", "--clean"},
			negatable = true,
			description = {
					"Clean working directory",
					"defaults to ${DEFAULT-VALUE}"
				}, 
			defaultValue = "true")
	private boolean clean;
	
	@Option(
			names = {"-T", "--page-template"},
			description = {
					"Page template URI relative ",
					"to the current directory"
				})
	private String pageTemplate;
	
	@Option(
			names = {"-r", "--errors"},	
			description = {
				"Expected number of page errors",
				"-1 for any (not fail on errors)",
				"default is 0"	
			})
	private int pageErrors;

	@Override
	protected SupplierFactory<Integer> getSupplierFactory() {
		return ctx -> new org.nasdanika.common.Supplier<Integer>() {

			@Override
			public double size() {
				return 1;
			}

			@Override
			public String name() {
				return "Site generator";
			}

			@Override
			public Integer execute(ProgressMonitor progressMonitor) {
				try {
					return generate(ctx, progressMonitor);
				} catch (IOException | DiagnosticException e) {
					throw new NasdanikaException(e);
				}				
			}
			
		};
	}
	
	protected int generate(Context context, ProgressMonitor progressMonitor) throws IOException, DiagnosticException {
		
		AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
			
			@Override
			protected ResourceSet createResourceSet(Context context, ProgressMonitor progressMonitor) {
				Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
				return getCapabilityLoader().loadOne(requirement, progressMonitor);
			}
			
			@Override
			protected boolean isDeleteOutputPath(String path) {
				if (includes != null) {
					boolean matched = false;
					for (String include: includes) {
						AntPathMatcher matcher = new AntPathMatcher();
						if (matcher.match(include, path)) {
							matched = true;
							break;
						}
					}
					if (!matched) {
						return false;
					}
				}
				
				if (excludes != null) {
					for (String exclude: excludes) {
						AntPathMatcher matcher = new AntPathMatcher();
						if (matcher.match(exclude, path)) {
							return false;
						}
					}					
				}
				
				return true;
			}	
			
			@Override
			protected ProgressMonitor createProgressMonitor() {
				return progressMonitor.split("Generating site", 1);
			}
			
		};
		
		if (baseDir == null) {
			baseDir = new File(".");
		}
		URI contextURI = URI.createFileURI(baseDir.getCanonicalPath()).appendSegment("");
		URI modelURI = getModelURI(contextURI);
		URI pageTemplateURI = pageTemplate == null ? Theme.Cerulean.pageTemplateCdnURI : URI.createURI(pageTemplate).resolve(contextURI);
		
		URI outputURI = URI.createFileURI(getOutput()).resolve(contextURI);
		File outputDir = new File(outputURI.toFileString());
		
		Map<String, Collection<String>> errors = actionSiteGenerator.generate(
				modelURI, 
				pageTemplateURI, 
				domian, 
				outputDir,  
				workDir == null ? Files.createTempDirectory("app-site-workdir-").toFile() : workDir, 
				clean);
				
		int errorCount = 0;
		for (Entry<String, Collection<String>> ee: errors.entrySet()) {
			progressMonitor.worked(Status.ERROR, 1, ee.getKey(), ee.getValue());
			errorCount += ee.getValue().size();
		}				
		progressMonitor.worked(Status.ERROR, 1, "There are " + errorCount + " page errors");
		return Math.abs(errors.size() - errorCount);
	}

}
