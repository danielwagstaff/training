package com.triad.common.eventsourcing.preprocessor.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.triad.common.eventsourcing.Event;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface AggregateRootCommand
{
  Class<? extends Event>[] raisesEvents();
}
