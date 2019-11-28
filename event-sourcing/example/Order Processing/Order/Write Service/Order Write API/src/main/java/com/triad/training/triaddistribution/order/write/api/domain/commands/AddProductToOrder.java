package com.triad.training.triaddistribution.order.write.api.domain.commands;

import java.util.UUID;

public class AddProductToOrder
{
  private UUID orderId;
  private UUID productId;
  private int quantity;
  public AddProductToOrder()
  {
    /* For (un)marshalling */
  }
  public AddProductToOrder(UUID orderId, UUID productId, int quantity)
  {
    this.orderId = orderId;
    this.productId = productId;
    this.quantity = quantity;
  }

  public UUID getOrderId()
  {
    return orderId;
  }

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }

  public UUID getProductId()
  {
    return productId;
  }

  public void setProductId(UUID productId)
  {
    this.productId = productId;
  }

  public int getQuantity()
  {
    return quantity;
  }

  public void setQuantity(int quantity)
  {
    this.quantity = quantity;
  }
}
