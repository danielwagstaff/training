package com.triad.common.eventsourcing.eventstore;

public class SaveFailedException extends Exception
{
  private static final long serialVersionUID = 1L;

  public SaveFailedException(String message)
  {
    super(message);
  }

  public SaveFailedException(Throwable exception)
  {
    super(exception);
  }

  public SaveFailedException(String message, Throwable exception)
  {
    super(message, exception);
  }
}
