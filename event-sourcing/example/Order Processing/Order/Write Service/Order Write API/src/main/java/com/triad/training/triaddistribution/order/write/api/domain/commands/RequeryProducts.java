package com.triad.training.triaddistribution.order.write.api.domain.commands;

import java.util.UUID;

public class RequeryProducts
{
  private UUID orderId;

  public RequeryProducts()
  {
    /* For (un)marshalling */
  }

  public RequeryProducts(UUID orderId)
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
