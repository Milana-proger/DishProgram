package menu;

import component.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInterface extends JFrame {
    private JTable orderTable;
    private OrderTableModel tableModel;
    private List<OrderItem> orders;
    private Dish currentDish;
    private int selectedCount;
    private String currentState; // Хранит текущее состояние блюда

    public UserInterface() {
        setTitle("Нордское рагу");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(null);

        orders = new ArrayList<>();
        currentDish = new NordicStew();
        selectedCount = 0;
        currentState = "Нордское рагу"; // Начальное состояние

        initComponents();
    }

    private void initComponents() {
        // Таблица (окошко в левом верхнем углу)
        tableModel = new OrderTableModel();
        orderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBounds(20, 20, 350, 200);
        add(scrollPane);

        // 4 кнопки справа от таблицы
        JButton fireBtn = new JButton("Огненный соус (+40)");
        JButton doubleFireBtn = new JButton("Двойная порция оленины (+20)");
        JButton berryBtn = new JButton("Снежные ягоды (+6)");
        JButton breadBtn = new JButton("Нордская лепешка (+7)");

        fireBtn.setBounds(390, 20, 180, 35);
        doubleFireBtn.setBounds(390, 65, 180, 35);
        berryBtn.setBounds(390, 110, 180, 35);
        breadBtn.setBounds(390, 155, 180, 35);

        fireBtn.addActionListener(e -> addSupplement("fire"));
        doubleFireBtn.addActionListener(e -> addSupplement("doubleFire"));
        berryBtn.addActionListener(e -> addSupplement("berry"));
        breadBtn.addActionListener(e -> addSupplement("bread"));

        add(fireBtn);
        add(doubleFireBtn);
        add(berryBtn);
        add(breadBtn);

        // Панель для execute
        JPanel executePanel = new JPanel();
        executePanel.setBounds(20, 240, 550, 200);
        executePanel.setBorder(BorderFactory.createEtchedBorder());
        executePanel.setLayout(new BorderLayout());

        JTextArea executeLog = new JTextArea();
        executeLog.setEditable(false);
        executeLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(executeLog);
        executePanel.add(logScroll, BorderLayout.CENTER);

        JButton executeBtn = new JButton("Оформить заказ");
        executeBtn.addActionListener(e -> {
            if (selectedCount == 0) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Вы не выбрали ни одной добавки. Оформить базовое блюдо?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION);
                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Получаем финальное состояние и цену
            String description = currentDish.getDescription();
            double cost = currentDish.getCost();

            // Создаем заказ
            OrderItem order = new OrderItem(description, cost);
            orders.add(order);
            tableModel.fireTableDataChanged();

            // Логируем процесс приготовления через execute()
            executeLog.append("   Заказ    \n");
            StringBuilder stateBuilder = new StringBuilder();
            currentDish.execute(stateBuilder);
            executeLog.append("Состав: " + description + "\n");
            executeLog.append("Цена: " + cost + " септимов\n");
            executeLog.append("Состояние: " + currentState + "\n");

            // Сбрасываем заказ
            currentDish = new NordicStew();
            selectedCount = 0;
            currentState = "Нордское рагу";
            fireBtn.setEnabled(true);
            doubleFireBtn.setEnabled(true);
            berryBtn.setEnabled(true);
            breadBtn.setEnabled(true);

            JOptionPane.showMessageDialog(this,
                    "Заказ оформлен!\n" + description + "\nЦена: " + cost + " септимов",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        executePanel.add(executeBtn, BorderLayout.SOUTH);
        add(executePanel);
    }

    private void addSupplement(String type) {
        if (selectedCount >= 3) {
            JOptionPane.showMessageDialog(this,
                    "Нельзя добавить больше 3 модификаторов!",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Обновляем состояние и блюдо в зависимости от выбранной добавки
        switch (type) {
            case "fire":
                currentDish = new FierySause(currentDish);
                currentState += " +Огненный соус";
                break;
            case "doubleFire":
                currentDish = new DoubleVensionPortion(currentDish);
                currentState += " +Двойная порция оленины";
                break;
            case "berry":
                currentDish = new SnowBerries(currentDish);
                currentState += " +Снежные ягоды";
                break;
            case "bread":
                currentDish = new NordicFlatbread(currentDish);
                currentState += " +Нордская лепешка";
                break;
        }

        selectedCount++;

        if (selectedCount >= 3) {
            disableButtons();
        }
    }

    private void disableButtons() {
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text.contains("Огненный") || text.contains("Двойная") ||
                        text.contains("Снежные") || text.contains("Нордская")) {
                    btn.setEnabled(false);
                }
            }
        }
    }

    private class OrderTableModel extends AbstractTableModel {
        private String[] columns = {"Время", "Название", "Цена"};

        @Override
        public int getRowCount() {
            return orders.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int row, int col) {
            OrderItem order = orders.get(row);
            switch (col) {
                case 0: return order.getTime();
                case 1: return order.getDescription();
                case 2: return String.format("%.1f септимов", order.getCost());
                default: return null;
            }
        }
    }

    private class OrderItem {
        private String time;
        private String description;
        private double cost;

        public OrderItem(String description, double cost) {
            this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            this.description = description;
            this.cost = cost;
        }

        public String getTime() { return time; }
        public String getDescription() { return description; }
        public double getCost() { return cost; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new UserInterface().setVisible(true);
        });
    }
}