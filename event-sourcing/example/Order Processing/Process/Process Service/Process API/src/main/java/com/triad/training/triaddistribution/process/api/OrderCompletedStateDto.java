package com.triad.training.triaddistribution.process.api;

import java.util.UUID;

public class OrderCompletedStateDto
{
  private UUID orderId;
  private String state;

  public OrderCompletedStateDto(UUID orderId, String state)
  {
    this.orderId = orderId;
    this.state = state;
  }

  public UUID getOrderId()
  {
    return orderId;
  }

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }
}
