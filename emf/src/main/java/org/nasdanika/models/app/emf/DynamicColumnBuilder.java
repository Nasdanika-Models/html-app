package org.nasdanika.models.app.emf;

import org.eclipse.emf.ecore.ETypedElement;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.emf.EObjectActionResolver.Context;

/**
 * Interface for building table columns definitions to pass to a dynamic table.
 * @author Pavel
 *
 */
public interface DynamicColumnBuilder<T> {

	/**
	 * Builds a header cell.
	 * @param base
	 * @param typedElement
	 * @param context
	 * @param progressMonitor
	 * @return Map to be converted to JSON object to pass to the table columns attribute.
	 */
	public org.nasdanika.ncore.Map buildHeader(
			Action base, 
			ETypedElement typedElement,
			Context context, 
			ProgressMonitor progressMonitor);

	/**
	 * Builds a value cell and adds it to the row object (item)
	 * @param element 
	 * @param item
	 * @param base
	 * @param typedElement
	 * @param context
	 * @param progressMonitor
	 */
	public void buildCell(
			T element,
			org.nasdanika.ncore.Map item,
			Action base, 
			ETypedElement typedElement,
			Context context, 
			ProgressMonitor progressMonitor);
	
}
