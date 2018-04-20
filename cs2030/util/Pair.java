package cs2030.util;

public class Pair<T,U> {
  /** The first element in Pair. */
  public T first;
  /** The second element in Pair. */
  public U second;

  /**
   * Construct the Pair.
   * @param t The first element.
   * @param u The second element.
   */
  public Pair(T t, U u) {
    this.first = t;
    this.second = u;
  }
}
