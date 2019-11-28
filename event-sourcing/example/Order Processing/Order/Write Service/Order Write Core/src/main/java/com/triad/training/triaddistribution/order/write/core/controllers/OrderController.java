package com.triad.training.triaddistribution.order.write.core.controllers;

import com.triad.common.eventsourcing.aggregateroot.UnableToApplyEventException;
import com.triad.common.eventsourcing.command.AggregateNotFoundException;
import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import com.triad.common.eventsourcing.eventstore.SaveFailedException;
import com.triad.training.triaddistribution.order.write.api.domain.commands.*;
import com.triad.training.triaddistribution.order.write.core.models.InvalidCommandRequestException;
import com.triad.training.triaddistribution.order.write.core.services.OrderCmdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
  private final OrderCmdService orderCmdService;

  @Inject
  public OrderController(OrderCmdService orderCmdService)
  {
    this.orderCmdService = orderCmdService;
  }

  @POST()
  @Path("")
  public Response cmdCreateNewOrder(CreateNewOrder createNewOrder)
  {
    LOGGER.info("Command Create New Order");
    try
    {
      UUID newOrderId = orderCmdService.cmd(createNewOrder);
      return Response.ok(newOrderId).build();
    }
    catch (SaveFailedException | UnableToApplyEventException e)
    {
      return exceptionToResponseEntity(e);
    }
  }

  @POST()
  @Path("/complete")
  public Response cmdCompleteOrder(CompleteOrder completeOrder)
  {
    LOGGER.info("Command Complete Order");
    try
    {
      orderCmdService.cmd(completeOrder);
      return Response.ok().build();
    }
    catch (InvalidCommandRequestException | AggregateNotFoundException | RetrieveFailedException | SaveFailedException | UnableToApplyEventException e)
    {
      return exceptionToResponseEntity(e);
    }
  }

  @POST()
  @Path("/cancel")
  public Response cmdCancelOrder(CancelOrder cancelOrder)
  {
    LOGGER.info("Command Cancel Order");
    try
    {
      orderCmdService.cmd(cancelOrder);
    }
    catch (RetrieveFailedException | SaveFailedException | InvalidCommandRequestException | AggregateNotFoundException | UnableToApplyEventException e)
    {
      return exceptionToResponseEntity(e);
    }
    return Response.ok().build();
  }

  @POST()
  @Path("/add-product")
  public Response cmdAddProductToOrder(AddProductToOrder addProductToOrder)
  {
    LOGGER.info("Command Add Product to Order");
    try
    {
      orderCmdService.cmd(addProductToOrder);
    }
    catch (RetrieveFailedException | SaveFailedException | InvalidCommandRequestException | AggregateNotFoundException | UnableToApplyEventException e)
    {
      return exceptionToResponseEntity(e);
    }
    return Response.ok().build();
  }

  @POST()
  @Path("/requery-products")
  public Response cmdReQueryProducts(RequeryProducts requeryProducts)
  {
    LOGGER.info("Command Re-Query Products");
    try
    {
      orderCmdService.cmd(requeryProducts);
    }
    catch (RetrieveFailedException | SaveFailedException | InvalidCommandRequestException | AggregateNotFoundException | UnableToApplyEventException e)
    {
      return exceptionToResponseEntity(e);
    }
    return Response.ok().build();
  }

  @POST()
  @Path("/remove-suspensions")
  public Response cmdRemoveSuspensions(RemoveSuspensionsFromOrder removeSuspensionsFromOrder)
  {
    LOGGER.info("Command Add Product to Order");
    try
    {
      orderCmdService.cmd(removeSuspensionsFromOrder);
    }
    catch (RetrieveFailedException | SaveFailedException | InvalidCommandRequestException | AggregateNotFoundException | UnableToApplyEventException e)
    {
      return exceptionToResponseEntity(e);
    }
    return Response.ok().build();
  }

  private Response exceptionToResponseEntity(Throwable throwable)
  {
    Status status;
    if (throwable instanceof SaveFailedException || throwable instanceof RetrieveFailedException || throwable instanceof UnableToApplyEventException)
    {
      status = Status.INTERNAL_SERVER_ERROR;
    }
    else if (throwable instanceof InvalidCommandRequestException)
    {
      status = Status.FORBIDDEN;
    }
    else if (throwable instanceof AggregateNotFoundException)
    {
      status = Status.NOT_FOUND;
    }
    else
    {
      status = Status.INTERNAL_SERVER_ERROR;
    }
    LOGGER.error("{}", throwable);
    return Response.status(status).entity(throwable.getMessage()).build();
  }
}
