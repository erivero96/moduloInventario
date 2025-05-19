public void registerProduct(Producto producto) throws SQLException {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (producto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        String sql = "CALL registrar_producto_por_nombre(?, ?, ?, ?, ?);";

        try (Connection conn = getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, producto.getNombre());
            cs.setString(2, producto.getDescripcion());
            cs.setDouble(3, producto.getPrecio());
            cs.setString(4, producto.getCategoria());
            cs.setInt(5, producto.getStock());

            cs.execute();
            System.out.println("Producto registrado correctamente: " + producto.getNombre());

        } catch (SQLException e) {
            System.err.println("Error al registrar producto: " + e.getMessage());
            throw e;
        }
    }

    public void deleteProduct(String nombreProducto) throws SQLException {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        String sql = "CALL eliminar_producto_por_nombre(?);";

        try (Connection conn = getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, nombreProducto);
            cs.execute();
            System.out.println("Producto eliminado correctamente: " + nombreProducto);

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            throw e;
        }
    }