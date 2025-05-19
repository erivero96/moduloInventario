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
    id CHAR(8) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(100) NOT NULL,
    rol VARCHAR(50) DEFAULT 'usuario',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.productos (
    codigo SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    categoria_id INTEGER NOT NULL REFERENCES categorias(id),
    stock_minimo INTEGER DEFAULT 5,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE public.movimientos_stock (
    id CHAR(5) PRIMARY KEY,
    codigo_producto INTEGER REFERENCES productos(codigo),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cantidad INTEGER NOT NULL,
    motivo TEXT,
    usuario_id CHAR(8) REFERENCES usuarios(id)
);

-- busqueda parcial por nombre de producto
CREATE INDEX idx_productos_nombre ON public.productos USING gin (nombre gin_trgm_ops);
-- busqueda por codigo
CREATE INDEX idx_productos_codigo ON public.productos(codigo);
-- busqueda por categoria
CREATE INDEX idx_productos_categoria ON public.productos(categoria_id);
-- solo productos activos
CREATE INDEX idx_productos_estado ON public.productos(estado) WHERE estado = TRUE;
