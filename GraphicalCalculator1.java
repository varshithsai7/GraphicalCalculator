import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GraphicalCalculator1 {
    private JFrame frame;
    private JTextField textField;
    private boolean justEvaluated = false; // Declared properly as a field

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphicalCalculator1::new);
    }

    public GraphicalCalculator1() {
        frame = new JFrame("Graphical Calculator");
        textField = new JTextField("0"); // Default to 0

        frame.setLayout(new BorderLayout());
        frame.add(textField, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 4));
        String[] buttons = {
            "7", "8", "9", "+",
            "4", "5", "6", "-",
            "1", "2", "3", "*",
            "0", "C", "=", "/"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(new CalculatorButtonActionListener());
            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class CalculatorButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            String currentText = textField.getText();

            if (command.equals("C")) {
                textField.setText("0");
                justEvaluated = false;
            } else if (command.equals("=")) {
                try {
                    String expression = textField.getText();
                    String postfix = infixToPostfix(expression);
                    double result = evaluatePostfix(postfix);
                    textField.setText(String.valueOf(result));
                    justEvaluated = true;
                } catch (Exception ex) {
                    textField.setText("Error");
                    justEvaluated = true;
                }
            } else {
                if (justEvaluated) {
                    if (command.matches("[0-9\\.]")) {
                        textField.setText(command); // Start fresh for number/dot
                    } else {
                        textField.setText(currentText + command); // Continue for operator
                    }
                    justEvaluated = false;
                } else {
                    if (currentText.equals("0") && command.matches("[0-9]")) {
                        textField.setText(command); // Avoid leading zeros
                    } else {
                        textField.setText(currentText + command);
                    }
                }
            }
        }

        private String infixToPostfix(String infix) {
            StringBuilder postfix = new StringBuilder();
            Stack<Character> stack = new Stack<>();
            for (int i = 0; i < infix.length(); i++) {
                char c = infix.charAt(i);
                if (Character.isDigit(c)) {
                    postfix.append(c);
                    // Add space if next character is not a digit
                    if (i + 1 == infix.length() || !Character.isDigit(infix.charAt(i + 1))) {
                        postfix.append(' ');
                    }
                } else if (c == '(') {
                    stack.push(c);
                } else if (c == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        postfix.append(stack.pop()).append(' ');
                    }
                    if (!stack.isEmpty()) stack.pop();
                } else if (isOperator(c)) {
                    while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                        postfix.append(stack.pop()).append(' ');
                    }
                    stack.push(c);
                }
            }
            while (!stack.isEmpty()) {
                postfix.append(stack.pop()).append(' ');
            }
            return postfix.toString().trim();
        }

        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/';
        }

        private int precedence(char c) {
            return switch (c) {
                case '+', '-' -> 1;
                case '*', '/' -> 2;
                default -> -1;
            };
        }

        private double evaluatePostfix(String postfix) {
            Stack<Double> stack = new Stack<>();
            String[] tokens = postfix.split("\\s+");
            for (String token : tokens) {
                if (token.matches("\\d+(\\.\\d+)?")) {
                    stack.push(Double.parseDouble(token));
                } else if (token.matches("[+\\-*/]")) {
                    double b = stack.pop();
                    double a = stack.pop();
                    switch (token.charAt(0)) {
                        case '+' -> stack.push(a + b);
                        case '-' -> stack.push(a - b);
                        case '*' -> stack.push(a * b);
                        case '/' -> stack.push(a / b);
                    }
                }
            }
            return stack.pop();
        }
    }
}
