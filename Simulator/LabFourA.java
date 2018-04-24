import cs2030.simulator.Simulator;
import cs2030.simulator.SimState;
import cs2030.simulator.Event;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Optional;
import java.lang.Double;

/**
 * The LabOFourA class is the entry point into Lab 4a.
 *
 * @author Kirsten
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 4a
 */
class LabFourA {
  /**
   * The main method for Lab 4a. Reads data from file and
   * then run a simulation based on the input data.
   *
   * @param args two arguments, first an integer specifying number of servers
   *     in the shop. Second a file containing a sequence of double values, each
   *     being the arrival time of a customer (in any order).
   */
  public static void main(String[] args) {
    Optional<Scanner> opScanner = createScanner(args);
    if (!opScanner.isPresent()) {
      return;
    }

    // Read the first line of input as number of servers in the shop
    opScanner.map(
      scanner -> {
        int numOfServers = scanner.nextInt();
        Simulator sim = new Simulator(numOfServers);
        scanner.tokens() 
            .map(x -> Double.parseDouble(x)) 
            .forEach(arrivalTime -> 
            sim.state.addEvent(arrivalTime, 
              s -> s.simulateArrival(arrivalTime)));
        opScanner.get().close();
        return sim.run();
      }
    ).ifPresent(System.out::println);
  }

  /**
   * Create and return a scanner. If a command line argument is given,
   * treat the argument as a file and open a scanner on the file. Else,
   * create a scanner that reads from standard input.
   *
   * @param args The arguments provided for simulation.
   * @return A scanner or {@code Optional.empty()} if a filename is provided but the file
   *     cannot be open.
   */
  private static Optional<Scanner> createScanner(String[] args) {
    Optional<Scanner> opScanner = Optional.empty();

    try {
      // Read from stdin if no filename is given, otherwise read from the
      // given file.
      if (args.length == 0) {
        // If there is no argument, read from standard input.
        opScanner = Optional.of((new Scanner(System.in)));
      } else {
        // Else read from file
        FileReader fileReader = new FileReader(args[0]);
        opScanner = Optional.of(new Scanner(fileReader));
      }
    } catch (FileNotFoundException exception) {
      System.err.println("Unable to open file " + args[0] + " "
          + exception);
    }
    return opScanner;
  }
}
