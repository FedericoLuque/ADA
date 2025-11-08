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
import java.sql.Statement;

/**
 *
 * @author federico
 */
public class ConsultasEmpresaEj2 {

    // --- 1. CONFIGURACIÓN DE LA CONEXIÓN ---
    private static final String URL = "jdbc:mysql://localhost:3306/EMPRESA?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "1234";

    public static void main(String[] args) {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CLAVE)) {
            System.out.println("¡Conexión establecida con éxito!\n");

            // --- LLAMADAS A CADA CONSULTA ---
            // Descomenta la que quieras probar una a una para no saturar la consola
            // consulta01(conexion);
            // consulta02(conexion);
            // consulta03(conexion);
            // consulta04(conexion);
            // consulta05(conexion);
            // consulta06(conexion);
            // consulta07(conexion);
            // consulta08(conexion);
            // consulta09(conexion);
            // consulta10(conexion);
            // consulta11(conexion);
            // consulta12(conexion);

        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos:");
            e.printStackTrace();
        }
    }

    // 1. Hallar comisión, nombre y salario de empleados con > 3 hijos, ordenado por comisión y nombre.
    private static void consulta01(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 1: Empleados con > 3 hijos ---");
        String sql = "SELECT Comision, Nombre, Salario FROM EMPLEADOS WHERE Num_hijos > 3 ORDER BY Comision, Nombre";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            // Cabecera de resultados
            System.out.printf("%-10s %-20s %-10s%n", "Comisión", "Nombre", "Salario");
            System.out.println("------------------------------------------");
            
            while (rs.next()) {
                // Usamos getDouble/getString según el tipo de dato de la columna
                // rs.getObject maneja los nulos de 'Comision' mejor para visualizar
                System.out.printf("%-10s %-20s %-10.2f%n", 
                        rs.getObject("Comision"), rs.getString("Nombre"), rs.getDouble("Salario"));
            }
        }
        System.out.println();
    }

    // 2. Obtener los nombres de los departamentos que no dependen de otros.
    private static void consulta02(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 2: Deptos. que no dependen de otros ---");
        // "no dependen de otros" significa que su columna Depto_jefe es NULL
        String sql = "SELECT Nombre FROM DEPARTAMENTOS WHERE Depto_jefe IS NULL";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Departamento: " + rs.getString("Nombre"));
            }
        }
        System.out.println();
    }

    // 3. Obtener, por orden alfabético, nombres y salarios de empleados con salario entre 1250 y 1300 euros.
    private static void consulta03(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 3: Salarios entre 1250 y 1300 ---");
        String sql = "SELECT Nombre, Salario FROM EMPLEADOS WHERE Salario BETWEEN 1250 AND 1300 ORDER BY Nombre";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%-20s %.2f €%n", rs.getString("Nombre"), rs.getDouble("Salario"));
            }
        }
        System.out.println();
    }

    // 4. Datos de empleados que cumplen la condición anterior O tienen al menos un hijo.
    private static void consulta04(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 4: Salario 1250-1300 O al menos un hijo ---");
        // La condición es compuesta con OR. "al menos un hijo" es Num_hijos >= 1 (o > 0)
        String sql = "SELECT * FROM EMPLEADOS WHERE (Salario BETWEEN 1250 AND 1300) OR (Num_hijos > 0)";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("%-5s %-20s %-10s %-5s%n", "Cod", "Nombre", "Salario", "Hijos");
            System.out.println("---------------------------------------------");
            while (rs.next()) {
                 System.out.printf("%-5d %-20s %-10.2f %-5d%n", 
                         rs.getInt("Cod"), rs.getString("Nombre"), rs.getDouble("Salario"), rs.getInt("Num_hijos"));
            }
        }
        System.out.println();
    }

    // 5. Nombres de deptos que NO contengan 'Dirección' NI 'Sector', ordenados alfabéticamente.
    private static void consulta05(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 5: Deptos sin 'Dirección' ni 'Sector' ---");
        // Usamos NOT LIKE para excluir patrones. El símbolo % es el comodín.
        String sql = "SELECT Nombre FROM DEPARTAMENTOS WHERE Nombre NOT LIKE '%DIRECCION%' AND Nombre NOT LIKE '%SECTOR%' ORDER BY Nombre";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString("Nombre"));
            }
        }
        System.out.println();
    }

    // 6. Deptos que: (Director en funciones 'F' Y Presupuesto <= 5) O (No dependen de ninguno), ordenados.
    private static void consulta06(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 6: Deptos con condiciones complejas ---");

        String sql = "SELECT Nombre FROM DEPARTAMENTOS WHERE (Tipo_dir = 'F' AND Presupuesto <= 5) OR (Depto_jefe IS NULL) ORDER BY Nombre";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString("Nombre"));
            }
        }
        System.out.println();
    }

    // 7. Nombre y salario total (salario + comision) de empleados con total > 1300, ordenado por Cod.
    private static void consulta07(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 7: Salario Total > 1300 ---");

        String sql = "SELECT Cod, Nombre, (Salario + IFNULL(Comision, 0)) AS Salario_Total " +
                     "FROM EMPLEADOS " +
                     "WHERE (Salario + IFNULL(Comision, 0)) > 1300 " +
                     "ORDER BY Cod";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.printf("%-5s %-20s %-10s%n", "Cod", "Nombre", "Total");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-10.2f%n", 
                        rs.getInt("Cod"), rs.getString("Nombre"), rs.getDouble("Salario_Total"));
            }
        }
        System.out.println();
    }

    // 8. Número total de empleados de toda la empresa.
    private static void consulta08(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 8: Total de empleados ---");

        String sql = "SELECT COUNT(*) AS Total FROM EMPLEADOS";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("Total Empleados: " + rs.getInt("Total"));
            }
        }
        System.out.println();
    }

    // 9. Cuántos departamentos existen y el presupuesto medio global.
    private static void consulta09(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 9: Total deptos y presupuesto medio ---");

        String sql = "SELECT COUNT(*) AS Num_Deptos, AVG(Presupuesto) AS Presupuesto_Medio FROM DEPARTAMENTOS";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("Número Deptos: " + rs.getInt("Num_Deptos"));
                // Formateamos la media a 2 decimales
                System.out.printf("Presupuesto Medio: %.2f '000 €%n", rs.getDouble("Presupuesto_Medio"));
            }
        }
        System.out.println();
    }

    // 10. Número de empleados y de extensiones telefónicas DISTINTAS del departamento 112.
    private static void consulta10(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 10: Empleados y extensiones en Depto 112 ---");

        String sql = "SELECT COUNT(*) AS Num_Emps, COUNT(DISTINCT Telefono) AS Num_Exts_Distintas " +
                     "FROM EMPLEADOS WHERE Departamento = 112";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("Empleados en Depto 112: " + rs.getInt("Num_Emps"));
                System.out.println("Extensiones distintas: " + rs.getInt("Num_Exts_Distintas"));
            }
        }
        System.out.println();
    }

    // 11. Operaciones de conjuntos: Códigos de departamentos que NO hacen de jefe.

    private static void consulta11(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 11: Deptos que NO son jefes (Conjuntos) ---");

        String sql = "SELECT Numero, Nombre FROM DEPARTAMENTOS " +
                     "WHERE Numero NOT IN (SELECT DISTINCT Depto_jefe FROM DEPARTAMENTOS WHERE Depto_jefe IS NOT NULL)";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("[%d] %s%n", rs.getInt("Numero"), rs.getString("Nombre"));
            }
        }
        System.out.println();
    }

    // 12. Ídem pero que SÍ hacen de departamento jefe (al contrario que la 11).
    private static void consulta12(Connection con) throws SQLException {
        System.out.println("--- CONSULTA 12: Deptos que SÍ son jefes ---");

        String sql = "SELECT Numero, Nombre FROM DEPARTAMENTOS " +
                     "WHERE Numero IN (SELECT DISTINCT Depto_jefe FROM DEPARTAMENTOS WHERE Depto_jefe IS NOT NULL)";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                 System.out.printf("[%d] %s%n", rs.getInt("Numero"), rs.getString("Nombre"));
            }
        }
        System.out.println();
    }
}
