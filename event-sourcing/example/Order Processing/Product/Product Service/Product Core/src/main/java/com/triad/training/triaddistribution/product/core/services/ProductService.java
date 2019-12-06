package com.triad.training.triaddistribution.product.core.services;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductService
{
  private Map<UUID, Integer> productToAvailableQuantity = new HashMap<>();
  private Map<UUID, Integer> productReservations = new HashMap<>();

  public CompletionStage<Set<UUID>> getAll()
  {
    return CompletableFuture.supplyAsync(() -> productToAvailableQuantity.keySet());
  }

  public CompletionStage<Optional<Integer>> get(UUID productId)
  {
    return CompletableFuture.supplyAsync(() -> Optional.ofNullable(productToAvailableQuantity.get(productId)));
  }

  public CompletableFuture<Void> addProduct(UUID productId, int quantity)
  {
    return CompletableFuture.runAsync(() -> productToAvailableQuantity.merge(productId,
                                                                             quantity,
                                                                             (currentQty, newQty) -> currentQty + newQty));
  }

  public CompletableFuture<Void> reserveProduct(UUID productId, int reserveQty) throws
                                                                                ProductDoesNotExistException,
                                                                                ProductNotAvailableException
  {
    Integer currentQty = productToAvailableQuantity.get(productId);
    if (currentQty != null)
    {
      int qtyAfterReservation = currentQty - reserveQty;
      if (qtyAfterReservation >= 0)
      {
        return CompletableFuture.runAsync(() -> {
          productToAvailableQuantity.replace(productId, qtyAfterReservation);
          productReservations.merge(productId,
                                    reserveQty,
                                    (currentReservedQty, toReserveQty) -> currentReservedQty + toReserveQty);
        });
      }
      else
      {
        throw new ProductNotAvailableException("Product " + productId + " only has " + currentQty + " available, but attempted to reserve " + reserveQty);
      }
    }
    else
    {
      throw new ProductDoesNotExistException("Product " + productId + "does not exist");
    }
  }

  public CompletionStage<Map<UUID, Integer>> getAllReservations()
  {
    return CompletableFuture.supplyAsync(() -> productReservations);
  }
}
