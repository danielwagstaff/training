package com.triad.training.triaddistribution.order.read.core.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.NoNodeAvailableException;
import com.datastax.oss.driver.api.core.auth.AuthenticationException;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.QueryExecutionException;
import com.datastax.oss.driver.api.core.servererrors.QueryValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triad.common.eventsourcing.Event;
import com.triad.common.eventsourcing.eventstore.IEventStore;
import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Singleton
public class CassandraEventStore implements IEventStore
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CassandraEventStore.class);
  private static final String MSG_FAILED_TO_CONNECT = "Failed to connect to Cassandra cluster";
  private static final String MSG_FAILED_TO_EXECUTE = "Failed to execute Cassandra query";
  private static final String MSG_FAILED_TO_UNPACK = "Failed to unpack Event";

  @ConfigProperty(name = "cassandra.connection.node") String node;
  @ConfigProperty(name = "cassandra.connection.port") int port;
  @ConfigProperty(name = "cassandra.connection.dataCentre") String dataCentre;
  @ConfigProperty(name = "cassandra.connection.tableName") String tableName;

  private CqlSession session;

  private CqlSession getSession()
  {
    if (session == null)
    {
      LOGGER.info("Creating CQL Session: {}:{}", node, port);
      session = CqlSession.builder()
                          .addContactPoint(new InetSocketAddress(node, port))
                          .withLocalDatacenter(dataCentre)
                          .build();
    }
    return session;
  }

  @Override
  public void saveEvents(UUID id, List<Event> events, final long expectedStoredVersion)
  {
    throw new UnsupportedOperationException("This is the read side - not the write side!");
  }

  @Override
  public List<Event> retrieveAllEvents() throws RetrieveFailedException
  {

    List<Event> events = new ArrayList<>();

    try
    {
      String query = "SELECT event FROM " + tableName + ";";
      ResultSet resultSet = getSession().execute(query);

      for (Row row : resultSet.all())
      {
        String strEvent = row.get(0, String.class);
        ObjectMapper mapper = new ObjectMapper();
        EventDescriptor eventDescriptor = mapper.readValue(strEvent, EventDescriptor.class);
        Event event = (Event) mapper.readValue(eventDescriptor.getJsonEvent(),
                                               Class.forName(eventDescriptor.getEventType()));
        events.add(event);
      }

    }
    catch (NoNodeAvailableException | AuthenticationException | IllegalStateException e)
    {
      throw new RetrieveFailedException(MSG_FAILED_TO_CONNECT, e);
    }
    catch (QueryExecutionException | QueryValidationException e)
    {
      throw new RetrieveFailedException(MSG_FAILED_TO_EXECUTE, e);
    }
    catch (IOException | ClassNotFoundException e)
    {
      throw new RetrieveFailedException(MSG_FAILED_TO_UNPACK, e);
    }

    return events;
  }

  @Override
  public List<Event> retrieveEvents(UUID id) throws RetrieveFailedException
  {

    List<Event> events = new ArrayList<>();

    try
    {
      String query = "SELECT event FROM " + tableName + " WHERE aggregate_id = " + id + ";";
      ResultSet resultSet = getSession().execute(query);
      for (Row row : resultSet.all())
      {
        String strEvent = row.get(0, String.class);
        ObjectMapper mapper = new ObjectMapper();
        EventDescriptor eventDescriptor = mapper.readValue(strEvent, EventDescriptor.class);
        Event event = (Event) mapper.readValue(eventDescriptor.getJsonEvent(),
                                               Class.forName(eventDescriptor.getEventType()));
        events.add(event);
      }
    }
    catch (NoNodeAvailableException | AuthenticationException | IllegalStateException e)
    {
      throw new RetrieveFailedException(MSG_FAILED_TO_CONNECT, e);
    }
    catch (QueryExecutionException | QueryValidationException e)
    {
      throw new RetrieveFailedException(MSG_FAILED_TO_EXECUTE, e);
    }
    catch (IOException | ClassNotFoundException e)
    {
      throw new RetrieveFailedException(MSG_FAILED_TO_UNPACK, e);
    }

    return events;
  }

  private static class EventDescriptor
  {
    private String eventType;
    private String jsonEvent;

    public String getEventType()
    {
      return eventType;
    }

    public void setEventType(String eventType)
    {
      this.eventType = eventType;
    }

    public String getJsonEvent()
    {
      return jsonEvent;
    }

    public void setJsonEvent(String event)
    {
      this.jsonEvent = event;
    }
  }
}
