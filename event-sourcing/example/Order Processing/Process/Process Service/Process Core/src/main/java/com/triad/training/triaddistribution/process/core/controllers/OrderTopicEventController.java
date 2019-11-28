package com.triad.training.triaddistribution.process.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triad.training.triaddistribution.order.write.api.domain.events.DomainEvent;
import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import com.triad.training.triaddistribution.process.core.services.OrderCompletedService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class OrderTopicEventController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderTopicEventController.class);
  private final OrderCompletedService orderCompletedService;

  @Inject
  public OrderTopicEventController(OrderCompletedService orderCompletedService)
  {
    this.orderCompletedService = orderCompletedService;
  }

  @Incoming("order")
  public void process(String incomingEvent)
  {
    try
    {
      ObjectMapper objectMapper = new ObjectMapper();
      DomainEvent domainEvent = objectMapper.readValue(incomingEvent, DomainEvent.class);

      if (domainEvent instanceof OrderCompletedDomainEvent)
      {
        LOGGER.info("Received OrderCompletedDomainEvent event: {}", domainEvent);
        orderCompletedService.handle((OrderCompletedDomainEvent) domainEvent);
      }
      else
      {
        LOGGER.error("Unhandled event: {}", incomingEvent);
      }
    }
    catch (IOException e)
    {
      LOGGER.error("Unable to unpack event: {}", e);
    }
  }
}
