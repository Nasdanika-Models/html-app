package org.nasdanika.models.app.emf;

import org.eclipse.emf.ecore.ETypedElement;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.bootstrap.TableCell;
import org.nasdanika.models.app.emf.EObjectActionResolver.Context;

/**
 * Interface for building table columns.
 * @author Pavel
 * @deprecated Use org.nasdanika.models.bootstrap.util.ColumnBuilder
 */
@Deprecated
public interface ColumnBuilder<T> {

	/**
	 * Builds a header cell.
	 * @param header
	 */
	public void buildHeader(
			TableCell header,
			Action base, 
			ETypedElement typedElement,
			Context context, 
			ProgressMonitor progressMonitor);

	/**
	 * Builds a value cell
	 */
	public void buildCell(
			T rowElement, 
			TableCell cell,
			Action base, 
			ETypedElement typedElement,
			Context context, 
			ProgressMonitor progressMonitor);
	
}
