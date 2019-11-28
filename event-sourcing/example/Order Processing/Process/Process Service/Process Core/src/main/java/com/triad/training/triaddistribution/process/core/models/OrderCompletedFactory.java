package com.triad.training.triaddistribution.process.core.models;

import com.triad.training.triaddistribution.process.core.services.ProductService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderCompletedFactory
{
  private final ProductService productService;

  @Inject
  public OrderCompletedFactory(ProductService productService)
  {
    this.productService = productService;
  }

  public OrderCompletedState create(OrderCompletedState.State state)
  {
    OrderCompletedState orderCompletedState;
    switch (state)
    {
      case INIT:
        orderCompletedState = new OrderCompletedStateInit(productService);
        break;
      case STOCK_RESERVED:
        orderCompletedState = new OrderCompletedStateStockReserved();
        break;
      case STOCK_NOT_RESERVED:
        orderCompletedState = new OrderCompletedStateStockNotReserved();
        break;
      case FINISHED:
        orderCompletedState = new OrderCompletedStateFinished();
        break;
      default:
        throw new UnsupportedOperationException("Unknown state");
    }
    return orderCompletedState;
  }
}
