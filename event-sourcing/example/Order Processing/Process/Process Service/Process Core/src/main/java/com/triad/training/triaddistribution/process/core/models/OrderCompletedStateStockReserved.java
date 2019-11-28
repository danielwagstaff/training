package com.triad.training.triaddistribution.process.core.models;

import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderCompletedStateStockReserved implements OrderCompletedState
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderCompletedStateStockReserved.class);

  @Override
  public OrderCompletedState nextState(OrderCompletedDomainEvent orderCompletedDomainEvent)
  {
    LOGGER.info("STOCK_RESERVED-1");
    return this;
  }

  @Override
  public State getState()
  {
    return State.STOCK_RESERVED;
  }
}
