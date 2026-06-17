import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReverseEven {
    private static boolean isDelimiter(char c) {
        return Character.isWhitespace(c) || Character.getType(c) == Character.START_PUNCTUATION || Character.getType(c) == Character.END_PUNCTUATION;
    }

    public static void main(String[] args) {
        List<int[]> lines = new ArrayList<>();

        try (NewScanner scanner = new NewScanner(System.in)) {
            scanner.setDelimiter(ReverseEven::isDelimiter);

            while (scanner.hasNextLine()) {
                int[] numbers = new int[1];
                int numCount = 0;

                while (scanner.hasNextIntInLine()) {
                    if (numCount == numbers.length) {
                        numbers = Arrays.copyOf(numbers, numbers.length * 2);
                    }

                    numbers[numCount] = scanner.nextIntInLine();
                    numCount++;
                }

                lines.add(Arrays.copyOf(numbers, numCount));

                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }
        }

        for (int i = lines.size() - 1; i >= 0; i--) {
            int[] numbers = lines.get(i);
            boolean firstNumber = true;

            for (int j = numbers.length - 1; j >= 0; j--) {
                if ((i + j) % 2 == 0) {
                    if (!firstNumber) {
                        System.out.print(" ");
                    }

                    System.out.print(numbers[j]);
                    firstNumber = false;
                }
            }

            System.out.println();
        }
    }
}