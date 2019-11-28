package com.triad.training.triaddistribution.order.write.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triad.training.triaddistribution.order.write.api.domain.events.DomainEvent;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventPublishService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(EventPublishService.class);
  private FlowableEmitter<String> emitter;
  private Flowable<String> outgoingStream;

  public EventPublishService()
  {
    outgoingStream = Flowable.create(em -> this.emitter = em, BackpressureStrategy.BUFFER);
  }

  public void produce(DomainEvent event)
  {
    try
    {
      ObjectMapper objectMapper = new ObjectMapper();
      String obj = objectMapper.writeValueAsString(event);
      emitter.onNext(obj);
    }
    catch (JsonProcessingException e)
    {
      LOGGER.error("Unable to publish event: {}", e);
    }
  }

  @PreDestroy
  void dispose()
  {
    emitter.onComplete();
  }

  @Outgoing("stream-order")
  @Broadcast
  public Flowable<String> generate()
  {
    return outgoingStream;
  }
}
