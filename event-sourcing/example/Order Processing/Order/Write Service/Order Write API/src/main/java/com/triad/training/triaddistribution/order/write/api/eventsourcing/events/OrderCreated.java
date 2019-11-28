package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class OrderCreated extends Event
{
  private static final long serialVersionUID = 1L;
  private UUID creatorId;
  private UUID customerId;

  public OrderCreated()
  {
    /* For (un)marshalling */
  }

  public OrderCreated(UUID orderId, UUID creatorId, UUID customerId)
  {
    this.setOrderId(orderId);
    this.setCreatorId(creatorId);
    this.setCustomerId(customerId);
  }

  public UUID getOrderId()
  {
    return getAggregateRootId();
  }

  public void setOrderId(UUID orderId)
  {
    setAggregateRootId(orderId);
  }

  public UUID getCreatorId()
  {
    return creatorId;
  }

  public void setCreatorId(UUID creatorId)
  {
    this.creatorId = creatorId;
  }

  public UUID getCustomerId()
  {
    return customerId;
  }

  public void setCustomerId(UUID customerId)
  {
    this.customerId = customerId;
  }
}
