CREATE OR REPLACE PROCEDURE listar_movimientos_por_producto(
    IN p_codigo_producto INTEGER,
    OUT ref refcursor
)
AS $$
BEGIN
    OPEN ref FOR
    SELECT * FROM movimientos_stock
    WHERE codigo_producto = p_codigo_producto
    ORDER BY fecha DESC;
END;
$$ LANGUAGE plpgsql;
