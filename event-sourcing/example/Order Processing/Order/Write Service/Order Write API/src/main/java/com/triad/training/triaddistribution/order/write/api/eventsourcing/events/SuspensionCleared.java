package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;
import com.triad.training.triaddistribution.order.write.api.eventsourcing.events.OrderSuspended.Reason;

import java.util.UUID;

public class SuspensionCleared extends Event
{
  private static final long serialVersionUID = 1L;
  private Reason suspensionReason;

  public SuspensionCleared()
  {
    /* For (un)marshalling */
  }

  public SuspensionCleared(UUID orderId, Reason suspensionReason)
  {
    this.setOrderId(orderId);
    this.setSuspensionReason(suspensionReason);
  }

  public UUID getOrderId()
  {
    return getAggregateRootId();
  }

  public void setOrderId(UUID orderId)
  {
    setAggregateRootId(orderId);
  }

  public Reason getSuspensionReason()
  {
    return suspensionReason;
  }

  public void setSuspensionReason(Reason quantity)
  {
    this.suspensionReason = quantity;
  }
}
