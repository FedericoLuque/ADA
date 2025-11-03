package ud2.ejercicio1.src.main.java.com.ada;

import java.sql.*;
import java.util.Scanner;

public class Main {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tienda";
        String user = "root"; 
        String password = "root"; 
        return DriverManager.getConnection(url, user, password);
    }

    // Método para insertar datos
    public static void insertarCliente() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce el password:");
        String password = scanner.nextLine();

        System.out.println("Introduce el usuario:");
        String usuario = scanner.nextLine();

        System.out.println("Introduce la dirección:");
        String direccion = scanner.nextLine();

        System.out.println("Introduce el teléfono:");
        String telefono = scanner.nextLine();

        String query = "INSERT INTO clientes (password, usuario, direccion, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, password);
            stmt.setString(2, usuario);
            stmt.setString(3, direccion);
            stmt.setString(4, telefono);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente insertado correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar datos
    public static void actualizarCliente() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce el usuario del cliente a actualizar:");
        String usuario = scanner.nextLine();

        System.out.println("Introduce la nueva dirección:");
        String nuevaDireccion = scanner.nextLine();

        System.out.println("Introduce el nuevo teléfono:");
        String nuevoTelefono = scanner.nextLine();

        String query = "UPDATE clientes SET direccion = ?, telefono = ? WHERE usuario = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuevaDireccion);
            stmt.setString(2, nuevoTelefono);
            stmt.setString(3, usuario);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente actualizado correctamente.");
            } else {
                System.out.println("No se encontró el usuario.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para borrar datos
    public static void borrarCliente() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce el usuario del cliente a borrar:");
        String usuario = scanner.nextLine();

        String query = "DELETE FROM clientes WHERE usuario = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cliente borrado correctamente.");
            } else {
                System.out.println("No se encontró el usuario.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método principal
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Selecciona una opción:");
            System.out.println("1. Insertar cliente");
            System.out.println("2. Actualizar cliente");
            System.out.println("3. Borrar cliente");
            System.out.println("4. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    insertarCliente();
                    break;
                case 2:
                    actualizarCliente();
                    break;
                case 3:
                    borrarCliente();
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}
