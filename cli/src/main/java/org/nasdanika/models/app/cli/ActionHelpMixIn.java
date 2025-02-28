package org.nasdanika.models.app.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.nasdanika.cli.Description;
import org.nasdanika.cli.HelpCommand;
import org.nasdanika.cli.ParentCommands;
import org.nasdanika.common.DefaultConverter;
import org.nasdanika.common.DocumentationFactory;
import org.nasdanika.common.MarkdownHelper;
import org.nasdanika.common.NullProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Util;
import org.nasdanika.exec.content.ContentFactory;
import org.nasdanika.exec.content.Text;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.AppFactory;

import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.IHelpSectionRenderer;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.IAnnotatedElementProvider;
import picocli.CommandLine.Model.IGetter;
import picocli.CommandLine.Model.ISetter;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import picocli.CommandLine.Model.UsageMessageSpec;
import picocli.CommandLine.Option;


@ParentCommands(HelpCommand.class)
public class ActionHelpMixIn implements HelpCommand.OutputFormatMixIn {
	
	private CommandLine rootCommand;

	public ActionHelpMixIn(CommandLine rootCommand) {
		this.rootCommand = rootCommand;
	}
		
	@Option(names = {"-a", "--action-model"}, description = "Output to action model")
	private boolean actionModel;

	@Override
	public boolean match() {
		return actionModel;
	}

	@Override
	public void write(OutputStream out) throws IOException {
		Action rootAction = createCommandLineAction(
				rootCommand,
				Collections.emptySet(),
				new NullProgressMonitor()); // TODO - from parent's capability loader
		ResourceSet actionModelsResourceSet = new ResourceSetImpl();
		actionModelsResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
		
		URI actionModelResourceURI = URI.createFileURI(File.createTempFile("command-action-model-", ".xml").getAbsolutePath());		
		Resource actionModelResource = actionModelsResourceSet.createResource(actionModelResourceURI);
		actionModelResource.getContents().add(rootAction);
		actionModelResource.save(out, null);
	}
	
	private record DescriptionRecord(AnnotatedElement annotatedElement, Description description) {}
	
	private static DescriptionRecord getDescriptionRecord(ArgSpec argSpec) {
		AnnotatedElement annotatedElement = null;
		ISetter setter = argSpec.setter();
		if (setter instanceof IAnnotatedElementProvider) {
			annotatedElement = ((IAnnotatedElementProvider) setter).getAnnotatedElement();
		} else {
			IGetter getter = argSpec.getter();
			if (getter instanceof IAnnotatedElementProvider) {
				annotatedElement = ((IAnnotatedElementProvider) getter).getAnnotatedElement();				
			}
		}
		
		if (annotatedElement == null) {
			return null;
		}
		
		Description description = annotatedElement.getAnnotation(Description.class);
		return description == null ? null : new DescriptionRecord(annotatedElement, description);
	}
	
	private static Action argSpecAction(
			ArgSpec argSpec, 
			Collection<DocumentationFactory> documentationFactories,
			Collection<String> classifiers, 
			ProgressMonitor progressMonitor) throws IOException {
		DescriptionRecord descriptionRecord = getDescriptionRecord(argSpec);
		if (descriptionRecord == null) {
			return null;
		}
		Action action = createAction();
		String icon = descriptionRecord.description().icon();
		if (!Util.isBlank(icon)) {
			action.setIcon(icon);
		}
		String tooltip = descriptionRecord.description().tooltip();
		if (!Util.isBlank(tooltip)) {
			action.setTooltip(tooltip);
		}
		
		Member member = (Member) descriptionRecord.annotatedElement();
		boolean handled = generateDocumentation(
				action, 
				descriptionRecord.description(), 
				member.getDeclaringClass(),
				classifiers,	
				argSpec, 
				documentationFactories,
				progressMonitor);				
		
		if (!handled) {
			if (Util.isBlank(descriptionRecord.description().format()) || Description.MARKDOWN_FORMAT.equals(descriptionRecord.description().format())) {		
				StringBuilder builder = new StringBuilder();
				String markdown = descriptionRecord.description().value();
				if (Util.isBlank(markdown)) {
					String dResource = descriptionRecord.description().resource();
					if (!Util.isBlank(dResource)) {
						InputStream dStream = member.getDeclaringClass().getResourceAsStream(dResource);
						if (dStream != null) {
							markdown = DefaultConverter.INSTANCE.toString(dStream);
						} else {
							markdown = "Resource not found: ``" + dResource + "`` by class ``" + member.getDeclaringClass().getName() + "``";
						}
					}
				}
				
				builder
					.append("<div class=\"markdown-body\">")
					.append(System.lineSeparator())
					.append(MarkdownHelper.INSTANCE.markdownToHtml(markdown))
					.append(System.lineSeparator())
					.append("</div>")
					.append(System.lineSeparator());				
				
				addContent(action, builder.toString());
			} else {
				addContent(action, "Unsupported description format: " + descriptionRecord.description().format()); 				
			}
		}
		
		return action;
	}
	
