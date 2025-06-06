package org.nasdanika.models.app.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Util;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.exec.content.Text;
import org.nasdanika.html.HTMLFactory;
import org.nasdanika.models.html.HtmlFactory;
import org.nasdanika.models.html.Script;
import org.nasdanika.models.html.Tag;
import org.nasdanika.ncore.NcoreFactory;

/**
 * Builds a dynamic (Bootstrap Vue) table.
 * @param <T> Row element type.
 * @author Pavel
 *
 */
public class DynamicTableBuilder<T> {
	
	/**
	 * Builds header and data cells.
	 * @author Pavel
	 *
	 */
	public interface ColumnBuilder<T> {
		
		/**
		 * Builds a header cell.
		 * @param progressMonitor
		 * @return Map to be converted to JSON object to pass to the table columns attribute.
		 */
		public org.nasdanika.ncore.Map buildHeader(ProgressMonitor progressMonitor);

		/**
		 * Builds a value cell and adds it to the row object (item)
		 * @param element Row element
		 * @param item Row item
		 * @param progressMonitor
		 */
		public void buildCell(T element, org.nasdanika.ncore.Map item, ProgressMonitor progressMonitor);
		
	}
	
	private Collection<ColumnBuilder<? super T>> columnBuilders = new ArrayList<>();
	
	public DynamicTableBuilder(Collection<ColumnBuilder<? super T>> columnBuilders) {
		if (columnBuilders != null) {
			this.columnBuilders.addAll(columnBuilders);
		}
	}
	
	@SafeVarargs
	public DynamicTableBuilder(ColumnBuilder<? super T>... columnBuilders) {
		for (ColumnBuilder<? super T> columnBuilder: columnBuilders) {
			this.columnBuilders.add(columnBuilder);
		}
	}	

	public DynamicTableBuilder<T> addColumnBuilders(@SuppressWarnings("unchecked") ColumnBuilder<? super T>... columnBuilders) {
		for (ColumnBuilder<? super T> columnBuilder: columnBuilders) {
			this.columnBuilders.add(columnBuilder);
		}
		return this;
	}		

	public DynamicTableBuilder<T> addColumnBuilder(ColumnBuilder<? super T> columnBuilder) {
		this.columnBuilders.add(columnBuilder);
		return this;
	}
	
	protected String tagName;
	
	public DynamicTableBuilder() {
		this("nsd-table");
	}
	
	public DynamicTableBuilder(String tagName) {
		this.tagName = tagName;
	}	
	
	public DynamicTableBuilder<T> addColumnBuilder(
			String key, 
			boolean visible, 
			boolean sortable,
			EObject headerLabel,
			Function<T, EObject> cellValueProvider) {		
		return addColumnBuilder(new ColumnBuilder<T>() {
			@Override
			public org.nasdanika.ncore.Map buildHeader(ProgressMonitor progressMonitor) {
				
				org.nasdanika.ncore.Map header = NcoreFactory.eINSTANCE.createMap();
				if (visible) {
					header.put("visible", visible);
				}
				if (sortable) {
					header.put("sortable", sortable);
				}
				header.put("key", key); 
				header.put("label", headerLabel);
				return header;
			}

			@Override
			public void buildCell(T element, org.nasdanika.ncore.Map item, ProgressMonitor progressMonitor) {
				EObject cellValue = cellValueProvider.apply(element);				
				if (cellValue != null) {
					item.put(key, cellValue); 
				}				
			}
			
		});		
	}
	
	public DynamicTableBuilder<T> addColumnBuilder(
			String key, 
			boolean visible, 
			boolean sortable,
			String headerLabel,
			Function<T, EObject> cellValueProvider) {
		
		Text text = ContentFactory.eINSTANCE.createText();
		text.setContent(headerLabel);
		return addColumnBuilder(key, visible, sortable, text, cellValueProvider);
	}
	
