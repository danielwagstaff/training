package com.triad.training.triaddistribution.order.write.api.eventsourcing.events;

import com.triad.common.eventsourcing.Event;

import java.util.UUID;

public class ProductExistenceConfirmed extends Event
{
  private static final long serialVersionUID = 1L;
  private UUID orderId;
  private UUID productId;

  public ProductExistenceConfirmed()
  {
    /* For (un)marshalling */
  }

  public ProductExistenceConfirmed(UUID orderId, UUID productId)
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
