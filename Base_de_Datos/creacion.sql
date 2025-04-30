-- Crear la base de datos
CREATE DATABASE inventario;

-- Conectarse a la base de datos
\c inventario;

-- Tabla de usuarios
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(100) NOT NULL,
    rol VARCHAR(50) DEFAULT 'usuario',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de categorías
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
);

-- Eliminar la tabla si ya existe (opcional)
DROP TABLE IF EXISTS productos;

-- Crear tabla productos
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,       -- para búsquedas por código
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    categoria_id INTEGER NOT NULL REFERENCES categorias(id)
);
