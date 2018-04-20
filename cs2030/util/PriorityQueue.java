package cs2030.util;

import java.util.Optional;

public class PriorityQueue<T> {
  /** The priorityqueue in the java.util package. */
  java.util.PriorityQueue<T> pq;

  /** The Constructor of the priorityqueue. */
  public PriorityQueue() {
    pq = new java.util.PriorityQueue<T>();
  }

  /**
   * Come up a new Priorityqueue with the old one.
   *
   * @param old The old priorityqueue.
   */
  public PriorityQueue(PriorityQueue<T> old) {
    this.pq = new java.util.PriorityQueue<T>();
    this.pq.addAll(old.pq);
  }

  /**
   * Add a server to the PQ.
   *
   * @param object New server.
   * @return A new PQ.
   */
  public PriorityQueue<T> add(T object) {
    this.pq.add(object);
    return this;
  }

  /**
   * Poll a server from a PQ.
   *
   * @return A Pair with type {@code Pair<Optional<T>, PriorityQueue<T>>}.
   */
  public Pair<Optional<T>, PriorityQueue<T>> poll() {
    Optional<T> t = Optional.ofNullable(this.pq.poll());
    return new Pair<>(t, this); 
    // <T,U> = < Optional<Event>, PriorityQueue<Event> >
  }
}
