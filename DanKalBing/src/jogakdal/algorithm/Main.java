package jogakdal.algorithm;

import java.util.Scanner;

public class Main {
    private static class EndPoint {
        public int x;
        public int y;
        public int contactCount;

        EndPoint(int x, int y) {
            this.x = x;
            this.y = y;
            contactCount = 1;
        }

        public boolean isSame(EndPoint other) {
            return other.x == x && other.y == y;
        }
        public boolean isSame(int _x, int _y) {
            return _x == x && _y == y;
        }
    }

    private static class Line {
        public int Sx;
        public int Sy;
        public int Ex;
        public int Ey;

        Line(int Sx, int Sy, int Ex, int Ey) {
            if (Sx < Ex || (Sx == Ex && Sy > Ey)) {
                this.Sx = Sx;
                this.Sy = Sy;
                this.Ex = Ex;
                this.Ey = Ey;
            }
            else {
                this.Sx = Ex;
                this.Sy = Ey;
                this.Ex = Sx;
                this.Ey = Sy;
            }
        }

        public boolean isFlatLinear(Line other) {
            return  (other.Ey - other.Sy)*(Ex - Sx) == (other.Ex - other.Sx)*(Ey - Sy)  &&
                    (other.Ex - other.Sx)*(Sy - other.Sy) == (other.Ey - other.Sy)*(Sx - other.Sx) &&
                    (Ex - Sx)*(Sy - other.Sy) == (Ey - Sy)*(Sx - other.Sx);
        }
    }

    private static class Circle {
        public int Cx, Cy;
        public int R;

        public int square(int n) {
            return n*n;
        }
        public int abs(int n) {
            return (n < 0)? -n : n;
        }

        public boolean hasCollision(Line l) {
            int a = square(l.Ex - l.Sx) + square(l.Ey - l.Sy);
            int b = 2*((l.Ex - l.Sx)*(l.Sx - Cx) + (l.Ey - l.Sy)*(l.Sy - Cy));
            int c = square(l.Sx - Cx) + square(l.Sy - Cy) - square(R);
            int d = b*b - 4*a*c;
            if (d < 0) return false;
            double f1 = (-b + Math.sqrt(d)) / (2*a);
            double f2 = (-b - Math.sqrt(d)) / (2*a);
            return (f1 >= 0 && f1 <= 1) || (f2 >= 0 && f2 <= 1);
        }

        public boolean hasCollision(Circle c) {
            if (c.Cx == Cx && c.Cy == Cy && c.R != R) return false;
            int d2 = square(c.Cx - Cx) + square(c.Cy - Cy);
            if (square(c.R - R) > d2 || square(c.R + R) < d2) return false;
            // if (square(c.R + R) < d2) return false;

            return true;
        }

        public boolean isSame(Circle c) {
            return c.Cx == Cx && c.Cy == Cy && c.R == R;
        }
    }

    private static int getCutCount(Circle[] circles, Line[] lines, int circleCount, int lineCount) {
        int result = 0;
        for (int i = 0; i < circleCount; i++) {
            boolean hasCollision = false;

            // 선분과 만나는 경우가 있는가
            for (int j = 0; j < lineCount; j++) {
                if (circles[i].hasCollision(lines[j])) {
                    hasCollision = true;
                    break;
                }
            }

            // 다른 원과 만나는 경우가 있는가
            if (!hasCollision) {
                for (int j = 0; j < circleCount; j++) {
                    if (!circles[i].isSame(circles[j])) {
                        if (circles[i].hasCollision(circles[j])) {
                            hasCollision = true;
                            break;
                        }
                    }
                }
            }

            // 독립된 원인 경우 카운트 증가
            if (!hasCollision) result++;
        }

        if (lineCount == 0 && circleCount != 0 && result == 0) return 1;

        if (lineCount > 0) {
            // 접점 수 계산
            EndPoint[] ep = new EndPoint[lineCount * 2];
            int epCount = 0;
            for (int i = 0; i < lineCount; i++) {
                int j;
                for (j = 0; j < epCount; j++) {
                    if (ep[j].isSame(lines[i].Sx, lines[i].Sy)) {
                        ep[j].contactCount++;
                        break;
                    }
                }
                if (j == epCount) { // 접점이 하나도 없는 경우
                    ep[epCount] = new EndPoint(lines[i].Sx, lines[i].Sy);
                    epCount++;
                }

                for (j = 0; j < epCount; j++) {
                    if (ep[j].isSame(lines[i].Ex, lines[i].Ey)) {
                        ep[j].contactCount++;
                        break;
                    }
                }
                if (j == epCount) { // 접점이 하나도 없는 경우
                    ep[epCount] = new EndPoint(lines[i].Ex, lines[i].Ey);
                    epCount++;
                }
            }

            int oddCount = 0;
            for (int i = 0; i < epCount; i++) { // 접점의 갯수가 홀수인 점들을 카운트
                if (ep[i].contactCount % 2 == 1) oddCount++;
            }
            result = result + oddCount / 2;
        }

        return result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int C = sc.nextInt();
        for (int i = 0; i < C; i++) {
            int N = sc.nextInt();
            Line[] lines = new Line[N];
            Circle[] circles = new Circle[N];
            int lineCount = 0;
            int circleCount = 0;
            for (int j = 0; j < N; j++) {
                String shape = sc.next();
                if (shape.equals("circle")) {
                    circles[circleCount] = new Circle();
                    circles[circleCount].Cx = sc.nextInt();
                    circles[circleCount].Cy = sc.nextInt();
                    circles[circleCount].R = sc.nextInt();
                    circleCount++;
                }
                else if (shape.equals("segment")) {
                    int Sx = sc.nextInt();
                    int Sy = sc.nextInt();
                    int Ex = sc.nextInt();
                    int Ey = sc.nextInt();

                    lines[lineCount] = new Line(Sx, Sy, Ex, Ey);

                    // 평행하게 겹치는 선분은 하나의 선분으로 만든다.
                    int k;
                    for (k = 0; k < lineCount; k++) {
                        if (lines[lineCount].isFlatLinear(lines[k])) {
                            if (lines[lineCount].Sx < lines[k].Sx || lines[lineCount].Sx == lines[k].Sx && lines[lineCount].Sy > lines[k].Sy) {
                                lines[k].Sx = lines[lineCount].Sx;
                                lines[k].Sy = lines[lineCount].Sy;
                            }
                            if (lines[lineCount].Ex > lines[k].Ex || lines[lineCount].Ex == lines[k].Ex && lines[lineCount].Ey < lines[k].Ey) {
                                lines[k].Ex = lines[lineCount].Ex;
                                lines[k].Ey = lines[lineCount].Ey;
                            }
                            break;
                        }
                    }
                    if (k == lineCount) lineCount++;
                }
            }

            int result = getCutCount(circles, lines, circleCount, lineCount);
            // if (i == 0) System.out.println();
            System.out.println(result);
        }
    }
}
/*
5
4
circle 0 0 10
circle 15 0 10
segment -10 0 5 0
segment 0 0 25 0
8
circle 0 0 10
segment 0 0 -10 -10
segment -10 -10 10 -10
segment 10 -10 0 0
segment 0 0 0 -15
segment -5 -10 -5 -15
segment 5 -10 5 -15
circle 0 5 2
2
circle 0 0 1
circle 10 10 2
2
circle 0 0 10
circle 1 1 10
2
segment 0 0 10 10
segment 11 10 12 13
 */