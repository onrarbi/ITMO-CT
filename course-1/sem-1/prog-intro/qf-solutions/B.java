import java.util.Scanner;

public class B {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.println(-710 * 25_000 + i * 710);
        }

        scanner.close();
    }
}