public class Main {
    // static int[] inputs = new int[] {1, 2, 3, 2, 3, 1, 3, 1, 2, 1, 2, 3, 2, 1, 2, 3, 1};
    // static int N = 17;

    // static int[] inputs = new int[] {2, 1, 1, 3, 1, 1};
    // static int N = 6;

    static int[] inputs = new int[] {1, 2, 3, 1, 2, 2, 3, 1, 2, 1, 3};
    static int N = 11;

    private static int stackBlocks(int step, int index) {
        if (index >= N) return step;
        if (index == N - 1) return step + 1;
        if (inputs[index] == 2) {
            step = stackBlocks(step + 1, index + 1);
            if (step != -1) return step;
        }
        if (index <= N - 2 && inputs[index] + inputs[index + 1] >= 3  && inputs[index] + inputs[index + 1] <= 5) {
            step = stackBlocks(step + 1, index + 2);
            if (step != -1) return step;
        }
        if (index <= N - 3 && inputs[index]*inputs[index + 1]*inputs[index + 2] == 6) {
            step = stackBlocks(step + 1, index + 3);
            if (step != -1) return step;
        }
        return -1;
    }

    public static void main(String[] args) {

        System.out.println(stackBlocks(0, 0));

    }
}
