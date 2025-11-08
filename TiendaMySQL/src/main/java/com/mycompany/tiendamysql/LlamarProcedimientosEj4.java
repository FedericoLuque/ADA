/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendamysql;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author federico
 */
public class LlamarProcedimientosEj4 {


    private static final String URL = "jdbc:mysql://localhost:3306/BD_EJERCICIO4";
    private static final String USER = "root";       
    private static final String PASSWORD = "1234";   

    private Connection getConexion() throws SQLException {
        try {
            // Carga explícita del driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de MySQL (el archivo .jar).");
            e.printStackTrace();
            throw new SQLException("Driver no encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Método de utilidad para imprimir encabezados
    private void imprimirEncabezado(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
    }

    /**
     * a) Nombre de los trabajadores cuya tarifa este entre dos extremos.
     */
    public void ejecutarConsulta_A(double minTarifa, double maxTarifa) {
        imprimirEncabezado("a) Tarifa entre " + minTarifa + " y " + maxTarifa);
        String sql = "{CALL sp_trabajadores_por_tarifa(?, ?)}"; 
        
        try (Connection conn = getConexion(); // Llama al método de esta clase
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setDouble(1, minTarifa); 
            cstmt.setDouble(2, maxTarifa); 
            
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Nombre: " + rs.getString("NOMBRE") + 
                                       ", Tarifa: " + rs.getDouble("TARIFA"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta A: " + e.getMessage());
        }
    }

    /**
     * b) ¿Cuáles son los oficios de los trabajadores asignados un edificio?
     */
    public void ejecutarConsulta_B(int idEdificio) {
        imprimirEncabezado("b) Oficios en edificio " + idEdificio);
        String sql = "{CALL sp_oficios_por_edificio(?)}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setInt(1, idEdificio);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Oficio: " + rs.getString("OFICIO"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta B: " + e.getMessage());
        }
    }

    /**
     * c) Indicar el nombre del trabajador y el de su supervisor.
     */
    public void ejecutarConsulta_C() {
        imprimirEncabezado("c) Trabajador y Supervisor");
        String sql = "{CALL sp_trabajador_y_supervisor()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Trabajador: " + rs.getString("Trabajador") + 
                                   ", Supervisor: " + rs.getString("Supervisor"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta C: " + e.getMessage());
        }
    }

    /**
     * d) Nombre de los trabajadores asignados a oficinas.
     */
    public void ejecutarConsulta_D() {
        imprimirEncabezado("d) Trabajadores en Oficinas");
        String sql = "{CALL sp_trabajadores_en_oficinas()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Nombre: " + rs.getString("NOMBRE"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta D: " + e.getMessage());
        }
    }

    /**
     * e) ¿Cuál es el número total de días que se han dedicado a una actividad (oficio) 
     * en un edificio concreto?
     */
    public void ejecutarConsulta_E(String oficio, int idEdificio) {
        imprimirEncabezado("e) Días de '" + oficio + "' en edificio " + idEdificio);
        String sql = "{CALL sp_dias_por_oficio_y_edificio(?, ?)}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setString(1, oficio);
            cstmt.setInt(2, idEdificio);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total días: " + rs.getInt("TotalDias"));
                } else {
                    System.out.println("Total días: 0");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta E: " + e.getMessage());
        }
    }

    /**
     * f) ¿Cuántos tipos de oficios diferentes hay?
     */
    public void ejecutarConsulta_F() {
        imprimirEncabezado("f) Conteo de oficios distintos");
        String sql = "{CALL sp_contar_oficios_diferentes()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            if (rs.next()) {
                System.out.println("Total oficios distintos: " + rs.getInt("TotalOficios"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta F: " + e.getMessage());
        }
    }

    /**
     * g) Para cada supervisor, ¿Cuál es la tarifa por hora más alta?
     */
    public void ejecutarConsulta_G() {
        imprimirEncabezado("g) Tarifa MÁXIMA por Supervisor");
        String sql = "{CALL sp_tarifa_maxima_por_supervisor()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Supervisor: " + rs.getString("Supervisor") + 
                                   ", Tarifa Máxima: " + rs.getDouble("TarifaMaxima"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta G: " + e.getMessage());
        }
    }

    /**
     * h) Para cada supervisor que supervisa a más de un trabajador, ¿cuál es la tarifa más alta?
     */
    public void ejecutarConsulta_H() {
        imprimirEncabezado("h) Tarifa MÁXIMA (supervisores con +1 trabajador)");
        String sql = "{CALL sp_tarifa_maxima_supervisor_mas_de_uno()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Supervisor: " + rs.getString("Supervisor") + 
                                   ", Tarifa Máxima: " + rs.getDouble("TarifaMaxima"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta H: " + e.getMessage());
        }
    }

    /**
     * i) ¿Qué trabajadores reciben una tarifa por hora menor que la del promedio?
     */
    public void ejecutarConsulta_I() {
        imprimirEncabezado("i) Tarifa MENOR al promedio GLOBAL");
        String sql = "{CALL sp_tarifa_menor_promedio_global()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Nombre: " + rs.getString("NOMBRE") + 
                                   ", Tarifa: " + rs.getDouble("TARIFA"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta I: " + e.getMessage());
        }
    }

    /**
     * j) ¿Qué trabajadores reciben una tarifa menor que la del promedio de su mismo oficio?
     */
    public void ejecutarConsulta_J() {
        imprimirEncabezado("j) Tarifa MENOR al promedio de su OFICIO");
        String sql = "{CALL sp_tarifa_menor_promedio_oficio()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Nombre: " + rs.getString("NOMBRE") + 
                                   ", Oficio: " + rs.getString("OFICIO") +
                                   ", Tarifa: " + rs.getDouble("TARIFA"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta J: " + e.getMessage());
        }
    }

    /**
     * k) ¿Qué trabajadores reciben una tarifa menor que la del promedio de su supervisor?
     */
    public void ejecutarConsulta_K() {
        imprimirEncabezado("k) Tarifa MENOR al promedio de su SUPERVISOR");
        String sql = "{CALL sp_tarifa_menor_promedio_supervisor()}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql);
             ResultSet rs = cstmt.executeQuery()) {
             
            while (rs.next()) {
                System.out.println("Nombre: " + rs.getString("NOMBRE") + 
                                   ", Supervisor ID: " + rs.getInt("ID_SUPV") +
                                   ", Tarifa: " + rs.getDouble("TARIFA"));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta K: " + e.getMessage());
        }
    }

    /**
     * l) ¿Qué supervisores tienen trabajadores con tarifa por encima de un valor?
     */
    public void ejecutarConsulta_L(double tarifaLimite) {
        imprimirEncabezado("l) Supervisores con trabajadores que ganan > " + tarifaLimite);
        String sql = "{CALL sp_supervisor_por_tarifa_alta(?)}";
        
        try (Connection conn = getConexion();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.setDouble(1, tarifaLimite);
            
            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Supervisor: " + rs.getString("Supervisor"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta L: " + e.getMessage());
        }
    }


    /**
     * MÉTODO PRINCIPAL PARA EJECUTAR TODAS LAS CONSULTAS
     */
    public static void main(String[] args) {
        LlamarProcedimientosEj4 app = new LlamarProcedimientosEj4();
        
        System.out.println("=== INICIANDO PRUEBAS EJERCICIO 4 ===");

        // --- Pruebas del Ejercicio 4 ---
        
        // a) Prueba con 10 y 12 euros (según el ejercicio)
        app.ejecutarConsulta_A(10.0, 12.0);
        
        // b) Prueba con el edificio 312
        app.ejecutarConsulta_B(312);

        // c)
        app.ejecutarConsulta_C();
        
        // d)
        app.ejecutarConsulta_D();
        
        // e) Prueba con FONTANERO y edificio 312 (según el ejercicio)
        app.ejecutarConsulta_E("FONTANERO", 312);

        // f)
        app.ejecutarConsulta_F();
        
        // g)
        app.ejecutarConsulta_G();
        
        // h)
        app.ejecutarConsulta_H();
        
        // i)
        app.ejecutarConsulta_I();
        
        // j)
        app.ejecutarConsulta_J();
        
        // k)
        app.ejecutarConsulta_K();
        
        // l) Prueba con un límite de 15 euros
        app.ejecutarConsulta_L(15.0);
        
        System.out.println("\n=== PRUEBAS EJERCICIO 4 COMPLETADAS ===");
    }
}