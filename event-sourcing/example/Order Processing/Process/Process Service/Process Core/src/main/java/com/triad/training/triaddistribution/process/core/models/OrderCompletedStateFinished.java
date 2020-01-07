package com.triad.training.triaddistribution.process.core.models;

import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderCompletedStateFinished implements OrderCompletedState
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderCompletedStateFinished.class);

  @Override
  public OrderCompletedState nextState(OrderCompletedDomainEvent orderCompletedDomainEvent)
  {
    LOGGER.info("FINISHED-1");
    return this;
  }

  @Override
  public State getState()
  {
    return State.FINISHED;
  }
}
