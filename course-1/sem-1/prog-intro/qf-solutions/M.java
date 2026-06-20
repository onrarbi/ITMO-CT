import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class M {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int t = scanner.nextInt();
        StringBuilder result = new StringBuilder();

        for (int test = 0; test < t; test++) {
            int n = scanner.nextInt();
            int[] a = new int[n];

            for (int i = 0; i < n; i++) {
                a[i] = scanner.nextInt();
            }

            int triples = 0;

            Map<Integer, Integer> possibleK = new HashMap<>();
            possibleK.put(a[n-1], 1);

            for (int j = n - 2; j >= 1; j--) {
                for (int i = 0; i < j; i++) {
                    int target = 2 * a[j] - a[i];
                    triples += possibleK.getOrDefault(target, 0);
                }

                possibleK.put(a[j], possibleK.getOrDefault(a[j], 0) + 1);
            }

            result.append(triples).append("\n");
        }

        System.out.print(result);

        scanner.close();
    }
}