package org.nasdanika.models.app.graph.emf;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.emf.EOperationConnection;
import org.nasdanika.models.app.Label;
import org.nasdanika.models.app.graph.WidgetFactory;

/**
 * Annotation for an outgoing operation builder method. 
 * The method shall have 4 or 5 parameters compatible with parameters of <p/>
 * 
 *  <code>EObjectNodeProcessor.buildOutgoingOperation(
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;			{@link EOperation} eOperation,
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;			{@link List}&lt;{@link Entry}&lt;{{@link EOperationConnection}, {@link WidgetFactory}&gt;&gt; operationOutgoingEndpoints,
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;			{@link Collection}&lt;{@link Label}&gt; labels,
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;			{@link Map}&lt;{@link EOperationConnection}, {@link Collection}&lt;{@link Label}&gt;&gt outgoingLabels,
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;			{@link ProgressMonitor} progressMonitor)</code>
 * <p/>			
 * In the case of 4 parameters it is the last 4 parameters because the operation is already bound by the annotation.			 
 * @author Pavel
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OutgoingOperationBuilder {

	/**
	 * Operation ID, obtained from EPackage constants, e.g. <code>{@link EcorePackage}.EOBJECT___ECONTAINER</code>.
	 * @return
	 */
	int value();
	
	/**
	 * Declaring class ID, obtained from EPackage constants, e.g. <code>{@link EcorePackage}.ECLASS</code>.
	 * Declaring class ID may need to be specified in situations with multiple inheritance where the reference builder
	 * method is defined in a common sub-class. 
	 * @return
	 */
	int classID() default -1; 
	
	/**
	 * Namespace URI of declaring class' {@link EPackage}, obtained from EPackage constants, e.g. <code>{@link EcorePackage}.eNS_URI</code>.
	 * Namespace URI may need to be specified in situations with multiple inheritance where the reference builder
	 * method is defined in a common sub-class. 
	 * @return
	 */	
	String nsURI() default "";
	
}

