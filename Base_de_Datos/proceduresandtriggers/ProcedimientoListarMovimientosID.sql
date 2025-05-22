CREATE OR REPLACE PROCEDURE listar_movimientos_usuario(usuario CHAR(8))
LANGUAGE plpgsql
AS $$
DECLARE
    mov RECORD;
BEGIN
    RAISE NOTICE 'Movimientos de stock del usuario % en los últimos 7 días:', usuario;

    FOR mov IN
        SELECT id, codigo_producto, fecha, cantidad, motivo
        FROM movimientos_stock
        WHERE usuario_id = usuario
          AND fecha >= CURRENT_DATE - INTERVAL '7 days'
        ORDER BY fecha DESC
    LOOP
        RAISE NOTICE 'ID: %, Producto: %, Fecha: %, Cantidad: %, Motivo: %',
            mov.id, mov.codigo_producto, mov.fecha, mov.cantidad, mov.motivo;
    END LOOP;
END;
$$;