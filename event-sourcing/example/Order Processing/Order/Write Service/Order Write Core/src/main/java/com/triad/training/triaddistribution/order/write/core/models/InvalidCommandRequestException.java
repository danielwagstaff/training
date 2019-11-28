package com.triad.training.triaddistribution.order.write.core.models;

public class InvalidCommandRequestException extends Exception
{
  private static final long serialVersionUID = 1L;

  public InvalidCommandRequestException(String message)
  {
    super(message);
  }
}
