package com.triad.training.triaddistribution.order.write.api.domain.commands;

import java.util.UUID;

public class CreateNewOrder
{
  private UUID creatorId;
  private UUID customerId;

  public CreateNewOrder()
  {
    /* For (un)marshalling */
  }

  public CreateNewOrder(UUID creatorId, UUID customerId)
  {
    this.creatorId = creatorId;
    this.customerId = customerId;
  }

  public UUID getCreatorId()
  {
    return creatorId;
  }

  public void setCreatorId(UUID creatorId)
  {
    this.creatorId = creatorId;
  }

  public UUID getCustomerId()
  {
    return customerId;
  }

  public void setCustomerId(UUID customerId)
  {
    this.customerId = customerId;
  }
}
