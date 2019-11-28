package com.triad.training.triaddistribution.process.core.models;

import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderCompletedStateStockNotReserved implements OrderCompletedState
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderCompletedStateStockNotReserved.class);

  @Override
  public OrderCompletedState nextState(OrderCompletedDomainEvent orderCompletedDomainEvent)
  {
    LOGGER.info("STOCK_NOT_RESERVED-1");
    return this;
  }

  @Override
  public State getState()
  {
    return State.STOCK_NOT_RESERVED;
  }
}
