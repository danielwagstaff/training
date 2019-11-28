package com.triad.common.eventsourcing.eventstore;

public class RetrieveFailedException extends Exception
{
  private static final long serialVersionUID = 1L;

  public RetrieveFailedException(String message)
  {
    super(message);
  }

  public RetrieveFailedException(Throwable exception)
  {
    super(exception);
  }

  public RetrieveFailedException(String message, Throwable exception)
  {
    super(message, exception);
  }
}
