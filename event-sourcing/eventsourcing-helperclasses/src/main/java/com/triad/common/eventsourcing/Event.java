package com.triad.common.eventsourcing;

import java.io.Serializable;
import java.util.UUID;

public abstract class Event implements Serializable
{
  private static final long serialVersionUID = 1L;
  private UUID aggregateRootId;
  private long streamPosition;

  public Event()
  {
  }

  public UUID getAggregateRootId()
  {
    return aggregateRootId;
  }

  protected void setAggregateRootId(UUID aggregateRootId)
  {
    this.aggregateRootId = aggregateRootId;
  }

  public long getStreamPosition()
  {
    return this.streamPosition;
  }

  public void setStreamPosition(long streamPosition)
  {
    this.streamPosition = streamPosition;
  }
}
