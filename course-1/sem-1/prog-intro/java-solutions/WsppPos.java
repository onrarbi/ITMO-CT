import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WsppPos {
    private static boolean isWordChar(char c) {
        return Character.isLetter(c)
                || Character.isDigit(c)
                || c == '\'' || c == '$' || c == '_'
                || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static void main(String[] args) {
        List<String> allWords = new ArrayList<>();
        List<Integer> wordLines = new ArrayList<>();

        try (NewScanner scanner = new NewScanner(new FileInputStream(args[0]))) {
            int lineNumber = 1;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                NewScanner lineScanner = new NewScanner(line);
                lineScanner.setDelimiter(c -> !isWordChar(c));

                while (lineScanner.hasNext()) {
                    allWords.add(lineScanner.next().toLowerCase(Locale.ROOT));
                    wordLines.add(lineNumber);
                }

                lineScanner.close();
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
            return;
        }

        Map<String, List<String>> wordPositions = new LinkedHashMap<>();
        int totalWords = allWords.size();

        for (int i = 0; i < totalWords; i++) {
            String word = allWords.get(i);
            String position = wordLines.get(i) + ":" + (totalWords - i);

            List<String> positions = wordPositions.get(word);
            if (positions == null) {
                positions = new ArrayList<>();
                wordPositions.put(word, positions);
            }

            positions.add(position);
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<String>> entry : wordPositions.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue().size());

                for (String position : entry.getValue()) {
                    writer.write(" " + position);
                }

                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Output error: " + e.getMessage());
        }
    }
}