	public DynamicTableBuilder<T> addStringColumnBuilder(
			String key, 
			boolean visible, 
			boolean sortable,
			String headerLabel,
			Function<T, String> cellValueProvider) {
		
		Text text = ContentFactory.eINSTANCE.createText();
		text.setContent(headerLabel);
		return addColumnBuilder(key, visible, sortable, text, element -> {
			String cellValue = cellValueProvider.apply(element);
			if (cellValue == null) {
				return null;
			}
			Text txt = ContentFactory.eINSTANCE.createText();
			txt.setContent(cellValue);
			return txt;
		});
	}
	
	public DynamicTableBuilder<T> addBooleanColumnBuilder(
			String key, 
			boolean visible, 
			boolean sortable,
			String headerLabel,
			Predicate<T> cellValueProvider) {
		
		Text text = ContentFactory.eINSTANCE.createText();
		text.setContent(headerLabel);
		return addColumnBuilder(key, visible, sortable, text, element -> {
			if (cellValueProvider.test(element)) {
				Text txt = ContentFactory.eINSTANCE.createText();
				txt.setContent("<i class=\"fas fa-check\"></i>");
				return txt;
			}
			return null;
		});
	}
	
	protected Map<String,String> properties = new HashMap<>();
	
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
	
	/**
	 * Builds a dynamic configurable table.
	 * @param <T>
	 * @param elements
	 * @param columnBuilders
	 * @param base
	 * @param typedElement
	 * @param configKey Browser local storage key for table configuration.
	 * @param appId Id for the application div. Generated if null.
	 * @param context
	 * @param progressMonitor
	 * @return
	 */
	public Tag build(
			Collection<? extends T> elements,
			String configKey, 
			String appId, 
			ProgressMonitor progressMonitor) {
		org.nasdanika.ncore.List columns = NcoreFactory.eINSTANCE.createList();
		for (ColumnBuilder<? super T> cb: columnBuilders) {
			org.nasdanika.ncore.Map column = cb.buildHeader(progressMonitor);
			columns.getValue().add(column);
		}
		
		org.nasdanika.ncore.List items = NcoreFactory.eINSTANCE.createList();
		for (T element: elements) {
			org.nasdanika.ncore.Map item = NcoreFactory.eINSTANCE.createMap();
			for (ColumnBuilder<? super T> cb: columnBuilders) {
				cb.buildCell(element, item, progressMonitor);
			}
			items.getValue().add(item);
		}
		Tag table = HtmlFactory.eINSTANCE.createTag();
		table.setName(tagName);
		table.getAttributes().put(":columns", columns);
		table.getAttributes().put(":items", items);
		if (!Util.isBlank(configKey)) {
			table.getAttributes().put("config-key", createText(configKey));			
		}
		
		for (Entry<String, String> pe: properties.entrySet()) {
			table.getAttributes().put(pe.getKey(), createText(pe.getValue()));
		}
		
		Tag appDiv = HtmlFactory.eINSTANCE.createTag();
		appDiv.getContent().add(table);
		appDiv.setName("div");
		if (Util.isBlank(appId)) {
			appId = HTMLFactory.INSTANCE.nextId()+"-vue-app";
		}
		appDiv.getAttributes().put("id", createText(appId));
		
		Tag wrapperDiv = HtmlFactory.eINSTANCE.createTag();
		wrapperDiv.getContent().add(appDiv);
		wrapperDiv.setName("div");
		
		Script script = HtmlFactory.eINSTANCE.createScript();
		wrapperDiv.getContent().add(script);
		String code = "new Vue({ el: '#" + appId + "' });";
		script.setSource(createText(code));
		
		return wrapperDiv;
	}

	/**
	 * Convenience method to create Text and set content in one shot.
	 * @param content
	 * @return
	 */
	private static Text createText(String content) {
		Text text = ContentFactory.eINSTANCE.createText();
		text.setContent(content);
		return text;
	}	

}
