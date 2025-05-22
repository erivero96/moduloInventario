-- 1. Trigger para evitar precio <= 0
CREATE OR REPLACE FUNCTION trg_precio_mayor_cero()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.precio <= 0 THEN
        RAISE EXCEPTION 'El precio debe ser mayor a cero. Valor actual: %', NEW.precio;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER precio_check
BEFORE INSERT OR UPDATE ON productos
FOR EACH ROW EXECUTE FUNCTION trg_precio_mayor_cero();
