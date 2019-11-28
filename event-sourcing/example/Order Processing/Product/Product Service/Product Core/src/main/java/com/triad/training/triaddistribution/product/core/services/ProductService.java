package com.triad.training.triaddistribution.product.core.services;

import com.triad.training.triaddistribution.product.core.models.ProductReservation;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductService
{
  private List<UUID> products = new ArrayList<>();
  private List<ProductReservation> productReservations = new ArrayList<>();

  public CompletionStage<List<UUID>> getAll()
  {
    return CompletableFuture.supplyAsync(() -> products);
  }

  public CompletionStage<Optional<UUID>> get(UUID productId)
  {
    return CompletableFuture.supplyAsync(() -> {
      if (products.contains(productId))
      {
        return Optional.of(productId);
      }
      else
      {
        return Optional.empty();
      }
    });
  }

  public CompletableFuture<Void> addProduct(UUID productId)
  {
    return CompletableFuture.runAsync(() -> products.add(productId));
  }

  public CompletableFuture<Void> reserveProduct(UUID productId, int qty) throws ProductDoesNotExistException
  {
    if (products.contains(productId))
    {
      return CompletableFuture.runAsync(() -> productReservations.add(new ProductReservation(productId, qty)));
    }
    else
    {
      throw new ProductDoesNotExistException("Product " + productId + "does not exist");
    }
  }

  public CompletionStage<List<ProductReservation>> getAllReservations()
  {
    return CompletableFuture.supplyAsync(() -> productReservations);
  }
}
