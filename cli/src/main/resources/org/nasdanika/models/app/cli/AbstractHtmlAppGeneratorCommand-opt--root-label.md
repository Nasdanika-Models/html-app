[Label](https://html-app.models.nasdanika.org/references/eClassifiers/Label/index.html) or its subclass, e.g. [Action](https://html-app.models.nasdanika.org/references/eClassifiers/Action/index.html)
to use as the root of the [HTML Application](https://html-app.models.nasdanika.org/index.html) model.

If specified, labels generated from the diagram are added to the principal action, which is the first child of the root label, if it is present.
If the principal action is not present or ``-R, --add-to-root`` option is set, then the generated labels are added to the root label as children.

#### Example

```yaml
Action:
    icon: https://docs.nasdanika.org/images/nasdanika-logo.png
    text: Nasdanika Templates
    location: https://github.com/Nasdanika-Templates
    children:
      - Action:
          location: ${base-uri}search.html
          icon: fas fa-search
          text: Search
          content:
            Interpolator:
              source:
                exec.content.Resource: classpath://org/nasdanika/models/app/gen/search.html
      - Action:
          location: ${base-uri}glossary.html
          text: Glossary
          content:
            Interpolator:
              source:
                exec.content.Resource: classpath://org/nasdanika/models/app/gen/semantic-info.html
    navigation:
      - Action:
          text: Source
          icon: fab fa-github
          location: https://github.com/Nasdanika-Templates/drawio-site  
```

