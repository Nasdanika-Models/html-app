module org.nasdanika.models.app.graph {
		
	requires transitive org.nasdanika.models.app.gen;
	requires transitive org.nasdanika.graph;
	requires org.apache.commons.text;
	requires org.eclipse.emf.ecore.xmi;
	requires org.nasdanika.drawio;
	
	exports org.nasdanika.models.app.graph;
	exports org.nasdanika.models.app.graph.drawio;
	exports org.nasdanika.models.app.graph.emf;
	
	opens org.nasdanika.models.app.graph.drawio to org.nasdanika.common;
	opens org.nasdanika.models.app.graph.emf to org.nasdanika.common;
	
}
