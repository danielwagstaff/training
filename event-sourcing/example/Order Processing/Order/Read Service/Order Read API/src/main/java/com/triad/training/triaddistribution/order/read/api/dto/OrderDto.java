package com.triad.training.triaddistribution.order.read.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderDto
{
  private UUID orderId;
  private List<UUID> productIds = new ArrayList<>();

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }

  public List<UUID> getProductIds()
  {
    return productIds;
  }

  public void addProductId(UUID productId)
  {
    this.productIds.add(productId);
  }

  public void setProductIds(List<UUID> productIds)
  {
    this.productIds = productIds;
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof OrderDto && Objects.equals(((OrderDto) other).getOrderId(), this.getOrderId());
  }

  @Override
  public int hashCode()
  {
    return getOrderId().hashCode();
  }

  public UUID getOrderId()
  {
    return orderId;
  }
}
