package com.triad.training.triaddistribution.order.read.core.controllers;

import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import com.triad.training.triaddistribution.order.read.core.services.OrderQueryService;
import com.triad.training.triaddistribution.order.write.api.domain.events.OrderCompletedDomainEvent;
import io.smallrye.reactive.messaging.annotations.Channel;
import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
  private final OrderQueryService orderQueryService;
  @Inject @Channel("internal-stream-order") Publisher<OrderCompletedDomainEvent> orders;

  @Inject
  public OrderController(OrderQueryService orderQueryService)
  {
    this.orderQueryService = orderQueryService;
  }

  @GET
  @Path("")
  public Response getAllOrders()
  {
    LOGGER.info("GET all Orders");
    try
    {
      return Response.ok(orderQueryService.allOrders()).build();
    }
    catch (RetrieveFailedException e)
    {
      return exceptionToResponseEntity(e);
    }
  }

  @GET
  @Path("/customers")
  public Response getAllCustomers()
  {
    LOGGER.info("GET all Customers");
    try
    {
      return Response.ok(orderQueryService.allCustomers()).build();
    }
    catch (RetrieveFailedException e)
    {
      return exceptionToResponseEntity(e);
    }
  }

  @GET
  @Path("/{orderId}")
  public Response getOrder(@PathParam("orderId") String productId)
  {
    LOGGER.info("GET Order");
    try
    {
      UUID orderIdAsUuid = UUID.fromString(productId);
      return Response.ok(orderQueryService.getOrder(orderIdAsUuid)).build();
    }
    catch (RetrieveFailedException e)
    {
      return exceptionToResponseEntity(e);
    }
  }

  @GET
  @Path("/stream-order")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @SseElementType("application/json")
  public Publisher<OrderCompletedDomainEvent> getOrderStream()
  {
    return orders;
  }

  private Response exceptionToResponseEntity(Throwable throwable)
  {
    Response.Status status;
    if (throwable instanceof RetrieveFailedException)
    {
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }
    else
    {
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }
    LOGGER.error("{}", throwable);
    return Response.status(status).entity(throwable.getMessage()).build();
  }
}
