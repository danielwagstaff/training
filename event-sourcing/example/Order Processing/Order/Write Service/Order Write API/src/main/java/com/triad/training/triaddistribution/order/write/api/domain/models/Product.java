package com.triad.training.triaddistribution.order.write.api.domain.models;

import java.util.UUID;

public class Product
{
  private UUID productId;
  private int quantity;

  public Product()
  {
  }

  public Product(UUID productId, int quantity)
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
