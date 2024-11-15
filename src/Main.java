import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.time.YearMonth;
import java.time.LocalDate;

class ChatBot1 {
    private String userName;
    private Map<String, String> userPreferences = new HashMap<>();
    private String currentTriviaQuestion = null;
    private String currentTriviaAnswer = null;
    private Map<LocalDate, String> workSchedule = new HashMap<>();
    private Map<String, Double> expenses = new HashMap<>(); // For budget tracker
    private double monthlyBudget = 0.0; // For budget tracker

    private JTextArea outputArea; // For displaying chatbot responses
    private JTextField inputField; // For user input

    public ChatBot1() {
        // Create and set up the GUI
        JFrame frame = new JFrame("WELCOME TO CHULBUL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Create a custom panel with a gradient background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), Color.black);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField("Type something..."); // Placeholder text
        inputField.setForeground(Color.black); // Set placeholder color
        panel.add(inputField, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Send");
        panel.add(sendButton, BorderLayout.EAST);

        // Add action listener for the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText();
                inputField.setText(""); // Clear input field

                if (userInput.equalsIgnoreCase("exit")) {
                    outputArea.append(getFarewell() + "\n");
                    System.exit(0);
                } else {
                    String response = getResponse(userInput);
                    outputArea.append(userName + ": " + userInput + "\n");
                    outputArea.append("ChatBot: " + response + "\n");
                }
            }
        });

