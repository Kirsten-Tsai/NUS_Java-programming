package cs2030.simulator;

import java.util.Optional;
import java.util.function.Function;
/**
 * The Event class encapsulates information and methods pertaining to a
 * Simulator event.  This is an abstract class that should be subclassed
 * into a specific event in the simulator.  The {@code simulate} method
 * must be written.
 *
 * @author Kirsten
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 1b
 */

public class Event implements Comparable<Event> {
  /** The time this event occurs at. */
  private double time;
  /** The lambda this event is going to use. */
  private Function<SimState, SimState> lambda;   

  /**
   * Creates an event and initializes it.
   *
   * @param time The time of occurrence.
   * @param f The lambda expression it is going to use.
   */
  public Event(double time, Function<SimState, SimState> f) {
    this.time = time;
    this.lambda = f;
  }


  /**
   * Defines natural ordering of events by their time.
   * Events ordered in ascending order of their timestamps.
   *
   * @param other Another event to compare against.
   * @return 0 if two events occur at same time, a positive number if
   *     this event has later than other event, a negative number otherwise.
   */
  public int compareTo(Event other) {
    return (int)Math.signum(this.time - other.time);
  }

  /**
   * Simulate the event depending on its own lambda.
   * 
   * @param state The old SimState.
   * @return A new SimState after simulateArrival or simulateDone.
   */
  public SimState simulate(SimState state) {
    return this.lambda.apply(state);
  }

}

