package com.triad.training.triaddistribution.order.write.api.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class DomainEvent implements Serializable
{
  private static final long serialVersionUID = 1L;
}