	/**
	 * Renders command list with links to commands
	 * @param help
	 * @return
	 */
	private static String renderCommandList(Help help) {
		Map<String, Help> subcommands = help.subcommands();
		if (subcommands.isEmpty()) {
			return "";
		}

		StringBuilder listBuilder = new StringBuilder("<ul>");
        for (Map.Entry<String, Help> entry : subcommands.entrySet()) {
            Help subCommandHelp = entry.getValue();
            UsageMessageSpec usage = subCommandHelp.commandSpec().usageMessage();
            String[] uHeader = usage.header();
            String header;
            if (uHeader == null || uHeader.length == 0) {
            	String[] uDescription = usage.description();
            	if (uDescription == null || uDescription.length == 0) {
            		header = "";
            	} else {
            		header = uDescription[0];
            	}
            } else {
            	header = usage.header()[0];
            }
            
            listBuilder
            	.append("<li>")
            	.append("<a href=\"")
            	.append(entry.getKey())
            	.append("/index.html\">")
            	.append(subCommandHelp.commandNamesText(", "))
            	.append("</a> - ")
            	.append(header)
            	.append("</li>");
        }
        return listBuilder.append("</ul>").toString();
    }	
	
//	private static String renderOptionList(Help help) {		
//		result = help.renhelp.createDefaultLayout();
//		
//		Layout linkingLayout = new Layout(defaultLayout.colorScheme(), defaultLayout.textTable()) {
//			
//		};
//		
//		
//		String optionList = help.optionList(
//			defaultLayout, 
//			help.createDefaultOptionSort(),
//			help.parameterLabelRenderer());
//		
//		optionList = StringEscapeUtils.escapeHtml4(optionList);
//		
//		for (OptionSpec opt: help.commandSpec().options()) {
//			String label = opt.shortestName();
//			System.out.println(label);
//		}
//		
//		return optionList;
//	}
	
	private static class EscapingHelpSectionRenderer implements IHelpSectionRenderer {
		
		private IHelpSectionRenderer renderer;

		EscapingHelpSectionRenderer(IHelpSectionRenderer renderer) {
			this.renderer = renderer;
		}

		@Override
		public String render(Help help) {
 			String result = renderer.render(help); 	
			return StringEscapeUtils.escapeHtml4(result);
		}
		
		static EscapingHelpSectionRenderer wrap(IHelpSectionRenderer renderer) {
			if (renderer instanceof EscapingHelpSectionRenderer) {
				return (EscapingHelpSectionRenderer) renderer;
			}
			return new EscapingHelpSectionRenderer(renderer);
		}
		
	}
			
	private static IHelpSectionRenderer filter(String key, IHelpSectionRenderer renderer) {
 		switch (key) {
 		// Links to commands
 		case CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST:
 			return ActionHelpMixIn::renderCommandList;
// 		case CommandLine.Model.UsageMessageSpec.SECTION_KEY_OPTION_LIST:
// 			return renderer::render; // No escaping
// 	 		case CommandLine.Model.UsageMessageSpec.SECTION_KEY_PARAMETER_LIST:
// 			return ActionHelpMixIn::renderParameterList;
 		}
 		return EscapingHelpSectionRenderer.wrap(renderer);
	}
	
