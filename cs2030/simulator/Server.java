package cs2030.simulator;

import java.util.Optional;

/**
 * The Server class keeps track of who is the customer being served (if any)
 * and who is the customer waiting to be served (if any).
 *
 * @author Kirsten
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 4b
 */
class Server {
  /** The unique ID of the last created server. */
  private static int lastServerId = 0;

  /** The unique ID of this server. */
  private final int id;

  /** The customer currently being served, if any. */
  private Optional<Customer> currentCustomer;

  /** The customer currently waiting, if any. */
  private Optional<Customer> waitingCustomer;

  /**
   * Creates a server and initalizes it with a unique id.
   */
  public Server() {
    this.currentCustomer = Optional.empty();
    this.waitingCustomer = Optional.empty();
    this.id = Server.lastServerId;
    Server.lastServerId++;
  }

  /**
   * Change this server's state to idle by removing its current customer.
   * @return A new server with the current customer removed.
   */
  public Optional<Server> makeIdle() {
    this.currentCustomer = Optional.empty();
    return Optional.of(this);
  }

  /**
   * Checks if the current server is idle.
   * @return true if the server is idle (no current customer); false otherwise.
   */
  public boolean isIdle() {
    return !this.currentCustomer.isPresent();
  }

  /**
   * Checks if there is a customer waiting for given server.
   * @return true if a customer is waiting for given server; false otherwise.
   */
  public boolean customerWaiting() {
    return this.waitingCustomer.isPresent(); //!=null
  }

  /**
   * Returns waiting customer for given server.
   * @return customer waiting for given server.
   */
  public Optional<Customer> getWaitingCustomer() {
    return this.waitingCustomer;
  }

  /**
   * Removes the customer waiting for given server.
   * @return The new server with waiting customer removed.
   */
  public Optional<Server> removeWaitingCustomer() {
    this.waitingCustomer = Optional.empty();
    return Optional.of(this);
  }

  /**
   * Serve a customer.
   * @param opCustomer The customer to be served.
   * @return The new server serving this customer.
   */
  public Optional<Server> serve(Optional<Customer> opCustomer) {
    this.currentCustomer = opCustomer;
    return Optional.of(this);
  }

  /**
   * Make a customer wait for this server.
   * @param opCustomer The customer who will wait for this server.
   * @return The new server with a waiting customer.
   */
  public Optional<Server> askToWait(Optional<Customer> opCustomer) {
    this.waitingCustomer = opCustomer;
    return Optional.of(this);
  }

  /**
   * Return a string representation of this server.
   * @return A string S followed by the ID of the server, followed by the
   *     waiting customer.
   */
  public String toString() {
    return "S" + this.id + " (Q: " +
        ((waitingCustomer.isPresent()) ? waitingCustomer : "-") + ")";
  }

  /**
   * Checks if two servers have the same id.
   * @param  obj Another objects to compared against.
   * @return  true if obj is a server with the same id; false otherwise.
   */
  public boolean equals(Object obj) {
    if (!(obj instanceof Server)) {
      return false;
    }
    return (this.id == ((Server)obj).id);
  }

  /**
   * Return the hashcode for this server.
   * @return the ID of this server as its hashcode.
   */
  public int hashCode() {
    return this.id;
  }
}
