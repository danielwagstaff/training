package com.triad.common.eventsourcing.repository;

import com.triad.common.eventsourcing.Event;
import com.triad.common.eventsourcing.aggregateroot.AggregateRoot;
import com.triad.common.eventsourcing.aggregateroot.UnableToApplyEventException;
import com.triad.common.eventsourcing.eventstore.IEventStore;
import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import com.triad.common.eventsourcing.eventstore.SaveFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AggregateRootRepository<R extends AggregateRoot>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(AggregateRootRepository.class);
  private Class<R> clazz;
  private IEventStore eventStore;

  protected AggregateRootRepository(Class<R> clazz, IEventStore eventStore)
  {
    this.clazz = clazz;
    this.eventStore = eventStore;
  }

  public void save(R aggregateRoot) throws SaveFailedException
  {
    LOGGER.info("Saving events");
    eventStore.saveEvents(aggregateRoot.getId(),
                          aggregateRoot.getUncommittedChanges(),
                          aggregateRoot.getLastStoredStreamPosition());
  }

  public Optional<R> findById(UUID id) throws RetrieveFailedException
  {
    try
    {
      LOGGER.info("Retrieveing events for ID: {}", id);
      List<Event> retrievedEvents = eventStore.retrieveEvents(id);

      if (!retrievedEvents.isEmpty())
      {
        R aggregateRoot;
        aggregateRoot = clazz.getConstructor().newInstance();
        aggregateRoot.rehydrate(retrievedEvents);
        return Optional.of(aggregateRoot);
      }
      else
      {
        return Optional.empty();
      }
    }
    catch (UnableToApplyEventException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
    {
      throw new RetrieveFailedException(e);
    }
  }
}
