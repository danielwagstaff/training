package com.triad.training.triaddistribution.order.write.core.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.TEXT_HTML)
public class PingController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

  @GET()
  @Path("")
  public Response index()
  {
    LOGGER.info("GET ping");

    return Response.ok("pong").build();
  }
}
