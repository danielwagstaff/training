package com.triad.training.triaddistribution.order.write.api.domain.commands;

import java.util.UUID;

public class RemoveSuspensionsFromOrder
{
  private UUID orderId;

  public RemoveSuspensionsFromOrder()
  {
    /* For (un)marshalling */
  }

  public RemoveSuspensionsFromOrder(UUID orderId)
  {
    this.orderId = orderId;
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
