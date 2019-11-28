package com.triad.training.triaddistribution.order.write.core.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.NoNodeAvailableException;
import com.datastax.oss.driver.api.core.auth.AuthenticationException;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.servererrors.QueryExecutionException;
import com.datastax.oss.driver.api.core.servererrors.QueryValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triad.common.eventsourcing.Event;
import com.triad.common.eventsourcing.aggregateroot.AggregateRoot;
import com.triad.common.eventsourcing.eventstore.IEventStore;
import com.triad.common.eventsourcing.eventstore.RetrieveFailedException;
import com.triad.common.eventsourcing.eventstore.SaveFailedException;
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
  private static final String MSG_FAILED_TO_CONVERT_JSON = "Failed to convert Event to JSON";

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
  public void saveEvents(UUID id, List<Event> events, final long expectedStoredVersion) throws SaveFailedException
  {
    try
    {
      StringBuilder sbQuery = new StringBuilder("BEGIN BATCH ");

      StringBuilder sbInsertValuesPre = new StringBuilder("UPDATE ").append(tableName).append(" SET event = '");

      StringBuilder sbInsertValuesPost = new StringBuilder("'");

      long lastStoredEventVersion = expectedStoredVersion;
      for (Event event : events)
      {
        event.setStreamPosition(++lastStoredEventVersion);

        EventDescriptor eventDescriptor = new EventDescriptor();
        eventDescriptor.setJsonEvent(eventToJson(event));
        eventDescriptor.setEventType(event.getClass().getName());

        sbQuery.append(sbInsertValuesPre)
               .append(eventToJson(eventDescriptor))
               .append(sbInsertValuesPost)
               .append(" WHERE aggregate_id = ")
               .append(id)
               .append(" and event_time = now() ")
               .append(" and event_type = '")
               .append(eventDescriptor.eventType)
               .append("'")
               .append(";");
      }

      sbQuery.append("UPDATE ")
             .append(tableName)
             .append(" SET event_version = ")
             .append(lastStoredEventVersion)
             .append(" WHERE aggregate_id = ")
             .append(id);

      if (expectedStoredVersion != AggregateRoot.NO_STREAM_POSITION)
      {
        sbQuery.append(" IF event_version = ").append(expectedStoredVersion);
      }

      sbQuery.append(";").append(" APPLY BATCH ;");

      ResultSet resultSet = getSession().execute(sbQuery.toString());
      if (!resultSet.wasApplied())
      {
        Row result = resultSet.one();
        String reason = "undefined";
        if (result != null)
        {
          reason = result.getFormattedContents();
        }
        throw new SaveFailedException("Failed to apply events: " + reason);
      }
      else
      {
        LOGGER.info("Events applied successfully");
      }
    }
    catch (JsonProcessingException e)
    {
      throw new SaveFailedException(MSG_FAILED_TO_CONVERT_JSON, e);
    }
    catch (NoNodeAvailableException | AuthenticationException | IllegalStateException e)
    {
      throw new SaveFailedException(MSG_FAILED_TO_CONNECT, e);
    }
    catch (QueryExecutionException | QueryValidationException e)
    {
      throw new SaveFailedException(MSG_FAILED_TO_EXECUTE, e);
    }
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

  private <T> String eventToJson(T eventDescriptor) throws JsonProcessingException
  {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(eventDescriptor);
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
