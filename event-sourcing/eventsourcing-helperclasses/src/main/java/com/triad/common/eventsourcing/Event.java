package com.triad.common.eventsourcing;

import java.io.Serializable;

public abstract class Event implements Serializable
{
  private static final long serialVersionUID = 1L;
  private long streamPosition;

  public Event()
  {
  }

  public void setStreamPosition(long streamPosition)
  {
    this.streamPosition = streamPosition;
  }

  public long getStreamPosition()
  {
    return this.streamPosition;
  }
}
