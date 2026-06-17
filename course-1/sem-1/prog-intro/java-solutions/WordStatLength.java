import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;

public class WordStatLength {
    private static boolean isWordChar(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static void main(String[] args) {
        Map<String, Integer> words = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8))) {
            char[] buffer = new char[1024];
            StringBuilder word = new StringBuilder();

            int read = reader.read(buffer);
            while (read != -1) {
                for (int i = 0; i < read; i++) {
                    char c = buffer[i];

                    if (isWordChar(c)) {
                        word.append(c);
                    } else if (!word.isEmpty()) {
                        String currentWord = word.toString().toLowerCase();
                        words.put(currentWord, words.getOrDefault(currentWord, 0) + 1);
                        word.setLength(0);
                    }
                }

                read = reader.read(buffer);
            }

            if (!word.isEmpty()) {
                String currentWord = word.toString().toLowerCase();
                words.put(currentWord, words.getOrDefault(currentWord, 0) + 1);
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), StandardCharsets.UTF_8))) {
            ArrayList<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(words.entrySet());

            sortedWords.sort(Comparator.comparingInt(entry -> entry.getKey().length()));

            for (Map.Entry<String, Integer> entry : sortedWords) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Output error: " + e.getMessage());
        }
    }
}