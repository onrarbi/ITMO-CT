package search;

public class BinarySearch {

    /*
    Model: x = args[0] (целое число), a = args[1...n] (массив целых чисел, отсортированный по невозрастанию)
     */

    // Pred: args.length >= 1 && для всех i,j: 1 <= i < j < args.length: args[i] >= args[j]
    // Post: res = min{0 <= i <= a.length | i = a.length ИЛИ a[i] <= x}
    public static void main(String[] args) {
        // Pred: args.length >= 1
        int x = Integer.parseInt(args[0]);
        // Post: P1 = (x = Integer.parseInt(args[0]) && args.length >= 1)

        // Pred: P1
        int[] a = new int[args.length - 1];
        // Post: P2 = (P1 && a = new int[args.length - 1])

        // Pred: P2
        // Inv: для 0 <= j <= i - 1: a[j] = Integer.parseInt(args[j + 1]) && 1 <= i < args.length
        for (int i = 1; i < args.length; i++) {
            // Inv
            a[i - 1] = Integer.parseInt(args[i]);
            // Post: a[i-1] = Integer.parseInt(args[i]) && Inv
        }
        // Post: P3 = (для 0 <= i <= args.length - 1: a[i] = Integer.parseInt(args[i + 1]) && для i, j 0 <= i < j < a.length: a[i] >= a[j] && P1)
        // && для 0 <= i < j < a.length: a[i] >= a[j] && P1)

        // Pred: P3
        int resIterative = iterativeBinSearch(x, a);
        // Post: P4 = (resIterative = min{0 <= i <= a.length | i = a.length ИЛИ a[i] <= x} && P3)

        // Pred: P4
        System.out.println(resIterative);
        // Post: выведено resIterative && P4
    }

    // Pred: P1 = для всех i,j: 0 <= i < j < a.length: a[i] >= a[j]
    // Post: res = min{0 <= i <= a.length | i = a.length ИЛИ a[i] <= x}
    public static int iterativeBinSearch(int x, int[] a) {
        // Pred: P1
        int left = -1;
        int right = a.length;
        // Post: P2 = (left = -1 && right = a.length && P1)

        // Pred: P2
        // Inv: -1 <= left < right <= a.length && для всех 0 <= i <= left: a[i] > x && для всех right <= j < a.length: a[j] <= x
        while (left < right - 1) {
            // Pred: Inv
            int mid = (left + right) / 2;
            // Post: P3 = (Inv && mid = (left + right) / 2 && left < mid < right)

            // Pred: P3 && a[mid] > x || P3 && a[mid] <= x
            if (a[mid] > x) {
                // Pred: P3 && a[mid] > x
                left = mid;
                // Post: left` = mid && left` < right && для 0 <= i <= left`: a[i] >= x && для right <= j < a.length: a[j] < x
            } else {
                // Pred: P3 && a[mid] <= x
                right = mid;
                // Post: right` = mid && left < right` && для 0 <= i <= left: a[i] >= x && для right` <= j < a.length: a[j] < x
            }
            // Post: Inv

        }
        // Post: Inv

        // Pred: Inv
        return right;
        // Post: right = min{0 <= i <= a.length | i = a.length ИЛИ a[i] <= x}
    }

    // Pred: P1 = -1 <= left < right <= a.length && для всех 0 <= i <= left: a[i] > x && для всех right <= j < a.length: a[j] <= x
    // && для всех 0 <= i < j < a.length: a[i] >= a[j]
    // Post: res = min{left < i <= right | i = a.length ИЛИ a[i] <= x}
    public static int recursiveBinSearch(int x, int[] a, int left, int right) {
        // Pred: P1 && left == right - 1
        if (left == right - 1) {
            // Prev: P1 && left == right - 1
            return right;
            // Post: right = min{left < i <= right | i = a.length ИЛИ a[i] <= x}
        }
        // Post: P1

        // Pred: P1
        int mid = (left + right) / 2;
        // Post: P2 = (P1 && mid = (left + right) / 2 && && left < mid < right)

        // Pred: P2 && (a[mid] > x || P2 && a[mid] <= x)
        if (a[mid] > x) {
            // Pred: P2 && a[mid] > x
            return recursiveBinSearch(x, a, mid, right);
            // Post: recursiveBinSearch(x, a, mid, right) = min{mid < i <= right | i = a.length ИЛИ a[i] <= x}
        } else {
            // Pred: P2 && a[mid] <= x
            return recursiveBinSearch(x, a, left, mid);
            // Post: recursiveBinSearch(x, a, left, mid) = min{left < i <= mid | i = a.length ИЛИ a[i] <= x}
        }
        // Post: res = min{left < i <= right | i = a.length ИЛИ a[i] <= x}
    }
}
