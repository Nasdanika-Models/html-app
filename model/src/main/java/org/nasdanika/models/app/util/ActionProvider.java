package org.nasdanika.models.app.util;

import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.models.app.Action;

/**
 * Adapter interface. Creates an action for the target.
 * Implementations shall call the argument {@link BiConsumer} passing the target and resulting action as argument to use
 * later in resolve() phase.
 * @author Pavel
 * @deprecated Migrate to LabelProvider
 *
 */
public interface ActionProvider extends org.nasdanika.common.Function<BiConsumer<EObject,Action>,Action> {

}