	public static Action createCommandLineAction(
			CommandLine commandLine,
			Collection<DocumentationFactory> documentationFactories,
			ProgressMonitor progressMonitor) throws IOException {		
		CommandSpec commandSpec = commandLine.getCommandSpec();

//		commandLine.setHelpFactory((cSpec, colorScheme) -> {
//			return new Help(cSpec, colorScheme) {
//			
//				@Override
//		        public IOptionRenderer createDefaultOptionRenderer() {
//					IOptionRenderer optRenderer = super.createDefaultOptionRenderer();
//					
//					return (OptionSpec option, IParamLabelRenderer paramLabelRenderer, ColorScheme scheme) -> {
//						picocli.CommandLine.Help.Ansi.Text[][] result = optRenderer.render(option, paramLabelRenderer, colorScheme);
//						for (int rowIdx = 0; rowIdx < result.length; ++rowIdx) {
//							picocli.CommandLine.Help.Ansi.Text[] row = result[rowIdx];
//							for (int colIdx = 0; colIdx < row.length; ++colIdx) {
//								if (rowIdx == 0 && colIdx == 1) {
//									result[rowIdx][colIdx] = ansi().text(StringEscapeUtils.escapeHtml4(row[colIdx].plainString()));									
//								} else {
//									result[rowIdx][colIdx] = ansi().text(StringEscapeUtils.escapeHtml4(row[colIdx].plainString()));
//								}
//							}
//						}
//						return result;
//					};
//		        }				
//				
//			};
//			
//	    });		
		
		Action action = createAction();
		action.setText(commandSpec.name());
		action.setLocation(commandSpec.name() + "/index.html");
		
		String[] version = commandSpec.version(); // TODO: Nav, float right
		StringBuilder builder = new StringBuilder();
		if (version.length > 0) {
			builder.append("<table><tr><th valign=\"top\">Version:</th><td> ").append(System.lineSeparator());			
			for (String vs: version) {
				builder.append(vs).append(System.lineSeparator());
				builder.append("<br/>").append(System.lineSeparator());
			}
			builder.append("</td></tr></table>").append(System.lineSeparator());
		}

//		To customize rendering, e.g. add links to sub-commands:
//		 	Map<String, IHelpSectionRenderer> helpSections = commandLine.getHelpSectionMap();
//			IHelpSectionRenderer commandListSection = helpSections.get(CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST);
//			IHelpSectionRenderer myCommandListSection = help -> "Purum<P/>" + commandListSection.render(help);
//			helpSections.put(CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST, myCommandListSection);
//		HTML is not rendered in <PRE>. If links are to be rendered then all sections shall be wrapped in <PRE> or CSS equivalent of PRE and then individual sections
//		shall be customized. Can be done with switch - case - default		
		
		builder
			.append("<div style=\"font-family:monospace;white-space:pre;padding:5px;width:max-content\">")
			.append(System.lineSeparator());
		
	 	Map<String, IHelpSectionRenderer> helpSections = commandLine.getHelpSectionMap();
	 	for (Entry<String, IHelpSectionRenderer> hse: helpSections.entrySet()) {	 		
 			IHelpSectionRenderer sectionRenderer = filter(hse.getKey(), hse.getValue());
 			if (sectionRenderer != null) {
 				hse.setValue(sectionRenderer);
 			}
	 	}
		
		StringWriter usageWriter = new StringWriter();		
		try (PrintWriter pw = new PrintWriter(usageWriter)) {
			commandLine.usage(pw, Help.Ansi.OFF);
		}
		usageWriter.close();
		builder.append(usageWriter.toString());
		builder.append("</div>").append(System.lineSeparator());
		
		addContent(action, builder.toString());

		// Description 
		Object userObject = commandSpec.userObject();
		if (userObject != null) {
			Class<? extends Object> clazz = userObject.getClass();
			Description description = clazz.getAnnotation(Description.class);
			if (description != null) {
				String icon = description.icon();
				if (!Util.isBlank(icon)) {
					action.setIcon(icon);
				}
				String tooltip = description.tooltip();
				if (!Util.isBlank(tooltip)) {
					action.setTooltip(tooltip);
				}
				
				boolean handled = generateDocumentation(
						action, 
						description, 
						clazz, 
						Collections.singleton(null),
						commandLine, 
						documentationFactories,
						progressMonitor);				
				
				if (!handled) {
					if (Util.isBlank(description.format()) || Description.MARKDOWN_FORMAT.equals(description.format())) {
						String markdown = description.value();
						if (Util.isBlank(markdown)) {
							String dResource = description.resource();
							if (Util.isBlank(dResource)) {
								dResource = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1) + ".md";						
							}
							InputStream dStream = clazz.getResourceAsStream(dResource);
							if (dStream != null) {
								markdown = DefaultConverter.INSTANCE.toString(dStream);
							}
						}
						
						StringBuilder markdownBuilder = new StringBuilder("<div class=\"markdown-body\">")
							.append(System.lineSeparator())
							.append(MarkdownHelper.INSTANCE.markdownToHtml(markdown))
							.append(System.lineSeparator())
							.append("</div>")
							.append(System.lineSeparator());
						addContent(action, markdownBuilder.toString());
					} else {
						addContent(action, "Unsupported description format: " + description.format()); 
					}
				}
			}
		}
		
		EList<EObject> children = action.getChildren();
		for (CommandLine subCommand: commandLine.getSubcommands().values().stream().sorted((a,b) -> a.getCommandName().compareTo(b.getCommandName())).collect(Collectors.toList())) {
			children.add(createCommandLineAction(
					subCommand, 
					documentationFactories, 
					progressMonitor));
		}
		
