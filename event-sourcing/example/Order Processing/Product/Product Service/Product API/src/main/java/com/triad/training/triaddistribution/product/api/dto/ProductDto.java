package com.triad.training.triaddistribution.product.api.dto;

import java.util.Objects;
import java.util.UUID;

public class ProductDto
{
  private UUID id;
  private Integer quantity;

  public UUID getId()
  {
    return id;
  }

  public void setId(UUID id)
  {
    this.id = id;
  }

  public Integer getQuantity()
  {
    return quantity;
  }

  public void setQuantity(Integer quantity)
  {
    this.quantity = quantity;
  }

  @Override
  public int hashCode()
  {
    return getId().hashCode();
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof ProductDto && Objects.equals(((ProductDto) other).getId(), this.getId());
  }
}
