/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendamysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author federico
 */
public class ConsultasPreparadasEj3 {

    // Configuración de conexión
    private static final String URL = "jdbc:mysql://localhost:3306/EMPRESA?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE = "1234";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL, USUARIO, CLAVE)) {

            // I. Empleados con más de N hijos
            System.out.println("=== I. PRUEBAS DE HIJOS ===");
            consulta01_Hijos(con, 1);
            consulta01_Hijos(con, 3);
            consulta01_Hijos(con, 5);

            // II. Salario entre dos extremos
            System.out.println("\n=== II. PRUEBAS DE RANGO SALARIAL ===");
            consulta02_SalarioRango(con, 1200, 1300);
            consulta02_SalarioRango(con, 1400, 1500);

            // III. Departamentos que contienen palabra
            System.out.println("\n=== III. PRUEBAS DE PALABRAS EN DEPARTAMENTOS ===");
            consulta03_BuscarDepto(con, "SECTOR");
            consulta03_BuscarDepto(con, "CION");

            // IV. Info centro por ID
            System.out.println("\n=== IV. INFO CENTRO ===");
            consulta04_InfoCentro(con, 10);
            
            // V. Info empleado por nombre
            System.out.println("\n=== V. INFO EMPLEADO ===");
            consulta05_InfoEmpleado(con, "PONS, CESAR");

            // VI. Cumpleaños en mes X
            System.out.println("\n=== VI. CUMPLEAÑOS ===");
            consulta06_CumpleanosMes(con, 1);
            consulta06_CumpleanosMes(con, 11);

            // VII. Salario total supera X
            System.out.println("\n=== VII. SALARIO TOTAL SUPERIOR A... ===");
            consulta07_SalarioTotal(con, 1300);
            consulta07_SalarioTotal(con, 1500);

            // VIII. Estadística por departamento
            System.out.println("\n=== VIII. ESTADÍSTICAS POR DEPARTAMENTO ===");
            consulta08_EstadisticasDepto(con);

            // IX. Empleados que cobran más de X en cada depto
            System.out.println("\n=== IX. COBRAN MÁS DE X POR DEPTO ===");
            consulta09_RicosPorDepto(con, 1300);
            consulta09_RicosPorDepto(con, 1400);

            // X. Edad al entrar a la empresa
            System.out.println("\n=== X. EDAD AL ENTRAR ===");
            consulta10_EdadEntrada(con, 20);
            consulta10_EdadEntrada(con, 40);