        // Add focus listener to reset placeholder text
        inputField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (inputField.getText().equals("Type something...")) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK); // Set text color to black
                }
            }

            public void focusLost(FocusEvent evt) {
                if (inputField.getText().isEmpty()) {
                    inputField.setForeground(Color.GRAY); // Set placeholder color
                    inputField.setText("Type something...");
                }
            }
        });

        frame.add(panel); // Add the custom panel to the frame
        frame.setVisible(true);
        initializeChat();
    }

    private void initializeChat() {
        String name = JOptionPane.showInputDialog("Please enter your name:");
        userName = name != null ? name : "User";
        outputArea.append(getGreeting() + "\n");
        outputArea.append("Welcome to the ChatBot, " + userName + "!\n");
        outputArea.append("Type 'exit' to quit.\n");
    }

    public String getResponse(String userInput) {
        if (userInput.equalsIgnoreCase("hello")) {
            return "Hello, " + userName + "! How are you today?";
        } else if (userInput.equalsIgnoreCase("how are you")) {
            return "I'm doing great, thanks for asking!";
        } else if (userInput.equalsIgnoreCase("what is your name")) {
            return "My name is ChatBot! Everyone calls me Chulbull!";
        } else if (userInput.equalsIgnoreCase("what is the date")) {
            return getCurrentDate();
        } else if (userInput.equalsIgnoreCase("what is the time")) {
            return getCurrentDateTime();
        } else if (userInput.equalsIgnoreCase("show calendar")) {
            return getCalendar();
        } else if (userInput.toLowerCase().startsWith("set reminder")) {
            return setReminder(userInput);
        } else if (userInput.equalsIgnoreCase("ask me a trivia question")) {
            return askTriviaQuestion();
        } else if (currentTriviaQuestion != null) {
            return checkTriviaAnswer(userInput);
        } else if (userInput.equalsIgnoreCase("play rock paper scissors")) {
            return startRockPaperScissors();
        } else if (userPreferences.containsKey("game") && userPreferences.get("game").equals("rockpaperscissors")) {
            return playRockPaperScissors(userInput);
        } else if (userInput.toLowerCase().startsWith("calculate")) {
            return calculate(userInput);
        } else if (userInput.toLowerCase().startsWith("log expense")) {
            return logExpense(userInput);
        } else if (userInput.toLowerCase().startsWith("set budget")) {
            return setBudget(userInput);
        } else if (userInput.equalsIgnoreCase("show expenses")) {
            return showExpenses();
        } else if (userInput.equalsIgnoreCase("remaining budget")) {
            return remainingBudget();
        } else {
            return "I didn't understand that. Can you please rephrase?";
        }
    }

    // Budget tracker: Log an expense
    private String logExpense(String userInput) {
        try {
            String[] parts = userInput.split(" ");
            String category = parts[2];
            double amount = Double.parseDouble(parts[3]);

            expenses.put(category, expenses.getOrDefault(category, 0.0) + amount);
            return "Logged expense: " + amount + " in " + category;
        } catch (Exception e) {
            return "Invalid format. Use: log expense [category] [amount]";
        }
    }

    // Budget tracker: Set a monthly budget
    private String setBudget(String userInput) {
        try {
            String[] parts = userInput.split(" ");
            monthlyBudget = Double.parseDouble(parts[2]);
            return "Budget set to: " + monthlyBudget;
        } catch (Exception e) {
            return "Invalid format. Use: set budget [amount]";
        }
    }

    // Budget tracker: Show all logged expenses
    private String showExpenses() {
        if (expenses.isEmpty()) {
            return "No expenses logged yet.";
        }
        StringBuilder expenseReport = new StringBuilder("Expenses so far:\n");
        double total = 0;
        for (String category : expenses.keySet()) {
            double amount = expenses.get(category);
            expenseReport.append(category).append(": ").append(amount).append("\n");
            total += amount;
        }
        expenseReport.append("Total: ").append(total);
        return expenseReport.toString();
    }

    // Budget tracker: Show remaining budget
    private String remainingBudget() {
        double totalSpent = expenses.values().stream().mapToDouble(Double::doubleValue).sum();
        if (monthlyBudget == 0.0) {
            return "No budget set.";
        }
        return "Remaining budget: " + (monthlyBudget - totalSpent);
    }

    private String setReminder(String userInput) {
        try {
            String[] parts = userInput.split(" ");
            String datePart = parts[2];
            StringBuilder reminder = new StringBuilder();
            for (int i = 3; i < parts.length; i++) {
                reminder.append(parts[i]).append(" ");
            }
            LocalDate date = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            workSchedule.put(date, reminder.toString().trim());
            return "Reminder set for " + date + ": " + reminder.toString().trim();
        } catch (Exception e) {
            return "Invalid format. Use: set reminder [dd/MM/yyyy] [your reminder]";
        }
    }

    public String getCalendar() {
        YearMonth yearMonth = YearMonth.now();
        StringBuilder calendar = new StringBuilder();
        calendar.append("Calendar for ").append(yearMonth.getMonth()).append(" ").append(yearMonth.getYear()).append("\n");
        calendar.append("Mon Tue Wed Thu Fri Sat Sun \n");

        LocalDate firstDay = yearMonth.atDay(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // Monday = 1, Sunday = 7

        // Pad the start of the calendar with empty spaces for the first week
        for (int i = 1; i < dayOfWeek; i++) {
            calendar.append("    "); // 4 spaces for each day
        }

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            String dayDisplay = String.format("%2d ", day);
            // Highlight today's date
            if (currentDate.equals(LocalDate.now())) {
                dayDisplay = "[" + dayDisplay.trim() + "] ";
            }
            calendar.append(dayDisplay);

            // New line for the end of the week
            if ((day + dayOfWeek - 1) % 7 == 0) {
                calendar.append("\n");
            }
        }
        return calendar.toString();
    }

    private String calculate(String userInput) {
        try {
            String[] parts = userInput.split(" ");
            if (parts.length == 4) {
                double num1 = Double.parseDouble(parts[1]);
                String operator = parts[2];
                double num2 = Double.parseDouble(parts[3]);

                switch (operator) {
                    case "+":
                        return "Result: " + (num1 + num2);
                    case "-":
                        return "Result: " + (num1 - num2);
                    case "*":
                        return "Result: " + (num1 * num2);
                    case "/":
                        if (num2 != 0) {
                            return "Result: " + (num1 / num2);
                        } else {
                            return "Error: Division by zero!";
                        }
                    default:
                        return "Unknown operator: " + operator;
                }
            } else {
                return "Invalid format. Use: calculate [number1] [operator] [number2]";
            }
        } catch (Exception e) {
            return "An error occurred. Please ensure you're using the correct format.";
        }
    }

    private String startRockPaperScissors() {
        userPreferences.put("game", "rockpaperscissors");
        return "Let's play Rock, Paper, Scissors! Type your choice.";
    }

    private String playRockPaperScissors(String userInput) {
        String[] options = {"rock", "paper", "scissors"};
        Random random = new Random();
        String botChoice = options[random.nextInt(options.length)];

        if (userInput.equalsIgnoreCase(botChoice)) {
            return "It's a tie! I chose " + botChoice;
        } else if ((userInput.equalsIgnoreCase("rock") && botChoice.equals("scissors")) ||
                (userInput.equalsIgnoreCase("paper") && botChoice.equals("rock")) ||
                (userInput.equalsIgnoreCase("scissors") && botChoice.equals("paper"))) {
            userPreferences.remove("game");
            return "You win! I chose " + botChoice;
        } else {
            userPreferences.remove("game");
            return "You lose! I chose " + botChoice;
        }
    }

    private String askTriviaQuestion() {
        currentTriviaQuestion = "What is the capital of France?";
        currentTriviaAnswer = "Paris";
        return "Trivia Question: " + currentTriviaQuestion;
    }

    private String checkTriviaAnswer(String userInput) {
        if (userInput.equalsIgnoreCase(currentTriviaAnswer)) {
            currentTriviaQuestion = null; // Reset the question
            return "Correct! Well done.";
        } else {
            return "Wrong answer. Try again!";
        }
    }

    public String getGreeting() {
        return "Welcome to the ChatBot!";
    }

    public String getFarewell() {
        return "Goodbye! Have a great day!";
    }

    public String getCurrentDate() {
        LocalDate today = LocalDate.now();
        return "Today's date is: " + today.toString();
    }

    public String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Current date and time: " + now.format(formatter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatBot1::new);
    }
}


