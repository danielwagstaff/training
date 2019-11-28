package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class ProductNonExistenceConfirmed extends Event
{
  private static final long serialVersionUID = 1L;
  private UUID orderId;
  private UUID productId;

  public ProductNonExistenceConfirmed()
  {
    /* For (un)marshalling */
  }

  public ProductNonExistenceConfirmed(UUID orderId, UUID productId)
  {
    this.setOrderId(orderId);
    this.setProductId(productId);
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
}
