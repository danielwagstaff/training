package com.triad.training.triaddistribution.product.api.dto;

import java.util.UUID;

public class ProductReservationDto
{
  private UUID productId;
  private int quantity;

  public ProductReservationDto(UUID productId, int quantity)
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
