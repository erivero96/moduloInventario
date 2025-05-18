CREATE OR REPLACE PROCEDURE registrar_producto_por_nombre(
    nom_prod VARCHAR,
    desc_prod TEXT,
    prec_prod DOUBLE PRECISION,
    nombre_categoria VARCHAR,
    stock_prod INTEGER DEFAULT 0
)
LANGUAGE plpgsql AS $$
DECLARE
    cat_id INTEGER;
    existe INTEGER;
BEGIN
    -- Verificar si ya existe un producto con el mismo nombre
    SELECT COUNT(*) INTO existe
    FROM productos
    WHERE LOWER(nombre) = LOWER(nom_prod);

    IF existe > 0 THEN
        RAISE EXCEPTION 'Ya existe un producto con el nombre "%".', nom_prod;
    END IF;

    -- Obtener el ID de la categoría
    SELECT id INTO cat_id
    FROM categorias
    WHERE nombre = nombre_categoria;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Categoría "%" no existe.', nombre_categoria;
    END IF;

    -- Verificar precio válido
    IF prec_prod <= 0 THEN
        RAISE EXCEPTION 'El precio debe ser mayor a cero. Valor dado: %', prec_prod;
    END IF;

    -- Insertar el nuevo producto
    INSERT INTO productos (nombre, descripcion, precio, stock, categoria_id)
    VALUES (nom_prod, desc_prod, prec_prod, stock_prod, cat_id);
END;
$$;
