[Bootstrap Page](https://bootstrap.models.nasdanika.org/references/eClassifiers/Page/index.html) to use as a template for site pages.

In the page template file you can:

* Modify the [theme](https://javadoc.io/doc/org.nasdanika.html/bootstrap/latest/org.nasdanika.html.bootstrap/org/nasdanika/html/bootstrap/Theme.html) - choose one of 20+ 
[Bootswatch themes](https://bootswatch.com/4/) or the default theme. Please note that generated sites look good with light themes, and not so good (in my personal opinion) with dark themes.
* Set ``fluid`` to ``true``.
* Modify [navigation panel style](https://html-app.models.nasdanika.org/references/eClassifiers/NavigationPanelStyle/index.html)

See [Bootstrap Page Load Specification](https://bootstrap.models.nasdanika.org/references/eClassifiers/Page/load-specification.html) for supported configuration keys.

Sample page template in YAML:
            
```yaml
bootstrap.Page:
  cdn: true
  theme: Simplex
  name: Application Page Template
  language: EN
  stylesheets: 
    - https://cdn.jsdelivr.net/gh/Nasdanika-Models/html-app@master/gen/web-resources/css/app.css 
    - https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css
    - https://cdn.jsdelivr.net/npm/jstree@3.3.11/dist/themes/default/style.min.css
    - https://cdn.jsdelivr.net/npm/github-markdown-css@5.1.0/github-markdown.min.css
    - https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.3.1/build/styles/default.min.css
    - https://cdn.jsdelivr.net/npm/bootstrap-vue@2.21.2/dist/bootstrap-vue.css
  scripts:
    - https://cdn.jsdelivr.net/gh/Nasdanika-Models/html-app@master/gen/web-resources/js/common.js 
    - https://cdn.jsdelivr.net/gh/Nasdanika-Models/html-app@master/gen/web-resources/js/dark-head.js 
    - https://cdn.jsdelivr.net/npm/jstree@3.3.11/dist/jstree.min.js
    - https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.3.1/build/highlight.min.js
    - https://cdn.jsdelivr.net/npm/vue@2.6.14/dist/vue.min.js
    - https://cdn.jsdelivr.net/npm/bootstrap-vue@2.21.2/dist/bootstrap-vue.min.js
    - https://cdn.jsdelivr.net/gh/Nasdanika-Models/html-app@master/gen/web-resources/js/components/table.js
    - https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js
  body:
    app.Page:
      fluid: false
      navigation-panel:
        style: SEARCHABLE_TREE # COLLAPSIBLE_CARDS
        collapsible: true
```

