import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/retail_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Ps@0005Sri";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void insertSampleProducts() {
        String sql = "INSERT INTO Product(product_id, product_name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        Object[][] products = {
            {101, "Keyboard", "Electronics", 15, 799.0},
            {102, "Mouse", "Electronics", 30, 499.0},
            {103, "Notebook", "Stationery", 10, 60.0}
        };

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (Object[] p : products) {
                ps.setInt(1, (Integer) p[0]);
                ps.setString(2, (String) p[1]);
                ps.setString(3, (String) p[2]);
                ps.setInt(4, (Integer) p[3]);
                ps.setDouble(5, (Double) p[4]);
                try {
                    ps.executeUpdate();
                    System.out.println("Inserted product ID: " + p[0]);
                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println("Duplicate product ID " + p[0] + " - record not inserted.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    private static void retrieveByCategory(Scanner sc) {
        System.out.print("Enter category: ");
        String category = sc.nextLine();
        String sql = "SELECT * FROM Product WHERE category = ? AND quantity < 20 ORDER BY price DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    printProduct(rs);
                }
                if (!found) System.out.println("No matching products found.");
            }
        } catch (SQLException e) {
            System.out.println("Retrieval error: " + e.getMessage());
        }
    }

    private static void addStock(Scanner sc) {
        System.out.print("Enter product ID: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Enter quantity to add: ");
        int amount = Integer.parseInt(sc.nextLine());
        if (amount <= 0) {
            System.out.println("Quantity to add must be greater than zero.");
            return;
        }
        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Stock updated successfully." : "Product does not exist.");
        } catch (SQLException e) {
            System.out.println("Stock update error: " + e.getMessage());
        }
    }

    private static void updatePrice(Scanner sc) {
        System.out.print("Enter product ID: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Enter new price: ");
        double price = Double.parseDouble(sc.nextLine());
        if (price <= 0) {
            System.out.println("Price must be greater than zero.");
            return;
        }
        String select = "SELECT * FROM Product WHERE product_id = ?";
        String update = "UPDATE Product SET price = ? WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement find = con.prepareStatement(select)) {
            find.setInt(1, id);
            try (ResultSet rs = find.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Product does not exist.");
                    return;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setDouble(1, price);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(select)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Updated product details:");
                        printProduct(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Price update error: " + e.getMessage());
        }
    }

    private static void showInventoryValue() {
        String sql = "SELECT COALESCE(SUM(quantity * price), 0) AS total_value FROM Product";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) System.out.printf("Total inventory value: %.2f%n", rs.getDouble("total_value"));
        } catch (SQLException e) {
            System.out.println("Aggregate query error: " + e.getMessage());
        }
    }

    private static void deleteIfZero(Scanner sc) {
        System.out.print("Enter product ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());
        String sql = "DELETE FROM Product WHERE product_id = ? AND quantity = 0";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Product deleted successfully." :
                    "Product not found or quantity is not zero; cannot delete.");
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    private static void printProduct(ResultSet rs) throws SQLException {
        System.out.printf("ID: %d | Name: %s | Category: %s | Quantity: %d | Price: %.2f%n",
                rs.getInt("product_id"), rs.getString("product_name"),
                rs.getString("category"), rs.getInt("quantity"), rs.getDouble("price"));
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            try (Connection con = getConnection()) {
                System.out.println("Connected to retail_db successfully.");
            } catch (SQLException e) {
                System.out.println("Database connection failed: " + e.getMessage());
                return;
            }

            int choice;
            do {
                System.out.println("\\n===== INVENTORY MANAGEMENT SYSTEM =====");
                System.out.println("1. Insert three sample products");
                System.out.println("2. Retrieve products by category (quantity < 20)");
                System.out.println("3. Add stock");
                System.out.println("4. Search product and update price");
                System.out.println("5. Show total inventory value");
                System.out.println("6. Delete product only if quantity is zero");
                System.out.println("7. Exit");
                System.out.print("Enter choice: ");
                try {
                    choice = Integer.parseInt(sc.nextLine());
                    switch (choice) {
                        case 1 -> insertSampleProducts();
                        case 2 -> retrieveByCategory(sc);
                        case 3 -> addStock(sc);
                        case 4 -> updatePrice(sc);
                        case 5 -> showInventoryValue();
                        case 6 -> deleteIfZero(sc);
                        case 7 -> System.out.println("Exiting application.");
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (NumberFormatException e) {
                    choice = 0;
                    System.out.println("Please enter a valid number.");
                }
            } while (choice != 7);
        }
    }
}
