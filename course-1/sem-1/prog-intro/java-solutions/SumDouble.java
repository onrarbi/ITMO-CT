public class SumDouble {
    public static void main(String[] args) {
        double sum = 0.0;

        for (String arg : args) {
            int numBegin = -1;

            for (int i = 0; i <= arg.length(); i++) {
                if (i < arg.length() && !Character.isWhitespace(arg.charAt(i)) && numBegin == -1) {
                    numBegin = i;
                } else if ((i == arg.length() || Character.isWhitespace(arg.charAt(i))) && numBegin != -1) {
                    sum += Double.parseDouble(arg.substring(numBegin, i));
                    numBegin = -1;
                }
            }
        }

        System.out.println(sum);
    }
}