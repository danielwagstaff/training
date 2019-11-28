package com.triad.training.triaddistribution.order.read.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.triad.training.triaddistribution.order.write.api.domain.events.DomainEvent;
import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Optional;

@ApplicationScoped
public class OrderTopicEventController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderTopicEventController.class);

  @Incoming("order")
  @Outgoing("internal-stream-order")
  @Broadcast
  public Optional<OrderCompletedDomainEvent> process(String incomingEvent)
  {
    LOGGER.info("Received event: {}", incomingEvent);

    try
    {
      ObjectMapper objectMapper = new ObjectMapper();
      DomainEvent domainEvent = objectMapper.readValue(incomingEvent, DomainEvent.class);

      if (domainEvent instanceof OrderCompletedDomainEvent)
      {
        LOGGER.info("Received OrderCompletedDomainEvent event: {}", domainEvent);
        return Optional.of((OrderCompletedDomainEvent) domainEvent);
      }
      else
      {
        LOGGER.error("Unhandled event: {}", incomingEvent);
        return Optional.empty();
      }
    }
    catch (IOException e)
    {
      LOGGER.error("Unable to unpack event: {}", e);
      return Optional.empty();
    }
  }
}
