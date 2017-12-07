package com.company;

public class Main {
    private static int[] boxes = new int[] {3, 10, 5, 2, 8, 100, 7, 4};

    private static int result;
    private static int size;
    private static int loopCount = 0;

    private static void calc(int index, int prevLess, int prevGreater, int sum) {
        loopCount++;
        if (sum > result) result = sum;
        if (index >= size) return;

        calc(index + 1, prevLess, prevGreater, sum);
        if (boxes[index] > prevLess) {
//            calc(index + 1, prevLess, prevGreater, sum);
            calc(index + 1,boxes[index], prevGreater, sum + boxes[index]);
        }
        if (boxes[index] < prevGreater) {
//            calc(index + 1,prevLess, prevGreater, sum);
            calc(index + 1,prevLess, boxes[index], sum + boxes[index]);
        }
    }

    public static void main(String[] args) {
	    int minVal = boxes[0], maxVal = boxes[0];

        size = boxes.length;

	    for (int i = 0; i < size; i++) {
	        if (boxes[i] < minVal) minVal = boxes[i];
	        if (boxes[i] > maxVal) maxVal = boxes[i];
        }

        result = minVal;
	    calc(0, minVal - 1, maxVal + 1, 0);
        System.out.println(result);
        System.out.println(loopCount);
    }
}
