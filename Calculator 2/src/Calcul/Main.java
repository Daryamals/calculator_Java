package Calcul;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Main extends JFrame {
    private JLabel display;
    private CalculatorModel calculator;
    private DepositModel depositCalculator;
    private CreditModel creditCalculator;
    private StringBuilder currentInput;
    private boolean isResultDisplayed;
    private JPanel currentPanel;
    private int mode = 0;
    private JTextField[] depositFields;
    private JTextField[] creditFields;
    private JComboBox<String> depositFrequency;
    private JComboBox<String> depositCapitalization;
    private JComboBox<String> creditType;
    private ArrayList<String> history;

    public Main() {
        calculator = new CalculatorModel();
        depositCalculator = new DepositModel();
        creditCalculator = new CreditModel();
        currentInput = new StringBuilder();
        isResultDisplayed = false;
        history = new ArrayList<>();

        setTitle("Калькулятор");
        setSize(400, 650); // увеличено для кнопки "История"
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.DARK_GRAY);  // Черный фон

        display = createDisplay();
        add(display, BorderLayout.NORTH);

        JButton modeSwitchButton = createStyledButton("Переключить режим", Color.BLUE);
        modeSwitchButton.addActionListener(e -> switchMode());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 2));
        bottomPanel.add(modeSwitchButton);

        JButton historyButton = createStyledButton("История", Color.ORANGE);
        historyButton.addActionListener(e -> showHistory());
        bottomPanel.add(historyButton);

        add(bottomPanel, BorderLayout.SOUTH);

        JPanel standardPanel = createStandardCalculator();
        JPanel depositPanel = createDepositCalculator();
        JPanel creditPanel = createCreditCalculator();

        currentPanel = standardPanel;
        add(currentPanel, BorderLayout.CENTER);
    }

    private JLabel createDisplay() {
        JLabel display = new JLabel("", SwingConstants.RIGHT);
        display.setFont(new Font("Segoe UI", Font.BOLD, 22));
        display.setBackground(Color.BLACK);
        display.setOpaque(true);
        display.setForeground(Color.WHITE);
        display.setPreferredSize(new Dimension(600, 150));
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return display;
    }

    private JPanel createStandardCalculator() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new GridLayout(6, 4, 5, 5));

        String[] buttons = {
                "C", "⌫", "+/-", "OFF",
                "sin", "cos", "tan", "sqrt",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "+", "="
        };

        for (String text : buttons) {
            JButton button;
            if (text.matches("[0-9.]")) {
                button = createStyledButton(text, Color.GRAY); // серый цвет для цифровых кнопок
            } else {
                button = createStyledButton(text, Color.LIGHT_GRAY);
            }
            if (text.equals("OFF")) {
                button.setBackground(Color.RED);
                button.setForeground(Color.WHITE);
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        button.setBackground(Color.GREEN);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        button.setBackground(Color.RED);
                    }
                });
            }
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        return panel;
    }

    private void calculateResult() {
        try {
            calculator.setExpression(currentInput.toString());
            double result = calculator.parse();
            String resultText = (result == (int) result) ? String.valueOf((int) result) : String.valueOf(result);
            display.setText("<html>" + resultText + "</html>");
            currentInput.setLength(0);
            currentInput.append(resultText);
            isResultDisplayed = true;
            history.add(currentInput.toString());
        } catch (Exception ex) {
            display.setText("<html>Ошибка: " + ex.getMessage() + "</html>");
            currentInput.setLength(0);
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (isResultDisplayed && !isOperator(command)) {
                currentInput.setLength(0);
                isResultDisplayed = false;
            }

            switch (command) {
                case "C":
                    clearInput();
                    break;
                case "⌫":
                    backspace();
                    break;
                case "+/-":
                    applyPlusMinus();
                    break;
                case "OFF":
                    System.exit(0);
                    break;
                case "=":
                    calculateResult();
                    break;
                default:
                    appendInput(command);
                    break;
            }
        }

        private boolean isOperator(String command) {
            return "+-*/".contains(command);
        }

        private void applyPlusMinus() {
            try {
                String currentText = display.getText();
                if (!currentText.isEmpty()) {
                    double value = Double.parseDouble(currentText);
                    value = -value;
                    display.setText(String.valueOf(value));
                    currentInput.setLength(0);
                    currentInput.append(value);
                }
            } catch (NumberFormatException ex) {
                display.setText("Ошибка");
                currentInput.setLength(0);
            }
        }

        private void clearInput() {
            currentInput.setLength(0);
            display.setText("");
        }

        private void backspace() {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
                display.setText(currentInput.toString());
            }
        }

        private void appendInput(String command) {
            if (isResultDisplayed && !isOperator(command)) {
                currentInput.setLength(0);
                isResultDisplayed = false;
            }

            if (isOperator(command)) {
                if (currentInput.length() == 0 || isOperator(currentInput.substring(currentInput.length() - 1))) {
                    if (currentInput.length() > 0) {
                        currentInput.setLength(currentInput.length() - 1);
                    }
                }
                isResultDisplayed = false;
            }

            currentInput.append(command);
            display.setText("<html>" + currentInput.toString() + "</html>");
        }
    }

    private JPanel createDepositCalculator() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new GridLayout(12, 2, 5, 5));

        String[] labels = {
                "Сумма депозита:", "Процентная ставка (%):", "Срок (месяцы):", "Налог (%):",
                "Пополнения:", "Снятия:", "Пополнение каждые (мес.):", "Снятие каждые (мес.):",
                "Периодичность:", "Капитализация:"
        };

        depositFields = new JTextField[8];
        String[] tooltips = {
                "Введите начальную сумму вклада. Пример: 100000.",
                "Введите годовую процентную ставку. Пример: 5.",
                "Введите срок вклада в месяцах. Пример: 12.",
                "Введите процент налога на доходы. Пример: 13.",
                "Введите сумму регулярного пополнения депозита. Пример: 1000.",
                "Введите сумму регулярного снятия с депозита. Пример: 500.",
                "Введите периодичность пополнения в месяцах. Пример: 1.",
                "Введите периодичность снятия в месяцах. Пример: 3."
        };

        for (int i = 0; i < 8; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setForeground(Color.WHITE);
            depositFields[i] = new JTextField();
            depositFields[i].setToolTipText(tooltips[i]);
            depositFields[i].setBackground(Color.BLACK);
            depositFields[i].setForeground(Color.WHITE);
            panel.add(label);
            panel.add(depositFields[i]);
        }

        depositFrequency = new JComboBox<>(new String[]{"Ежемесячно", "Ежеквартально", "Ежегодно"});
        depositFrequency.setToolTipText("Выберите периодичность начисления процентов.");
        panel.add(new JLabel("Периодичность:")).setForeground(Color.WHITE);
        depositFrequency.setBackground(Color.BLACK);
        depositFrequency.setForeground(Color.WHITE);
        panel.add(depositFrequency);

        depositCapitalization = new JComboBox<>(new String[]{"Да", "Нет"});
        depositCapitalization.setToolTipText("Выберите 'Да' для включения капитализации процентов.");
        panel.add(new JLabel("Капитализация:")).setForeground(Color.WHITE);
        depositCapitalization.setBackground(Color.BLACK);
        depositCapitalization.setForeground(Color.WHITE);
        panel.add(depositCapitalization);

        JButton calculateButton = createStyledButton("Рассчитать", Color.LIGHT_GRAY);
        calculateButton.addActionListener(e -> calculateDeposit());
        panel.add(calculateButton);

        JButton clearButton = createStyledButton("Очистить", Color.GRAY);
        clearButton.addActionListener(e -> clearDepositFields());
        panel.add(clearButton);

        return panel;
    }

    private void clearDepositFields() {
        for (JTextField field : depositFields) {
            field.setText("");
        }
        depositFrequency.setSelectedIndex(0);
        depositCapitalization.setSelectedIndex(0);
        display.setText("");
    }

    private void calculateDeposit() {
        try {
            double deposit = Double.parseDouble(depositFields[0].getText());
            double rate = Double.parseDouble(depositFields[1].getText());
            int term = Integer.parseInt(depositFields[2].getText());
            double tax = Double.parseDouble(depositFields[3].getText());
            double replenishments = Double.parseDouble(depositFields[4].getText());
            double withdrawals = Double.parseDouble(depositFields[5].getText());
            int monthR = Integer.parseInt(depositFields[6].getText());
            int monthW = Integer.parseInt(depositFields[7].getText());

            int periodicity = depositFrequency.getSelectedIndex();
            int effectivePeriodicity;

            switch (periodicity) {
                case 0:
                    effectivePeriodicity = 1;
                    break;
                case 1:
                    effectivePeriodicity = 3;
                    break;
                default:
                    effectivePeriodicity = 1;
            }

            char capitalization = depositCapitalization.getSelectedIndex() == 0 ? 'y' : 'n';

            if (rate == 0 || term == 0) {
                display.setText("Ошибка: Процентная ставка и срок не могут быть равны нулю.");
                return;
            }

            DepositConditions conditions = new DepositConditions(deposit, rate, term, tax, replenishments, withdrawals, monthR, monthW, effectivePeriodicity, capitalization);
            depositCalculator.calculateDeposit(conditions);

            String resultText = "<html>" +
                    "Доход: " + String.format("%.2f", depositCalculator.getIncome()) + "<br>" +
                    "Налог: " + String.format("%.2f", depositCalculator.getTaxOutcome()) + "<br>" +
                    "Итого: " + String.format("%.2f", depositCalculator.getTotalSum()) +
                    "</html>";

            display.setText(resultText);
            history.add(resultText.replaceAll("<br>", "\n").replaceAll("<html>", "").replaceAll("</html>", ""));
        } catch (Exception ex) {
            display.setText("Ошибка: " + ex.getMessage());
        }
    }

    private JPanel createCreditCalculator() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setLayout(new GridLayout(10, 2, 5, 5));

        String[] labels = {
                "Тип кредита:", "Сумма кредита:", "Процентная ставка (%):", "Срок (месяцы):"
        };

        creditFields = new JTextField[3];
        for (int i = 1; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setForeground(Color.WHITE);
            creditFields[i - 1] = new JTextField();
            creditFields[i-1].setToolTipText("Введите " + labels[i].toLowerCase() + ".");
            creditFields[i-1].setBackground(Color.BLACK);
            creditFields[i-1].setForeground(Color.WHITE);
            panel.add(label);
            panel.add(creditFields[i - 1]);
        }

        String[] creditTypes = {"Аннуитетный", "Дифференцированный"};
        creditType = new JComboBox<>(creditTypes);
        creditType.setToolTipText("Выберите тип кредита: аннуитетный (равными платежами) или дифференцированный (уменьшающимися платежами).");
        creditType.setBackground(Color.BLACK);
        creditType.setForeground(Color.WHITE);
        panel.add(new JLabel(labels[0])).setForeground(Color.WHITE);
        panel.add(creditType);

        JButton calculateButton = createStyledButton("Рассчитать", Color.LIGHT_GRAY);
        calculateButton.addActionListener(e -> calculateCredit());
        panel.add(calculateButton);

        JButton clearButton = createStyledButton("Очистить", Color.GRAY);
        clearButton.addActionListener(e -> clearCreditFields());
        panel.add(clearButton);

        return panel;
    }

    private void clearCreditFields() {
        for (JTextField field : creditFields) {
            field.setText("");
        }
        creditType.setSelectedIndex(0);
        display.setText("");
    }

    private void calculateCredit() {
        try {
            char type = creditType.getSelectedIndex() == 0 ? 'a' : 'd';
            double totalCredit = Double.parseDouble(creditFields[0].getText());
            double rate = Double.parseDouble(creditFields[1].getText());
            int term = Integer.parseInt(creditFields[2].getText());

            CreditConditions conditions = new CreditConditions(type, totalCredit, rate, term);
            creditCalculator.calculateCredit(conditions);

            StringBuilder paymentsResult = new StringBuilder();
            for (double payment : creditCalculator.getPayments()) {
                paymentsResult.append("Платеж: ").append(String.format("%.2f", payment)).append("<br>");
            }

            String resultText = "<html>" +
                    paymentsResult +
                    "Переплата: " + String.format("%.2f", creditCalculator.getOverpayment()) + "<br>" +
                    "Итого платежей: " + String.format("%.2f", creditCalculator.getTotalPayment()) +
                    "</html>";

            display.setText(resultText);
            history.add(resultText.replaceAll("<br>", "\n").replaceAll("<html>", "").replaceAll("</html>", ""));
        } catch (Exception ex) {
            display.setText("Ошибка: " + ex.getMessage());
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(100, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void switchMode() {
        remove(currentPanel);

        switch (mode) {
            case 0:
                currentPanel = createDepositCalculator();
                setSize(1000, 650); // аннуитный размер для истории
                break;
            case 1:
                currentPanel = createCreditCalculator();
                setSize(800, 650); // стандартный размер для истории
                break;
            case 2:
                currentPanel = createStandardCalculator();
                setSize(400, 650); // стандартный размер для истории
                break;
        }

        add(currentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        revalidate();
        repaint();

        mode = (mode + 1) % 3;
    }

    private void showHistory() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);

        for (String record : history) {
            textArea.append(record + "\n");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "История вычислений", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main calculatorGUI = new Main();
            calculatorGUI.setVisible(true);
        });
    }
}