CREATE OR REPLACE PROCEDURE login_usuario(
    IN p_correo VARCHAR,
    IN p_contraseña VARCHAR,
    OUT p_id CHAR(8),
    OUT p_rol VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    SELECT id, rol
    INTO p_id, p_rol
    FROM usuarios
    WHERE correo = p_correo AND contraseña = p_contraseña;

    IF NOT FOUND THEN
        -- Si no hay coincidencia, los valores de salida se ponen en NULL
        p_id := NULL;
        p_rol := NULL;
    END IF;
END;
$$;
