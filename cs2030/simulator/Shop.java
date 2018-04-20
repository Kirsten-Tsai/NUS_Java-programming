package cs2030.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A shop object maintains the list of servers and support queries
 * for server.
 *
 * @author Kirsten
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 4b
 */
class Shop {
  /** List of servers. */
  private final List<Optional<Server>> servers;

  /**
   * Create a new shop with a given number of servers.
   * @param numOfServers The number of servers.
   */
  Shop(int numOfServers) {
    this.servers = Stream.generate(() -> Optional.of(new Server()))
      .limit(numOfServers)
      .collect(Collectors.toList());
  }

  /**
   * Create a copy of shop with old shop.
   * @param newServers The old server list.
   */
  public Shop(List<Optional<Server>> newServers) {
    this.servers = newServers;
  }

  /**
   * If the server has changed, 
   * need to update the shop.
   *
   * @param server Updated server.
   * @return A new Shop.
   */
  public Shop update(Optional<Server> server) {
    List<Optional<Server>> newServers = new ArrayList<>();
    newServers.addAll(this.servers);
    int index = 0;
    newServers.forEach(s -> {
      if (s.equals(server)) {
        newServers.set(newServers.indexOf(s), server);
      }
    });
    return new Shop(newServers);
  }

  /**
   * Return the first idle server in the list.
   *
   * @return An idle server, or {@code Optional.empty()} if every server is busy.
   */
  public Optional<Server> findIdleServer() {
    return servers.stream().filter(s -> s.get().isIdle())
      .findFirst()
      .orElse(Optional.empty());
  }

  

  /**
   * Return the first server with no waiting customer.
   * @return A server with no waiting customer, or {@code Optional.empty()} is every
   *     server already has a waiting customer.
   */
  public Optional<Server> findServerWithNoWaitingCustomer() {
    return servers.stream().filter(s -> !s.get().customerWaiting())
      .findFirst()
      .orElse(Optional.empty());
  }

  /**
   * Return the first idle server in the list.
   *
   * @param predicate The predicate(lambda expression) to find the target.
   * @return An idle server, or {@code Optional.empty()} if every server is busy.
   */
  public Optional<Server> findServer(Predicate<Optional<Server>> predicate) {
    return servers.stream().filter(predicate)
      .findFirst()
      .orElse(Optional.empty());
  }

  /**
   * Return a string representation of this shop.
   * @return A string reprensetation of this shop.
   */
  public String toString() {
    return servers.toString();
  }
}
