package search;

public class BinarySearch3233 {

    /*
    Model: a = args[0...n] && a[k] < a[k+1] < ... < a[n-1] < a[0] < a[1] < ... < a[k-1]
     */

    // Pred: args.length >= 1
    // Post: выведено k (индекс циклического сдвига массива a) && 0 <= k < args.length
    public static void main(String[] args) {
        // Pred: args.length >= 1
        int[] a = new int[args.length];
        // Post: P1 = (a = new int[args.length])

        // Pred: P1
        // Inv: для всех 0 <= j < i: a[j] = Integer.parseInt(args[j])
        for (int i = 0; i < args.length; i++) {
            // Inv
            a[i] = Integer.parseInt(args[i]);
            // Post: a[i] = Integer.parseInt(args[i]) && Inv
        }
        // Post: P2 = (для 0 <= i < args.length: a[i] = Integer.parseInt(args[i]))

        // Pred: P2
        int k1 = iterativeBinSearch(a);
        // Post: P3 = (k1 - индекс циклического сдвига массива a && P2)

        // Pred: P3
        int k2 = recursiveBinSearch(a, 0, a.length - 1);
        // Post: P4 = (k2 - индекс циклического сдвига массива a && P3)

        // Pred: (k1 == k2 && P4) || (k1 != k2 && P4)
        if (k1 == k2) {
            // Pred: k1 == k2 && P4
            System.out.println(k1);
            // Post: выведено k1 && P4
        } else {
            // Pred: k1 != k2 && P4
            System.err.println("Iterative search: " + k1 + "; Recursive search: " + k2);
            // Post: выведено k1 && выведено k2 && P4
        }
    }

    // Pred: P1 = (a.length > 0)
    // Post: res = k && 0 <= k < a.length && k - индекс минимального элемента массива a
    public static int iterativeBinSearch(int[] a) {
        // Pred: P1
        int left = 0;
        int right = a.length - 1;
        // Post: P2 = (left = 0 && right = a.length - 1 && P1)

        // Pred: P2
        // Inv: left <= k <= right
        while (left < right) {
            // Pred: Inv
            int mid = (left + right) / 2;
            // Post: P3 = (Inv && mid = (left + right) / 2 && left <= mid < right)

            // Pred: P3 && a[mid] > a[right] || P3 && a[mid] <= a[right]
            if (a[mid] > a[right]) {
                // Pred: P3 && a[mid] > a[right] && left <= mid < k <= right
                left = mid + 1;
                // Post: left` = mid + 1 && left` <= k <= right
            } else {
                // Pred: P3 && a[mid] <= a[right] && left <= k <= mid <= right
                right = mid;
                // Post: right` = mid && left <= k <= mid <= right`
            }
            // Post: Inv

        }
        // Post: Inv

        // Pred: Inv
        return right;
        // Post: res = k && 0 <= k < a.length && k - индекс минимального элемента массива a
    }

    // Pred: P1 = (a.length > 0 && 0 <= left <= right < a.length)
    // Post: res = k && 0 <= k < a.length && k - индекс минимального элемента массива a
    public static int recursiveBinSearch(int[] a, int left, int right) {
        // Pred: P1 && left == right
        if (left == right) {
            // Prev: P1 && left == right
            return right;
            // Post: k && 0 <= k < a.length && k - индекс циклического сдвига массива a
        }
        // Post: P1

        // Pred: P1
        int mid = (left + right) / 2;
        // Post: P2 = (P1 && mid = (left + right) / 2 && left <= mid <= right)

        // Pred: P2 && a[mid] > a[right] || P2 && a[mid] <= a[right]
        if (a[mid] > a[right]) {
            // Pred: P2 && a[mid] > a[right] && left <= mid < k <= right
            return recursiveBinSearch(a, mid + 1, right);
            // Post: k && 0 <= k < a.length && k - индекс циклического сдвига массива a в правой части
        } else {
            // Pred: P2 && a[mid] <= a[right] && left <= k <= mid <= right
            return recursiveBinSearch(a, left, mid);
            // Post: k && 0 <= k < a.length && k - индекс циклического сдвига массива a в левой части
        }
        // Post: res = k && 0 <= k < a.length && k - индекс минимального элемента массива a
    }
}
