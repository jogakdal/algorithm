package com.company;

import java.util.Random;

public class Main {
    static int MAP_WIDTH = 10;
    static int MAP_HEIGHT = 10;
    static int NUM_OF_WALL = (MAP_WIDTH*MAP_HEIGHT)/3;
    static int NUM_OF_WEAK_WALL = (MAP_WIDTH*MAP_HEIGHT)/10;
    static int NUM_OF_BOMB = 3;

    static class Coord {
        public int x;
        public int y;
        public int dist;

        public Coord() {
            x = 0;
            y = 0;
            dist = 0;
        }
        public Coord(int _x, int _y, int _dist) {
            x = _x;
            y = _y;
            dist = _dist;
        }
        public Coord(Coord other) {
            x = other.x;
            y = other.y;
            dist = other.dist;
        }
    };

    static Coord start;
    static Coord goal;

    static int[][] masterMap = new int [MAP_HEIGHT][MAP_WIDTH];
    static int minDist = 0;

    private static void copyMap(int[][] src, int[][] dest) {
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                dest[i][j] = src[i][j];
            }
        }
    }

    private static void makeMaze() {
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

        // 임의의 죄표에 폭탄으로 뚫을 수 있는 벽을 만듦
        for (int i = 0; i < NUM_OF_WEAK_WALL; i++) {
            x = rand.nextInt(MAP_WIDTH);
            y = rand.nextInt(MAP_HEIGHT);
            masterMap[y][x] = 2;
        }

        // 시작점 생성
        start = new Coord (rand.nextInt(MAP_HEIGHT), rand.nextInt(MAP_WIDTH), 0);
        masterMap[start.y][start.x] = 3;

        // 도착점 생성
        goal = new Coord();
        while(true) {
            goal.x = rand.nextInt(MAP_WIDTH);
            goal.y = rand.nextInt(MAP_HEIGHT);
            if (masterMap[goal.y][goal.x] != 3) {
                masterMap[goal.y][goal.x] = 4;
                break;
            }
        }
    }

    static Coord[] coordQueue = new Coord[MAP_HEIGHT*MAP_WIDTH];
    static int enq = 0;
    static int deq = 0;

    static void Enqueue(Coord coord) {
        coordQueue[enq] = new Coord(coord);
        enq++;
    }

    static Coord Dequeue() {
        if (deq >= enq) return null;
        deq++;
        return coordQueue[deq - 1];
    }

    static boolean isQueueEmpty() {
        return deq >= enq;
    }

    public static int travel(int[][] argMap, int startX, int startY) {
        // int dist = 0;
        int[][] map = new int[MAP_HEIGHT][MAP_WIDTH];
        Coord curr;

        copyMap(argMap, map);

        enq = 0;
        deq = 0;
        Enqueue(new Coord(startX, startY, 0));

        while(!isQueueEmpty()) {
            curr = Dequeue();
            if (null == curr) return -1;

            if (map[curr.y][curr.x] == 4) return curr.dist;

            if (curr.y > 0) {
                if (map[curr.y - 1][curr.x] == 4) return curr.dist + 1;
                if (map[curr.y - 1][curr.x] == 0) {
                    Enqueue(new Coord(curr.x, curr.y - 1, curr.dist + 1));
                    //  한 번 queue에 들어 간 것은 다시 넣지 않기 위해
                    map[curr.y - 1][curr.x] = -1;
                }
            }

            if (curr.y < MAP_HEIGHT - 1) {
                if (map[curr.y + 1][curr.x] == 4) return curr.dist + 1;
                if (map[curr.y + 1][curr.x] == 0) {
                    Enqueue(new Coord(curr.x, curr.y + 1, curr.dist + 1));
                    //  한 번 queue에 들어 간 것은 다시 넣지 않기 위해
                    map[curr.y + 1][curr.x] = -1;
                }
            }

            if (curr.x > 0) {
                if (map[curr.y][curr.x - 1] == 4) return curr.dist + 1;
                if (map[curr.y][curr.x - 1] == 0) {
                    Enqueue(new Coord(curr.x - 1, curr.y, curr.dist + 1));
                    //  한 번 queue에 들어 간 것은 다시 넣지 않기 위해
                    map[curr.y][curr.x - 1] = -1;
                }
            }

            if (curr.x < MAP_WIDTH - 1) {
                if (map[curr.y][curr.x + 1] == 4) return curr.dist + 1;
                if (map[curr.y][curr.x + 1] == 0) {
                    Enqueue(new Coord(curr.x + 1, curr.y, curr.dist + 1));
                    //  한 번 queue에 들어 간 것은 다시 넣지 않기 위해
                    map[curr.y][curr.x + 1] = -1;
                }
            }

            //  한 번 방문한 곳은 다시 가지 않기 위해
            map[curr.y][curr.x] = -1;
        }

        return -1;
    }

    // Weak wall을 NUM_OF_BOMB 개씩 제거해 가면서 최단거리를 구한다.
    private static void ElimAndTravel(int[][] argMap, Coord[] arrWeakWalls, int[] combi, int index, int numOfWalls, int k, int target) {
        if (k == 0) { // combi에 3개가 채워진 경우
            int[][] map = new int[MAP_HEIGHT][MAP_WIDTH];
            Coord cTemp;

            copyMap(argMap, map);

            // 조합된 3개의 week wall을 길로 변경.
            for (int i = 0; i < NUM_OF_BOMB; i++) {
                cTemp = arrWeakWalls[combi[i]];
                map[cTemp.y][cTemp.x] = 0;
                // map[arrWeakWalls[i].y][arrWeakWalls[i].x] = 0;
            }

            // 변경한 상태에서 최단 거리 탐색
            int dist = travel(map, start.x, start.y);

            //  이전에 탐색한 루트보다 짧으면 답의 후보로 저장
            if (dist != -1 && dist < minDist) minDist = dist;
        }
        else if (target >= numOfWalls) return;
        else {
            combi[index] = target;
            ElimAndTravel(argMap, arrWeakWalls, combi, index + 1, numOfWalls, k - 1, target + 1);
            ElimAndTravel(argMap, arrWeakWalls, combi, index, numOfWalls, k, target + 1);
        }
        return;
    }

    public static int travelWithBomb(int[][] argMap) {
        Coord[] coordWeakWall = new Coord[NUM_OF_WEAK_WALL + 1];
        int index = 0;

        // 폭탄으로 터뜨릴 벽의 좌표를 모두 구함
        for(int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                if (argMap[i][j] == 2) {
                    coordWeakWall[index] = new Coord(j, i, 0);
                    index++;
                }
            }
        }
        minDist = MAP_WIDTH*MAP_HEIGHT + 1;

        int[] combi = new int[NUM_OF_BOMB + 1];
        ElimAndTravel(argMap, coordWeakWall, combi, 0, index, NUM_OF_BOMB, 0);

        if (minDist == MAP_WIDTH*MAP_HEIGHT + 1) return -1;
        return minDist;
    }


    public static void printMap(int[][] argMap) {
        int[][] map = new int[MAP_HEIGHT][MAP_WIDTH];

        copyMap(argMap, map);

        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int dist;

        makeMaze();

        printMap(masterMap);

        dist = travelWithBomb(masterMap);

        System.out.println("Shortest route is " + dist);
    }
}
