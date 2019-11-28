package com.triad.training.triaddistribution.order.write.core.services;

import com.triad.common.eventsourcing.aggregateroot.UnableToApplyEventException;
import com.triad.common.eventsourcing.command.AggregateNotFoundException;
import com.triad.common.eventsourcing.command.CommandHandler;
import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import com.triad.common.eventsourcing.eventstore.SaveFailedException;
import com.triad.training.triaddistribution.order.write.api.domain.commands.*;
import com.triad.training.triaddistribution.order.write.core.models.InvalidCommandRequestException;
import com.triad.training.triaddistribution.order.write.core.models.OrderAggregateRoot;
import com.triad.training.triaddistribution.order.write.core.repositories.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@Dependent
public class OrderCmdService implements CommandHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderCmdService.class);
  private static final String CANNOT_FIND_AGGREGATE_ROOT = "Could not find aggregate root for id: ";
  private final OrderRepository orderRepository;
  private final ProductService productService;
  private final EventPublishService eventPublishService;

  @Inject
  public OrderCmdService(OrderRepository orderRepository,
                         ProductService productService,
                         EventPublishService eventPublishService)
  {
    this.orderRepository = orderRepository;
    this.productService = productService;
    this.eventPublishService = eventPublishService;
  }

  public UUID cmd(CreateNewOrder createNewOrder) throws SaveFailedException, UnableToApplyEventException
  {
    LOGGER.info("Command Create New Order");

    OrderAggregateRoot order = new OrderAggregateRoot(UUID.randomUUID(),
                                                      createNewOrder.getCreatorId(),
                                                      createNewOrder.getCustomerId());
    orderRepository.save(order);

    return order.getId();
  }

  public void cmd(CompleteOrder completeOrder) throws
                                               RetrieveFailedException,
                                               SaveFailedException,
                                               InvalidCommandRequestException,
                                               AggregateNotFoundException,
                                               UnableToApplyEventException
  {
    LOGGER.info("Command Complete Order");

    Optional<OrderAggregateRoot> optOrder = orderRepository.findById(completeOrder.getOrderId());
    if (optOrder.isPresent())
    {
      OrderAggregateRoot order = optOrder.get();
      order.completeOrder(completeOrder.getRequestorId(), eventPublishService);
      orderRepository.save(order);
    }
    else
    {
      throw new AggregateNotFoundException(CANNOT_FIND_AGGREGATE_ROOT + completeOrder.getOrderId());
    }
  }

  public void cmd(CancelOrder cancelOrder) throws
                                           RetrieveFailedException,
                                           SaveFailedException,
                                           InvalidCommandRequestException,
                                           AggregateNotFoundException,
                                           UnableToApplyEventException
  {
    LOGGER.info("Command Cancel Order");

    Optional<OrderAggregateRoot> optOrder = orderRepository.findById(cancelOrder.getOrderId());
    if (optOrder.isPresent())
    {
      OrderAggregateRoot order = optOrder.get();
      order.cancelOrder(cancelOrder.getRequestorId());
      orderRepository.save(order);
    }
    else
    {
      throw new AggregateNotFoundException(CANNOT_FIND_AGGREGATE_ROOT + cancelOrder.getOrderId());
    }
  }

  public void cmd(AddProductToOrder addProductToOrder) throws
                                                       RetrieveFailedException,
                                                       SaveFailedException,
                                                       InvalidCommandRequestException,
                                                       AggregateNotFoundException,
                                                       UnableToApplyEventException
  {
    LOGGER.info("Command Add Product to Order");
    Optional<OrderAggregateRoot> optOrder = orderRepository.findById(addProductToOrder.getOrderId());
    if (optOrder.isPresent())
    {
      OrderAggregateRoot order = optOrder.get();
      order.addProductToOrder(addProductToOrder.getProductId(), addProductToOrder.getQuantity(), productService);
      orderRepository.save(order);
    }
    else
    {
      throw new AggregateNotFoundException(CANNOT_FIND_AGGREGATE_ROOT + addProductToOrder.getOrderId());
    }
  }

  public void cmd(RemoveSuspensionsFromOrder removeSuspensionsFromOrder) throws
                                                                         RetrieveFailedException,
                                                                         SaveFailedException,
                                                                         InvalidCommandRequestException,
                                                                         AggregateNotFoundException,
                                                                         UnableToApplyEventException
  {
    LOGGER.info("Command Remove Suspension From Order");
    Optional<OrderAggregateRoot> optOrder = orderRepository.findById(removeSuspensionsFromOrder.getOrderId());
    if (optOrder.isPresent())
    {
      OrderAggregateRoot order = optOrder.get();
      order.attemptToRemoveSuspensions();
      orderRepository.save(order);
    }
    else
    {
      throw new AggregateNotFoundException(CANNOT_FIND_AGGREGATE_ROOT + removeSuspensionsFromOrder.getOrderId());
    }
  }

  public void cmd(RequeryProducts requeryProducts) throws
                                                   RetrieveFailedException,
                                                   SaveFailedException,
                                                   InvalidCommandRequestException,
                                                   AggregateNotFoundException,
                                                   UnableToApplyEventException
  {
    LOGGER.info("Command Re-query products");
    Optional<OrderAggregateRoot> optOrder = orderRepository.findById(requeryProducts.getOrderId());
    if (optOrder.isPresent())
    {
      OrderAggregateRoot order = optOrder.get();
      order.reQueryProducts(productService);
      orderRepository.save(order);
    }
    else
    {
      throw new AggregateNotFoundException(CANNOT_FIND_AGGREGATE_ROOT + requeryProducts.getOrderId());
    }
  }
}
