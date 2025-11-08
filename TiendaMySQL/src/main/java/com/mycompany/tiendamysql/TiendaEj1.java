/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tiendamysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author federico
 */

public class TiendaEj1 {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = ConexionMySQL.conectar()) {
            System.out.println("✅ Conectado a la base de datos.");

            int opcion;
            do {
                System.out.println("\n--- MENÚ ---");
                System.out.println("1. Insertar cliente");
                System.out.println("2. Actualizar cliente");
                System.out.println("3. Borrar cliente");
                System.out.println("0. Salir");
                System.out.print("Elige una opción: ");
                opcion = sc.nextInt();
                sc.nextLine(); // limpiar buffer

                switch (opcion) {
                    case 1 -> insertarCliente(conn);
                    case 2 -> actualizarCliente(conn);
                    case 3 -> borrarCliente(conn);
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción no válida.");
                }
            } while (opcion != 0);

        } catch (SQLException e) {
            System.out.println("❌ Error al conectar o ejecutar operación: " + e.getMessage());
        }
    }

    private static void insertarCliente(Connection conn) {
        try {
            System.out.print("Usuario: ");
            String usuario = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();
            System.out.print("Dirección: ");
            String direccion = sc.nextLine();
            System.out.print("Teléfono: ");
            String telefono = sc.nextLine();

            String sql = "INSERT INTO clientes (password, usuario, direccion, telefono) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, password);
                ps.setString(2, usuario);
                ps.setString(3, direccion);
                ps.setString(4, telefono);
                ps.executeUpdate();
                System.out.println("✅ Cliente insertado correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar cliente: " + e.getMessage());
        }
    }

    private static void actualizarCliente(Connection conn) {
        try {
            System.out.print("Usuario a actualizar: ");
            String usuario = sc.nextLine();
            System.out.print("Nuevo teléfono: ");
            String telefono = sc.nextLine();

            String sql = "UPDATE clientes SET telefono = ? WHERE usuario = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, telefono);
                ps.setString(2, usuario);
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    System.out.println("✅ Teléfono actualizado correctamente.");
                } else {
                    System.out.println("⚠️ Usuario no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar cliente: " + e.getMessage());
        }
    }

    private static void borrarCliente(Connection conn) {
        try {
            System.out.print("Usuario a eliminar: ");
            String usuario = sc.nextLine();

            String sql = "DELETE FROM clientes WHERE usuario = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario);
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    System.out.println("✅ Cliente eliminado correctamente.");
                } else {
                    System.out.println("⚠️ Usuario no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar cliente: " + e.getMessage());
        }
    }
}