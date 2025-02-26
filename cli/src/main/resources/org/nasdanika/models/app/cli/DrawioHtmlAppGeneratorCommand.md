## Example

``drawio diagram.drawio html-app -r root-action.yml --add-to-root site -r=-1 -F page-template.yml docs``

* Parent [drawio](../index.html) command loads ``diagram.drawio`` diagram resource
* This command:
    * Generates an [html application model](https://html-app.models.nasdanika.org/index.html) from the diagram elements
    * Loads a root action from ``root-action.yml`` resource
    * Adds the generated labels to the root action
* Passes the resulting model to the [site](site/index.html) sub-command to generate a documentation site