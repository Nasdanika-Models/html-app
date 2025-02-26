## Example

``drawio diagram.drawio html-app -r root-action.yml --add-to-root site -r=-1 -F page-template.yml docs``

* Grandparent ``drawio`` command loads ``diagram.drawio`` diagram resource
* Parent ``html-app`` command generates an [html application model](https://html-app.models.nasdanika.org/index.html) from the diagram elements
* This command:
    * Loads a page template from ``page-template.yml``
    * Generates a documentation site ignoring page errors (``-r=-1``)
    * Outputs the generated site to the ``docs`` directory