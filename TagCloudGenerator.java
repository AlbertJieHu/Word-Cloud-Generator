import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;

/**
 * Generates a tag cloud with a specific number of word given a text. The cloud
 * is alphabetically ordered, most common words will be shown and the font size
 * of each word indicated how often the word appears in the text.
 *
 * @author Albert Hu
 * @author Sayan Bisi
 *
 */
public final class TagCloudGenerator {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloudGenerator() {
    }

    /**
     * Comparator to sort Maps alphabetically.
     */
    private static class MapStringLT
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> o1,
                Map.Pair<String, Integer> o2) {
            return o1.key().compareToIgnoreCase(o2.key());
        }
    }

    /**
     * Comparator to sort Maps by IntegerGT.
     */
    private static class MapIntegerGT
            implements Comparator<Map.Pair<String, Integer>> {
        @Override
        public int compare(Map.Pair<String, Integer> o1,
                Map.Pair<String, Integer> o2) {
            return o1.value().compareTo(o2.value());
        }
    }

    /**
     * Outputs html code for the Header.
     *
     * @param title
     *            the input file name
     * @param outFile
     *            the output file for the html
     * @param wordCount
     *            the number of words shown in the tag cloud
     */
    private static void printHeader(String title, SimpleWriter outFile,
            int wordCount) {
        outFile.println("<html>");
        outFile.println("<head><title> Tag Cloud of" + title + "</title>");
        outFile.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231"
                        + "/web-sw2/assignments/projects/tag-cloud-generator/data"
                        + "/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        outFile.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        outFile.println("</head>");
        outFile.println("<body>");
        outFile.println(
                "<h2>Top " + wordCount + " Words in " + title + "</h2>");
        outFile.println("<hr>");
        outFile.println("<div class=\"cdiv\">");
        outFile.println("<p class=\"cbox\">");

    }

    /**
     * Outputs html code for the Footer.
     *
     * @param outFile
     *            the output file for the html
     */
    private static void printFooter(SimpleWriter outFile) {

        outFile.println("</p></div></body></html>");
    }

    /**
     * Compiles words into a Map with their counts. Is not case-sensitive.
     *
     * @param inFile
     *            the input file
     * @param map
     *            the Map containing words and counts
     */
    private static void generateMap(SimpleReader inFile,
            Map<String, Integer> map) {
        String line = "";
        String word;
        //Reads through whole file
        while (!inFile.atEOS()) {
            word = "";
            line = inFile.nextLine();
            //Reads the line for words
            for (int i = 0; i < line.length(); i++) {
                //Checks if char is a letter or digit
                if (Character.isLetter(line.charAt(i))) {
                    word = word + line.charAt(i);
                } else if (!word.equals("")) {
                    if (map.hasKey(word.toLowerCase())) {
                        map.replaceValue(word.toLowerCase(),
                                map.value(word.toLowerCase()) + 1);
                        word = "";
                    } else {
                        map.add(word.toLowerCase(), 1);
                        word = "";
                    }
                }
            }
            //This is for the very last word in a line
            if (!word.equals("")) {
                if (map.hasKey(word.toLowerCase())) {
                    map.replaceValue(word.toLowerCase(),
                            map.value(word.toLowerCase()) + 1);
                    word = "";
                } else {
                    map.add(word.toLowerCase(), 1);
                    word = "";
                }
            }
        }
    }

    /**
     * Outputs html code for the tag cloud.
     *
     * @param pairs
     *            the SortingMachine containing Map.Pairs
     * @param outFile
     *            the output file
     * @param maxCount
     *            the highest count in the SortingMachine
     */
    private static void printCloud(
            SortingMachine<Map.Pair<String, Integer>> pairs,
            SimpleWriter outFile, int maxCount) {

        for (Map.Pair<String, Integer> pair : pairs) {
            double percent = (double) pair.value() / maxCount;
            double size = percent * 37;
            int fontSize = (int) size + 11;
            outFile.println("<span style=\"cursor:default\" class=\"f"
                    + fontSize + "\" title=\"count: " + pair.value() + "\">"
                    + pair.key() + "</span>");
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        //Declare initial variables
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        String inputFile = "";
        String outputFile = "";
        String count = "";
        int wordCount = 0;

        //Asks for Input File
        boolean invalidInput = true;
        while (invalidInput) {
            out.println("----------------------");
            out.println("Input Text File: ");
            inputFile = in.nextLine();
            //Checks if input is a text file
            if (inputFile.length() > 4) {
                if (inputFile
                        .substring(inputFile.length() - 4, inputFile.length())
                        .equals(".txt")) {
                    invalidInput = false;
                }
            } else {
                out.println("Not a Valid Text File.");
            }
        }
        SimpleReader inFile = new SimpleReader1L(inputFile);

        // Asks User for Output File
        boolean invalidOutput = true;
        while (invalidOutput) {
            out.println("----------------------");
            out.println("Output HTML File: ");
            outputFile = in.nextLine();
            //Checks if input is a HTML file
            if (outputFile.length() > 5) {
                if (outputFile
                        .substring(outputFile.length() - 5, outputFile.length())
                        .equals(".html")) {
                    invalidOutput = false;
                } else {
                    out.println("Not a Valid HTML File.");
                }
            } else {
                out.println("Not a Valid HTML File.");
            }
        }
        SimpleWriter outFile = new SimpleWriter1L(outputFile);

        //Creates two SortingMachines for key and value
        Comparator<Map.Pair<String, Integer>> alpha = new MapStringLT();
        Comparator<Map.Pair<String, Integer>> gt = new MapIntegerGT();
        SortingMachine<Map.Pair<String, Integer>> words = new SortingMachine1L<>(
                alpha);
        SortingMachine<Map.Pair<String, Integer>> counts = new SortingMachine1L<>(
                gt);

        //Creates a Map of words and their counts
        Map<String, Integer> wordMap = new Map1L<>();
        generateMap(inFile, wordMap);

        //Asks User for Number of Words
        boolean invalidInt = true;
        while (invalidInt) {
            out.println("----------------------");
            out.println("Word Count: ");
            count = in.nextLine();
            //Checks if input is a valid positive number
            wordCount = Integer.parseInt(count, 10);
            if (wordCount < 0) {
                out.println("Not a Positive Number.");
            } else if (wordCount > wordMap.size()) {
                out.println("Text doesn't have enough words.");
                out.println("Please choose a number below " + wordMap.size());
            } else {
                invalidInt = false;
            }
        }

        //Fills the counts with every Map.Pair
        for (Map.Pair<String, Integer> pair : wordMap) {
            counts.add(pair);
        }
        counts.changeToExtractionMode();

        //Removes the smallest counts
        while (counts.size() > wordCount) {
            counts.removeFirst();
        }

        //Finds the highest count
        int maxCount = 0;
        for (Map.Pair<String, Integer> pair : counts) {
            maxCount = pair.value();
        }

        //Adds the top N pairs to words
        for (Map.Pair<String, Integer> pair : counts) {
            words.add(pair);
        }
        words.changeToExtractionMode();

        //Prints the html code
        printHeader(inputFile, outFile, wordCount);
        printCloud(words, outFile, maxCount);
        printFooter(outFile);

        in.close();
        out.close();
        inFile.close();
        outFile.close();
    }

}
