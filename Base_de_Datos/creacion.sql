-- Crear la base de datos
CREATE DATABASE inventario;

-- Conectarse a la base
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

-- Tabla de productos (usa código como clave primaria)
CREATE TABLE public.productos (

    codigo VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    categoria_id INTEGER NOT NULL REFERENCES categorias(id)
);

-- Tabla de movimientos de stock
CREATE TABLE movimientos_stock (

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

-- Procedimiento: registrar producto
CREATE OR REPLACE PROCEDURE registrar_producto(
    cod VARCHAR,
    nom VARCHAR,
    desc_ TEXT,
    prec DOUBLE PRECISION,
    cat_id INTEGER,
    stock_ INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM categorias WHERE id = cat_id) THEN
        RAISE EXCEPTION 'La categoría con ID % no existe.', cat_id;
    END IF;

    IF EXISTS (SELECT 1 FROM productos WHERE codigo = cod) THEN
        RAISE EXCEPTION 'Ya existe un producto con el código "%".', cod;
    END IF;

    INSERT INTO productos (codigo, nombre, descripcion, precio, stock, categoria_id)
    VALUES (cod, nom, desc_, prec, stock_, cat_id);
END;
$$;

-- Procedimiento: actualizar stock
CREATE OR REPLACE PROCEDURE actualizar_stock(
    cod VARCHAR,
    nuevo_stock INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = cod) THEN
        RAISE EXCEPTION 'No existe un producto con el código "%".', cod;
    END IF;

    IF nuevo_stock < 0 THEN
        RAISE EXCEPTION 'El stock no puede ser negativo.';
    END IF;

    UPDATE productos SET stock = nuevo_stock WHERE codigo = cod;
END;
$$;

-- Procedimiento: registrar movimiento de stock
CREATE OR REPLACE PROCEDURE registrar_movimiento_stock(
    cod_producto VARCHAR,
    cantidad INT,
    motivo TEXT,
    usuario_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO movimientos_stock (codigo_producto, cantidad, motivo, usuario_id)
    VALUES (cod_producto, cantidad, motivo, usuario_id);
END;
$$;

-- Procedimiento: dar de baja producto (pone stock en 0)
CREATE OR REPLACE PROCEDURE dar_de_baja_producto(
    cod VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = cod) THEN
        RAISE EXCEPTION 'El producto con código "%" no existe.', cod;
    END IF;

    UPDATE productos SET stock = 0 WHERE codigo = cod;
END;
$$;

CREATE OR REPLACE FUNCTION listar_productos()
RETURNS TABLE (
    codigo VARCHAR,
    nombre VARCHAR,
    descripcion TEXT,
    precio DOUBLE PRECISION,
    stock INTEGER,
    categoria TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.codigo,
        p.nombre,
        p.descripcion,
        p.precio,
        p.stock,
        c.nombre
    FROM productos p
    JOIN categorias c ON p.categoria_id = c.id;
END;
$$;



