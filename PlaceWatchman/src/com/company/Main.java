package com.company;

import java.util.Random;

public class Main {
    static int MAP_WIDTH = 10;
    static int MAP_HEIGHT = 10;
    static int NUM_OF_WALL = (MAP_WIDTH*MAP_HEIGHT)/3;

    static int[][] masterMap = new int[MAP_HEIGHT][MAP_WIDTH];
    static int[] roadX = new int[MAP_HEIGHT*MAP_WIDTH];
    static int[] roadY = new int[MAP_HEIGHT*MAP_WIDTH];
    static int roadCount;

    private static void makeMap() {
        Random rand = new Random();
        int x, y;

        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                masterMap[i][j] = 0;
            }
        }

        // 임의의 죄표에 벽을 만듦
        for (int i = 0; i < NUM_OF_WALL; i++) {
            x = rand.nextInt(MAP_WIDTH);
            y = rand.nextInt(MAP_HEIGHT);
            masterMap[y][x] = 1;
        }
    }

    private static void listAllRoad() {
        roadCount = 0;

        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                if (masterMap[i][j] == 0) {
                    roadX[roadCount] = j;
                    roadY[roadCount] = i;
                    roadCount++;
                }
            }
        }
    }

    private static void copyMap(int[][] src, int[][] dest) {
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                dest[i][j] = src[i][j];
            }
        }
    }

    private static int calcWatchAreas(int[][] map, int x, int y) {
        int ret = 0;
        int tx, ty;

        tx = x;
        while(true) {
            if (tx < 0 || map[y][tx] == 1) break; // 경계에 닿았거나 벽을 만나면 Blocking
            if (map[y][tx] == 0) {
                map[y][tx] = -1; // 관찰된 길로 마킹
                ret++;
            }
            tx--;
        }

        tx = x + 1;
        while(true) {
            if (tx >= MAP_WIDTH || map[y][tx] == 1) break; // 경계에 닿았거나 벽을 만나면 Blocking
            if (map[y][tx] == 0) {
                map[y][tx] = -1; // 관찰된 길로 마킹
                ret++;
            }
            tx++;
        }

        ty = y;
        while(true) {
            if (ty < 0 || map[ty][x] == 1) break; // 경계에 닿았거나 벽을 만나면 Blocking
            if (map[ty][x] == 0) {
                map[ty][x] = -1; // 관찰된 길로 마킹
                ret++;
            }
            ty--;
        }

        ty = y + 1;
        while(true) {
            if (ty >= MAP_HEIGHT || map[ty][x] == 1) break; // 경계에 닿았거나 벽을 만나면 Blocking
            if (map[ty][x] == 0) {
                map[ty][x] = -1; // 관찰된 길로 마킹
                ret++;
            }
            ty++;
        }

        return ret;
    }
    private static int calcWatchable(int x1, int y1, int x2, int y2, int x3, int y3) {
        int[][] map = new int[MAP_HEIGHT][MAP_WIDTH];

        copyMap(masterMap, map);
        return calcWatchAreas(map, x1, y1) + calcWatchAreas(map, x2, y2) + calcWatchAreas(map, x3, y3);
    }

    public static void printMap() {
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                if (masterMap[i][j] == 0) System.out.print('.');
                if (masterMap[i][j] == 1) System.out.print('#');
                if (masterMap[i][j] == 2) System.out.print('&');
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int maxWatchable = 0;
        int optPlaceX1 = 0, optPlaceY1 = 0;
        int optPlaceX2 = 0, optPlaceY2 = 0;
        int optPlaceX3 = 0, optPlaceY3 = 0;

        makeMap();

        listAllRoad();

        for (int i = 0; i < roadCount - 2; i++) {
            for (int j = i + 1; j < roadCount - 1; j++) {
                for (int k = j + 1; k < roadCount; k++) {
                    int watchable = calcWatchable(roadX[i], roadY[i], roadX[j], roadY[j], roadX[k], roadY[k]);
                    if (watchable > maxWatchable) {
                        maxWatchable = watchable;
                        optPlaceX1 = roadX[i]; optPlaceY1 = roadY[i];
                        optPlaceX2 = roadX[j]; optPlaceY2 = roadY[j];
                        optPlaceX3 = roadX[k]; optPlaceY3 = roadY[k];
                    }
                }
            }
        }

        masterMap[optPlaceY1][optPlaceX1] = 2;
        masterMap[optPlaceY2][optPlaceX2] = 2;
        masterMap[optPlaceY3][optPlaceX3] = 2;

        printMap();

        System.out.println("minimal deadspace is " + (roadCount - maxWatchable));
    }

}
