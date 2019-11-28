package com.triad.common.eventsourcing.preprocessor.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({ "com.triad.common.eventsourcing.preprocessor.processor.AggregateRootCommand" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AggregateRootCommandProcessor extends AbstractProcessor
{

  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment)
  {
    super.init(processingEnvironment);
    this.messager = processingEnvironment.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
  {

    Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(AggregateRootCommand.class);
    for (Element element : annotatedElements)
    {
      validate(element);
    }

    // Don't claim annotations, to allow other processors to process them
    return false;
  }

  private void validate(Element method)
  {

    /* TODO: THIS DOESN'T HANDLE INHERITANCE, I.E. WON'T FIND A PARENT METHOD */

    List<String> raisedEvents = new ArrayList<>();
    try
    {
      // To get annotation values, have to catch exception!
      // https://docs.oracle.com/javase/7/docs/api/javax/lang/model/element/Element.html#getAnnotation(java.lang.Class)
      method.getAnnotation(AggregateRootCommand.class).raisesEvents();
    }
    catch (MirroredTypesException e)
    {
      raisedEvents.addAll(e.getTypeMirrors().stream().map(TypeMirror::toString).collect(Collectors.toList()));
    }

    List<? extends Element> elements = method.getEnclosingElement().getEnclosedElements();
    List<String> handledEvents = new ArrayList<>();
    for (Element element : elements)
    {
      if ((element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.CONSTRUCTOR) &&
          "when".equals(element.getSimpleName().toString()))
      {
        handledEvents.add(element.toString());
      }
    }

    for (String raisedEvent : raisedEvents)
    {
      if (handledEvents.stream().noneMatch(handledEvent -> handledEvent.contains(raisedEvent)))
      {
        messager.printMessage(Diagnostic.Kind.ERROR,
                              "AggregateRootCommand Processor: No valid Event handler found for event {" +
                                                     raisedEvent +
                                                     "}",
                              method);
      }
    }
  }
}
