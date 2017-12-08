package jogakdal.algorithm;

public class Main {
    static int[] denominations = new int[] {1, 4, 6};
    static int goal = 8;
    static int result = -1;

    private static void DP(int k, int sum, int coinCount, String order) {
        if (sum == goal) {
            if (result > coinCount) result = coinCount;
            System.out.println(order + ": coinCount = " + coinCount);
            return;
        }

        if (k >= denominations.length) {
            System.out.println(order);
            return;
        }


        for (int i = 0; sum + i*denominations[k] <= goal; i++) {
            DP(k + 1, sum + i*denominations[k], coinCount + i, order + denominations[k] + "won * " + i + "  +  ");
        }
    }

    public static void main(String[] args) {
        result = goal + 1;

        DP(0, 0, 0, "");

        if (result == goal + 1) System.out.println("No result.");
        else System.out.println(result);
    }
}
