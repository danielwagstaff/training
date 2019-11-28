package com.triad.common.eventsourcing.aggregateroot;

import com.triad.common.eventsourcing.Event;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot implements Serializable
{
  public static final long NO_STREAM_POSITION = -1L;
  private static final long serialVersionUID = 1L;
  private final List<Event> uncommittedChanges = new ArrayList<>();

  private long lastStoredStreamPosition = NO_STREAM_POSITION;

  public AggregateRoot()
  {
  }

  public abstract UUID getId();

  public final void rehydrate(List<Event> events) throws UnableToApplyEventException
  {
    for (Event event : events)
    {
      applyChange(event, false);
      lastStoredStreamPosition = event.getStreamPosition();
    }
  }

  public final List<Event> getUncommittedChanges()
  {
    synchronized (uncommittedChanges)
    {
      List<Event> returnedChanges = new ArrayList<>(uncommittedChanges);
      uncommittedChanges.clear();
      return returnedChanges;
    }
  }

  public final long getLastStoredStreamPosition()
  {
    return lastStoredStreamPosition;
  }

  protected final void applyChange(final Event event) throws UnableToApplyEventException
  {
    applyChange(event, true);
  }

  private void applyChange(final Event event, final boolean isNew) throws UnableToApplyEventException
  {
    try
    {
      Method whenMethod = this.getClass().getDeclaredMethod("when", event.getClass());
      whenMethod.setAccessible(true);
      whenMethod.invoke(this, event);
      if (isNew)
      {
        synchronized (uncommittedChanges)
        {
          uncommittedChanges.add(event);
        }
      }
    }
    catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
    {
      throw new UnableToApplyEventException(e);
    }
  }
}
