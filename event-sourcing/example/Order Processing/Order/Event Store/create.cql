CREATE  KEYSPACE IF NOT EXISTS event_store 
  WITH REPLICATION = { 
    'class' : 'SimpleStrategy', 'replication_factor' : 1
  }
;
-- Should us NetworkTopologyStrategy in production

CREATE TABLE IF NOT EXISTS event_store.events (
  aggregate_id uuid,
  event_time timeuuid,
  event_type text,
  event text,
  event_version bigint static,
  PRIMARY KEY (aggregate_id, event_time, event_type)
);

CREATE INDEX IF NOT EXISTS event_type ON event_store.events (event_type);
