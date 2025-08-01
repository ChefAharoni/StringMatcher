import java.io.*;
import java.util.*;
//import java.util.stream.Collectors;

public class StringMatcher
{

    private String pattern;
    private StringBuilder outputText;
    private int occurrences;
    private double timing;

    // to optimize?
//    private ArrayList<Integer> inxs;
    private Queue<Integer> inxs;

    //tmp TODO--delete
    private String inputText;

    private final String RESET = "\u001B[0m";
    private final String GREEN = "\u001B[32m";

    private final int MIN_READ = 131072;
    private final int MAX_READ = 262144;


    /**
     * Constructor
     * @param algorithm The string matching algorithm to perform,
     *                  naive or Horspool.
     * @param filePath The path to the input text file,
     *                 including file name and extension.
     * @param pattern The text pattern to look inside the text file.
     */
    public StringMatcher(String algorithm, String filePath, String pattern)
    {
        // algorithm
        this.pattern = pattern;
        this.outputText = new StringBuilder();

//        inxs = new ArrayList<>();
        inxs = new LinkedList<>();

        String text = null;
        this.occurrences = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            /* TODO - Read chunks of chars, not all file
            You must read between 131,072 and 262,144 characters in a block of data,
            process the block, and print the results to the screen.
            Do not read the entire file into memory at once; some inputs may be too large.
             */
//            text = br.lines().collect(Collectors.joining(System.lineSeparator()));

//            this.inputText = readFile(filePath, br);
            readFile(filePath, br);

            // by here we've already checked that the algorithm var is valid

           if (algorithm.compareToIgnoreCase("naive") == 0)
               naive(inputText, pattern);
           else if (algorithm.compareToIgnoreCase("horspool") == 0)
               horspool(inputText, pattern);
           else // should never reach here
               System.err.println("Error: Unknown algorithm '" + algorithm + "'"
               + ".\nBad error location");

        } catch (IOException ioe)
        {
            System.err.println(ioe);
            System.exit(1);
        }
    }

    /**
     * Reads a chunk of data from the file path and returns a buffered reader
     * of the input text from the text file.
     * @param filePath The path to the input text file,
     *                 including file name and extension
     * @return Input text as buffered reader.
     */
    private String readFile(String filePath, BufferedReader br) throws IOException
    {
        StringBuilder inputText = new StringBuilder();
        String line;
        int lineLen = 0;

        while ((line = br.readLine()) != null &&
                lineLen < MIN_READ && lineLen < MAX_READ )
        {
            inputText.append(line);
            inputText.append("\n");
            lineLen += line.length();
        }

        this.inputText = inputText.toString();

        return inputText.toString(); // perhaps keep as StringBuilder?
    }

    public void horspool(String text, String pattern)
    {
        long startTime = System.nanoTime();
        // TODO

        long endTime = System.nanoTime();
        long durationInNanos = endTime - startTime;
        // Convert to milliseconds
        timing += (double) durationInNanos / 1_000_000.0;

    }

    public void naive(String text, String pattern)
    {
        long startTime = System.nanoTime(); // start time measure

        int patternLen = pattern.length();
        // call the core x times
        int chunk = pattern.length(); // TODO - change

        for (int i = 0; i < text.length() - patternLen; i++)
        {
            String subText = text.substring(i, i + patternLen); // TODO: substring not allowed
//            int index = naiveCore(subText, pattern);
            int isPattern = naiveCore(subText, pattern);
            if (isPattern != -1)
            {
                // DEBUG
                System.out.println("Debug; index: " + i);
                this.occurrences++;
                inxs.add(i); // add index of pattern word
//                markIndex(subText, i);
            }
        }

        long endTime = System.nanoTime();
        long durationInNanos = endTime - startTime;
        // Convert to milliseconds
        timing += (double) durationInNanos / 1_000_000.0;
    }

    private int naiveCore(String text, String pattern)
    {
        // DEBUG
//        System.out.println("Debug; Text received: " + text);
//        System.out.println("Debug; Pattern Received: " + pattern);

        int n = text.length(),
                m = pattern.length();

        for (int i = 0, end = n - m; i <= end; i++)
        {
            for (int j = 0; j < m; j++)
            {
                if (pattern.charAt(j) != text.charAt(i + j))
                    break;

                if (j == m-1) return i;
            }
        }

        return -1;
    }


    private void markIndex(String text, int index)
    {
        // TODO: fix and use index, word after / word before etc..
        outputText.append(GREEN);
        outputText.append(text);
        outputText.append(RESET);
    }

    private void buildOutput(String inputText)
    {
        int inputLen = inputText.length();

        for (int i = 0; i < inputLen; i++)
        {
            //
        }
    }


    public void printSolution()
    {
        // TODO: implement timings
//        timing = -1.0;

        // TODO: delete; Debug
        System.out.println("Input text: " + inputText);

        System.out.println(outputText);

        // TODO: change occurrences to singular if one
        System.out.println("Occurrences of \"" + pattern + "\": " + occurrences);
        System.out.println("Search time: " + timing + " ms");
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

        StringMatcher sm = new StringMatcher(algorithm, filePath, pattern);
        sm.printSolution();


        System.exit(0);

    }
}
