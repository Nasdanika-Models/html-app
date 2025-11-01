## Example

``drawio diagram.drawio html-app -r root-action.yml --add-to-root site -r=-1 -F page-template.yml docs``

* Parent [drawio](../index.html) command loads ``diagram.drawio`` diagram resource
* This command:
    * Generates an [html application model](https://html-app.models.nasdanika.org/index.html) from the diagram elements
    * Loads a root action from ``root-action.yml`` resource
    * Adds the generated labels to the root action
* Passes the resulting model to the [site](site/index.html) sub-command to generate a documentation site

## Diagram element configuration 

Diagram elements can be configured with the following properties:

* ``child-comparator`` - a YAML specification for sorting child elements as explained below
* ``documentation`` - documentation text in documentation format
* ``doc-format`` - explicitly specified documentation format for ``documentation`` and ``doc-ref``. Out of the box:
    * ``markdown`` (default), 
    * ``html``
    * ``text``
* ``doc-ref`` - URI of a documentation resource resolved relative to the URI of the diagram file. Documentation format is derived from the URI extension, defaulting to ``markdown``. Use ``doc-format`` to override.
* ``icon`` - diagram element icon URL resolved relative to the diagram file. If there is no slash (``/``) in the icon name then it is treated as a CSS style, e.g. ``fas fa-user``. 
For image diagram elements icons are derived from element images. It is recommended to use SVG 20x20 OR 18x18 pixels for icons because they are also used in page titles and PNG images get blurry when scaled up.
* ``parent`` - Connection property with values ``source`` or ``target``. Use to generate documentation from mind maps where parent/child relationship is defined by connections, not by containment.
* ``prototype`` & ``proto-ref`` – YAML specification of [html application](https://html-app.models.nasdanika.org/index.html) [action](https://html-app.models.nasdanika.org/references/eClassifiers/Action/index.html), [link](https://html-app.models.nasdanika.org/references/eClassifiers/Link/index.html) or [label](https://html-app.models.nasdanika.org/references/eClassifiers/Label/index.html). See load specifications of respective model elements for supported configuration keys. If both properties are specified, ``prototype`` takes precedence over ``proto-ref``. With prototypes you can:
    * Generate complex site pages (actions) with children, navigation, sections, ...
    * Reuse action models. For example, generate an action model from one diagram and use it as a prototype for an element of another diagram. Or generate an action model for CLI or Ecore documentation.
* ``role`` - action/page role:
    * ``anonymous`` (default for connections)
    * ``child`` (default for nodes)
    * ``navigation``
    * ``section``
* ``sort-key`` - By default generated pages (actions) are sorted alphabetically by title. This property can be used to customize sorting. If it is set then pages are sorted first by the property value and then by page title.
* ``title`` - By default the element label is used as page title (action text). Use this property to explicitly set the page title. For example, for elements with long labels.

Site pages are generated only for elements with:

* Documentation (``documentation`` or ``doc-ref``), prototypes (``prototype`` or ``prototype-ref``), or both.
* Labels or ``title`` property. If you don't want an element label to be visible (e.g. on connections) uncheck "Font color" checkbox on the "Text" tab.

### child-comparator

The specification can be a list, a string, or a map. Lists are treated as maps with null values and strings are treated as singleton maps with null values.

Map entry keys are comparator names and values are configurations. The following comparators are supported:

* ``enumerate`` and ``reverse-enumerate`` order model elements using enumerate value. 
Elements without enumerate value are considered equal to any other elements including those
with enumerate value. 
This is done to allow chaining with, say, flow comparator. 
As a result, this comparator will violate the transitivity requirement if some elements don't have enumerate value. 
Therefore, it shall be chained with other comparators. For example, flow and then position or label.    
Enumerate value is treated as path of dot-separated values and two enumerate values are compared
element-by-element with elements containing only digits parsed and compared as integers.
For example, ``20`` would be greater than ``3``, ``1.1.1`` would be greater than ``1.1`` and smaller than ``2.5.6`` or ``3``.
Numbers are considered smaller than strings ``1.12`` is smaller than ``1.a``
Practical use - ordering connections emanating from the same node. Say, excursions from the same location.  
If those excursions have multiple segments, then this comparator can be chained with the flow comparator
and possibly terminated by the position or label comparator just in case.
* ``flow`` and ``reverse-flow`` order elements based on how they are connected to each other. These comparators configuration is either null, string or a list of strings. Strings are evaluated as [Spring Expression Language](https://docs.spring.io/spring-framework/reference/core/expressions.html) (SpEL) boolean expressions in the context of connections. If the expression evaluates to ``false``, then the given connection is not included in the flow traversal. It can be used if there are loops. For example, you plan a trip and want the destinations to be sorted in the visit order. There is a loop - the returning flight home. You can provide an expression which evaluates to ``false`` for that flight.
* ``position`` and ``reverse-position`` order elements by their position in the container's children list (z-order).  
* ``down-right`` - Compares nodes by their vertical order first with higher nodes being smaller and then by horizontal order with nodes on the left being smaller. Nodes are considered vertically equal if they vertically overlap. This comparator can be used for org. charts.
* ``left-down`` - Compares nodes by their horizontal order first with nodes on the right being smaller and then by vertical order with higher nodes being smaller. Nodes are considered horizontally equal if they horizontally overlap. 
* ``left-up`` - Compares nodes by their horizontal order first with nodes on the right being smaller and then by vertical order with lower nodes being smaller. Nodes are considered horizontally equal if they horizontally overlap. 
* ``right-down`` - Compares nodes by their horizontal order first with nodes on the left being smaller and then by vertical order with higher nodes being smaller. Nodes are considered horizontally equal if they horizontally overlap. 
* ``right-up`` - Compares nodes by their horizontal order first with nodes on the left being smaller and then by vertical order with lower nodes being smaller. Nodes are considered horizontally equal if they horizontally overlap. 
* ``up-left`` - Compares nodes by their vertical order first with lower nodes being smaller and then by horizontal order with nodes on the right being smaller. Nodes are considered vertically equal if they vertically overlap. 
* ``up-right`` - Compares nodes by their vertical order first with lower nodes being smaller and then by horizontal order with nodes on the left being smaller. Nodes are considered vertically equal if they vertically overlap. 

### icon

Drawio has more than two thousand built-in icons. You can find more icons on the below sites:

* [Font Awesome 5 icons](https://fontawesome.com/v5/search?ic=free) 
* [FlatIcons](https://www.flaticon.com/pricing) - 16.8 million icons. Free with attribution, a paid plan is available - no attribution. In-browser editor. PNG and SVG (premium) formats.
* [IconFinder](https://www.iconfinder.com/) - the world's largest marketplace for icons, ... 6 million icons at your fingertips including free icons. Multiple subscription levels.
* [Iconduck](https://iconduck.com/) - more than 250 000 free open source icons
* [Techicons](https://techicons.dev/) - SVG and PNG tech icons, sourced from https://github.com/devicons/devicon. 473 icons. 
* Cloud providers:
    * [Azure](https://learn.microsoft.com/en-us/azure/architecture/icons/)
    * [AWS](https://aws.amazon.com/architecture/icons/)  

### prototype

If your top-level diagram page is generated as a principal action and the root action or link references some external site, e.g. your company site, then you need to use the following prototype for proper generation:

```yaml
Action:
  location: ${base-uri}index.html
```  

Below is an example of a prototype which defines a navigation action:

```yaml
Action:
    navigation:
      - Action:
          location: about-wendy.html
          icon: fas fa-help
          text: About
          content:
            Interpolator:
              source:
                exec.content.Text: |    
                  This is an example of a page from an action prototype.
```

### proto-ref

Property value is a URI of the prototype action or label resolved relative to the element base URI.
For example ``bob-prototype.yml#/``.
    
### Element ID 

Element IDs are used to construct element page URLs. 
Element IDs are generated as long random strings. 
They are editable - double-click on the ID at the top of the data dialog.
So, if you'd like to have semantic URLs - customize the IDs.      

## Page and element links

You may link elements to pages and other element using the [extended link syntax](https://docs.nasdanika.org/core/drawio/index.html#page-and-element-links).

When an element links to a page, the page root[^api] is logically merged with the linking element and page elements (except elements liking to other elements) become logical children of the linking element.

If an element links to another element, then that element is not considered a logical child of the page/root/page linking element. 
The link chain is traversed and the diagram element on the generated site is linked to the page of the link target element if that element has a page (i.e. it is documented). Otherwise there is no link.

[^api]: See [Drawio API](https://docs.nasdanika.org/core/drawio/index.html#api)

## Multiple top-level pages

A top-level diagram page is a page that is not linked from any diagram element. 
If there is more than one top-level page, add a principal action to the ``root-action.yml`` and link pages to the principal action.

Examples:

* [Beyond Diagrams Illustrations](https://nasdanika-demos.github.io/beyond-diagrams/index.html)
* [Declarative Command Pipelines](https://nasdanika-demos.github.io/declarative-command-pipelines/)
