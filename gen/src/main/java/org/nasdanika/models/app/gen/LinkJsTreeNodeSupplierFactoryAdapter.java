package org.nasdanika.models.app.gen;

import java.util.List;

import org.nasdanika.common.Consumer;
import org.nasdanika.common.Context;
import org.nasdanika.common.ExecutionException;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Supplier;
import org.nasdanika.common.SupplierFactory;
import org.nasdanika.common.Util;
import org.nasdanika.html.HTMLFactory;
import org.nasdanika.html.Tag;
import org.nasdanika.html.jstree.JsTreeNode;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppPackage;
import org.nasdanika.models.app.Link;
import org.nasdanika.models.html.gen.ContentConsumer;

public class LinkJsTreeNodeSupplierFactoryAdapter<M extends Link> extends LabelJsTreeNodeSupplierFactoryAdapter<M> {
	
	public LinkJsTreeNodeSupplierFactoryAdapter(M link) {
		super(link);
		if (link instanceof Action) {
			throw new IllegalArgumentException("Actions must be converted to links first: " + link.getText());
		}
	}
	
	/**
	 * For links. Placing here to simplify code. This implementation returns empty supplier.
	 * @return
	 */
	protected SupplierFactory<Tag> getModalFactory() {
		return SupplierFactory.empty();
	}
	
	@Override
	public Supplier<JsTreeNode> create(Context context) {
		@SuppressWarnings("resource")
		Consumer<JsTreeNode> configurator = new Consumer<JsTreeNode>() {

			@Override
			public double size() {
				return 1;
			}

			@Override
			public String name() {
				return "Binding jsTree node";
			}

			@Override
			public void execute(JsTreeNode jsTreeNode, ProgressMonitor progressMonitor) {
				M semanticElement = getTarget();
				jsTreeNode.disabled(semanticElement.isDisabled());
				jsTreeNode.id(semanticElement.getId());
				
				if (!semanticElement.isDisabled()) {
					Tag modal = (Tag) jsTreeNode.getData(AppPackage.Literals.LINK__MODAL);
					if (modal != null) {
						if (modal.getId() == null) {
							modal.id(context.get(HTMLFactory.class, HTMLFactory.INSTANCE).nextId());
						}
						jsTreeNode.anchorAttribute("data-toggle", "modal");
						jsTreeNode.anchorAttribute("data-target", "#" + modal.getId());
			
						@SuppressWarnings("unchecked")
						List<Object> pageBody = context.get(org.nasdanika.models.html.gen.PageSupplierFactoryAdapter.PAGE_BODY_PROPERTY, List.class);
						if (pageBody != null) {
							pageBody.add(modal);
						} else {
							ContentConsumer contentConsumer = context.get(ContentConsumer.class);
							if (contentConsumer == null) {
								throw new ExecutionException("Cannot contribute a modal - there is neither page body nor content consumer in the context");
							}
							contentConsumer.accept(modal);
						}
					} else {									
						String location = context.interpolateToString(semanticElement.getLocation());
						String confirmation = context.interpolateToString(semanticElement.getConfirmation());
						if (!Util.isBlank(location)) {
							String activatorScript = "window.location='" + location + "';";
							if (Util.isBlank(confirmation)) {
								jsTreeNode.anchorAttribute("onclick", activatorScript);
							} else {
								jsTreeNode.anchorAttribute("onclick", "if (confirm('" + confirmation + "')) { " + activatorScript + " } return false;");								
							}
						} else { 
							String script = context.interpolateToString(semanticElement.getScript());
							if (!Util.isBlank(script)) {
								if (!Util.isBlank(confirmation)) {
									script = "if (confirm('" + confirmation + "')) { "+ script +" } return false;";
								}
								jsTreeNode.anchorAttribute("onclick", script);
//								anchor.style("cursor", "pointer");
							}
						}
					}
				}
			}
			
		};
		
		return super.create(context).then(configurator.asFunction());
	}	
		
}
