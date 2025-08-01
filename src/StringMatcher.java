import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StringMatcher
{
    String RESET = "\u001B[0m";
    String GREEN = "\u001B[32m";

    public static int horspool(String text, String pattern)
    {
        // TODO
        return -1;
    }

    public static int naive(String text, String pattern)
    {
        // TODO
        return -1;
    }


    public static void main(String[] args) throws IOException
    {
        if (args.length < 3)
        {
            System.err.println("Usage: java StringMatcher " +
                    "<algorithm> <input file> <pattern>");
            System.exit(1);
        }

        String algorithm = args[0], // horspool or naive
                filePath = args[1], // input file
                pattern = args[2]; // search term

        if (!algorithm.equalsIgnoreCase("horspool") &&
        !algorithm.equalsIgnoreCase("naive"))
        {
            System.err.println("Error: Unknown algorithm '" + algorithm + "'");
            System.exit(1);
        }

        String text;
        int occurrences = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            /* TODO - Read chunks of chars, not all file
            You must read between 131,072 and 262,144 characters in a block of data,
            process the block, and print the results to the screen.
            Do not read the entire file into memory at once; some inputs may be too large.
             */
            text = br.lines().collect(Collectors.joining(System.lineSeparator()));

            // by here we've already checked that the algorithm var is valid
            occurrences = algorithm.compareToIgnoreCase("horspool") == 0
                    ? horspool(text, pattern) : naive(text, pattern);

        } catch (IOException ioe)
        {
            System.err.println(ioe);
            System.exit(1);
        }

        double timing = -1.0;

        // TODO: change occurrences to singular if one
        System.out.println("Occurrences of \"" + pattern + "\": " + occurrences);
        System.out.println("Search time: " + timing + " ms");
        System.exit(0);
    }
}
