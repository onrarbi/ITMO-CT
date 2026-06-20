import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Wspp {
    private static boolean isWordChar(char c) {
        return Character.isLetter(c)
            || c == '\''
            || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static void main(String[] args) {
        Map<String, List<Integer>> wordPositions = new LinkedHashMap<>();
        int wordCounter = 0;

        try (NewScanner scanner = new NewScanner(new FileInputStream(args[0]))) {
            scanner.setDelimiter(c -> !isWordChar(c));

            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                wordCounter++;

                List<Integer> positions = wordPositions.get(word);
                if (positions == null) {
                    positions = new ArrayList<>();
                    wordPositions.put(word, positions);
                }

                positions.add(wordCounter);
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<Integer>> entry : wordPositions.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue().size());

                for (int position : entry.getValue()) {
                    writer.write(" " + position);
                }

                writer.newLine();
            }
        } catch(IOException e) {
            System.err.println("Output error: " + e.getMessage());
        }
    }
}