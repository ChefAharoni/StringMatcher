import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StringMatcher
{
    public static int horspool(String text)
    {
        // TODO
        return -1;
    }

    public static int naive(String text)
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
            text = br.lines().collect(Collectors.joining(System.lineSeparator()));

            // by here we've already checked that the algorithm var is valid
            occurrences = algorithm.compareToIgnoreCase("horspool") == 0
                    ? horspool(text) : naive(text);

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
