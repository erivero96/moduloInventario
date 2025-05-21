CREATE OR REPLACE PROCEDURE listar_movimientos_por_usuario(
    IN p_usuario_id CHAR(8),
    OUT ref refcursor
)
AS $$
BEGIN
    OPEN ref FOR
    SELECT * FROM movimientos_stock
    WHERE usuario_id = p_usuario_id
    ORDER BY fecha DESC;
END;
$$ LANGUAGE plpgsql;
