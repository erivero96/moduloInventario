-- 3–5. Procedimiento de movimiento de stock con validaciones y avisos
CREATE OR REPLACE PROCEDURE registrar_movimiento_stock(
    mov_id CHAR(5),
    cod_prod INTEGER,
    cantidad INT,
    motivo TEXT,
    usuario_ref CHAR(8)
)
LANGUAGE plpgsql AS $$
DECLARE
    stock_actual INTEGER;
    nombre_prod TEXT;
BEGIN
    SELECT stock, nombre INTO stock_actual, nombre_prod
      FROM productos
     WHERE codigo = cod_prod
     FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe producto con código "%".', cod_prod;
    END IF;

    IF cantidad < 0 AND stock_actual + cantidad < 0 THEN
        RAISE EXCEPTION 'Stock insuficiente para el producto "%". Máximo a retirar: %', nombre_prod, stock_actual;
    END IF;

    INSERT INTO movimientos_stock (id, codigo_producto, cantidad, motivo, usuario_id)
    VALUES (mov_id, cod_prod, cantidad, motivo, usuario_ref);

    UPDATE productos
       SET stock = stock + cantidad
     WHERE codigo = cod_prod;

    SELECT stock INTO stock_actual
      FROM productos
     WHERE codigo = cod_prod;

    IF stock_actual = 0 THEN
        UPDATE productos
           SET estado = FALSE
         WHERE codigo = cod_prod;
    ELSIF stock_actual > 0 THEN
        UPDATE productos
           SET estado = TRUE
         WHERE codigo = cod_prod;
    END IF;

    IF stock_actual <= 5 THEN
        RAISE NOTICE 'ATENCIÓN: Stock de "%": % unidades. Reponer producto (comprar).',
            nombre_prod, stock_actual;
    END IF;
END;
$$;
