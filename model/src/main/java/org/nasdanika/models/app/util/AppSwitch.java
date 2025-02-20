/**
 */
package org.nasdanika.models.app.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;
import org.nasdanika.common.Adaptable;
import org.nasdanika.models.bootstrap.BootstrapElement;
import org.nasdanika.models.bootstrap.Item;
import org.nasdanika.models.html.HtmlElement;
import org.nasdanika.models.app.*;
import org.nasdanika.ncore.Marked;
import org.nasdanika.ncore.ModelElement;
import org.nasdanika.ncore.Reference;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.nasdanika.models.app.AppPackage
 * @generated
 */
public class AppSwitch<T1> extends Switch<T1> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static AppPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AppSwitch() {
		if (modelPackage == null) {
			modelPackage = AppPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T1 doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case AppPackage.LABEL: {
				Label label = (Label)theEObject;
				T1 result = caseLabel(label);
				if (result == null) result = caseBootstrapElement(label);
				if (result == null) result = caseItem(label);
				if (result == null) result = caseHtmlElement(label);
				if (result == null) result = caseModelElement(label);
				if (result == null) result = caseMarked(label);
				if (result == null) result = caseAdaptable(label);
				if (result == null) result = caseIMarked(label);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.LINK: {
				Link link = (Link)theEObject;
				T1 result = caseLink(link);
				if (result == null) result = caseLabel(link);
				if (result == null) result = caseBootstrapElement(link);
				if (result == null) result = caseItem(link);
				if (result == null) result = caseHtmlElement(link);
				if (result == null) result = caseModelElement(link);
				if (result == null) result = caseMarked(link);
				if (result == null) result = caseAdaptable(link);
				if (result == null) result = caseIMarked(link);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.PAGE: {
				Page page = (Page)theEObject;
				T1 result = casePage(page);
				if (result == null) result = caseBootstrapElement(page);
				if (result == null) result = caseHtmlElement(page);
				if (result == null) result = caseModelElement(page);
				if (result == null) result = caseMarked(page);
				if (result == null) result = caseAdaptable(page);
				if (result == null) result = caseIMarked(page);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.PAGE_PART: {
				PagePart pagePart = (PagePart)theEObject;
				T1 result = casePagePart(pagePart);
				if (result == null) result = caseBootstrapElement(pagePart);
				if (result == null) result = caseHtmlElement(pagePart);
				if (result == null) result = caseModelElement(pagePart);
				if (result == null) result = caseMarked(pagePart);
				if (result == null) result = caseAdaptable(pagePart);
				if (result == null) result = caseIMarked(pagePart);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.HEADER: {
				Header header = (Header)theEObject;
				T1 result = caseHeader(header);
				if (result == null) result = casePagePart(header);
				if (result == null) result = caseBootstrapElement(header);
				if (result == null) result = caseHtmlElement(header);
				if (result == null) result = caseModelElement(header);
				if (result == null) result = caseMarked(header);
				if (result == null) result = caseAdaptable(header);
				if (result == null) result = caseIMarked(header);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.NAVIGATION_BAR: {
				NavigationBar navigationBar = (NavigationBar)theEObject;
				T1 result = caseNavigationBar(navigationBar);
				if (result == null) result = casePagePart(navigationBar);
				if (result == null) result = caseBootstrapElement(navigationBar);
				if (result == null) result = caseHtmlElement(navigationBar);
				if (result == null) result = caseModelElement(navigationBar);
				if (result == null) result = caseMarked(navigationBar);
				if (result == null) result = caseAdaptable(navigationBar);
				if (result == null) result = caseIMarked(navigationBar);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.NAVIGATION_PANEL: {
				NavigationPanel navigationPanel = (NavigationPanel)theEObject;
				T1 result = caseNavigationPanel(navigationPanel);
				if (result == null) result = casePagePart(navigationPanel);
				if (result == null) result = caseBootstrapElement(navigationPanel);
				if (result == null) result = caseHtmlElement(navigationPanel);
				if (result == null) result = caseModelElement(navigationPanel);
				if (result == null) result = caseMarked(navigationPanel);
				if (result == null) result = caseAdaptable(navigationPanel);
				if (result == null) result = caseIMarked(navigationPanel);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.CONTENT_PANEL: {
				ContentPanel contentPanel = (ContentPanel)theEObject;
				T1 result = caseContentPanel(contentPanel);
				if (result == null) result = casePagePart(contentPanel);
				if (result == null) result = caseBootstrapElement(contentPanel);
				if (result == null) result = caseHtmlElement(contentPanel);
				if (result == null) result = caseModelElement(contentPanel);
				if (result == null) result = caseMarked(contentPanel);
				if (result == null) result = caseAdaptable(contentPanel);
				if (result == null) result = caseIMarked(contentPanel);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.FOOTER: {
				Footer footer = (Footer)theEObject;
				T1 result = caseFooter(footer);
				if (result == null) result = casePagePart(footer);
				if (result == null) result = caseBootstrapElement(footer);
				if (result == null) result = caseHtmlElement(footer);
				if (result == null) result = caseModelElement(footer);
				if (result == null) result = caseMarked(footer);
				if (result == null) result = caseAdaptable(footer);
				if (result == null) result = caseIMarked(footer);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.ACTION: {
				Action action = (Action)theEObject;
				T1 result = caseAction(action);
				if (result == null) result = caseLink(action);
				if (result == null) result = caseLabel(action);
				if (result == null) result = caseBootstrapElement(action);
				if (result == null) result = caseItem(action);
				if (result == null) result = caseHtmlElement(action);
				if (result == null) result = caseModelElement(action);
				if (result == null) result = caseMarked(action);
				if (result == null) result = caseAdaptable(action);
				if (result == null) result = caseIMarked(action);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case AppPackage.ACTION_REFERENCE: {
				ActionReference actionReference = (ActionReference)theEObject;
				T1 result = caseActionReference(actionReference);
				if (result == null) result = caseReference(actionReference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Label</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Label</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLabel(Label object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Link</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Link</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLink(Link object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Page</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Page</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 casePage(Page object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Page Part</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Page Part</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 casePagePart(PagePart object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Header</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Header</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseHeader(Header object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Navigation Bar</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Navigation Bar</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseNavigationBar(NavigationBar object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Navigation Panel</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Navigation Panel</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseNavigationPanel(NavigationPanel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Content Panel</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Content Panel</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseContentPanel(ContentPanel object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Footer</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Footer</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseFooter(Footer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseAction(Action object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Action Reference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Action Reference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseActionReference(ActionReference object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IMarked</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IMarked</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIMarked(org.nasdanika.persistence.Marked object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Marked</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Marked</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseMarked(Marked object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Adaptable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Adaptable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseAdaptable(Adaptable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Model Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Model Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseModelElement(ModelElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseHtmlElement(HtmlElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseBootstrapElement(BootstrapElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Item</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseItem(Item object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <T> T1 caseReference(Reference<T> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T1 defaultCase(EObject object) {
		return null;
	}

} //AppSwitch
