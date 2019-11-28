package com.triad.training.triaddistribution.product.api.dto;

import java.util.UUID;

public class ProductReservation
{
  private UUID productId;
  private int quantity;

  public ProductReservation(UUID productId, int quantity)
  {
    this.productId = productId;
    this.quantity = quantity;
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
