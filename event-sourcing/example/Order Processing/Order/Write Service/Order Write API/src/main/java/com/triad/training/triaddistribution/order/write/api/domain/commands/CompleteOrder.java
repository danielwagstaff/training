package com.triad.training.triaddistribution.order.write.api.domain.commands;

import java.util.UUID;

public class CompleteOrder
{
  private UUID orderId;
  private UUID requestorId;

  public CompleteOrder()
  {
    /* For (un)marshalling */
  }

  public CompleteOrder(UUID orderId, UUID requestorId)
  {
    this.orderId = orderId;
    this.requestorId = requestorId;
  }

  public UUID getOrderId()
  {
    return orderId;
  }

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }

  public UUID getRequestorId()
  {
    return requestorId;
  }

  public void setRequestorId(UUID requestorId)
  {
    this.requestorId = requestorId;
  }
}
