package com.triad.training.triaddistribution.process.core.models;

import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import com.triad.training.triaddistribution.process.core.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderCompletedStateInit implements OrderCompletedState
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderCompletedStateInit.class);
  private final ProductService productService;

  public OrderCompletedStateInit(ProductService productService)
  {
    this.productService = productService;
  }

  @Override
  public OrderCompletedState nextState(OrderCompletedDomainEvent orderCompletedDomainEvent)
  {
    LOGGER.info("INIT-1");

    List<Pair<UUID, Boolean>> products = orderCompletedDomainEvent.getProducts().stream().map(product -> {
      LOGGER.info("Reserving {} of {}", product.getQuantity(), product.getProductId());
      return new Pair<>(product.getProductId(),
                        productService.reserveProduct(product.getProductId(), product.getQuantity())
                                      .toCompletableFuture()
                                      .join());
    }).collect(Collectors.toList());

    if (products.stream().anyMatch(pair -> Boolean.FALSE.equals(pair.getValue())))
    {
      return new OrderCompletedStateStockNotReserved();
    }
    else
    {
      return new OrderCompletedStateStockReserved();
    }
  }

  @Override
  public State getState()
  {
    return State.INIT;
  }

  private class Pair<A, B>
  {
    private A key;
    private B value;

    public Pair(A key, B value)
    {
      this.key = key;
      this.value = value;
    }

    public A getKey()
    {
      return key;
    }

    public B getValue()
    {
      return value;
    }
  }
}
