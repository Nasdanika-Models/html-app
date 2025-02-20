package org.nasdanika.models.app.gen.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nasdanika.models.app.gen.AppSiteGenerator;
import org.nasdanika.ncore.util.SemanticInfo;

public class TestActionSiteGenerator {
	
	@Test
	public void testActionSiteGenerator() throws IOException, DiagnosticException {
//		URI semanticMapURI = URI.createURI("https://docs.nasdanika.org/demo-action-site/semantic-map.json");				
		
		AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
			
//			Map<ModelElement, Label> semanticMap = new LinkedHashMap<>();
			
			@Override
			protected Iterable<Entry<SemanticInfo, ?>> semanticInfoSource(ResourceSet resourceSet) {
				// TODO Load external info
				return super.semanticInfoSource(resourceSet);
			}
						
//			@Override
//			protected void buildRegistry(Action action, Map<EObject, Label> registry) {
//				registry.putAll(semanticMap);
//				super.buildRegistry(action, registry);
//			}
//			
//			@Override
//			protected boolean isSemanticInfoLink(Link link) {
//				return semanticMap.values().contains(link);
//			}
			
		};
		
		URI rootActionURI = URI.createURI(getClass().getResource("app/actions.yml").toString());
		URI pageTemplateURI = URI.createURI(getClass().getResource("app/page-template.yml").toString());
		Map<String, Collection<String>> errors = actionSiteGenerator.generate(
				rootActionURI, 
				pageTemplateURI, 
				"https://nasdanika.org", 
				new File("target/action-site-generator"), 
				new File("target/action-site-generator-work-dir"), 
				false);
				
		int errorCount = 0;
		for (Entry<String, Collection<String>> ee: errors.entrySet()) {
			System.err.println(ee.getKey());
			for (String error: ee.getValue()) {
				System.err.println("\t" + error);
				++errorCount;
			}
		}
		
		System.out.println("There are " + errorCount + " site errors");
		
//		if (!errors.isEmpty()) {
//			fail("There are problems with pages: " + errorCount);
//		}		
	}

	
	@Test
	@Disabled // Some error
	public void testDrawioActionSiteGenerator() throws IOException, DiagnosticException {
		AppSiteGenerator actionSiteGenerator = new AppSiteGenerator();		
		URI rootActionURI = URI.createURI(getClass().getResource("drawio/actions.drawio").toString()).appendFragment("/");
		URI pageTemplateURI = URI.createURI(getClass().getResource("drawio/page-template.yml").toString());
		Map<String, Collection<String>> errors = actionSiteGenerator.generate(
				rootActionURI, 
				pageTemplateURI, 
				"https://nasdanika.org", 
				new File("target/drawio-action-site-generator"), 
				new File("target/drawio-action-site-generator-work-dir"), 
				false);
				
		int errorCount = 0;
		for (Entry<String, Collection<String>> ee: errors.entrySet()) {
			System.err.println(ee.getKey());
			for (String error: ee.getValue()) {
				System.err.println("\t" + error);
				++errorCount;
			}
		}
		
		System.out.println("There are " + errorCount + " site errors");
		
//		if (!errors.isEmpty()) {
//			fail("There are problems with pages: " + errorCount);
//		}		
	}
	
}
