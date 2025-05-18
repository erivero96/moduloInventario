-- Crear la base de datos
CREATE DATABASE inventario;

-- Conectarse a la base de datos
\c inventario;

-- Crear la base de datos
CREATE DATABASE inventario;

-- Conectarse a la base de datos
\c inventario;

-- Crear tablas

CREATE TABLE public.categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE public.usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(100) NOT NULL,
    rol VARCHAR(50) DEFAULT 'usuario',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.productos (
    codigo VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    categoria_id INTEGER NOT NULL REFERENCES categorias(id),
    stock_minimo INTEGER DEFAULT 5,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE public.movimientos_stock (
    id SERIAL PRIMARY KEY,
    codigo_producto VARCHAR(50) REFERENCES productos(codigo),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cantidad INTEGER NOT NULL,
    motivo TEXT,
    usuario_id INTEGER REFERENCES usuarios(id)
);

