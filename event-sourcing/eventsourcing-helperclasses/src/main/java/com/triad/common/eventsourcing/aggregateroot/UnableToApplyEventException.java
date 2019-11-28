package com.triad.common.eventsourcing.aggregateroot;

public class UnableToApplyEventException extends Exception
{
  private static final long serialVersionUID = 1L;

  public UnableToApplyEventException(Throwable exception)
  {
    super(exception);
  }

  public UnableToApplyEventException(String message, Throwable exception)
  {
    super(message, exception);
  }
}
