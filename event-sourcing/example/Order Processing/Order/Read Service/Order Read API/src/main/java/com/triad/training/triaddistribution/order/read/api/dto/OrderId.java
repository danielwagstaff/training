package com.triad.training.triaddistribution.order.read.api.dto;

import java.util.Objects;
import java.util.UUID;

public class OrderId
{
  private UUID id;

  public UUID getOrderId()
  {
    return id;
  }

  public void setOrderId(UUID orderId)
  {
    this.id = orderId;
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof OrderId && Objects.equals(((OrderId) other).getOrderId(), this.getOrderId());
  }

  @Override
  public int hashCode()
  {
    return getOrderId().hashCode();
  }
}
