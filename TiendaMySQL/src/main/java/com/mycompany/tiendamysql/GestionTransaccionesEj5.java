/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tiendamysql; // Asegúrate de que sea tu paquete

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Necesario para los nuevos empleados


public class GestionTransaccionesEj5 {

    private static final String URL = "jdbc:mysql://localhost:3306/EMPRESA";
    private static final String USER = "root";       // Tu usuario
    private static final String PASSWORD = "1234";   // Tu contraseña

    private Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de MySQL (.jar).");
            throw new SQLException("Driver no encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Transacción 1: Cambiar un director de 'F' a 'P' y subir su sueldo un 20%.
     * @param idDirector El 'Cod' del empleado que es director.
     */
    public void promoverDirector(int idDirector) {
        Connection conn = null;
        // Necesitamos 2 sentencias: 1 para EMPLEADOS, 1 para DEPARTAMENTOS
        PreparedStatement pstmtEmpleado = null;
        PreparedStatement pstmtDepto = null;

        String sqlUpdateSalario = "UPDATE EMPLEADOS SET Salario = Salario * 1.20 WHERE Cod = ?";
        String sqlUpdateTipoDir = "UPDATE DEPARTAMENTOS SET Tipo_dir = 'P' WHERE Director = ? AND Tipo_dir = 'F'";

        System.out.println("\n--- Transacción 1: Promoviendo al director " + idDirector + " ---");

        try {
            conn = getConexion();
            conn.setAutoCommit(false);

            // 1. Aumentar salario
            pstmtEmpleado = conn.prepareStatement(sqlUpdateSalario);
            pstmtEmpleado.setInt(1, idDirector);
            int filasAfectadasEmp = pstmtEmpleado.executeUpdate();
            System.out.println("Paso 1: Actualizando salario. Filas afectadas: " + filasAfectadasEmp);

            if (filasAfectadasEmp == 0) {
                throw new SQLException("No se encontró al empleado con código " + idDirector + ". Haciendo rollback.");
            }

            // 2. Cambiar tipo de director
            pstmtDepto = conn.prepareStatement(sqlUpdateTipoDir);
            pstmtDepto.setInt(1, idDirector);
            int filasAfectadasDepto = pstmtDepto.executeUpdate();
            System.out.println("Paso 2: Actualizando tipo director. Filas afectadas: " + filasAfectadasDepto);

            if (filasAfectadasDepto == 0) {
                System.out.println("Advertencia: El empleado existe pero no era director en 'Funciones'.");
            }
            
            conn.commit();
            System.out.println("-> ÉXITO: Transacción completada (commit).");

        } catch (SQLException e) {

            System.err.println("¡ERROR! Ocurrió un error en la transacción.");
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.err.println("-> FALLO: Revirtiendo cambios (rollback).");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al intentar hacer rollback: " + ex.getMessage());
            }
        } finally {
            // Cerramos todos los recursos
            try { if (pstmtEmpleado != null) pstmtEmpleado.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtDepto != null) pstmtDepto.close(); } catch (SQLException e) { /* ign */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ign */ }
        }
    }

    /**
     * Transacción 2: Cambiar el nombre de un centro y de TODOS los departamentos
     * que están en ese centro.
     * @param idCentro El 'Numero' del centro.
     * @param nuevoNombreCentro El nuevo nombre para el centro.
     * @param nuevoNombreDeptos El nuevo nombre para TODOS los deptos de ese centro.
     */
    public void renombrarCentroYDeptos(int idCentro, String nuevoNombreCentro, String nuevoNombreDeptos) {
        Connection conn = null;
        PreparedStatement pstmtCentro = null;
        PreparedStatement pstmtDepto = null;

        String sqlCentro = "UPDATE CENTROS SET Nombre = ? WHERE Numero = ?";
        String sqlDepto = "UPDATE DEPARTAMENTOS SET Nombre = ? WHERE Centro = ?";
        
        System.out.println("\n--- Transacción 2: Renombrando centro " + idCentro + " ---");

        try {
            conn = getConexion();
            conn.setAutoCommit(false);

            // 1. Actualizar CENTROS
            pstmtCentro = conn.prepareStatement(sqlCentro);
            pstmtCentro.setString(1, nuevoNombreCentro);
            pstmtCentro.setInt(2, idCentro);
            int filasCentro = pstmtCentro.executeUpdate();
            System.out.println("Paso 1: Actualizando CENTROS. Filas afectadas: " + filasCentro);

            if (filasCentro == 0) {
                throw new SQLException("No se encontró el centro " + idCentro + ". Haciendo rollback.");
            }
            
            // 2. Actualizar DEPARTAMENTOS
            pstmtDepto = conn.prepareStatement(sqlDepto);
            pstmtDepto.setString(1, nuevoNombreDeptos);
            pstmtDepto.setInt(2, idCentro);
            int filasDepto = pstmtDepto.executeUpdate();
            System.out.println("Paso 2: Actualizando DEPARTAMENTOS. Filas afectadas: " + filasDepto);

            conn.commit();
            System.out.println("-> ÉXITO: Transacción completada (commit).");

        } catch (SQLException e) {
            System.err.println("¡ERROR! Ocurrió un error en la transacción.");
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.err.println("-> FALLO: Revirtiendo cambios (rollback).");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al intentar hacer rollback: " + ex.getMessage());
            }
        } finally {
            try { if (pstmtCentro != null) pstmtCentro.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtDepto != null) pstmtDepto.close(); } catch (SQLException e) { /* ign */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ign */ }
        }
    }

    /**
     * Transacción 3: Eliminar un empleado y disminuir el presupuesto
     * del departamento donde trabajaba un 5%.
     * @param idEmpleado El 'Cod' del empleado a eliminar.
     */
    public void eliminarEmpleado(int idEmpleado) {
        Connection conn = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtDelete = null;
        ResultSet rs = null;
        int idDepto = -1;

        String sqlSelect = "SELECT Departamento FROM EMPLEADOS WHERE Cod = ?";
        String sqlUpdate = "UPDATE DEPARTAMENTOS SET Presupuesto = Presupuesto * 0.95 WHERE Numero = ?";
        String sqlDelete = "DELETE FROM EMPLEADOS WHERE Cod = ?";

        System.out.println("\n--- Transacción 3: Eliminando empleado " + idEmpleado + " ---");

        try {
            conn = getConexion();
            conn.setAutoCommit(false);

            // 1. AVERIGUAR el departamento del empleado
            pstmtSelect = conn.prepareStatement(sqlSelect);
            pstmtSelect.setInt(1, idEmpleado);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                idDepto = rs.getInt("Departamento");
                System.out.println("Paso 1: Empleado encontrado. Pertenece al depto: " + idDepto);
            } else {
                throw new SQLException("No se encontró al empleado " + idEmpleado + ". Haciendo rollback.");
            }
            
            // 2. REDUCIR presupuesto del departamento
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, idDepto);
            int filasUpdate = pstmtUpdate.executeUpdate();
            System.out.println("Paso 2: Actualizando presupuesto depto. Filas afectadas: " + filasUpdate);
            
            // 3. ELIMINAR al empleado
            // OJO: Esto fallará si el empleado es Director o Depto_jefe
            // debido a las restricciones FOREIGN KEY
            // Para que funcione, el empleado no debe tener dependencias.
            pstmtDelete = conn.prepareStatement(sqlDelete);
            pstmtDelete.setInt(1, idEmpleado);
            int filasDelete = pstmtDelete.executeUpdate();
            System.out.println("Paso 3: Eliminando empleado. Filas afectadas: " + filasDelete);
            
            conn.commit();
            System.out.println("-> ÉXITO: Transacción completada (commit).");

        } catch (SQLException e) {
            System.err.println("¡ERROR! Ocurrió un error en la transacción.");
            if (e.getMessage().contains("foreign key constraint fails")) {
                System.err.println("Causa probable: El empleado es Director o Jefe de otro depto. No se puede borrar.");
            }
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.err.println("-> FALLO: Revirtiendo cambios (rollback).");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al intentar hacer rollback: " + ex.getMessage());
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtSelect != null) pstmtSelect.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtUpdate != null) pstmtUpdate.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtDelete != null) pstmtDelete.close(); } catch (SQLException e) { /* ign */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ign */ }
        }
    }

    /**
     * Transacción 4: Aumentar presupuesto de un depto un 20% y
     * contratar a 4 nuevos empleados.
     * @param idDepto El 'Numero' del departamento.
     */
    public void aumentarPresupuestoYContratar(int idDepto) {
        Connection conn = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtInsert = null;

        String sqlUpdate = "UPDATE DEPARTAMENTOS SET Presupuesto = Presupuesto * 1.20 WHERE Numero = ?";
        String sqlInsert = "INSERT INTO EMPLEADOS (Cod, Departamento, Telefono, Fecha_nacimiento, Fecha_ingreso, Salario, Num_hijos, Nombre) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("\n--- Transacción 4: Aumentando presupuesto y contratando en depto " + idDepto + " ---");

        try {
            conn = getConexion();
            conn.setAutoCommit(false);
            
            // 1. Aumentar presupuesto
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, idDepto);
            int filasUpdate = pstmtUpdate.executeUpdate();
            System.out.println("Paso 1: Actualizando presupuesto. Filas afectadas: " + filasUpdate);
            
            if(filasUpdate == 0) {
                 throw new SQLException("No se encontró el departamento " + idDepto + ". Haciendo rollback.");
            }
            
            // 2. Insertar 4 empleados (datos de ejemplo)
            pstmtInsert = conn.prepareStatement(sqlInsert);
            
            // Empleado 1
            pstmtInsert.setInt(1, 901);
            pstmtInsert.setInt(2, idDepto);
            pstmtInsert.setString(3, "901");
            pstmtInsert.setDate(4, Date.valueOf("1990-01-01"));
            pstmtInsert.setDate(5, Date.valueOf("2025-11-08"));
            pstmtInsert.setDouble(6, 1100);
            pstmtInsert.setInt(7, 0);
            pstmtInsert.setString(8, "Nuevo Empleado 1");
            pstmtInsert.executeUpdate();
            System.out.println("Paso 2.1: Insertando Empleado 901");

            // Empleado 2
            pstmtInsert.setInt(1, 902);
            pstmtInsert.setInt(2, idDepto);
            pstmtInsert.setString(3, "902");
            pstmtInsert.setDate(4, Date.valueOf("1992-02-02"));
            pstmtInsert.setDate(5, Date.valueOf("2025-11-08"));
            pstmtInsert.setDouble(6, 1100);
            pstmtInsert.setInt(7, 0);
            pstmtInsert.setString(8, "Nuevo Empleado 2");
            pstmtInsert.executeUpdate();
            System.out.println("Paso 2.2: Insertando Empleado 902");

            // Empleado 3
            pstmtInsert.setInt(1, 903);
            pstmtInsert.setInt(2, idDepto);
            pstmtInsert.setString(3, "903");
            pstmtInsert.setDate(4, Date.valueOf("1993-03-03"));
            pstmtInsert.setDate(5, Date.valueOf("2025-11-08"));
            pstmtInsert.setDouble(6, 1100);
            pstmtInsert.setInt(7, 0);
            pstmtInsert.setString(8, "Nuevo Empleado 3");
            pstmtInsert.executeUpdate();
            System.out.println("Paso 2.3: Insertando Empleado 903");

            // Empleado 4
            pstmtInsert.setInt(1, 904);
            pstmtInsert.setInt(2, idDepto);
            pstmtInsert.setString(3, "904");
            pstmtInsert.setDate(4, Date.valueOf("1994-04-04"));
            pstmtInsert.setDate(5, Date.valueOf("2025-11-08"));
            pstmtInsert.setDouble(6, 1100);
            pstmtInsert.setInt(7, 0);
            pstmtInsert.setString(8, "Nuevo Empleado 4");
            pstmtInsert.executeUpdate();
            System.out.println("Paso 2.4: Insertando Empleado 904");
            
            
            conn.commit();
            System.out.println("-> ÉXITO: Transacción completada (commit).");

        } catch (SQLException e) {
            System.err.println("¡ERROR! Ocurrió un error en la transacción.");
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.err.println("-> FALLO: Revirtiendo cambios (rollback).");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al intentar hacer rollback: " + ex.getMessage());
            }
        } finally {
            try { if (pstmtUpdate != null) pstmtUpdate.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtInsert != null) pstmtInsert.close(); } catch (SQLException e) { /* ign */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ign */ }
        }
    }

    /**
     * Transacción 5: Aumentar el número de hijos de un empleado. Si es
     * director en funciones ('F'), pasa a ser en propiedad ('P').
     * @param idEmpleado El 'Cod' del empleado.
     */
    public void incrementarHijosYPromover(int idEmpleado) {
        Connection conn = null;
        PreparedStatement pstmtHijos = null;
        PreparedStatement pstmtDepto = null;

        String sqlHijos = "UPDATE EMPLEADOS SET Num_hijos = Num_hijos + 1 WHERE Cod = ?";

        String sqlDepto = "UPDATE DEPARTAMENTOS SET Tipo_dir = 'P' WHERE Director = ? AND Tipo_dir = 'F'";

        System.out.println("\n--- Transacción 5: Incrementando hijos a empleado " + idEmpleado + " ---");

        try {
            conn = getConexion();
            conn.setAutoCommit(false);

            // 1. Incrementar hijos
            pstmtHijos = conn.prepareStatement(sqlHijos);
            pstmtHijos.setInt(1, idEmpleado);
            int filasHijos = pstmtHijos.executeUpdate();
            System.out.println("Paso 1: Incrementando hijos. Filas afectadas: " + filasHijos);

            if (filasHijos == 0) {
                throw new SQLException("No se encontró al empleado " + idEmpleado + ". Haciendo rollback.");
            }

            // 2. Intentar promover
            pstmtDepto = conn.prepareStatement(sqlDepto);
            pstmtDepto.setInt(1, idEmpleado);
            int filasDepto = pstmtDepto.executeUpdate();
            
            if (filasDepto > 0) {
                System.out.println("Paso 2: ¡Promoción aplicada! El empleado era director 'F' y ahora es 'P'.");
            } else {
                System.out.println("Paso 2: No se aplicó promoción (empleado no es director 'F').");
            }
            
            conn.commit();
            System.out.println("-> ÉXITO: Transacción completada (commit).");

        } catch (SQLException e) {
            System.err.println("¡ERROR! Ocurrió un error en la transacción.");
            e.printStackTrace();
            try {
                if (conn != null) {
                    System.err.println("-> FALLO: Revirtiendo cambios (rollback).");
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error al intentar hacer rollback: " + ex.getMessage());
            }
        } finally {
            try { if (pstmtHijos != null) pstmtHijos.close(); } catch (SQLException e) { /* ign */ }
            try { if (pstmtDepto != null) pstmtDepto.close(); } catch (SQLException e) { /* ign */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* ign */ }
        }
    }


    /**
     * MÉTODO PRINCIPAL PARA EJECUTAR LAS PRUEBAS
     */
public static void main(String[] args) {
        GestionTransaccionesEj5 app = new GestionTransaccionesEj5();

        System.out.println("=== INICIANDO PRUEBAS DE TRANSACCIONES (EJERCICIO 5) ===");
        System.out.println("Por favor, descomenta las pruebas en el main() una por una.");

        // --- Prueba 1: Promover director ---
        // OBJETIVO: Empleado 150 (PEREZ, 07IO) es director 'F' del depto 120.
        // RESULTADO ESPERADO: Su salario (1440) subirá a 1728,
        // y el Tipo_dir del depto 120 cambiará de 'F' a 'P'.
        // ---
        // app.promoverDirector(150);
        
        // --- Prueba 2: Renombrar centro y sus deptos ---
        // OBJETIVO: Renombrar el Centro 20 ('RELACION CON CLIENTE') y todos sus deptos.
        // RESULTADO ESPERADO: El Centro 20 se llamará "SEDE DE CLIENTES".
        // Los deptos 110, 111, 112 y 120 se llamarán "DEP. ATENCION CLIENTES".
        // ---
        // app.renombrarCentroYDeptos(20, "SEDE DE CLIENTES", "DEP. ATENCION CLIENTES");

        // --- Prueba 3: Eliminar empleado (CASO DE ÉXITO) ---
        // OBJETIVO: Eliminar al empleado 120 (LASA, 03IO). No es director ni jefe.
        // RESULTADO ESPERADO: El empleado 120 será eliminado.
        // El presupuesto de su depto (112), que es 9, bajará un 5% (a 8.55).
        // ---
        // app.eliminarEmpleado(120);

        // --- Prueba 3: Eliminar empleado (CASO DE FALLO Y ROLLBACK) ---
        // OBJETIVO: Intentar eliminar al empleado 180 (PEREZ, 03COS).
        // Es director de los deptos 110 y 111 (restricción Foreign Key).
        // RESULTADO ESPERADO: La transacción fallará en el paso 'DELETE'.
        // Se ejecutará un ROLLBACK. NI el empleado será borrado,
        // NI el presupuesto de su depto será reducido.
        // ---
        // app.eliminarEmpleado(180);

        // --- Prueba 4: Aumentar presupuesto y contratar (CASO DE ÉXITO) ---
        // OBJETIVO: Aumentar presupuesto del depto 130 (FINANZAS) y contratar a 4.
        // RESULTADO ESPERADO: El presupuesto del depto 130 (que es 2) subirá un 20% (a 2.4).
        // Se añadirán 4 nuevos empleados (IDs 901, 902, 903, 904) a ese depto.
        // ---
        // app.aumentarPresupuestoYContratar(130);
        
        // --- Prueba 4: Aumentar presupuesto (CASO DE FALLO Y ROLLBACK) ---
        // OBJETIVO: Ejecutar la prueba anterior POR SEGUNDA VEZ.
        // RESULTADO ESPERADO: El 'UPDATE' del presupuesto funcionará, pero el 'INSERT'
        // del empleado 901 fallará (Error de Primary Key duplicada).
        // La transacción entera se revertirá (ROLLBACK).
        // El presupuesto del depto 130 volverá a 2.4 (no se quedará en 2.88).
        // ---
        // app.aumentarPresupuestoYContratar(130); // ¡Ejecutar solo después de la anterior!

        // --- Prueba 5: Incrementar hijos (CASO 1: CON PROMOCIÓN) ---
        // OBJETIVO: Incrementar hijos al empleado 180 (PEREZ, 03COS).
        // Es director 'F' del depto 111.
        // RESULTADO ESPERADO: Sus hijos pasarán de 2 a 3.
        // El Tipo_dir del depto 111 cambiará de 'F' a 'P'.
        // ---
        // app.incrementarHijosYPromover(180);

        // --- Prueba 5: Incrementar hijos (CASO 2: SIN PROMOCIÓN) ---
        // OBJETIVO: Incrementar hijos al empleado 110 (PONS, CESAR).
        // NO es director 'F' de ningún depto.
        // RESULTADO ESPERADO: Sus hijos pasarán de 3 a 4.
        // No ocurrirá ninguna promoción. La transacción se completará con éxito.
        // ---
        // app.incrementarHijosYPromover(110);
        
        System.out.println("\n=== PRUEBAS EJERCICIO 5 LISTAS PARA EJECUTAR ===");
    }
}