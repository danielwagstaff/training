package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class OrderSuspended extends Event
{
  private static final long serialVersionUID = 1L;
  private UUID orderId;
  private Reason reason;

  public OrderSuspended()
  {
    /* For (un)marshalling */
  }

  public OrderSuspended(UUID orderId, Reason reason)
  {
    this.setOrderId(orderId);
    this.setReason(reason);
  }

  public UUID getOrderId()
  {
    return orderId;
  }

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }

  public Reason getReason()
  {
    return reason;
  }

  public void setReason(Reason quantity)
  {
    this.reason = quantity;
  }

  public enum Reason
  {
    PRODUCT_ADDED_TO_ORDER_DOES_NOT_EXIST
  }
}
