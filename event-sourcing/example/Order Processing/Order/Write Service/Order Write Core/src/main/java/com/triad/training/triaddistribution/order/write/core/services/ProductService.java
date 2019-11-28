package com.triad.training.triaddistribution.order.write.core.services;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

  @ConfigProperty(name = "service.product.http.url") String productServiceUrl;

  public CompletionStage<Boolean> confirmProductExists(UUID productId)
  {
    try
    {
      URL url = new URL("http://" + productServiceUrl + "/products/" + productId);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");

      if(con.getResponseCode() < 300)
      {
        LOGGER.info("Call to Product service returned response code: {}, {}", con.getResponseCode(), con.getResponseMessage());
        return CompletableFuture.supplyAsync(() -> true);
      }
      else
      {
        LOGGER.info("Successfully called Product service, response code: {}", con.getResponseCode());
        return CompletableFuture.supplyAsync(() -> false);
      }
    }
    catch (IOException e)
    {
      LOGGER.error("Call to Product Service failed: {}", e);
      return CompletableFuture.supplyAsync(() -> false);
    }
  }
}
