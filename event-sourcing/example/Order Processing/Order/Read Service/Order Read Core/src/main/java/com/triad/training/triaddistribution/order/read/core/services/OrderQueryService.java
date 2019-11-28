package com.triad.training.triaddistribution.order.read.core.services;

import com.triad.common.eventsourcing.eventstore.IEventStore;
import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import com.triad.training.triaddistribution.order.read.api.dto.OrderDto;
import com.triad.training.triaddistribution.order.write.api.eventsourcing.events.OrderCreated;
import com.triad.training.triaddistribution.order.read.api.dto.CustomerId;
import com.triad.training.triaddistribution.order.read.api.dto.OrderId;
import com.triad.training.triaddistribution.order.write.api.eventsourcing.events.ProductAddedToOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Dependent
public class OrderQueryService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderQueryService.class);
  private final IEventStore eventStore;

  @Inject
  public OrderQueryService(IEventStore eventStore)
  {
    this.eventStore = eventStore;
  }

  public List<OrderId> allOrders() throws RetrieveFailedException
  {
    LOGGER.info("Retrieving all Orders");

    return eventStore.retrieveAllEvents().stream().filter(event -> event instanceof OrderCreated).map(event -> {
      UUID orderId = ((OrderCreated) event).getOrderId();
      OrderId orderIdDto = new OrderId();
      orderIdDto.setOrderId(orderId);
      return orderIdDto;
    }).distinct().collect(Collectors.toList());
  }

  public List<CustomerId> allCustomers() throws RetrieveFailedException
  {
    LOGGER.info("Retrieving all Customers");

    return eventStore.retrieveAllEvents().stream().filter(event -> event instanceof OrderCreated).map(event -> {
      UUID customerId = ((OrderCreated) event).getCustomerId();
      CustomerId customerIdDto = new CustomerId();
      customerIdDto.setCustomerId(customerId);
      return customerIdDto;
    }).distinct().collect(Collectors.toList());
  }

  public OrderDto getOrder(UUID orderId) throws RetrieveFailedException
  {
    LOGGER.info("Retrieving Order");

    return eventStore.retrieveAllEvents().stream().filter(event -> {
      boolean correctId;
      if (event instanceof OrderCreated)  //TODO: for this reason, Event should contain the ID
      {
        correctId = ((OrderCreated) event).getOrderId().equals(orderId);
      }
      else if (event instanceof ProductAddedToOrder)
      {
        correctId = ((ProductAddedToOrder) event).getOrderId().equals(orderId);
      }
      else
      {
        correctId = false;
      }
      return correctId;
    }).reduce(new OrderDto(), (accOrderDto, event) -> {
      if (event instanceof OrderCreated)
      {
        accOrderDto.setOrderId(((OrderCreated) event).getOrderId());
      }
      else if(event instanceof ProductAddedToOrder)
      {
        accOrderDto.addProductId(((ProductAddedToOrder) event).getProductId());
      }
      return accOrderDto;
    }, (orderDto1, orderDto2) -> {
      orderDto1.getProductIds().addAll(orderDto2.getProductIds());
      return orderDto1;
    });
  }
}
