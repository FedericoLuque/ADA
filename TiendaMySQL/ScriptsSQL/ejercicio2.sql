CREATE DATABASE EMPRESA;
USE EMPRESA;


-- Tabla: CENTROS
-- No tiene dependencias foráneas, se crea primero.
CREATE TABLE CENTROS (
    Numero INT NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Direccion VARCHAR(255),
    CONSTRAINT PK_Centros PRIMARY KEY (Numero)
);

-- Tabla: DEPARTAMENTOS
-- Depende de CENTROS.
CREATE TABLE DEPARTAMENTOS (
    Numero INT NOT NULL,
    Centro INT NOT NULL,
    Director INT NOT NULL,
    Tipo_dir CHAR(1) NOT NULL,
    Presupuesto DECIMAL(12, 2),
    Depto_jefe INT,
    Nombre VARCHAR(100) NOT NULL,
    CONSTRAINT PK_Departamentos PRIMARY KEY (Numero),
    CONSTRAINT FK_Depto_Centro FOREIGN KEY (Centro) REFERENCES CENTROS(Numero),
    CONSTRAINT CK_Tipo_Director CHECK (Tipo_dir IN ('P', 'F'))
);

-- Tabla: EMPLEADOS
-- Depende de DEPARTAMENTOS.
CREATE TABLE EMPLEADOS (
    Cod INT NOT NULL,
    Departamento INT NOT NULL,
    Telefono VARCHAR(20),
    Fecha_nacimiento DATE,
    Fecha_ingreso DATE NOT NULL,
    Salario DECIMAL(10, 2) NOT NULL,
    Comision DECIMAL(10, 2),
    Num_hijos SMALLINT DEFAULT 0,
    Nombre VARCHAR(150) NOT NULL,
    CONSTRAINT PK_Empleados PRIMARY KEY (Cod),
    CONSTRAINT FK_Emp_Depto FOREIGN KEY (Departamento) REFERENCES DEPARTAMENTOS(Numero),
    CONSTRAINT CK_Num_Hijos CHECK (Num_hijos >= 0)
);

-- FK: Director del departamento (Dependencia circular DEPARTAMENTOS -> EMPLEADOS)
ALTER TABLE DEPARTAMENTOS
ADD CONSTRAINT FK_Depto_Director FOREIGN KEY (Director) REFERENCES EMPLEADOS(Cod);

-- FK: Departamento jefe (Dependencia recursiva DEPARTAMENTOS -> DEPARTAMENTOS)
ALTER TABLE DEPARTAMENTOS
ADD CONSTRAINT FK_Depto_Jefe FOREIGN KEY (Depto_jefe) REFERENCES DEPARTAMENTOS(Numero);

