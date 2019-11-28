package com.triad.training.triaddistribution.order.write.core.repositories;

import com.triad.common.eventsourcing.eventstore.IEventStore;
import com.triad.common.eventsourcing.repository.AggregateRootRepository;
import com.triad.training.triaddistribution.order.write.core.models.OrderAggregateRoot;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class OrderRepository extends AggregateRootRepository<OrderAggregateRoot>
{
  @Inject
  protected OrderRepository(IEventStore eventStore)
  {
    super(OrderAggregateRoot.class, eventStore);
  }
}
