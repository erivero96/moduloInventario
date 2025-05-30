-- busqueda parcial por nombre de producto
CREATE INDEX idx_productos_nombre ON public.productos USING gin (nombre gin_trgm_ops);

-- busqueda por codigo
CREATE INDEX idx_productos_codigo ON public.productos(codigo);

-- busqueda por categoria
CREATE INDEX idx_productos_categoria ON public.productos(categoria_id);

-- solo productos activos
CREATE INDEX idx_productos_estado ON public.productos(estado) WHERE estado = TRUE;

-- Añadir al final del archivo creacion.sql

CREATE OR REPLACE FUNCTION consultar_productos(
    p_codigo TEXT DEFAULT NULL,
    p_nombre TEXT DEFAULT NULL,
    p_categoria_id INTEGER DEFAULT NULL,
    p_solo_activos BOOLEAN DEFAULT TRUE,
    p_orden TEXT DEFAULT NULL,
    p_ascendente BOOLEAN DEFAULT TRUE
) RETURNS SETOF productos AS $$
DECLARE
    v_query TEXT;
BEGIN
    -- Validar parámetros de entrada
    IF p_codigo IS NOT NULL AND p_codigo !~ '^\d+$' THEN
        RAISE EXCEPTION 'El código debe ser un número válido';
    END IF;

    IF p_orden IS NOT NULL AND p_orden NOT IN ('codigo', 'nombre', 'precio', 'stock') THEN
        RAISE EXCEPTION 'Campo de orden inválido. Use: codigo, nombre, precio o stock';
    END IF;

    -- Construir consulta base
    v_query := 'SELECT * FROM productos WHERE 1=1';

    -- Añadir filtros
    IF p_codigo IS NOT NULL THEN
        v_query := v_query || ' AND codigo = ' || p_codigo;
    END IF;

    IF p_nombre IS NOT NULL THEN
        v_query := v_query || ' AND nombre ILIKE ''%' || REPLACE(p_nombre, '''', '''''') || '%''';
    END IF;

    IF p_categoria_id IS NOT NULL THEN
        v_query := v_query || ' AND categoria_id = ' || p_categoria_id;
    END IF;

    IF p_solo_activos THEN
        v_query := v_query || ' AND estado = TRUE';
    END IF;

    -- Añadir ordenamiento
    IF p_orden IS NOT NULL THEN
        v_query := v_query || ' ORDER BY ' || p_orden;
        IF NOT p_ascendente THEN
            v_query := v_query || ' DESC';
        END IF;
    END IF;

    -- Ejecutar consulta dinámica
    RETURN QUERY EXECUTE v_query;
END;
$$ LANGUAGE plpgsql;
