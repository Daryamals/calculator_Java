package Calcul;

import java.util.*;

public class CalculatorModel {
    // зоны, которые в нашей программе не может изменять пользователь и другие разработчики
    private Stack<Character> operations = new Stack<>();
    private Stack<Double> numbers = new Stack<>();
    private String expression;
    private double x;

    //функция, которая позволяет установить выражение. Удаляет предыдущие значения и подставляет новое
        public void setExpression(String expression) {
        operations.clear();
        numbers.clear();
        this.expression = expression;
    }
    // позволяет подставить х
    public void setXValue(double x) {
        this.x = x;
    }
    // функция, где проходит расчет
    public void calculate() {
        // проверка на корректность ввода.
        //Если нет операторов(+,- и т.д.) или чисел, то из программы выйдет и возникнет ошибка.
        if (operations.isEmpty() || numbers.isEmpty()) {
            throw new IllegalStateException("Incorrect expression");
        }
        // pop - функция, которая удаляет сверху оператор/число и присваевает его переменной.
        char sign = operations.pop();
        double num1 = numbers.pop();
        double num2 = 0;

        // ищем указанные знаки через метод возвращения индекса первого значения indexOf(),
        // если ничего не находит, то он вернет "-1"
        if ("+-*/^m".indexOf(sign) != -1) {
            if (numbers.isEmpty()) {
                throw new IllegalStateException("Incorrect expression");
            }
            num2 = numbers.pop();
        }
        switch (sign) {
            case '+': numbers.push(num1 + num2); break;
            case '-': numbers.push(num2 - num1); break;
            case '*': numbers.push(num1 * num2); break;
            case '/': numbers.push(num2 / num1); break;
            case 's': numbers.push(Math.sin(num1)); break;
            case 'c': numbers.push(Math.cos(num1)); break;
            case 't': numbers.push(Math.tan(num1)); break;
            case 'a': numbers.push(Math.asin(num1)); break;
            case 'g': numbers.push(Math.acos(num1)); break;
            case 'n': numbers.push(Math.atan(num1)); break;
            case 'l': numbers.push(Math.log(num1)); break;
            case 'o': numbers.push(Math.log10(num1)); break;
            case 'm': numbers.push(num2 % num1); break;
            case 'q': numbers.push(Math.sqrt(num1)); break;
            case '^': numbers.push(Math.pow(num2, num1)); break;
            default: numbers.push(num1); break;
        }
    }

    // Через метод проверяем есть ли в веденном выражении эти названия функций и присваиваем им индекс
    public int readFunction(String expression) {
        int index = 0;
        if (expression.startsWith("ln")) {
            operations.push('l');
            index = 2;
        } else if (expression.startsWith("log")) {
            operations.push('o');
            index = 3;
        } else if (expression.startsWith("mod")) {
            operations.push('m');
            index = 3;
        } else if (expression.startsWith("sin")) {
            operations.push('s');
            index = 3;
        } else if (expression.startsWith("cos")) {
            operations.push('c');
            index = 3;
        } else if (expression.startsWith("tan")) {
            operations.push('t');
            index = 3;
        } else if (expression.startsWith("asin")) {
            operations.push('a');
            index = 4;
        } else if (expression.startsWith("acos")) {
            operations.push('g');
            index = 4;
        } else if (expression.startsWith("atan")) {
            operations.push('n');
            index = 4;
        } else if (expression.startsWith("sqrt")) {
            operations.push('q');
            index = 4;
        } else {
            throw new IllegalStateException("Incorrect expression");
        }
        return index;
    }
    // присваиваем "действия"
    public int getRank(char type) {
        switch (type) {
            case '(': return 0;
            case '+':
            case '-': return 1;
            case '*':
            case '/':
            case 'm': return 2;
            case '^': return 3;
            case 's':
            case 'c':
            case 't':
            case 'a':
            case 'g':
            case 'n':
            case 'l':
            case 'o':
            case 'q': return 4;
            default: return -1;
        }
    }

    // парсер строки, чтобы считывать операторы, числа и функции
    public double parse() {
        int bracket = 0;
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch) || (i == 0 && "+-".indexOf(ch) != -1) ||
                    (i != 0 && expression.charAt(i - 1) == '(' && "+-".indexOf(ch) != -1)) {
                // выше условие проверяет, является ли текущий символ числом или знаком,
                // который находится либо в начале выражения, либо сразу после открывающей скобки
                StringBuilder numberBuilder = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numberBuilder.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(numberBuilder.toString()));
                i--;
            } else if ("+-*/^".indexOf(ch) != -1) {
                while (!operations.isEmpty() && getRank(ch) <= getRank(operations.peek()) && getRank(ch) != 3) {
                    calculate();
                }
                operations.push(ch);
            } else if (ch == '(') {
                bracket++;
                operations.push(ch);
            } else if (ch == ')') {
                bracket--;
                while (!operations.isEmpty() && operations.peek() != '(') {
                    calculate();
                }
                if (!operations.isEmpty()) {
                    operations.pop();
                }
            } else if (ch == 'x') {
                numbers.push(x);
            } else if ("sctalm".indexOf(ch) != -1) {
                i += readFunction(expression.substring(i)) - 1;
            } else {
                throw new IllegalStateException("Incorrect expression");
            }
        }
        while (!operations.isEmpty()) {
            calculate();
        }
        if (numbers.size() != 1) {
            throw new IllegalStateException("Incorrect expression");
        }
        return numbers.pop();
    }

    public static void main(String[] args) {
        CalculatorModel calculator = new CalculatorModel();
        calculator.setExpression("3+5*2");
        System.out.println(calculator.parse());  // Should print 13.0
    }
}
