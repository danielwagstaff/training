package com.triad.common.eventsourcing.command;

public class AggregateNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public AggregateNotFoundException(String message) {
    super(message);
  }

  public AggregateNotFoundException(Throwable exception) {
    super(exception);
  }

  public AggregateNotFoundException(String message, Throwable exception) {
    super(message, exception);
  }
}
