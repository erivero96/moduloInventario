--RF 06
CREATE OR REPLACE FUNCTION prevenir_borrado_movimientos()
RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'No está permitido eliminar registros de movimientos_stock';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevenir_borrado
BEFORE DELETE ON movimientos_stock
FOR EACH ROW EXECUTE FUNCTION prevenir_borrado_movimientos();
