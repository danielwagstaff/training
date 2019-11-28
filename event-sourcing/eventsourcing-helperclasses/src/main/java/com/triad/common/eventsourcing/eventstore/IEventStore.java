package com.triad.common.eventsourcing.eventstore;

import java.util.List;
import java.util.UUID;

import com.triad.common.eventsourcing.Event;

public interface IEventStore
{
  void saveEvents(final UUID id,
                  final List<Event> events,
                  final long expectedLastStreamPosition) throws SaveFailedException;

  List<Event> retrieveAllEvents() throws RetrieveFailedException;

  List<Event> retrieveEvents(final UUID id) throws RetrieveFailedException;
}
