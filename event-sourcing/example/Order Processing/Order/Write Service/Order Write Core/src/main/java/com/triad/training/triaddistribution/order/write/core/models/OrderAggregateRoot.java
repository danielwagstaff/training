package com.triad.training.triaddistribution.order.write.core.models;

import com.triad.common.eventsourcing.Event;
import com.triad.common.eventsourcing.aggregateroot.AggregateRoot;
import com.triad.common.eventsourcing.aggregateroot.UnableToApplyEventException;
import com.triad.common.eventsourcing.preprocessor.processor.AggregateRootCommand;
import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import com.triad.training.triaddistribution.order.write.api.domain.models.Product;
import com.triad.training.triaddistribution.order.write.api.eventsourcing.events.*;
import com.triad.training.triaddistribution.order.write.core.services.EventPublishService;
import com.triad.training.triaddistribution.order.write.core.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class OrderAggregateRoot extends AggregateRoot
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderAggregateRoot.class);
  private static final long serialVersionUID = 1L;

  private static final String INVALID_CMD_CLOSED_ORDER = "Cannot perform this command on a closed order";
  private static final String INVALID_CMD_NOT_CREATOR = "Request must be made by order creator";
  private static final String INVALID_CMD_PRODUCTS_NOT_RESERVED = "To perform this action, remaining products must be reserved: ";
  private static final String INVALID_CMD_ORDER_IN_SUSPENDED_STATE = "To perform this action, suspended states must be resolved: ";

  private UUID id;
  private UUID creatorId;
  private Map<UUID, Boolean> productExistence = new HashMap<>();
  private Map<UUID, Integer> productQuantities = new HashMap<>();
  private Set<OrderSuspended.Reason> suspendedReasons = new HashSet<>();
  private boolean orderClosed = false;

  @SuppressWarnings("unused") // used via reflection
  public OrderAggregateRoot()
  {
    /* For reflection construction */
  }

  @AggregateRootCommand(raisesEvents = { OrderCreated.class })
  public OrderAggregateRoot(UUID id, UUID creatorId, UUID customerId) throws UnableToApplyEventException
  {
    Event event = new OrderCreated(id, creatorId, customerId);
    this.applyChange(event);
  }

  /* Event Handlers ------------------------------------------------------------------------------------------------- */

  @Override
  public UUID getId()
  {
    return id;
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(OrderCreated orderCreated)
  {
    orderClosed = false;
    this.id = orderCreated.getOrderId();
    this.creatorId = orderCreated.getCreatorId();
    LOGGER.info("Order ({}) created by {} for {}",
                orderCreated.getOrderId(),
                orderCreated.getCreatorId(),
                orderCreated.getCustomerId());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(OrderCompleted orderCompleted)
  {
    orderClosed = true;
    LOGGER.info("Order ({}) completed", orderCompleted.getOrderId());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(OrderCancelled orderCancelled)
  {
    orderClosed = true;
    LOGGER.info("Order ({}) cancelled", orderCancelled.getOrderId());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(ProductAddedToOrder productAddedToOrder)
  {
    int currentQuantity = productQuantities.getOrDefault(productAddedToOrder.getProductId(), 0);
    productQuantities.put(productAddedToOrder.getProductId(), currentQuantity + productAddedToOrder.getQuantity());
    LOGGER.info("Order ({}) product added: {} x {}",
                productAddedToOrder.getOrderId(),
                productAddedToOrder.getQuantity(),
                productAddedToOrder.getProductId());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(ProductExistenceConfirmed productExistenceConfirmed)
  {
    productExistence.put(productExistenceConfirmed.getProductId(), true);
    LOGGER.info("Order ({}) product existence confirmed for product {}",
                productExistenceConfirmed.getOrderId(),
                productExistenceConfirmed.getProductId());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(ProductNonExistenceConfirmed productNonExistenceConfirmed)
  {
    productExistence.put(productNonExistenceConfirmed.getProductId(), false);
    LOGGER.info("Order ({}) product ({}) does not exist",
                productNonExistenceConfirmed.getOrderId(),
                productNonExistenceConfirmed.getProductId());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(OrderSuspended orderSuspended)
  {
    suspendedReasons.add(orderSuspended.getReason());
    LOGGER.info("Order ({}) suspended for reason: {}", orderSuspended.getOrderId(), orderSuspended.getReason());
  }

  @SuppressWarnings("unused") // used via reflection
  private void when(SuspensionCleared suspensionCleared)
  {
    suspendedReasons.remove(suspensionCleared.getSuspensionReason());
    LOGGER.info("Order ({}) suspension ({}) cleared",
                suspensionCleared.getOrderId(),
                suspensionCleared.getSuspensionReason());
  }

  /* Commands ------------------------------------------------------------------------------------------------------- */

  @AggregateRootCommand(raisesEvents = { OrderCompleted.class })
  public void completeOrder(UUID requestorId, EventPublishService eventPublishService) throws
                                                                                       InvalidCommandRequestException,
                                                                                       UnableToApplyEventException
  {
    if (!orderClosed)
    {
      if (suspendedReasons.isEmpty())
      {
        if (creatorId.equals(requestorId))
        {
          Set<UUID> allProductsNotYetConfirmed = getAllProductsNotConfirmedAsExisting();
          if (allProductsNotYetConfirmed.isEmpty())
          {
            Event event = new OrderCompleted(id);
            this.applyChange(event);

            LOGGER.info("Publishing Order Completed message");
            List<Product> products = productQuantities.entrySet()
                                                      .stream()
                                                      .map(entry -> new Product(entry.getKey(), entry.getValue()))
                                                      .collect(Collectors.toList());
            OrderCompletedDomainEvent orderCompletedEvent = new OrderCompletedDomainEvent(id, creatorId, products);
            eventPublishService.produce(orderCompletedEvent);
          }
          else
          {
            throw new InvalidCommandRequestException(INVALID_CMD_PRODUCTS_NOT_RESERVED + allProductsNotYetConfirmed);
          }
        }
        else
        {
          throw new InvalidCommandRequestException(INVALID_CMD_NOT_CREATOR);
        }
      }
      else
      {
        throw new InvalidCommandRequestException(INVALID_CMD_ORDER_IN_SUSPENDED_STATE + suspendedReasons.toString());
      }
    }
    else
    {
      throw new InvalidCommandRequestException(INVALID_CMD_CLOSED_ORDER);
    }
  }

  @AggregateRootCommand(raisesEvents = { OrderCancelled.class })
  public void cancelOrder(UUID requestorId) throws InvalidCommandRequestException, UnableToApplyEventException
  {
    if (!orderClosed)
    {
      if (creatorId.equals(requestorId))
      {
        Event event = new OrderCancelled(id);
        this.applyChange(event);
      }
      else
      {
        throw new InvalidCommandRequestException(INVALID_CMD_NOT_CREATOR);
      }
    }
    else
    {
      throw new InvalidCommandRequestException(INVALID_CMD_CLOSED_ORDER);
    }
  }

  @AggregateRootCommand(raisesEvents = { ProductAddedToOrder.class,
                                         ProductExistenceConfirmed.class,
                                         ProductNonExistenceConfirmed.class,
                                         OrderSuspended.class })
  public void addProductToOrder(UUID productId, int quantity, ProductService productService) throws
                                                                                             InvalidCommandRequestException,
                                                                                             UnableToApplyEventException
  {
    if (!orderClosed)
    {
      Event event = new ProductAddedToOrder(id, productId, quantity);
      this.applyChange(event);

      if (!productExistence.containsKey(productId))
      {
        Optional<Boolean> optProductExists = productExists(productId, productService);
        if (optProductExists.isPresent())
        {
          if (Boolean.TRUE.equals(optProductExists.get()))
          {
            Event evntProductExists = new ProductExistenceConfirmed(id, productId);
            this.applyChange(evntProductExists);
          }
          else
          {
            Event evntProductDoesNotExist = new ProductNonExistenceConfirmed(id, productId);
            this.applyChange(evntProductDoesNotExist);

            Event evntOrderSuspended = new OrderSuspended(id,
                                                          OrderSuspended.Reason.PRODUCT_ADDED_TO_ORDER_DOES_NOT_EXIST);
            this.applyChange(evntOrderSuspended);
          }
        }
      }
    }
    else
    {
      throw new InvalidCommandRequestException(INVALID_CMD_CLOSED_ORDER);
    }
  }

  @AggregateRootCommand(raisesEvents = { ProductExistenceConfirmed.class,
                                         ProductNonExistenceConfirmed.class,
                                         OrderSuspended.class })
  public void reQueryProducts(ProductService productService) throws
                                                             InvalidCommandRequestException,
                                                             UnableToApplyEventException
  {
    if (!orderClosed)
    {
      for (Map.Entry<UUID, Boolean> productExistsEntry : productExistence.entrySet())
      {
        Optional<Boolean> optProductExists = productExists(productExistsEntry.getKey(), productService);
        if (optProductExists.isPresent())
        {
          boolean productExists = optProductExists.get();
          boolean productPreviouslyExisted = productExistsEntry.getValue();
          if (productPreviouslyExisted && !productExists)
          {
            Event evntProductDoesNotExist = new ProductNonExistenceConfirmed(id, productExistsEntry.getKey());
            this.applyChange(evntProductDoesNotExist);

            Event evntOrderSuspended = new OrderSuspended(id,
                                                          OrderSuspended.Reason.PRODUCT_ADDED_TO_ORDER_DOES_NOT_EXIST);
            this.applyChange(evntOrderSuspended);
          }
          else if (!productPreviouslyExisted && productExists)
          {
            Event evntProductExists = new ProductExistenceConfirmed(id, productExistsEntry.getKey());
            this.applyChange(evntProductExists);
          }
        }
      }
    }
    else
    {
      throw new InvalidCommandRequestException(INVALID_CMD_CLOSED_ORDER);
    }
  }

  @AggregateRootCommand(raisesEvents = { SuspensionCleared.class })
  public void attemptToRemoveSuspensions() throws InvalidCommandRequestException, UnableToApplyEventException
  {
    if (!suspendedReasons.isEmpty())
    {
      for (OrderSuspended.Reason suspensionReason : suspendedReasons)
      {
        if (suspensionCanBeRemoved(suspensionReason))
        {
          Event event = new SuspensionCleared(id, suspensionReason);
          this.applyChange(event);
        }
        else
        {
          throw new InvalidCommandRequestException("Cannot remove suspension: " + suspensionReason);
        }
      }
    }
    else
    {
      throw new InvalidCommandRequestException("Cannot remove suspension from order with no current suspensions");
    }
  }

  /* Helper Methods ------------------------------------------------------------------------------------------------- */

  private boolean suspensionCanBeRemoved(OrderSuspended.Reason suspensionReason)
  {
    boolean suspensionRemoved;
    switch (suspensionReason)
    {
      case PRODUCT_ADDED_TO_ORDER_DOES_NOT_EXIST:
        suspensionRemoved = getAllProductsNotConfirmedAsExisting().isEmpty();
        break;
      default:
        LOGGER.error("Unhandled suspension reason: {}", suspensionReason);
        suspensionRemoved = false;
        break;
    }
    return suspensionRemoved;
  }

  /*
   * This call is made with a 500ms timeout, so a timely response can be given to the caller.
   * */
  private Optional<Boolean> productExists(UUID productId, ProductService productService)
  {
    try
    {
      return productService.confirmProductExists(productId)
                           .thenApply(Optional::of)
                           .toCompletableFuture()
                           .get(500L, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException | ExecutionException e)
    {
      LOGGER.error("Failed to call Product service. {}", e);
    }
    catch (TimeoutException e)
    {
      LOGGER.info("Failed to receive response from Product service before timeout");
    }
    return Optional.empty();
  }

  private Set<UUID> getAllProductsNotConfirmedAsExisting()
  {
    Set<UUID> productsConfirmedAsNonExistent = new HashSet<>();
    for (Map.Entry<UUID, Boolean> productExistsEntry : productExistence.entrySet())
    {
      if (Boolean.FALSE.equals(productExistsEntry.getValue()))
      {
        productsConfirmedAsNonExistent.add(productExistsEntry.getKey());
      }
    }
    return productsConfirmedAsNonExistent;
  }
}
