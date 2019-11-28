package com.triad.training.triaddistribution.process.core.services;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

  @ConfigProperty(name = "service.product.http.url") String productServiceUrl;

  public CompletionStage<Boolean> reserveProduct(UUID productId, int quantity)
  {
    HttpURLConnection con = null;

    try
    {
      URL url = new URL("http://" + productServiceUrl + "/products/" + productId + "/reserve");
      con = (HttpURLConnection) url.openConnection();
      con.setDoOutput(true);
      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Java client");
      con.setRequestProperty("Content-Type",  "application/json; utf-8");

      try (DataOutputStream wr = new DataOutputStream(con.getOutputStream()))
      {
        wr.write(Integer.toString(quantity).getBytes());
      }

      LOGGER.info("Calling {}", url);

      if (con.getResponseCode() < 300)
      {
        LOGGER.info("Call to Product service returned response code: {}, {}",
                    con.getResponseCode(),
                    con.getResponseMessage());
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
    finally
    {
      if (con != null)
      {
        con.disconnect();
      }
    }
  }
}
