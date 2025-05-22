CREATE OR REPLACE PROCEDURE cambiar_estado_producto(
    IN p_id INTEGER,
    IN p_estado BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_estado IS DISTINCT FROM TRUE AND p_estado IS DISTINCT FROM FALSE THEN
        RAISE EXCEPTION 'Estado inválido: solo se permite TRUE o FALSE';
    END IF;

    UPDATE productos
    SET estado = p_estado
    WHERE codigo = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Producto con código % no encontrado.', p_id;
    END IF;
END;
$$;
