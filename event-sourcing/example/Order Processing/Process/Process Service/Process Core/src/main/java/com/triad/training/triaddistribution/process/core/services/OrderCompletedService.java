package com.triad.training.triaddistribution.process.core.services;

import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import com.triad.training.triaddistribution.process.core.models.OrderCompletedFactory;
import com.triad.training.triaddistribution.process.core.models.OrderCompletedState;
import com.triad.training.triaddistribution.process.core.repository.OrderProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class OrderCompletedService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderCompletedService.class);
  private final OrderProcessRepository<OrderCompletedState.State> orderProcessRepository;
  private final OrderCompletedFactory orderCompletedFactory;

  @Inject
  public OrderCompletedService(OrderProcessRepository<OrderCompletedState.State> orderProcessRepository,
                               OrderCompletedFactory orderCompletedFactory)
  {
    this.orderProcessRepository = orderProcessRepository;
    this.orderCompletedFactory = orderCompletedFactory;
  }

  public void handle(OrderCompletedDomainEvent orderCompletedDomainEvent)
  {
    Optional<OrderCompletedState.State> optOrderCompletedProcess = orderProcessRepository.findById(orderCompletedDomainEvent.getOrderId());
    if (!optOrderCompletedProcess.isPresent())
    {
      OrderCompletedState orderCompletedState = orderCompletedFactory.create(OrderCompletedState.State.INIT);
      orderCompletedState = progressStateMachine(orderCompletedDomainEvent, orderCompletedState);
      orderProcessRepository.save(orderCompletedDomainEvent.getOrderId(), orderCompletedState.getState());
    }
    else
    {
      LOGGER.error("Already received OrderCompletedDomainEvent for order ID: {}",
                   orderCompletedDomainEvent.getOrderId());
    }
  }

  public Map<UUID, OrderCompletedState.State> getAll()
  {
    return orderProcessRepository.getAll();
  }

  private OrderCompletedState progressStateMachine(OrderCompletedDomainEvent orderCompletedDomainEvent,
                                                   OrderCompletedState currentState)
  {
    OrderCompletedState orderCompletedPrevState = currentState;
    OrderCompletedState orderCompletedState = currentState.nextState(orderCompletedDomainEvent);

    /* Progress through the state machine automatically as far as possible */
    while (!orderCompletedPrevState.equals(orderCompletedState))
    {
      orderCompletedPrevState = orderCompletedState;
      orderCompletedState = orderCompletedState.nextState(orderCompletedDomainEvent);
    }

    return orderCompletedState;
  }
}
