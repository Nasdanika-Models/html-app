package org.nasdanika.models.app.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.nasdanika.common.Status;

/**
 * Tests of descriptor view parts and wizards.
 * @author Pavel
 *
 */
public class TestPage extends TestBase {
	
	@Test
	public void test() throws Exception {	
		load(
				"page.yml", 
				obj -> {
					org.nasdanika.models.bootstrap.Page page = (org.nasdanika.models.bootstrap.Page) obj;
					assertThat(page.getBody()).hasSize(1);
					org.nasdanika.models.app.Page appPage = (org.nasdanika.models.app.Page) page.getBody().get(0);
					assertThat(appPage.getHeader()).isNotNull();
				},
				diagnostic -> {
					assertThat(diagnostic.getStatus()).isEqualTo(Status.SUCCESS);
				});		
	}
	
}
