import java.util.Scanner;

public class A {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int a = scanner.nextInt(), b = scanner.nextInt(), n = scanner.nextInt();
        int step = b - a;
        int moves = 2 * ((n - b + step - 1) / step) + 1;

        System.out.println(moves);

        scanner.close();
    }
}