		Action parametersSection = null;		
		for (PositionalParamSpec param: commandSpec.positionalParameters()) {
			Collection<String> classifiers = new ArrayList<>();
			DescriptionRecord descriptionRecord = getDescriptionRecord(param);
			if (descriptionRecord != null) {
				AnnotatedElement annotatedElement = descriptionRecord.annotatedElement();
				if (annotatedElement instanceof Field) {
					classifiers.add("-param-" + ((Field) annotatedElement).getName());
				}
			}
			
			Action paramAction = argSpecAction(
					param,
					documentationFactories,
					classifiers,
					progressMonitor);
			
			if (paramAction != null) {
				paramAction.setText(StringEscapeUtils.escapeHtml4(param.paramLabel()));
				paramAction.setName("param_" + StringEscapeUtils.escapeHtml4(param.paramLabel()));
				if (parametersSection == null) {
					parametersSection = createAction();
					parametersSection.setText("Parameters");
					action.getSections().add(parametersSection);
				}
				parametersSection.getSections().add(paramAction);
			}
		}
		
		Action optionsSection = null;
		for (OptionSpec opt: commandSpec.options().stream().sorted((a, b) -> a.shortestName().compareToIgnoreCase(b.shortestName())).toList()) {
			Collection<String> classifiers = new ArrayList<>();
			for (String optName: opt.names()) {
				classifiers.add("-opt" + optName);
			}
			Action optAction = argSpecAction(
					opt,
					documentationFactories,
					classifiers,
					progressMonitor);
			
			if (optAction != null) {				
				optAction.setText(StringEscapeUtils.escapeHtml4(String.join(", ", opt.names())));
				optAction.setName("opt" + StringEscapeUtils.escapeHtml4(opt.shortestName()));
				if (optionsSection == null) {
					optionsSection = createAction();
					optionsSection.setText("Options");
					action.getSections().add(optionsSection);
				}
				optionsSection.getSections().add(optAction);
			}
		}				

		return action;
	}

	/**
	 * Generates documentation using documentation factories
	 * @param action
	 * @param description
	 * @param clazz
	 * @param impliedName Whether to imply documentation resource name. true for classes and fields, false for methods
	 * @param classifier Used for implied resource name. null or empty string for classes, <code>-&lt;field name&gt;</code> for fields 
	 * @param docContext
	 * @param documentationFactories
	 * @param progressMonitor
	 * @return
	 */
	protected static boolean generateDocumentation(
			Action action, 
			Description description,
			Class<? extends Object> clazz, 
			Collection<String> classifiers,
			Object docContext, 
			Collection<DocumentationFactory> documentationFactories,
			ProgressMonitor progressMonitor) {
		if (documentationFactories != null) {
			String doc = description.value();
			if (Util.isBlank(doc)) {
				// Doc is blank, using resource
				String resource = description.resource();
				URI baseURI = Util.createClassURI(clazz);
				if (Util.isBlank(resource)) {
					for(String classifier: classifiers) {
						String baseName = clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
						if (!Util.isBlank(classifier)) {
							baseName += classifier;
						}
						baseName += ".";
						for (DocumentationFactory docFactory: documentationFactories) {
							for (String extension: docFactory.getExtensions()) {
								String resName = baseName + extension;
								InputStream dStream = clazz.getResourceAsStream(resName);
								if (dStream != null) {
									Collection<EObject> docObjs = docFactory.createDocumentation(
											docContext, 
											URI.createURI(resName).resolve(baseURI),
											null,
											progressMonitor);							
									action.getContent().addAll(docObjs);								
									return true;
								}								
							}
						}						
					}
				} else {
					URI resourceURI = URI.createURI(resource).resolve(baseURI);
					for (DocumentationFactory docFactory: documentationFactories) {						
						if (docFactory.canHandle(resourceURI)) {
							Collection<EObject> docObjs = docFactory.createDocumentation(
									docContext, 
									resourceURI, 
									null,
									progressMonitor);							
							action.getContent().addAll(docObjs);								
							return true;
						}
					}												
				}
			} else {
				String format = description.format();
				if (Util.isBlank(format)) {
					format = Description.MARKDOWN_FORMAT;
				}
				for (DocumentationFactory docFactory: documentationFactories) {						
					if (docFactory.canHandle(format)) {
						Collection<EObject> docObjs = docFactory.createDocumentation(
								docContext, 
								doc, 
								format, 
								Util.createClassURI(clazz),
								null,
								progressMonitor);
						
						action.getContent().addAll(docObjs);								
						return true;
					}
				}						
			}								
		}
		return false;
	}

	protected static Action createAction() {
		return AppFactory.eINSTANCE.createAction();
	}
	
	/**
	 * Adds textual content.
	 * @param content
	 */
	protected static void addContent(Action action, String content) {
		if (!Util.isBlank(content)) {
			Text text = createText(content);
			action.getContent().add(text);
		}
	}

	/**
	 * Convenience method to create Text and set content in one shot.
	 * @param content
	 * @return
	 */
	public static Text createText(String content) {
		Text text = ContentFactory.eINSTANCE.createText();
		text.setContent(content);
		return text;
	}

}
