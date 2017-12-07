package com.company;

public class Main {
    static int NUM_OF_OPERATOR = 7;

    static int STATE_NUMBER = 0;
    static int STATE_OPERATOR = 1;
    static int STATE_PARENTHESIS_OPEN = 2;
    static int STATE_PARENTHESIS_CLOSE = 3;
    static int STATE_PLUS_MINUS = 4;
    static int STATE_DOT = 5;

    static char[] operators = new char[] {'(', ')', '^', '*', '/', '%', '+', '-', ')', '.', ' ', '\0'};
    static int[] precedences = new int[] {-99, -99, 3, 2, 2, 2, 1, 1, 0, -1, -2};
    static char[] stack = new char[10000];
    static int sp = 0;

    private static void clearStack() { sp = 0; }
    private static void push(char val) {
        stack[sp++] = val;
    }
    private static char pop() {
        if (sp > 0) return stack[--sp];
        else return (char)-1;
    }

    private static char peek() {
        if (sp > 0) return stack[sp - 1];
        else return (char)-1;
    }

    private static boolean isStackEmpty() {
        return (sp <= 0);
    }

    private static int getPrecedence(char op) {
        for (int i = 0; operators[i] != '\0'; i++) {
            if (operators[i] == op) return precedences[i];
        }
        return -1;
    }

    private static String InfixToPostfix(String expr) {
        String result = "";
        char code;
        int prevState = STATE_OPERATOR;

        clearStack();

        for (int index = 0; index < expr.length(); index++) {
            code = expr.charAt(index);

            if (code == ' ') continue;

            if (code == '(') { // 시작 괄호를 만나면 푸시
                push(code);
                prevState = STATE_PARENTHESIS_OPEN;
            }
            else if (code == ')') { //  끝 괄호를 만나면 시작 괄호가 나올 때까지 pop하여 출력
                while(!isStackEmpty()) {
                    if (peek() == '(') break;
                    result = result + ' ' + pop();
                }

                if (isStackEmpty()) return "No matched parenthesis open!";

                pop(); // 시작 괄호 자체는 버린다.
                // prevState = STATE_PARENTHESIS_CLOSE;
            }
            else {
                int prec = getPrecedence(code);

                if (prec == -1) { // 숫자인 경우
                    boolean haveDot = false;

                    if (prevState == STATE_NUMBER) return "No operator!";

                    if (prevState != STATE_PLUS_MINUS) result = result + ' ';
                    while(index < expr.length()) {
                        code = expr.charAt(index);
                        if (code == '.') {
                            if (haveDot) { // 점이 두 개 있는 경우
                                return "Illegal dot!";
                            }
                            haveDot = true;
                        }
                        result = result + code;
                        if (index + 1 < expr.length()) {
                            // 숫자 또는 점이 연속해서 나오는 경우 하나의 숫자로 취급
                            if (getPrecedence(expr.charAt(index + 1)) == -1) index++;
                            else break;
                        }
                        else break;
                    }
                    if (code == '.') return "Illegal dot!";

                    prevState = STATE_NUMBER;
                }
                else { // 연산자이면
                    if (prevState == STATE_PLUS_MINUS) {
                        return "Illegal mark!";
                    }

                    if (prevState == STATE_OPERATOR || prevState == STATE_PARENTHESIS_OPEN) { // 연산자가 연속으로 오는 경우
                        if (code == '+' || code == '-') {
                            // '+', '-' 의 경우 직전의 기호가 연산자였으면 이 기호는 양수/음수 기호다.
                            if (code == '-') result = result + ' ' + code;
                            else result = result + ' '; // plus는 출력하지 말자!

                            prevState = STATE_PLUS_MINUS;
                        }
                        else {
                            return "Illegal operator!";
                        }
                    }
                    if (prevState == STATE_OPERATOR || prevState == STATE_PARENTHESIS_OPEN) { // 연산자가 연속으로 오는 경우
                        if (code == '+' || code == '-') {
                            // '+', '-' 의 경우 직전의 기호가 연산자였으면 이 기호는 양수/음수 기호다.
                            if (code == '-') result = result + ' ' + code;
                            else result = result + ' '; // plus는 출력하지 말자!

                            prevState = STATE_PLUS_MINUS;
                        }
                        else {
                            return "Illegal operator!";
                        }
                    }
                    else {
                        while (!isStackEmpty()) {
                            if (getPrecedence(peek()) >= prec) {
                                // 스택에 있는 연산자가 현재의 연산자보다 우선순위가 높거나 같으면 계속해서 pop하여 출력.
                                result = result + ' ' + pop();
                            } else break;
                        }
                        push(code); // 현 연산자를 스택에 푸시
                        prevState = STATE_OPERATOR;
                    }
                }
            }
        }

        if (prevState == STATE_OPERATOR) return "Missing operand!";

        // 스택에 남아있는 모든 연산자를 pop하여 출력.
        while(!isStackEmpty()) {
            code = pop();
            if (code == '(') return "No matched parenthesis close!";
            result = result + ' ' + code;
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println( InfixToPostfix("1 + 2*3 - (4*(5 + 6))*7") ); // 1 2 3 * + 4 5 6 + * 7 * -
        System.out.println( InfixToPostfix("1 + 2*3 - (4*(55 + 6))*7") ); // 1 2 3 * + 4 55 6 + * 7 * -
        System.out.println( InfixToPostfix("1 + 2*3 - (-4*(55 + 6))*7") ); // 1 2 3 * + -4 55 6 + * 7 * -
        System.out.println( InfixToPostfix("1 + 2*3 - (--4*(55 + 6))*7") ); // Illegal mark!
        System.out.println( InfixToPostfix("1 + 2*3 - (+4*(55 + 6))*7") ); // 1 2 3 * + 4 55 6 + * 7 * -
        System.out.println( InfixToPostfix("1 + 2*3 - (+-4*(55 + 6))*7") ); // Illegal mark!
        System.out.println( InfixToPostfix("1 + 2*3 - (-4.4*(55 + 6))*7") ); // 1 2 3 * + -4.4 55 6 + * 7 * -
        System.out.println( InfixToPostfix("1 + 2*3 - (-4.4.*(55 + 6))*7") ); // Illegal dot!
        System.out.println( InfixToPostfix("1 + 2*3 - (-4.4.4*(55 + 6))*7") ); // Illegal dot!
        System.out.println( InfixToPostfix("1 + 2*3 - (4*(5 5 + 6))*7") ); // No operator!
        System.out.println( InfixToPostfix("1 + 2*3 - (4*(55 + 6))7") ); // No operator!
        System.out.println( InfixToPostfix("1 + 2*3 - (/4.4*(55 + 6))*7") ); // Illegal operator!
        System.out.println( InfixToPostfix("1 + 2*3 - (4.4*(55 + 6))*7-") ); // Missing operand!
        System.out.println( InfixToPostfix("*1 + 2*3 - (-4.4*(55 + 6))*7") ); // Illegal operator!
        System.out.println( InfixToPostfix("1 + 2*3 - -(-4.4*(55 + 6))*7") ); // Illegal operator!
    }
}
