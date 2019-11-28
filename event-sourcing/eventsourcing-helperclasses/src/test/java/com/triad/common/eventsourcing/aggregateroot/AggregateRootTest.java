package com.triad.common.eventsourcing.aggregateroot;

import com.triad.common.eventsourcing.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class AggregateRootTest
{
  private FakeAggregateRoot aggregateRoot;

  @Before
  public void setUp()
  {
    aggregateRoot = new FakeAggregateRoot();
  }

  @Test
  public void shouldReturnId()
  {
    assertThat(aggregateRoot.getId(), instanceOf(UUID.class));
  }

  @Test
  public void shouldNotProduceUncommittedChangesWhenRehydrating()
  {
    List<Event> hydratingEvents = new ArrayList<>();
    hydratingEvents.add(new EventA());
    hydratingEvents.add(new EventB());

    try
    {
      aggregateRoot.rehydrate(hydratingEvents);
      assertEquals(0, aggregateRoot.getUncommittedChanges().size());
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldApplyAllEventsWhenRehydrating()
  {
    List<Event> hydratingEvents = new ArrayList<>();
    hydratingEvents.add(new DetectableEvent());
    hydratingEvents.add(new DetectableEvent());
    hydratingEvents.add(new DetectableEvent());
    try
    {
      aggregateRoot.rehydrate(hydratingEvents);
      assertEquals(hydratingEvents.size(), aggregateRoot.numberOfTimesDetectableEventReceived);
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldApplyAllEventsWhenViaCommand()
  {
    List<Event> events = new ArrayList<>();
    events.add(new DetectableEvent());
    events.add(new DetectableEvent());
    events.add(new DetectableEvent());
    try
    {
      aggregateRoot.fakeCmdRaisesGivenEvents(events);
      assertEquals(events.size(), aggregateRoot.numberOfTimesDetectableEventReceived);
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldApplyAllEventsWhenViaCommandAndWhenRehydrating()
  {
    List<Event> events = new ArrayList<>();
    events.add(new DetectableEvent());
    events.add(new DetectableEvent());
    events.add(new DetectableEvent());
    List<Event> hydratingEvents = new ArrayList<>();
    hydratingEvents.add(new DetectableEvent());
    hydratingEvents.add(new DetectableEvent());
    hydratingEvents.add(new DetectableEvent());
    try
    {
      aggregateRoot.rehydrate(hydratingEvents);
      aggregateRoot.fakeCmdRaisesGivenEvents(events);
      assertEquals(events.size() + hydratingEvents.size(), aggregateRoot.numberOfTimesDetectableEventReceived);
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldReturnLastStreamPositionAccordingToLastEvent()
  {
    List<Event> hydratingEvents = new ArrayList<>();
    EventA eventA0 = new EventA();
    eventA0.setStreamPosition(10L);
    hydratingEvents.add(eventA0);
    EventA eventA1 = new EventA();
    eventA1.setStreamPosition(100L);
    hydratingEvents.add(eventA1);

    try
    {
      aggregateRoot.rehydrate(hydratingEvents);
      assertEquals(100L, aggregateRoot.getLastStoredStreamPosition());
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldReturnAllEventsProducedByCommandAsUncommittedChanges()
  {
    EventA eventA0 = new EventA();
    EventA eventA1 = new EventA();
    List<Event> events = new ArrayList<>();
    events.add(eventA0);
    events.add(eventA1);
    try
    {
      aggregateRoot.fakeCmdRaisesGivenEvents(events);
      int numUncommittedChanges = aggregateRoot.getUncommittedChanges().size();
      assertEquals(2, numUncommittedChanges);
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldClearUncommittedChangesOnceRetrieved()
  {
    EventA eventA0 = new EventA();
    EventA eventA1 = new EventA();
    List<Event> events = new ArrayList<>();
    events.add(eventA0);
    events.add(eventA1);
    try
    {
      aggregateRoot.fakeCmdRaisesGivenEvents(events);
      int numUncommittedChangesBefore = aggregateRoot.getUncommittedChanges().size();
      int numUncommittedChangesAfter = aggregateRoot.getUncommittedChanges().size();
      assertEquals(2, numUncommittedChangesBefore);
      assertEquals(0, numUncommittedChangesAfter);
    }
    catch (UnableToApplyEventException e)
    {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldThrowExceptionIfWhenMethodDoesNotExistForEventType()
  {
    EventC eventC0 = new EventC();
    List<Event> events = new ArrayList<>();
    events.add(eventC0);
    try
    {
      aggregateRoot.fakeCmdRaisesGivenEvents(events);
      fail("Exception should have been thrown");
    }
    catch (UnableToApplyEventException e)
    {
      /* Exception thrown as expected */
    }
  }

  private class FakeAggregateRoot extends AggregateRoot
  {
    int numberOfTimesDetectableEventReceived = 0;

    @Override
    public UUID getId()
    {
      return UUID.randomUUID();
    }

    /* Event Handlers ----------------------------------------------------------------------------------------------- */

    @SuppressWarnings("unused")
    private void when(EventA eventA)
    {
    }

    @SuppressWarnings("unused")
    private void when(EventB eventB)
    {
    }

    @SuppressWarnings("unused")
    private void when(DetectableEvent detectableEvent)
    {
      numberOfTimesDetectableEventReceived++;
    }

    /* Command Handlers --------------------------------------------------------------------------------------------- */

    void fakeCmdRaisesGivenEvents(List<Event> events) throws UnableToApplyEventException
    {
      for (Event event : events)
      {
        applyChange(event);
      }
    }
  }

  private class EventA extends Event
  {
  }

  private class EventB extends Event
  {
  }

  private class EventC extends Event
  {
  }

  private class DetectableEvent extends Event
  {

  }
}
