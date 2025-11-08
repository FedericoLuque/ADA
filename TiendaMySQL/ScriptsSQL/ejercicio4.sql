USE BD_EJERCICIO4;

-- Cambiamos el delimitador para poder crear procedimientos
DELIMITER //

-- a) Nombre de los trabajadores cuya tarifa este entre dos extremos.
CREATE PROCEDURE sp_trabajadores_por_tarifa(IN min_tarifa REAL, IN max_tarifa REAL)
BEGIN
    SELECT NOMBRE, TARIFA 
    FROM TRABAJADOR 
    WHERE TARIFA BETWEEN min_tarifa AND max_tarifa;
END //

-- b) ¿Cuáles son los oficios de los trabajadores asignados un edificio?
CREATE PROCEDURE sp_oficios_por_edificio(IN id_edificio INT)
BEGIN
    SELECT DISTINCT T.OFICIO 
    FROM TRABAJADOR T
    JOIN ASIGNACION A ON T.ID_T = A.ID_T
    WHERE A.ID_E = id_edificio;
END //

-- c) Indicar el nombre del trabajador y el de su supervisor.
CREATE PROCEDURE sp_trabajador_y_supervisor()
BEGIN
    SELECT T1.NOMBRE AS Trabajador, T2.NOMBRE AS Supervisor
    FROM TRABAJADOR T1
    LEFT JOIN TRABAJADOR T2 ON T1.ID_SUPV = T2.ID_T;
END //

-- d) Nombre de los trabajadores asignados a oficinas.
CREATE PROCEDURE sp_trabajadores_en_oficinas()
BEGIN
    SELECT DISTINCT T.NOMBRE
    FROM TRABAJADOR T
    JOIN ASIGNACION A ON T.ID_T = A.ID_T
    JOIN EDIFICIO E ON A.ID_E = E.ID_E
    WHERE E.TIPO = 'OFICINA';
END //

-- e) ¿Cuál es el número total de días que se han dedicado a una actividad (oficio) en un edificio concreto?
CREATE PROCEDURE sp_dias_por_oficio_y_edificio(IN oficio_buscado CHAR(15), IN id_edificio INT)
BEGIN
    SELECT SUM(A.NUM_DIAS) AS TotalDias
    FROM ASIGNACION A
    JOIN TRABAJADOR T ON A.ID_T = T.ID_T
    WHERE T.OFICIO = oficio_buscado AND A.ID_E = id_edificio;
END //

-- f) ¿Cuántos tipos de oficios diferentes hay?
CREATE PROCEDURE sp_contar_oficios_diferentes()
BEGIN
    SELECT COUNT(DISTINCT OFICIO) AS TotalOficios FROM TRABAJADOR;
END //

-- g) Para cada supervisor, ¿Cuál es la tarifa por hora más alta?
CREATE PROCEDURE sp_tarifa_maxima_por_supervisor()
BEGIN
    SELECT T_SUPV.NOMBRE AS Supervisor, MAX(T_TRAB.TARIFA) AS TarifaMaxima
    FROM TRABAJADOR T_TRAB
    JOIN TRABAJADOR T_SUPV ON T_TRAB.ID_SUPV = T_SUPV.ID_T
    GROUP BY T_SUPV.ID_T, T_SUPV.NOMBRE;
END //

-- h) Para cada supervisor que supervisa a más de un trabajador, ¿cuál es la tarifa más alta?
CREATE PROCEDURE sp_tarifa_maxima_supervisor_mas_de_uno()
BEGIN
    SELECT T_SUPV.NOMBRE AS Supervisor, MAX(T_TRAB.TARIFA) AS TarifaMaxima
    FROM TRABAJADOR T_TRAB
    JOIN TRABAJADOR T_SUPV ON T_TRAB.ID_SUPV = T_SUPV.ID_T
    GROUP BY T_SUPV.ID_T, T_SUPV.NOMBRE
    HAVING COUNT(T_TRAB.ID_T) > 1;
END //

-- i) ¿Qué trabajadores reciben una tarifa por hora menor que la del promedio?
CREATE PROCEDURE sp_tarifa_menor_promedio_global()
BEGIN
    SELECT NOMBRE, TARIFA
    FROM TRABAJADOR
    WHERE TARIFA < (SELECT AVG(TARIFA) FROM TRABAJADOR);
END //

-- j) ¿Qué trabajadores reciben una tarifa menor que la del promedio de su mismo oficio?
CREATE PROCEDURE sp_tarifa_menor_promedio_oficio()
BEGIN
    SELECT T1.NOMBRE, T1.OFICIO, T1.TARIFA
    FROM TRABAJADOR T1
    WHERE T1.TARIFA < (
        SELECT AVG(T2.TARIFA) 
        FROM TRABAJADOR T2 
        WHERE T2.OFICIO = T1.OFICIO
    );
END //

-- k) ¿Qué trabajadores reciben una tarifa menor que la del promedio de los que dependen del mismo supervisor?
CREATE PROCEDURE sp_tarifa_menor_promedio_supervisor()
BEGIN
    SELECT T1.NOMBRE, T1.ID_SUPV, T1.TARIFA
    FROM TRABAJADOR T1
    WHERE T1.TARIFA < (
        SELECT AVG(T2.TARIFA) 
        FROM TRABAJADOR T2 
        WHERE T2.ID_SUPV = T1.ID_SUPV
    );
END //

-- l) ¿Qué supervisores tienen trabajadores que tienen una tarifa por hora por encima un determinado valor?
CREATE PROCEDURE sp_supervisor_por_tarifa_alta(IN tarifa_limite REAL)
BEGIN
    SELECT DISTINCT T_SUPV.NOMBRE AS Supervisor
    FROM TRABAJADOR T_TRAB
    JOIN TRABAJADOR T_SUPV ON T_TRAB.ID_SUPV = T_SUPV.ID_T
    WHERE T_TRAB.TARIFA > tarifa_limite;
END //

-- Restauramos el delimitador
DELIMITER ;
