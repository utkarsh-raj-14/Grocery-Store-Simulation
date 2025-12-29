import java.util.*;
import java.io.*;

class Product {
    String code;
    String name;
    double price;
    int quantity;
    String category;

    Product(String code, String name, double price, int quantity, String category) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }
}

class Customer {
    int id;
    String name;
    double budget;
    LinkedHashMap<String, String> cart;
    double total;

    Customer(int id, String name, double budget) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.cart = new LinkedHashMap<>();
        this.total = 0;
    }
}

class Store {
    HashMap<String, Product> inventory = new HashMap<>();

    void loadProducts(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = br.readLine()) != null) {
            String[] d = line.split(",");
            String name = d[0].trim();
            String code = name.substring(0, 3).toLowerCase();

            Product p = new Product(
                    code,
                    name,
                    Double.parseDouble(d[1].trim()),
                    Integer.parseInt(d[2].trim()),
                    d[3].trim()
            );
            inventory.put(code, p);
        }
        br.close();
    }

    void showMenu() {
        System.out.println("\n-------- PRODUCT MENU --------");
        System.out.println("Code   Product   Price   Stock");
        for (Product p : inventory.values()) {
            System.out.printf("%-6s %-8s ₹%-6.0f %d\n", p.code, p.name, p.price, p.quantity);
        }
        System.out.println("--------------------------------");
        System.out.println("Type 'done' to checkout\n");
    }

    Product getProductByCode(String code) {
        return inventory.get(code);
    }
}

public class GroceryStoreSimulation {

    static void cashierShopping(Customer c, Store store, Scanner sc) {

        while (true) {
            store.showMenu();
            System.out.print("Enter product code: ");
            String code = sc.nextLine().toLowerCase();

            if (code.equals("done")) break;

            Product p = store.getProductByCode(code);

            if (p == null) {
                System.out.println("Invalid product code.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int qty = sc.nextInt();
            sc.nextLine();

            if (qty > p.quantity) {
                System.out.println("Out of stock.");
                continue;
            }

            double cost = qty * p.price;

            if (c.total + cost > c.budget) {
                System.out.println("Insufficient budget.");
                continue;
            }

            p.quantity -= qty;
            c.total += cost;
            c.cart.put(p.name, qty + " x ₹" + p.price + " = ₹" + cost);

            System.out.println("Added to cart.");
        }
    }

    static void checkout(Customer c, BufferedWriter db) throws Exception {

        System.out.println("\n========== FINAL RECEIPT ==========");
        System.out.println("Customer ID   : " + c.id);
        System.out.println("Customer Name : " + c.name);
        System.out.println("----------------------------------");
        System.out.println("Items Purchased:");

        int totalItems = 0;

        for (Map.Entry<String, String> item : c.cart.entrySet()) {
            System.out.println(item.getKey() + " -> " + item.getValue());
            totalItems += Integer.parseInt(item.getValue().split(" ")[0]);
        }

        System.out.println("----------------------------------");
        System.out.println("Total Items Purchased : " + totalItems);
        System.out.println("Total Amount          : ₹" + c.total);
        System.out.println("Remaining Budget      : ₹" + (c.budget - c.total));
        System.out.println("==================================\n");

        db.write("Customer ID: " + c.id + "\n");
        db.write("Customer Name: " + c.name + "\n");
        db.write("Items Purchased:\n");

        for (Map.Entry<String, String> item : c.cart.entrySet()) {
            db.write(" - " + item.getKey() + " -> " + item.getValue() + "\n");
        }

        db.write("Total Items: " + totalItems + "\n");
        db.write("Total Amount: ₹" + c.total + "\n");
        db.write("Remaining Budget: ₹" + (c.budget - c.total) + "\n");
        db.write("----------------------------------\n");
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        Store store = new Store();
        store.loadProducts("C:\\Users\\Raj Utkarsh\\OneDrive\\Desktop\\JAVA programs\\JAVA DSA\\products.txt");

        BufferedWriter database = new BufferedWriter(
                new FileWriter("C:\\Users\\Raj Utkarsh\\OneDrive\\Desktop\\JAVA programs\\JAVA DSA\\database.txt", true)
        );

        System.out.print("Enter number of customers: ");
        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 1; i <= n; i++) {

            System.out.print("\nEnter customer name: ");
            String name = sc.nextLine();

            System.out.print("Enter customer budget: ");
            double budget = sc.nextDouble();
            sc.nextLine();

            Customer c = new Customer(i, name, budget);

            cashierShopping(c, store, sc);
            checkout(c, database);
        }

        database.close();

        System.out.println("\nFinal Inventory:");
        for (Product p : store.inventory.values()) {
            System.out.println(p.name + " | Remaining: " + p.quantity);
        }
    }
}
