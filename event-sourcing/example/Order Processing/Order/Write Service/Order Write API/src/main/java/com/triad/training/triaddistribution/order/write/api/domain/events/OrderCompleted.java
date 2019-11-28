package com.triad.training.triaddistribution.order.write.api.domain.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class OrderCompleted extends Event
{
  private static final long serialVersionUID = 1L;
  private UUID orderId;

  public OrderCompleted()
  {
    /* For (un)marshalling */
  }

  public OrderCompleted(UUID orderId)
  {
    this.setOrderId(orderId);
  }

  public UUID getOrderId()
  {
    return orderId;
  }

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }
}
