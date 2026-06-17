import java.io.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class WordStat {
    private static boolean isWordChar(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static void main(String[] args) {
        Map<String, Integer> words = new LinkedHashMap<>();

        try (NewScanner scanner = new NewScanner(new FileInputStream(args[0]))) {
            scanner.setDelimiter(c -> !isWordChar(c));

            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase(Locale.ROOT);
                words.put(word, words.getOrDefault(word, 0) + 1);
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(args[1]), java.nio.charset.StandardCharsets.UTF_8)
        )) {
            for (Map.Entry<String, Integer> entry : words.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Output error: " + e.getMessage());
        }
    }
}