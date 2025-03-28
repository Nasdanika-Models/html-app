Nasdanika application model provides model elements for building HTML applications such as static web sites, dynamic applications, and combinations of thereof.

Application models can be:

* Defined manually, 
* Created programmatically, for example generated from other models. [Sample Family](https://family.models.nasdanika.org/demos/mapping/) is an example of an action model generated from a [Family](https://family.models.nasdanika.org/) model.
* Loaded from [Drawio](https://www.diagrams.net/) diagrams as explained in [Beyond Diagrams](https://leanpub.com/beyond-diagrams) book.
 
These approaches can be combined. 

[TOC levels=6]

### API Documentation

* [Model](https://javadoc.io/doc/org.nasdanika.models.app/model)
* [CLI](https://javadoc.io/doc/org.nasdanika.models.app/cli)
* [EMF](https://javadoc.io/doc/org.nasdanika.models.app/emf)
* [Gen](https://javadoc.io/doc/org.nasdanika.models.app/gen)
* [Graph](https://javadoc.io/doc/org.nasdanika.models.app/graph)


### Concepts

With Nasdanika App model user interface is constructed from [actions](references/eClassifiers/Action/index.html) organized in a hierarchy.
Users activate actions to get results (action contents).

This approach allows developers to focus on the functionality of their application in terms of actions and select action placement in the UI and page styling/themes later.
Changing of the action placement (e.g. from child to navigation or section) does not change the application functionality, just the appearance. 
It allows to adjust the application as it evolves.
For example, an action of a documentation site might be in a section collection if it doesn’t have a lot of content and then can be moved to the children collection if it becomes large enough to be a page on its own.

#### Action UI life cycle

In the UI an action goes through the following stages:

* Display - an action link is displayed to the user. The link may have an icon, text or both. It may have a tooltip. Inline actions don’t go through this stage - they go directly to the last stage. Some actions are always visible to the user, e.g. the root and principal actions (see below). Some are only shown in a context of another active action, e.g. navigation and section actions.
* Activation - user clicks on the action link. Actions may have URL, script, and modal activators. URL activator navigates to a URL, script activator executes a script, and a modal activator shows action content in a [modal dialog](https://getbootstrap.com/docs/4.6/components/modal/).
* Display of the action content. At this stage the action content is displayed to the user. For inline actions content is displayed where the link is displayed for non-inline actions. If an action has a modal activator, then its content is displayed in a modal dialog. Otherwise, action's content is displayed in the content panel.

Section actions do not have activators and are displayed in the content panel of their parent action.

#### Action types

An application page is generated from 3 actions:

* Root action - the root of the action hierarchy. It is displayed in the header with its children, except the first one, displayed in the root navigation bar on the top right. On this page “Nasdanika Models” is the root action. Navigation actions of the root action are displayed in the footer.
* Principal action - by default the first child of the root action. Displayed in the navigation bar brand on the left. On this page “HTML Action Model” is the principal action. Navigation actions of the principal action are displayed in the navigation bar on the right of the principal action. Children of the principal action are displayed recursively in the navigation panel. On this page it is a list of package classifiers - Action, Link, ...
* Active action - any action in the hierarchy which is currently “active”, i.e. it’s content is displayed in the content panel and it is shown as active/selected in the navigation panel or navigation bar. On this page Products/HTML/Models/Application/Model is the active action - it is selected in the navigation tree and your are reading its content. Active action path is displayed in the breadcrumb.
* Navigation actions of the active action are displayed in in the active action navigation bar to the right of the action title.
* Section actions are displayed as page sections with their navigation actions displayed in section navigation bars to the right of section titles (for paragraph section style).
* Content/Section (float) left actions are displayed in the left content/section (float) navigation panel.
* Content/Section (float) right actions are displayed in the right content/section (float) navigation panel.

### Page structure

This section provides an overview of the page structure. 
You can hover over the diagram elements to see tooltips and click on the diagram elements to navigate to corresponding documentation sections.
You may also hide/show diagram layers.

```drawio-resource
page-structure.drawio
```

#### Header

Displays the root action's icon and text.
Click on the icon/text activates the root action, if it has an activator.

##### Root navigation

Displays 1+ children of the root action - siblings of the principal action.

#### Principal

Displays the principal's action icon and text. 
Click on the icon/text activates the principal action, if it as an activator.

##### Principal navigation

Displays navigation actions of the principal action.

#### Navigation Panel

Displays children of the principal action. 
[Navigation panels](references/eClassifiers/NavigationPanel/index.html) can display actions in several modes - list, tree, cards, cards with list or tree. Cards can be collapsible.
This page uses collapsible cards with trees.

#### Content Panel

Displays content of the active action.

##### Breadcrumb

Containment path of the active action. 

##### Title

Icon and text of the active action.

##### Active action navigation

Displays navigation actions of the active action. 
If a navigation action has children and no activator it is rendered as a drop-down.
This also applies to the root, footer, and section navigation actions. 

##### Content left navigation panel

Displays ``leftNavigation`` actions of the active action. 
Left navigation panel is displayed in a bootstrap column. 
It can be used to display, for example, a table of contents of the active action - the hierarchy of sections.

##### Content float left navigation panel

Displays ``floatLeftNavigation`` actions of the active action. 
Left navigation panel is displayed in a div with float style. 
Similarly to the content left panel it can be used to display, for example, a table of contents of the active action - the hierarchy of sections.

##### Content right navigation panel

Displays ``rightNavigation`` actions of the active action. 
Right navigation panel is displayed in a bootstrap column. 
It can be used to display, for example, a list of useful links.

##### Content float right navigation panel

Displays ``floatRightNavigation`` actions of the active action. 
Right navigation panel is displayed in a div with float style. 
Similarly to the content right panel it can be used to display, for example, a list of useful links.

##### Section

Actions may have zero or more sections. 
Action may define how sections shall be displayed by specifying [SectionStyle](references/eClassifiers/SectionStyle/index.html).
Sections can be nested.
A section is rendered in the same way as the active action, but without breadcrumb.

###### Section title

Icon and text of the section action.

###### Section navigation

Displays navigation actions of the section action. 

###### Section left navigation panel

Displays ``leftNavigation`` actions of the section action. 
Left navigation panel is displayed in a bootstrap column. 

###### Section float left navigation panel

Displays ``floatLeftNavigation`` actions of the section action. 
Left navigation panel is displayed in a div with float style. 

###### Section right navigation panel

Displays ``rightNavigation`` actions of the section action. 
Right navigation panel is displayed in a bootstrap column. 

###### Section float right navigation panel

Displays ``floatRightNavigation`` actions of the section action. 
Right navigation panel is displayed in a div with float style. 

#### Footer

Displays navigation actions of the root action. 

### Generation

HTML pages are generated from actions by combining the action model with a [page](references/eClassifiers/Page/index.html) template. 
[Example of a page template](https://github.com/Nasdanika/html/blob/master/model/app.gen/src/test/resources/org/nasdanika/html/model/app/gen/tests/app/page-template.yml)

#### Static sites

Static sites are generated by traversing the action model and generating pages for action with location matching a specific condition, e.g. 
located under a specified output directory.

Static sites can be generated programmatically with [SiteGenerator](https://javadoc.io/doc/org.nasdanika.html/app-model-gen/latest/org.nasdanika.html.model.app.gen/org/nasdanika/html/model/app/gen/SiteGenerator.html) class and its subclasses.  

There are also [Site Generator Maven Plugins](https://javadoc.io/doc/org.nasdanika.html/site-generator-maven-plugin/latest/org.nasdanika.html.model.app.gen.maven/org/nasdanika/html/model/app/gen/maven/package-summary.html)

#### Dynamic behavior

This section explains how to create a dynamic web application with Nasdanika action models.
The dynamic behavior approaches can be combined with each other and with static web site generation.

##### Server-side

Server-side dynamic behavior can be implemented by creating a servlet or another type of Java HTTP request processor, e.g. a Spring RestController
or Netty handler.

An action model can be generated or loaded from some resource and stored in HTTP session. 
Then individual pages would be generated on access. 

The action model can be re-generated on specific events, e.g. user log-in/log-out or changes in data which affect the model content.
If the action model is highly dynamic, it can be generated for every request instead of caching it in a session.

The dynamic part of a page (page content) can be injected via a context property. 
Some other options:

* Generate a page with a replacement token in the content panel. Cache the page in session. During request processing use String replace.
* Generate a page with an empty content panel. Parse the page using [Jsoup](https://jsoup.org/) and store the parsed document and the content panel element in session. During request processing replace content of the content panel element with dynamic content, write to response.

If the action model is constant and only the content panel shall be dynamic, then the server-side dynamic generation can be combined with static site generation: 
the static part would be hosted on a web server such as [Apache HTTPD](https://httpd.apache.org/) and dynamic requests would be forwarded to a servlet container. 

##### Client-side

Another option to introduce dynamic behavior is [single page applications](https://en.wikipedia.org/wiki/Single-page_application), e.g. built with:

* [Vue.js](https://vuejs.org/) and [BootstrapVue](https://bootstrap-vue.org/). The [search](search.html) and [glossary](glossary.html) pages of this site are created using this approach - the generation process creates ``search-documents.js`` file used by a Vue applications.
* [React](https://reactjs.org/) and [React Bootstrap](https://react-bootstrap.github.io/).

The client-side approach may be combined with the server-side approach - instead of application pages the server side would generate json responses used by the client side. 
Or it may generate both - an application page with a Vue/React application on it possibly parameterized during generation, and then json responses with data for the application. The server side may provide update endpoints for the single-page application as well.
