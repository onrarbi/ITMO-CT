import java.util.Scanner;

public class I {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();

        int xl = Integer.MAX_VALUE;
        int xr = Integer.MIN_VALUE;
        int yl = Integer.MAX_VALUE;
        int yr = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            int xi = scanner.nextInt();
            int yi = scanner.nextInt();
            int hi = scanner.nextInt();

            xl = Math.min(xl, xi - hi);
            xr = Math.max(xr, xi + hi);
            yl = Math.min(yl, yi - hi);
            yr = Math.max(yr, yi + hi);
        }

        int x = (xl + xr) / 2;
        int y = (yl + yr) / 2;
        int h = (int) Math.ceil(Math.max(xr - xl, yr - yl) / 2.0);

        System.out.println(x + " " + y + " " + h);

        scanner.close();
    }
}