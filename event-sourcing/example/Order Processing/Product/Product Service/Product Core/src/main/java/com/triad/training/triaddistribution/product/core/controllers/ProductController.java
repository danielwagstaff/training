package com.triad.training.triaddistribution.product.core.controllers;

import com.triad.training.triaddistribution.product.api.dto.ProductIdDto;
import com.triad.training.triaddistribution.product.api.dto.ProductReservationDto;
import com.triad.training.triaddistribution.product.core.services.ProductDoesNotExistException;
import com.triad.training.triaddistribution.product.core.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
  private final ProductService productService;

  @Inject
  public ProductController(ProductService productService)
  {
    this.productService = productService;
  }

  @GET()
  @Path("")
  public CompletionStage<Response> getAllProducts()
  {
    LOGGER.info("GET All Products");
    return CompletableFuture.supplyAsync(() -> {
      try
      {
        return Response.ok(productService.getAll()).build();
      }
      catch (Exception e)
      {
        return Response.serverError().build();
      }
    });
  }

  @GET()
  @Path("/{productId}")
  public CompletionStage<Response> getProduct(@PathParam("productId") String productId)
  {
    LOGGER.info("GET Product by ID :{}", productId);
    try
    {
      UUID productIdAsUuid = UUID.fromString(productId);
      return productService.get(productIdAsUuid).thenApply(optionalProductId -> {
        if (optionalProductId.isPresent())
        {
          ProductIdDto productIdDto = new ProductIdDto();
          productIdDto.setId(optionalProductId.get());
          return Response.ok(productIdDto).build();
        }
        else
        {
          return Response.status(Response.Status.NOT_FOUND).build();
        }
      });
    }
    catch (IllegalArgumentException e)
    {
      return CompletableFuture.supplyAsync(() -> Response.status(Response.Status.BAD_REQUEST).build());
    }
  }

  @POST()
  @Path("")
  public CompletionStage<Response> addProduct(ProductIdDto productIdDto)
  {
    LOGGER.info("POST Add Product");
    return CompletableFuture.supplyAsync(() -> {
      try
      {
        return Response.ok(productService.addProduct(productIdDto.getId())).build();
      }
      catch (Exception e)
      {
        return Response.serverError().build();
      }
    });
  }

  @POST()
  @Path("/{productId}/reserve/")
  public CompletionStage<Response> reserveProduct(@PathParam("productId") String productId, int quantity)
  {
    LOGGER.info("POST Reserve Product");
    return CompletableFuture.supplyAsync(() -> {
      try
      {
        UUID productIdAsUuid = UUID.fromString(productId);
        return Response.ok(productService.reserveProduct(productIdAsUuid, quantity)).build();
      }
      catch (ProductDoesNotExistException e)
      {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
      catch (Exception e)
      {
        return Response.serverError().build();
      }
    });
  }

  @GET()
  @Path("/reservations/{productId}")
  public CompletionStage<Response> getAllReservations(@PathParam("productId") String productId)
  {
    LOGGER.info("GET All Product Reservations");
    try
    {
      return productService.getAllReservations().thenApply(reservations -> {
        List<ProductReservationDto> productReservationDtos = reservations.stream()
                                                                         .filter(prod -> prod.getProductId()
                                                                                             .equals(UUID.fromString(
                                                                                                 productId)))
                                                                         .map(reservation -> new ProductReservationDto(
                                                                             reservation.getProductId(),
                                                                             reservation.getQuantity()))
                                                                         .collect(Collectors.toList());
        return Response.ok(productReservationDtos).build();
      });
    }
    catch (IllegalArgumentException e)
    {
      return CompletableFuture.supplyAsync(() -> Response.status(Response.Status.BAD_REQUEST).build());
    }
  }
}
