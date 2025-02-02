/**
 */
package org.nasdanika.models.app;

import org.eclipse.emf.common.util.URI;
import org.nasdanika.common.Util;
import org.nasdanika.models.bootstrap.Modal;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.nasdanika.models.app.Link#getLocation <em>Location</em>}</li>
 *   <li>{@link org.nasdanika.models.app.Link#getScript <em>Script</em>}</li>
 *   <li>{@link org.nasdanika.models.app.Link#getModal <em>Modal</em>}</li>
 *   <li>{@link org.nasdanika.models.app.Link#getName <em>Name</em>}</li>
 *   <li>{@link org.nasdanika.models.app.Link#getConfirmation <em>Confirmation</em>}</li>
 *   <li>{@link org.nasdanika.models.app.Link#getTarget <em>Target</em>}</li>
 *   <li>{@link org.nasdanika.models.app.Link#getAction <em>Action</em>}</li>
 * </ul>
 *
 * @see org.nasdanika.models.app.AppPackage#getLink()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='activator'"
 * @generated
 */
public interface Link extends Label {
	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(String)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Location()
	 * @model
	 * @generated
	 */
	String getLocation();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getLocation <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(String value);

	/**
	 * Returns the value of the '<em><b>Script</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Script</em>' attribute.
	 * @see #setScript(String)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Script()
	 * @model annotation="urn:org.nasdanika exclusive-with='location'"
	 * @generated
	 */
	String getScript();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getScript <em>Script</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Script</em>' attribute.
	 * @see #getScript()
	 * @generated
	 */
	void setScript(String value);

	/**
	 * Returns the value of the '<em><b>Modal</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modal</em>' containment reference.
	 * @see #setModal(Modal)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Modal()
	 * @model containment="true"
	 *        annotation="urn:org.nasdanika homogeneous='true' exclusive-with='location script'"
	 * @generated
	 */
	Modal getModal();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getModal <em>Modal</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Modal</em>' containment reference.
	 * @see #getModal()
	 * @generated
	 */
	void setModal(Modal value);

	/**
	 * Returns the value of the '<em><b>Confirmation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Confirmation</em>' attribute.
	 * @see #setConfirmation(String)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Confirmation()
	 * @model
	 * @generated
	 */
	String getConfirmation();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getConfirmation <em>Confirmation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Confirmation</em>' attribute.
	 * @see #getConfirmation()
	 * @generated
	 */
	void setConfirmation(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Name()
	 * @model annotation="urn:org.nasdanika exclusive-with='script modal location'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' attribute.
	 * @see #setTarget(String)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Target()
	 * @model
	 * @generated
	 */
	String getTarget();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getTarget <em>Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' attribute.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(String value);

	/**
	 * Returns the value of the '<em><b>Action</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action</em>' reference.
	 * @see #setAction(Action)
	 * @see org.nasdanika.models.app.AppPackage#getLink_Action()
	 * @model annotation="urn:org.nasdanika exclusive-with='script modal location'"
	 * @generated
	 */
	Action getAction();

	/**
	 * Sets the value of the '{@link org.nasdanika.models.app.Link#getAction <em>Action</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Action</em>' reference.
	 * @see #getAction()
	 * @generated
	 */
	void setAction(Action value);
	
	/**
	 * If link has a relative location the location is rebased by resolving against <code>from</code> and then deresolving against <code>to</code>
	 * @param from
	 * @param to
	 */
	@Override
	default void rebase(URI from, URI to) {
		String location = getLocation();
		if (!Util.isBlank(location)) {						
			URI locationURI = URI.createURI(location);
			if (from != null && !from.isRelative() && locationURI.isRelative()) {
				locationURI = locationURI.resolve(from);
			}						
			if (to != null && !to.isRelative()) {
				locationURI = locationURI.deresolve(to, true, true, true);
			}
			setLocation(locationURI.toString());
		}		
	}

} // Link
