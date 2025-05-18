-- RF 02
CREATE OR REPLACE PROCEDURE actualizar_stock(
    IN p_codigo_producto INTEGER,
    IN p_cantidad INTEGER,
    IN p_motivo TEXT,
    IN p_usuario_id CHAR(8),
    IN p_mov_id CHAR(5)
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM productos WHERE codigo = p_codigo_producto
    ) THEN
        RAISE EXCEPTION 'El producto con código % no existe', p_codigo_producto;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM usuarios WHERE id = p_usuario_id
    ) THEN
        RAISE EXCEPTION 'El usuario con ID % no existe', p_usuario_id;
    END IF;

    IF p_cantidad < 0 THEN
        PERFORM stock FROM productos WHERE codigo = p_codigo_producto;
        IF (SELECT stock FROM productos WHERE codigo = p_codigo_producto) + p_cantidad < 0 THEN
            RAISE EXCEPTION 'Stock insuficiente para salida de % unidades', ABS(p_cantidad);
        END IF;
    END IF;

    UPDATE productos
    SET stock = stock + p_cantidad
    WHERE codigo = p_codigo_producto;

    INSERT INTO movimientos_stock(id, codigo_producto, cantidad, motivo, usuario_id)
    VALUES (p_mov_id, p_codigo_producto, p_cantidad, p_motivo, p_usuario_id);

END;
$$;
