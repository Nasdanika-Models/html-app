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

@EClassifierNodeProcessorFactory(classifierID = AppPackage.SECTION_STYLE)
public class SectionStyleProcessorFactory {
	
	private Context context;

	public SectionStyleProcessorFactory(Context context) {
		this.context = context;
	}

//	AUTO(0, "Auto", "Auto"),
//	ACTION_GROUP(1, "ActionGroup", "ActionGroup"),
//	CARD(2, "Card", "Card"),
//	CARD_PILL(3, "CardPill", "CardPill"),
//	CARD_TAB(4, "CardTab", "CardTab"),
//	HEADER(5, "Header", "Header"),
//	PILL(6, "Pill", "Pill"),
//	TAB(7, "Tab", "Tab"),
//	TABLE(8, "Table", "Table");
	
} //SectionStyle
