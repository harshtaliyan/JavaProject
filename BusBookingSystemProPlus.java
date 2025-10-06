import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BusBookingSystemProPlus {

    static class Bus {
        int busNo;
        boolean ac;
        int capacity;
        String from;
        String to;
        int bookedSeats;
        double costPerSeat;
        int safetyRating;
        String imagePath;

        public Bus(int busNo, boolean ac, int capacity, String from, String to, double costPerSeat, int safetyRating, String imagePath) {
            this.busNo = busNo;
            this.ac = ac;
            this.capacity = capacity;
            this.from = from;
            this.to = to;
            this.costPerSeat = costPerSeat;
            this.safetyRating = safetyRating;
            this.imagePath = imagePath;
            this.bookedSeats = 0;
        }

        public boolean bookSeat(int seats) {
            if (bookedSeats + seats <= capacity) {
                bookedSeats += seats;
                return true;
            }
            return false;
        }

        public void cancelSeat(int seats) {
            bookedSeats = Math.max(0, bookedSeats - seats);
        }

        public int availableSeats() {
            return capacity - bookedSeats;
        }
    }

    static class Booking {
        String passengerName;
        int busNo;
        int seatsBooked;
        double totalCost;

        public Booking(String passengerName, int busNo, int seatsBooked, double totalCost) {
            this.passengerName = passengerName;
            this.busNo = busNo;
            this.seatsBooked = seatsBooked;
            this.totalCost = totalCost;
        }
    }

    static class BusBookingGUI extends JFrame {
        private final List<Bus> buses = new ArrayList<>();
        private final List<Booking> bookings = new ArrayList<>();

        private final DefaultTableModel busModel;
        private final DefaultTableModel bookingModel;
        private final JTable busTable, bookingTable;

        private final JTextField nameField, busNoField, seatsField;
        private final JTextField newBusNoField, newFromField, newToField, newCapField, newCostField;
        private final JCheckBox acCheck;
        private final JComboBox<Integer> ratingBox;
        private final JTextField imagePathField;
        private final JLabel busImageLabel;

        public BusBookingGUI() {
            setTitle("🚌 Bus Booking System Pro+");
            setSize(1200, 800);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));

            JLabel title = new JLabel("🚌 Bus Booking System Pro+", JLabel.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 30));
            title.setForeground(new Color(0, 70, 140));
            add(title, BorderLayout.NORTH);

            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(new Font("Arial", Font.PLAIN, 16));

            // ----------- BUS TAB -----------
            JPanel busPanel = new JPanel(new BorderLayout(10, 10));
            busPanel.setBackground(Color.WHITE);

            String[] columns = {"Bus No", "AC", "Route", "Capacity", "Available", "Cost", "Safety"};
            busModel = new DefaultTableModel(columns, 0);
            busTable = new JTable(busModel);
            busTable.setFont(new Font("Arial", Font.PLAIN, 15));
            busTable.setRowHeight(35);
            busTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
            JScrollPane scroll = new JScrollPane(busTable);
            busPanel.add(scroll, BorderLayout.CENTER);

            // Yellow stars for safety
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                              boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == 6 && value != null) {
                        lbl.setText(value.toString());
                        lbl.setForeground(Color.ORANGE);
                        lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    return lbl;
                }
            };
            busTable.getColumnModel().getColumn(6).setCellRenderer(renderer);

            // Bus image
            busImageLabel = new JLabel();
            busImageLabel.setHorizontalAlignment(JLabel.CENTER);
            busImageLabel.setBorder(BorderFactory.createTitledBorder("🖼️ Bus Image"));
            busPanel.add(busImageLabel, BorderLayout.EAST);

            busTable.getSelectionModel().addListSelectionListener(e -> {
                int row = busTable.getSelectedRow();
                if (row >= 0 && row < buses.size()) {
                    Bus selected = buses.get(row);
                    showBusImage(selected.imagePath);
                }
            });

            // Add new bus section
            JPanel addBusPanel = new JPanel(new GridLayout(3, 6, 10, 10));
            addBusPanel.setBackground(new Color(245, 245, 245));
            addBusPanel.setBorder(BorderFactory.createTitledBorder("➕ Add New Bus"));

            newBusNoField = new JTextField();
            newFromField = new JTextField();
            newToField = new JTextField();
            newCapField = new JTextField();
            newCostField = new JTextField();
            imagePathField = new JTextField("images/bus1.jpg");
            acCheck = new JCheckBox("AC Bus");
            ratingBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

            addBusPanel.add(new JLabel("Bus No:"));
            addBusPanel.add(newBusNoField);
            addBusPanel.add(new JLabel("From:"));
            addBusPanel.add(newFromField);
            addBusPanel.add(new JLabel("To:"));
            addBusPanel.add(newToField);
            addBusPanel.add(new JLabel("Capacity:"));
            addBusPanel.add(newCapField);
            addBusPanel.add(new JLabel("Cost/Seat:"));
            addBusPanel.add(newCostField);
            addBusPanel.add(new JLabel("Safety Rating:"));
            addBusPanel.add(ratingBox);
            addBusPanel.add(new JLabel("Image Path:"));
            addBusPanel.add(imagePathField);
            addBusPanel.add(acCheck);

            JButton addBusBtn = new JButton("Add Bus");
            addBusBtn.setFont(new Font("Arial", Font.BOLD, 15));
            addBusBtn.setBackground(new Color(76, 175, 80));
            addBusBtn.setForeground(Color.WHITE);
            addBusPanel.add(addBusBtn);
            busPanel.add(addBusPanel, BorderLayout.SOUTH);
            tabs.add("🚌 Manage Buses", busPanel);

            // ----------- BOOKING TAB -----------
            JPanel bookingPanel = new JPanel(new BorderLayout(10, 10));
            bookingPanel.setBackground(Color.WHITE);
            bookingModel = new DefaultTableModel(new Object[]{"Passenger", "Bus No", "Seats", "Total Cost"}, 0);
            bookingTable = new JTable(bookingModel);
            bookingTable.setFont(new Font("Arial", Font.PLAIN, 15));
            bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
            bookingTable.setRowHeight(30);
            bookingPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));
            form.setBorder(BorderFactory.createTitledBorder("🎟️ Book or Cancel"));
            form.setBackground(new Color(245, 245, 245));

            nameField = new JTextField();
            busNoField = new JTextField();
            seatsField = new JTextField();
            JButton bookBtn = new JButton("Book Now");
            JButton cancelBtn = new JButton("Cancel Booking");

            bookBtn.setFont(new Font("Arial", Font.BOLD, 17));
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 17));
            bookBtn.setForeground(Color.RED);
            cancelBtn.setForeground(Color.RED);
            bookBtn.setBackground(new Color(255, 240, 240));
            cancelBtn.setBackground(new Color(255, 220, 220));

            form.add(new JLabel("Passenger Name:"));
            form.add(nameField);
            form.add(new JLabel("Bus No:"));
            form.add(busNoField);
            form.add(new JLabel("Seats:"));
            form.add(seatsField);
            form.add(bookBtn);
            form.add(cancelBtn);

            bookingPanel.add(form, BorderLayout.SOUTH);
            tabs.add("🎟️ Bookings", bookingPanel);
            add(tabs, BorderLayout.CENTER);

            // ----------- CONTACT PANEL -----------
            JPanel contactPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            contactPanel.setBackground(new Color(230, 230, 230));

            JLabel dev = new JLabel("👨‍💻 Developed by Harsh Taliyan | 📞 6395966674 | ✉️ harshtaliyan2000@gmail.com");
            dev.setFont(new Font("Arial", Font.BOLD, 14));

            JButton instaBtn = new JButton("Instagram");
            JButton linkedinBtn = new JButton("LinkedIn");

            instaBtn.setFont(new Font("Arial", Font.BOLD, 14));
            linkedinBtn.setFont(new Font("Arial", Font.BOLD, 14));

            instaBtn.setForeground(new Color(255, 105, 180)); // 💗 Pink font
            instaBtn.setBackground(Color.WHITE);
            linkedinBtn.setForeground(new Color(10, 102, 194)); // 🔵 LinkedIn Blue
            linkedinBtn.setBackground(Color.WHITE);

            instaBtn.addActionListener(e -> openLink("https://www.instagram.com/harshhhh.49"));
            linkedinBtn.addActionListener(e -> openLink("https://www.linkedin.com/in/harsh-taliyan-aa8171358"));

            contactPanel.add(dev);
            contactPanel.add(instaBtn);
            contactPanel.add(linkedinBtn);
            add(contactPanel, BorderLayout.SOUTH);

            // ----------- DATA + ACTIONS -----------
            buses.add(new Bus(101, true, 40, "Delhi", "Jaipur", 450, 5, "images/bus1.jpg"));
            buses.add(new Bus(102, false, 35, "Delhi", "Agra", 350, 4, "images/bus2.jpg"));
            buses.add(new Bus(103, true, 45, "Jaipur", "Udaipur", 500, 3, "images/bus3.jpg"));
            buses.add(new Bus(104, true, 35, "Delhi", "Manali", 1000, 5, "images/bus4.jpg"));
            refreshBusTable();

            addBusBtn.addActionListener(e -> addNewBus());
            bookBtn.addActionListener(e -> bookSeats());
            cancelBtn.addActionListener(e -> cancelSeats());
        }

        private void showBusImage(String path) {
            try {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
                busImageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                busImageLabel.setIcon(null);
            }
        }

        private void addNewBus() {
            try {
                int busNo = Integer.parseInt(newBusNoField.getText().trim());
                String from = newFromField.getText().trim();
                String to = newToField.getText().trim();
                int cap = Integer.parseInt(newCapField.getText().trim());
                double cost = Double.parseDouble(newCostField.getText().trim());
                int rating = (int) ratingBox.getSelectedItem();
                String imgPath = imagePathField.getText().trim();
                boolean ac = acCheck.isSelected();

                if (!new File(imgPath).exists()) {
                    JOptionPane.showMessageDialog(this, "⚠️ Image not found!");
                    return;
                }

                buses.add(new Bus(busNo, ac, cap, from, to, cost, rating, imgPath));
                refreshBusTable();
                JOptionPane.showMessageDialog(this, "✅ Bus added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "⚠️ Invalid input!");
            }
        }

        private void bookSeats() {
            try {
                String name = nameField.getText().trim();
                int busNo = Integer.parseInt(busNoField.getText().trim());
                int seats = Integer.parseInt(seatsField.getText().trim());

                Bus selectedBus = buses.stream().filter(b -> b.busNo == busNo).findFirst().orElse(null);
                if (selectedBus == null) {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Bus Number!");
                    return;
                }

                if (selectedBus.bookSeat(seats)) {
                    double totalCost = seats * selectedBus.costPerSeat;
                    bookings.add(new Booking(name, busNo, seats, totalCost));
                    refreshBusTable();
                    refreshBookingTable();
                    JOptionPane.showMessageDialog(this, "✅ Booking confirmed for " + name + "\nTotal: ₹" + totalCost);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Not enough seats!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "⚠️ Invalid input!");
            }
        }

        private void cancelSeats() {
            try {
                int busNo = Integer.parseInt(busNoField.getText().trim());
                int seats = Integer.parseInt(seatsField.getText().trim());
                Bus selectedBus = buses.stream().filter(b -> b.busNo == busNo).findFirst().orElse(null);
                if (selectedBus == null) {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Bus Number!");
                    return;
                }
                selectedBus.cancelSeat(seats);
                bookings.removeIf(b -> b.busNo == busNo && b.seatsBooked == seats);
                refreshBusTable();
                refreshBookingTable();
                JOptionPane.showMessageDialog(this, "✅ Booking cancelled!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "⚠️ Enter valid numbers!");
            }
        }

        private void refreshBusTable() {
            busModel.setRowCount(0);
            for (Bus b : buses) {
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < b.safetyRating; i++) stars.append("★");
                busModel.addRow(new Object[]{
                        b.busNo, b.ac ? "Yes" : "No", b.from + " → " + b.to,
                        b.capacity, b.availableSeats(), "₹" + b.costPerSeat, stars.toString()
                });
            }
        }

        private void refreshBookingTable() {
            bookingModel.setRowCount(0);
            for (Booking bk : bookings) {
                bookingModel.addRow(new Object[]{bk.passengerName, bk.busNo, bk.seatsBooked, "₹" + bk.totalCost});
            }
        }

        private void openLink(String url) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Unable to open link!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new BusBookingGUI().setVisible(true);
        });
    }
}

