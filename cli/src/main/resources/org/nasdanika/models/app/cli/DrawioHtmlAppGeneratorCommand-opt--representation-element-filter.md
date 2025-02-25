[Invocable URI](https://docs.nasdanika.org/core/capability/index.html#loading-invocables-from-uris) returning an instance of [RepresentationElementFilter](https://javadoc.io/doc/org.nasdanika.models.app/graph/latest/org.nasdanika.models.app.graph/org/nasdanika/models/app/graph/drawio/RepresentationElementFilter.html).

Below is a Groovy example. 

[bob-decorator.groovy](https://github.com/Nasdanika-Demos/bob-the-builder/blob/main/bob-decorator.groovy) from [Bob The Builder demo](https://nasdanika-demos.github.io/bob-the-builder/) adds a border to the Bob icon:

```groovy
import org.nasdanika.models.app.graph.drawio.RepresentationElementFilter;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.app.graph.WidgetFactory;
import org.nasdanika.capability.CapabilityFactory.Loader

// Script arguments for reference
Loader loader = args[0];
ProgressMonitor loaderProgressMonitor = args[1];
Object data = args[2]; // From fragment

new RepresentationElementFilter() {

    @Override
    void filterRepresentationElement(
            ModelElement sourceElement, 
            ModelElement representationElement,
            Map<Element, ProcessorInfo<WidgetFactory>> registry, ProgressMonitor progressMonitor) {

        if ("Bob".equals(representationElement.getLabel())) {
            representationElement.style("imageBorder", "default");
        }
    }
    
}
```
