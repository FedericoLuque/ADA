CREATE DATABASE tienda;

USE tienda;

-- Creación de la tabla clientes
CREATE TABLE clientes (
    password TEXT,
    usuario TEXT,
    direccion TEXT,
    telefono TEXT
);

-- Inserción de los datos en la tabla clientes
INSERT INTO clientes (password, usuario, direccion, telefono)
VALUES
('333', 'Juan', 'Paseo de Roma 34', '333444555'),
('222', 'lola', 'Av. Alemania', '222333444'),
('555', 'María', 'Avda. París, 7', '555666777'),
('444', 'Pedro', 'Plaza de la Constitución, 1', '444555666'),
('111', 'pepe', 'Av. París', '111222333');