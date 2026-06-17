import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reverse {
    public static void main(String[] args) {
        List<int[]> lines = new ArrayList<>();

        try (NewScanner scanner = new NewScanner(System.in)) {
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

            for (int j = numbers.length - 1; j >= 0; j--) {
                if (j != numbers.length - 1) {
                    System.out.print(" ");
                }

                System.out.print(numbers[j]);
            }

            System.out.println();
        }
    }
}