            // XI. Antigüedad y sueldo tope
            System.out.println("\n=== XI. ANTIGÜEDAD Y TOPE SALARIAL ===");
            consulta11_AntiguedadSueldo(con, 3, 1300); 
            consulta11_AntiguedadSueldo(con, 20, 1500); 

        } catch (SQLException e) {
            System.err.println("ERROR GRAVE: " + e.getMessage());
        }
    }

    // ========================================================================
    // MÉTODOS CON PREPARED STATEMENTS
    // ========================================================================

    // I. Empleados con más de N hijos
    private static void consulta01_Hijos(Connection con, int numHijos) throws SQLException {
        String sql = "SELECT Comision, Nombre, Salario FROM EMPLEADOS WHERE Num_hijos > ? ORDER BY Comision, Nombre";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, numHijos);
            
            System.out.println(">> Buscando empleados con más de " + numHijos + " hijos:");
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hayResultados = false;
                while (rs.next()) {
                    hayResultados = true;
                    System.out.printf(" - %-20s (Salario: %.2f, Comisión: %s)%n", 
                            rs.getString("Nombre"), rs.getDouble("Salario"), rs.getObject("Comision"));
                }
                if (!hayResultados) System.out.println(" (Ningún empleado cumple la condición)");
            }
        }
    }

    // II. Salario entre dos extremos
    private static void consulta02_SalarioRango(Connection con, double min, double max) throws SQLException {
        String sql = "SELECT Nombre, Salario FROM EMPLEADOS WHERE Salario BETWEEN ? AND ? ORDER BY Nombre";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDouble(1, min);
            pstmt.setDouble(2, max);
            
            System.out.printf(">> Empleados con salario entre %.0f y %.0f:%n", min, max);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf(" - %-20s : %.2f €%n", rs.getString("Nombre"), rs.getDouble("Salario"));
                }
            }
        }
    }

    // III. Departamentos que contienen palabra
    private static void consulta03_BuscarDepto(Connection con, String palabra) throws SQLException {
        String sql = "SELECT Nombre FROM DEPARTAMENTOS WHERE Nombre LIKE ? ORDER BY Nombre";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "%" + palabra + "%");
            
            System.out.println(">> Departamentos que contienen '" + palabra + "':");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("Nombre"));
                }
            }
        }
    }

    // IV. Info centro por ID
    private static void consulta04_InfoCentro(Connection con, int idCentro) throws SQLException {
        String sql = "SELECT * FROM CENTROS WHERE Numero = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, idCentro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("Centro %d: %s (%s)%n", 
                            rs.getInt("Numero"), rs.getString("Nombre"), rs.getString("Direccion"));
                } else {
                    System.out.println("No existe centro con ID " + idCentro);
                }
            }
        }
    }

    // V. Info empleado por nombre
    private static void consulta05_InfoEmpleado(Connection con, String nombreExacto) throws SQLException {
        String sql = "SELECT * FROM EMPLEADOS WHERE Nombre = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, nombreExacto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                     System.out.printf("Empleado encontrado: Cod %d, Fecha Ingreso: %s, Salario: %.2f%n",
                             rs.getInt("Cod"), rs.getDate("Fecha_ingreso"), rs.getDouble("Salario"));
                } else {
                    System.out.println("No se encontró al empleado: " + nombreExacto);
                }
            }
        }
    }

    // VI. Cumpleaños en un mes concreto (1=Enero, 11=Noviembre)
    private static void consulta06_CumpleanosMes(Connection con, int mes) throws SQLException {

        String sql = "SELECT Nombre, Fecha_nacimiento FROM EMPLEADOS WHERE MONTH(Fecha_nacimiento) = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, mes);
            System.out.println(">> Cumpleaños en el mes " + mes + ":");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf(" - %s (%s)%n", rs.getString("Nombre"), rs.getDate("Fecha_nacimiento"));
                }
            }
        }
    }

    // VII. Salario total (base + comisión) supera un valor
    private static void consulta07_SalarioTotal(Connection con, double umbral) throws SQLException {

        String sql = "SELECT Cod, Nombre, (Salario + IFNULL(Comision, 0)) as Total " +
                     "FROM EMPLEADOS WHERE (Salario + IFNULL(Comision, 0)) > ? ORDER BY Cod";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDouble(1, umbral);
            System.out.printf(">> Empleados con salario total superior a %.2f:%n", umbral);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                    System.out.printf(" [%d] %-20s : %.2f €%n", 
                            rs.getInt("Cod"), rs.getString("Nombre"), rs.getDouble("Total"));
                }
            }
        }
    }

    // VIII. Número de empleados y extensiones distintas por departamento
    private static void consulta08_EstadisticasDepto(Connection con) throws SQLException {
        String sql = "SELECT Departamento, COUNT(*) as Num_Emps, COUNT(DISTINCT Telefono) as Ext_Distintas " +
                     "FROM EMPLEADOS GROUP BY Departamento";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("Depto | Empleados | Extensiones");
            System.out.println("------+-----------+------------");
            while (rs.next()) {
                System.out.printf(" %-4d | %-9d | %-10d%n", 
                        rs.getInt("Departamento"), rs.getInt("Num_Emps"), rs.getInt("Ext_Distintas"));
            }
        }
    }

    // IX. Número de empleados y nombre de los que cobran más de X en CADA depto

    private static void consulta09_RicosPorDepto(Connection con, double salarioMinimo) throws SQLException {
        String sql = "SELECT Departamento, Nombre, Salario FROM EMPLEADOS " +
                     "WHERE Salario > ? ORDER BY Departamento, Nombre";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDouble(1, salarioMinimo);
            System.out.printf(">> Empleados que cobran más de %.2f por departamento:%n", salarioMinimo);
            
            int deptoActual = -1;
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int deptoNuevo = rs.getInt("Departamento");
                    if (deptoNuevo != deptoActual) {
                        System.out.println("\n[Departamento " + deptoNuevo + "]");
                        deptoActual = deptoNuevo;
                    }
                    System.out.printf("  - %-20s : %.2f €%n", rs.getString("Nombre"), rs.getDouble("Salario"));
                }
            }
        }
    }

    // X. Tenían más de N años cuando entraron
    private static void consulta10_EdadEntrada(Connection con, int edadMinima) throws SQLException {

        String sql = "SELECT Nombre, Fecha_nacimiento, Fecha_ingreso, " +
                     "TIMESTAMPDIFF(YEAR, Fecha_nacimiento, Fecha_ingreso) as Edad_Entrada " +
                     "FROM EMPLEADOS WHERE TIMESTAMPDIFF(YEAR, Fecha_nacimiento, Fecha_ingreso) > ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, edadMinima);
            System.out.println(">> Empleados que entraron con más de " + edadMinima + " años:");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf(" - %-20s (Edad al entrar: %d años)%n", 
                            rs.getString("Nombre"), rs.getLong("Edad_Entrada"));
                }
            }
        }
    }

    // XI. Más de Y años en la empresa Y sueldo menor de Z
    private static void consulta11_AntiguedadSueldo(Connection con, int aniosMinimos, double sueldoMaximo) throws SQLException {

        String sql = "SELECT Cod, Nombre, Salario, Fecha_ingreso, " +
                     "TIMESTAMPDIFF(YEAR, Fecha_ingreso, CURDATE()) as Antiguedad " +
                     "FROM EMPLEADOS " +
                     "WHERE TIMESTAMPDIFF(YEAR, Fecha_ingreso, CURDATE()) > ? AND Salario < ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, aniosMinimos);
            pstmt.setDouble(2, sueldoMaximo);
            
            System.out.printf(">> Empleados con >%d años de antigüedad y salario < %.2f:%n", aniosMinimos, sueldoMaximo);
            try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                    System.out.printf(" - [%d] %-20s | Salario: %.2f | Antigüedad: %d años%n", 
                            rs.getInt("Cod"), rs.getString("Nombre"), rs.getDouble("Salario"), rs.getLong("Antiguedad"));
                }
            }
        }
    }
}
