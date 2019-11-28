package com.triad.training.triaddistribution.process.core.repository;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class OrderProcessRepository<T>
{
  private Map<UUID, T> orderIdToOrderState = new HashMap<>();

  public Map<UUID, T> getAll()
  {
    return orderIdToOrderState;
  }

  public Optional<T> findById(UUID orderId)
  {
    T orderProcessState = orderIdToOrderState.get(orderId);
    if(orderProcessState != null)
    {
      return Optional.of(orderProcessState);
    }
    else
    {
      return Optional.empty();
    }
  }

  public void save(UUID orderId, T orderProcessState)
  {
    orderIdToOrderState.put(orderId, orderProcessState);
  }
}
