/**
 */
package org.nasdanika.models.app.processors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;
import org.nasdanika.common.Context;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.models.ecore.graph.processors.EClassifierNodeProcessorFactory;

@EClassifierNodeProcessorFactory(classifierID = AppPackage.NAVIGATION_PANEL_STYLE)
public class NavigationPanelStyleProcessorFactory {
	
	private Context context;

	public NavigationPanelStyleProcessorFactory(Context context) {
		this.context = context;
	}

//	AUTO(0, "Auto", "Auto"),
//	CARDS(1, "Cards", "Cards"),
//	COLLAPSIBLE_CARDS(2, "CollapsibleCards", "CollapsibleCards"), 
//	TREE(3, "Tree", "Tree"), 
//	SEARCHABLE_TREE(4, "SearchableTree", "SearchableTree");
	
} //NavigationPanelStyle
