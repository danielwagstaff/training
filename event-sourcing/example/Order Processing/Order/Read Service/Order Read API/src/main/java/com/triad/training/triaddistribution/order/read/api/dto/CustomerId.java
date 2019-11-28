package com.triad.training.triaddistribution.order.read.api.dto;

import java.util.Objects;
import java.util.UUID;

public class CustomerId
{
  private UUID id;

  public UUID getCustomerId()
  {
    return id;
  }

  public void setCustomerId(UUID customerId)
  {
    this.id = customerId;
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof CustomerId && Objects.equals(((CustomerId) other).getCustomerId(), this.getCustomerId());
  }

  @Override
  public int hashCode()
  {
    return getCustomerId().hashCode();
  }
}
