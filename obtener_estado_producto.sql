CREATE OR REPLACE PROCEDURE obtener_estado_producto(
    IN cod_producto INTEGER,
    OUT estado_producto BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT estado
    INTO estado_producto
    FROM productos
    WHERE codigo = cod_producto;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Producto con código % no encontrado.', cod_producto;
    END IF;
END;
$$;
