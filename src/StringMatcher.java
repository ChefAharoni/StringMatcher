import java.io.*;
import java.util.*;

public class StringMatcher
{

    private String pattern;
    private int occurrences;
    private double timing;

    private Set<Integer> inxSet;

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
        this.pattern = pattern;
        this.inxSet = new HashSet<>();

        this.occurrences = 0;

        if (!algorithm.equals("horspool") &&
                !algorithm.equals("naive"))
        {
            System.err.println("Error: Unknown algorithm '" + algorithm + "'.");
            System.exit(1);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String block;
            while((block = readBlock(br)) != null)
            {
                // by here we've already checked that the algorithm var is valid
                if (algorithm.equals("naive"))
                    naive(block, pattern);
                else horspool(block, pattern);

                buildOutput(block);
            }

        } catch (IOException ioe)
        {
            System.err.println("Error: File '" + filePath + "' not found.");
            System.exit(1);
        }
    }

    /**
     * Reads a chunk of data from the file path and returns a buffered reader
     * of the input text from the text file.
     *
     * @return Input text as buffered reader.
     */
    private String readBlock(BufferedReader br) throws IOException
    {
        StringBuilder block = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
        {
            // +1 for newline
            int projectedSize = block.length() + line.length() + 1;

            block.append(line).append(System.lineSeparator());

            if (projectedSize > MAX_READ)
                if (block.length() >= MIN_READ) break;

            if (block.length() >= MIN_READ && block.length() > MAX_READ)
                break;
            if (block.length() >= MIN_READ && projectedSize > MAX_READ)
                break;
        }

        if (block.isEmpty()) return null;

        return block.toString();
    }

    /**
     * Public method to perform the Horspool algorithm for string matching.
     *
     * @param text The input text to search in.
     * @param pattern The pattern to search in the text.
     */
    public void horspool(String text, String pattern)
    {
        int textLen = text.length();
        int pattLen = pattern.length();
        if (pattLen == 0 || textLen < pattLen) return;


        // building the shiftTable before timing starts
        Map<Character, Integer> shiftTable = buildShiftTable(pattern);

        long startTime = System.nanoTime();

        // alignment: pattern[0..pattLen-1] aligned with text[i..i+pattLen-1]
        int i = 0;
        while (i <= textLen - pattLen)
        {
            int j = pattLen - 1;
            // compare backwards
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j))
            {
                j--;
            }
            if (j < 0)
            {
                // match found at position i
                this.occurrences++;
                inxSet.add(i);
                // shift by full pattern length
                i += pattLen;
//                i += 1; // to find overlapping words like (anana)
            } else
            {
                char mismatched = text.charAt(i + pattLen - 1);
                int shift = shiftTable.getOrDefault(mismatched, pattLen);
                i += shift;
            }
        }

        long endTime = System.nanoTime();
        timing += (double)(endTime - startTime) / 1_000_000.0;
    }

    /**
     * Builds the shift table from Horspool's algorithm. Ignores the last
     * character.
     * @param pattern The pattern to search for in the string.
     * @return A Map with char as the key and the shift val as value.
     */
    private Map<Character, Integer> buildShiftTable(String pattern)
    {
        int pattLen = pattern.length();
        Map<Character, Integer> shiftTable = new HashMap<>();

        // -1 because ignoring last char
        for (int i = 0; i < pattLen - 1; i++)
        {
            char c = pattern.charAt(i);
            shiftTable.put(c, pattLen - 1 - i);
        }

        return shiftTable;
    }


    /**
     * Public method to perform the naive method of matching a string.
     *
     * @param text The input text to search in.
     * @param pattern The pattern to search in the text.
     */
    public void naive(String text, String pattern)
    {
        int patternLen = pattern.length();
        if (patternLen == 0 || text.length() < patternLen) return;

        long startTime = System.nanoTime(); // start time measure
        // call the core x times
        for (int i = 0; i <= text.length() - patternLen; i++)
        {
            if (naiveCore(text, pattern, i))
            {
                this.occurrences++;
                this.inxSet.add(i); // add index of pattern word
            }
        }

        long endTime = System.nanoTime();
        long durationInNanos = endTime - startTime;
        // Convert to milliseconds
        timing += (double) durationInNanos / 1_000_000.0;
    }

    /**
     * Returns true if pattern matches text at position offset.
     *
     * @param text Text to match with pattern
     * @param pattern Pattern to test
     * @param offset Position to test
     * @return True if position matches text, false otherwise
     */
    private boolean naiveCore(String text, String pattern, int offset)
    {
        int pattLen = pattern.length();

        for (int i = 0; i < pattLen; i++)
        {
            if (pattern.charAt(i) != text.charAt(offset + i))
                return false;
        }

        return true;
    }


    /**
     * Build output with matched patterns highlighted.
     * Assumes `inxs` contains start indices of matches and `patternLen` is the length
     * of the matched pattern to highlight.
     *
     * @param inputText The input text upon the output text is built.
     */
    private void buildOutput(String inputText)
    {
        StringBuilder outputText = new StringBuilder();
        int patternLen = pattern.length();
        int inputLen = inputText.length();
        int i = 0;

        while (i < inputLen) {
            if (inxSet.contains(i))
            {
                // Found a match starting at i;
                //      append the full pattern as highlighted
                appendHighlighted(outputText, inputText, i, patternLen);
                i += patternLen; // skip over matched segment
            } else
            {
                // No match here; append single character
                outputText.append(inputText.charAt(i));
                i++;
            }
        }

        System.out.print(outputText);
        inxSet.clear();
    }

    /**
     * Helper method that adds the matched text (pattern) to the output text
     * in green color.
     *
     * @param s Matched pattern to add in green.
     */
    private void appendHighlighted(StringBuilder output, String inputText,
                                   int start, int len)
    {
        // building the match manually to avoid using substring
        output.append(GREEN);
        int end = Math.min(start + len, inputText.length());
        for (int k = start; k < end; k++)
            output.append(inputText.charAt(k));

        output.append(RESET);
    }

    public void printSolution()
    {
        System.out.print("\nOccurrences");
        System.out.println(" of \"" + pattern + "\": " + occurrences);
//        System.out.println("Search time: " + timing + " ms");
        System.out.printf("Search time: %.2f ms%n", timing); // formatted
    }


    public static void main(String[] args) throws IOException
    {
        if (args.length != 3)
        {
            System.err.println("Usage: java StringMatcher " +
                    "<algorithm> <input file> <pattern>");
            System.exit(1);
        }

        String algorithm = args[0], // horspool or naive
                filePath = args[1], // input file
                pattern = args[2]; // search term


        StringMatcher sm = new StringMatcher(algorithm, filePath, pattern);
        sm.printSolution();

        System.exit(0);
    }
}
