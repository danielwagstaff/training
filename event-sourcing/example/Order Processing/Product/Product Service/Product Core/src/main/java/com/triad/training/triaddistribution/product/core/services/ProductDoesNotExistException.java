package com.triad.training.triaddistribution.product.core.services;

public class ProductDoesNotExistException extends Exception
{
  public ProductDoesNotExistException(String message)
  {
    super(message);
  }
}
