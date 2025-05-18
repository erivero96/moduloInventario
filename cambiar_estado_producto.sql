CREATE OR REPLACE FUNCTION cambiar_estado_producto(p_id INTEGER, p_estado BOOLEAN)
RETURNS VOID AS $$
BEGIN
    IF p_estado IS NOT TRUE AND p_estado IS NOT FALSE THEN
        RAISE EXCEPTION 'Estado inválido: solo se permite TRUE o FALSE';
    END IF;

    UPDATE productos
    SET estado = p_estado
    WHERE codigo = p_id;
END;
$$ LANGUAGE plpgsql;
