package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class OrderCancelled extends Event
{
  private static final long serialVersionUID = 1L;

  public OrderCancelled()
  {
    /* For (un)marshalling */
  }

  public OrderCancelled(UUID orderId)
  {
    this.setOrderId(orderId);
  }

  public UUID getOrderId()
  {
    return getAggregateRootId();
  }

  public void setOrderId(UUID orderId)
  {
    setAggregateRootId(orderId);
  }
}
