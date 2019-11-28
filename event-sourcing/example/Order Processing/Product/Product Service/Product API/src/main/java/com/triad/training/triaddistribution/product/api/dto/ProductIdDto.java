package com.triad.training.triaddistribution.product.api.dto;

import java.util.Objects;
import java.util.UUID;

public class ProductIdDto
{
  private UUID id;

  public UUID getId()
  {
    return id;
  }

  public void setId(UUID id)
  {
    this.id = id;
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof ProductIdDto && Objects.equals(((ProductIdDto) other).getId(), this.getId());
  }

  @Override
  public int hashCode()
  {
    return getId().hashCode();
  }
}
