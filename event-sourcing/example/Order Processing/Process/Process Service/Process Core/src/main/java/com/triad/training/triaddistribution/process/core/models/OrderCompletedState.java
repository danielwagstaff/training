package com.triad.training.triaddistribution.process.core.models;

import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;

public interface OrderCompletedState
{
  enum State
  {
    INIT, STOCK_NOT_RESERVED, STOCK_RESERVED, FINISHED
  }

  OrderCompletedState nextState(OrderCompletedDomainEvent orderCompletedDomainEvent);
  State getState();
}
