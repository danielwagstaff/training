package com.triad.training.triaddistribution.order.write.api.domain.events;

import com.triad.training.triaddistribution.order.write.api.domain.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderCompletedDomainEvent extends DomainEvent
{
  private static final long serialVersionUID = 1L;
  private UUID orderId;
  private UUID completedById;
  private List<Product> products = new ArrayList<>();

  public OrderCompletedDomainEvent()
  {
    /* For (un)marshalling */
  }

  public OrderCompletedDomainEvent(UUID orderId, UUID completedById, List<Product> products)
  {
    this.setOrderId(orderId);
    this.setCompletedById(completedById);
    this.setProducts(products);
  }

  public UUID getOrderId()
  {
    return orderId;
  }

  public void setOrderId(UUID orderId)
  {
    this.orderId = orderId;
  }

  public UUID getCompletedById()
  {
    return completedById;
  }

  public void setCompletedById(UUID completedById)
  {
    this.completedById = completedById;
  }

  public List<Product> getProducts()
  {
    return products;
  }

  public void setProducts(List<Product> products)
  {
    this.products = products;
  }
}
