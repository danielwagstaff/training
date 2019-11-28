package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class ProductAddedToOrder extends Event
{
  private static final long serialVersionUID = 1L;
  private UUID orderId;
  private UUID productId;
  private int quantity;

  public ProductAddedToOrder()
  {
    /* For (un)marshalling */
  }

  public ProductAddedToOrder(UUID orderId, UUID productId, int quantity)
  {
    this.setOrderId(orderId);
    this.setProductId(productId);
    this.setQuantity(quantity);
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
