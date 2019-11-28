package com.triad.training.triaddistribution.product.api.dto;

import java.util.Objects;
import java.util.UUID;

public class ProductId
{
  private UUID id;

  public UUID getProductId()
  {
    return id;
  }

  public void setProductId(UUID productId)
  {
    this.id = productId;
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof ProductId && Objects.equals(((ProductId) other).getProductId(), this.getProductId());
  }

  @Override
  public int hashCode()
  {
    return getProductId().hashCode();
  }
}
