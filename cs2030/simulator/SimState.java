package cs2030.simulator;

import cs2030.util.PriorityQueue;
import cs2030.util.Pair;
import java.util.Optional;

import java.util.function.Function;

/**
 * This class encapsulates all the simulation states.  There are four main
 * components: (i) the event queue, (ii) the statistics, (iii) the shop
 * (the servers) and (iv) the event logs.
 *
 * @author Kirsten
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 4b
 */
public class SimState {
  /** The priority queue of events. */
  private PriorityQueue<Event> events;

  /** The statistics maintained. */
  private final Statistics stats;

  /** The shop of servers. */
  private final Shop shop;

  /**
   * Constructor for creating the simulation state from scratch.
   * @param numOfServers The number of servers.
   */
  public SimState(int numOfServers) {
    this.shop = new Shop(numOfServers);
    this.stats = new Statistics();
    this.events = new PriorityQueue<Event>();
  }

  /**
   * Constructor for creating the simulation state after modifying shop.
   * @param updatedShop The new Shop.
   * @param priorityQ The priorityqueue.
   * @param stats The statistics.
   */
  public SimState(Shop updatedShop, PriorityQueue<Event> priorityQ, Statistics stats) {
    this.shop = updatedShop;
    this.stats = stats;
    this.events = priorityQ;
  }

  /**
   * Add an event to the simulation's event queue.
   * @param  time The time when the event happens.
   * @param lambda The lambda expression of whether arrival or done.
   * @return The new simulation state.
   */
  public SimState addEvent(double time, Function<SimState, SimState> lambda) {
    return new SimState(this.shop, 
      new PriorityQueue<Event>(events.add(
        new Event(time, lambda))), this.stats);
  }

  /**
   * Retrieve the next event with earliest time stamp from the
   * priority queue, and a new state.  If there is no more event, an
   * Optional.empty will be returned.
   * @return A pair object with an (optional) event and the new simulation
   *     state.
   */
  public Pair<Optional<Event>, SimState> nextEvent() {
    Pair<Optional<Event>, PriorityQueue<Event>> result = this.events.poll();
    return new Pair<>(result.first, this);
  }

  /**
   * Called when a customer arrived in the simulation.
   * @param time The time the customer arrives.
   * @param c The customer that arrrives.
   * @return A new state of the simulation after the customer arrives.
   */
  private SimState customerArrives(double time, Optional<Customer> c) {
    System.out.printf("%6.3f %s arrives\n", time, c.get());
    return this;
  }

  /**
   * Called when a customer waits in the simulation.  This methods update
   * the logs of simulation.
   * @param time The time the customer starts waiting.
   * @param s The server the customer is waiting for.
   * @param c The customer who waits.
   * @return A new state of the simulation after the customer waits.
   */
  private SimState customerWaits(double time, Optional<Server> s, Optional<Customer> c) {
    System.out.printf("%6.3f %s waits for %s\n", time, c.get(), s.get());
    return this;
  }

  /**
   * Called when a customer is served in the simulation.  This methods
   * update the logs and the statistics of the simulation.
   * @param time The time the customer arrives.
   * @param s The server that serves the customer.
   * @param c The customer that is served.
   * @return A new state of the simulation after the customer is served.
   */
  private SimState customerServed(double time, Optional<Server> s, Optional<Customer> c) {
    System.out.printf("%6.3f %s served by %s\n", time, c.get(), s.get());
    return updateStatistics(stats.serveOneCustomer())
        .updateStatistics(stats.customerWaitedFor(time - c.get().timeArrived()));
  }

  /**
   * Called when the statistics have been modified,
   * then it should also come up with a new SimState.
   * @param updateStats The new Statistics.
   * @return A new state of the simulation after the update.
   */
  private SimState updateStatistics(Statistics updateStats) {
    return new SimState(this.shop, this.events, updateStats);
  }

  /**
   * Called when a customer is done being served in the simulation.
   * This methods update the logs of the simulation.
   * @param time The time the customer arrives.
   * @param s The server that serves the customer.
   * @param c The customer that is served.
   * @return A new state of the simulation after the customer is done being
   *     served.
   */
  private SimState customerDone(double time, Optional<Server> s, Optional<Customer> c) {
    System.out.printf("%6.3f %s done served by %s\n", time, c.get(), s.get());
    return this;
  }

