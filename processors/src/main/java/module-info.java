module org.nasdanika.models.app.processors {
		
	requires transitive org.nasdanika.html.model.app;
	requires transitive org.nasdanika.models.ecore.graph;
	
	exports org.nasdanika.models.app.processors;
	opens org.nasdanika.models.app.processors; // For loading resources
	
}
