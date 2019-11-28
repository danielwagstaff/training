package com.triad.training.triaddistribution.process.core.controllers;

import com.triad.training.triaddistribution.process.api.OrderCompletedStateDto;
import com.triad.training.triaddistribution.process.core.models.OrderCompletedState;
import com.triad.training.triaddistribution.process.core.services.OrderCompletedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
  private final OrderCompletedService orderCompletedService;

  @Inject
  public OrderController(OrderCompletedService orderCompletedService)
  {
    this.orderCompletedService = orderCompletedService;
  }

  @GET()
  @Path("/completed")
  public Response getAllCompleted()
  {
    LOGGER.info("GET All Completed Orders");
    return Response.ok(orderCompletedService.getAll()
                                            .entrySet()
                                            .stream()
                                            .map(entry -> mapper(entry.getKey(), entry.getValue()))
                                            .collect(Collectors.toList())).build();
  }

  private OrderCompletedStateDto mapper(UUID orderId, OrderCompletedState.State state)
  {
    return new OrderCompletedStateDto(orderId, state.toString());
  }
}