  /**
   * Called when a customer leaves the shops without service.
   * Update the log and statistics.
   * @param  time  The time this customer leaves.
   * @param  opCustomer The customer who leaves.
   * @return A new state of the simulation.
   */
  private SimState customerLeaves(double time, Optional<Customer> opCustomer) {
    System.out.printf("%6.3f %s leaves\n", time, opCustomer.get());
    return updateStatistics(stats.lostOneCustomer());
  }

  /**
   * Simulates the logic of what happened when a customer arrives.
   * The customer is either served, waiting to be served, or leaves.
   * @param time The time the customer arrives.
   * @return A new state of the simulation.
   */
  public SimState simulateArrival(double time) {
    Optional<Customer> opCustomer = Optional.of(new Customer(time));
    return customerArrives(time, opCustomer).servedOrLeave(time, opCustomer);
  }

  /**
   * Called from simulateArrival.  Handles the logic of finding
   * idle servers to serve the customer, or a server that the customer
   * can wait for, or leave.
   * @param time The time the customer arrives.
   * @param opCustomer The customer to be served.
   * @return A new state of the simulation.
   */
  private SimState servedOrLeave(double time, Optional<Customer> opCustomer) {
    
    Optional<Server> s = shop.findServer(servers -> servers.get().isIdle());
    if (s.isPresent()) {   //if (s!=null)
      return serveCustomer(time, s, opCustomer);
    }
    s = shop.findServer(servers -> !servers.get().customerWaiting());
    if (s.isPresent()) {
      return makeCustomerWait(time, s, opCustomer);
    }
    return customerLeaves(time, opCustomer);
  }

  /**
   * Simulates the logic of what happened when a customer is done being
   * served.  The server either serve the next customer or becomes idle.
   * @param time The time the service is done.
   * @param opServer The server serving the customer.
   * @param opCustomer The customer being served.
   * @return A new state of the simulation.
   */
  public SimState simulateDone(double time, Optional<Server> opServer,
      Optional<Customer> opCustomer) {
    return customerDone(time, opServer, opCustomer)
        .serveNextOrIdle(time, opServer);
  }

  /**
   * Called from simulateDone.  Handles the logic of checking if there is
   * a waiting customer, if so serve the customer, otherwise make the
   * server idle.
   * @param time The time the service is done.
   * @param opServer The server serving the next customer.
   * @return A new state of the simulation.
   */
  private SimState serveNextOrIdle(double time, Optional<Server> opServer) {
    Optional<Customer> c = opServer.get().getWaitingCustomer();
    if (c.isPresent()) {
      return updateServer(opServer.get().removeWaitingCustomer())
          .serveCustomer(time, opServer, c);
    }
    return updateServer(opServer.get().makeIdle());
  }

  /**
   * Handle the logic of server serving customer.  A new done event
   * is generated and scheduled.
   * @param  time  The time this customer is served.
   * @param  opServer The server serving this customer.
   * @param  opCustomer The customer being served.
   * @return A new state of the simulation.
   */
  private SimState serveCustomer(double time, Optional<Server> opServer,
      Optional<Customer> opCustomer) {
    double doneTime = time + Simulator.SERVICE_TIME;
    return addEvent(doneTime, s -> s.simulateDone(doneTime, opServer, opCustomer))
        .updateServer(opServer.get().serve(opCustomer)).customerServed(time, opServer, opCustomer);
  }

  /**
   * Called when the Server have been modified,
   * then it should also come up with a new shop and then a new SimState.
   * @param updatedServer The new Server.
   * @return A new state of the simulation after the update.
   */
  private SimState updateServer(Optional<Server> updatedServer) {
    return new SimState(shop.update(updatedServer), this.events, this.stats);
  }

  /**
   * Handle the logic of queueing up customer for server.   Make the
   * customer waits for server.
   * @param  time  The time this customer started waiting.
   * @param  opServer The server this customer is waiting for.
   * @param  opCustomer The customer who waits.
   * @return A new state of the simulation.
   */
  private SimState makeCustomerWait(double time, 
      Optional<Server> opServer, Optional<Customer> opCustomer) {
    return customerWaits(time, opServer, opCustomer)
        .updateServer(opServer.get().askToWait(opCustomer));
  }

  /**
   * Return a string representation of the simulation state, which
   * consists of all the logs and the stats.
   * @return A string representation of the simulation.
   */
  public String toString() {
    return stats.toString();
  }
}
