import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Reverse {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<int[]> lines = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);

            int[] numbers = new int[1];
            int numCount = 0;

            while (lineScanner.hasNextInt()) {
                if (numCount == numbers.length) {
                    numbers = Arrays.copyOf(numbers, numbers.length * 2);
                }

                numbers[numCount] = lineScanner.nextInt();
                numCount++;
            }

            lines.add(Arrays.copyOf(numbers, numCount));
            lineScanner.close();
        }

        scanner.close();

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