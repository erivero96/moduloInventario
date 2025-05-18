import static org.junit.Assert.*;

import org.example.InventarioDao;
import org.junit.Test;

public class cambiarEstadoProductoTest {
    @Test
    public void testCambiarEstadoProductoExitoso() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 1; // Asegúrate que este codigo existe
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado);
            boolean estadoActual = inventarioDao.obtenerEstadoProducto(codigo);
            assertEquals("El estado del producto no se actualizó correctamente.", nuevoEstado, estadoActual);
        } catch (Exception e) {
            fail("Se produjo una excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdInvalido() {
        InventarioDao inventarioDao = new InventarioDao();
        int idInvalido = -1;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(idInvalido, nuevoEstado);
            fail("Se esperaba una excepción por codigo inválido.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("no encontrado") ||
                            e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoActualIgual() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 2; // Asegúrate que este codigo existe
        boolean estadoActual = inventarioDao.obtenerEstadoProducto(codigo);

        try {
            inventarioDao.cambiarEstadoProducto(codigo, estadoActual);
            boolean estadoDespues = inventarioDao.obtenerEstadoProducto(codigo);
            assertEquals("El estado del producto cambió cuando no debería.", estadoActual, estadoDespues);
        } catch (Exception e) {
            fail("Se produjo una excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdNulo() {
        InventarioDao inventarioDao = new InventarioDao();
        Integer idNulo = null;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(idNulo, nuevoEstado);
            fail("Se esperaba una excepción por codigo nulo.");
        } catch (NullPointerException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("codigo no puede ser nulo"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoTrue() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 3; // Asegúrate que este codigo existe
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado);
            boolean estadoActual = inventarioDao.obtenerEstadoProducto(codigo);
            assertEquals("El estado del producto no se actualizó correctamente a true.", nuevoEstado, estadoActual);
        } catch (Exception e) {
            fail("Se produjo una excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoFalse() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 4; // Asegúrate que este codigo existe
        boolean nuevoEstado = false;

        try {
            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado);
            boolean estadoActual = inventarioDao.obtenerEstadoProducto(codigo);
            assertEquals("El estado del producto no se actualizó correctamente a false.", nuevoEstado, estadoActual);
        } catch (Exception e) {
            fail("Se produjo una excepción inesperada: " + e.getMessage());
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdMaximo() {
        InventarioDao inventarioDao = new InventarioDao();
        int idMaximo = Integer.MAX_VALUE;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(idMaximo, nuevoEstado);
            fail("Se esperaba una excepción por codigo fuera de rango.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("fuera de rango"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdMinimo() {
        InventarioDao inventarioDao = new InventarioDao();
        int idMinimo = Integer.MIN_VALUE;
        boolean nuevoEstado = false;

        try {
            inventarioDao.cambiarEstadoProducto(idMinimo, nuevoEstado);
            fail("Se esperaba una excepción por codigo fuera de rango.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("fuera de rango"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdCero() {
        InventarioDao inventarioDao = new InventarioDao();
        int idCero = 0;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(idCero, nuevoEstado);
            fail("Se esperaba una excepción por codigo inválido.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoNull() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 5; // Asegúrate que este codigo existe
        Boolean nuevoEstado = null;

        try {
            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado);
            fail("Se esperaba una excepción por estado nulo.");
        } catch (NullPointerException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("estado no puede ser nulo"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdNoExistente() {
        InventarioDao inventarioDao = new InventarioDao();
        int idNoExistente = 9999; // Asegúrate que este codigo no existe
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(idNoExistente, nuevoEstado);
            fail("Se esperaba una excepción por codigo no existente.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("no encontrado"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdNegativo() {
        InventarioDao inventarioDao = new InventarioDao();
        int idNegativo = -10;
        boolean nuevoEstado = false;

        try {
            inventarioDao.cambiarEstadoProducto(idNegativo, nuevoEstado);
            fail("Se esperaba una excepción por codigo negativo.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoCadena() {
        InventarioDao inventarioDao = new InventarioDao();
        String idCadena = "abc";
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idCadena), nuevoEstado);
            fail("Se esperaba una excepción por codigo no numérico.");
        } catch (NumberFormatException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoDecimal() {
        InventarioDao inventarioDao = new InventarioDao();
        double idDecimal = 1.5;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto((int) idDecimal, nuevoEstado);
            fail("Se esperaba una excepción por codigo decimal.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdMuyGrande() {
        InventarioDao inventarioDao = new InventarioDao();
        long idMuyGrande = Long.MAX_VALUE;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto((int) idMuyGrande, nuevoEstado);
            fail("Se esperaba una excepción por codigo demasiado grande.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("fuera de rango"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoEspacio() {
        InventarioDao inventarioDao = new InventarioDao();
        String idEspacio = " ";
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idEspacio), nuevoEstado);
            fail("Se esperaba una excepción por codigo no numérico.");
        } catch (NumberFormatException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoCaracterEspecial() {
        InventarioDao inventarioDao = new InventarioDao();
        String idCaracterEspecial = "@";
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idCaracterEspecial), nuevoEstado);
            fail("Se esperaba una excepción por codigo no numérico.");
        } catch (NumberFormatException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoCadenaVacia() {
        InventarioDao inventarioDao = new InventarioDao();
        String idCadenaVacia = "";
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idCadenaVacia), nuevoEstado);
            fail("Se esperaba una excepción por codigo no numérico.");
        } catch (NumberFormatException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoBoolean() {
        InventarioDao inventarioDao = new InventarioDao();
        boolean idBoolean = true;
        boolean nuevoEstado = false;

        try {
            inventarioDao.cambiarEstadoProducto((int) (idBoolean ? 1 : 0), nuevoEstado);
            fail("Se esperaba una excepción por codigo inválido.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoObjeto() {
        InventarioDao inventarioDao = new InventarioDao();
        Object idObjeto = new Object();
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto((int) idObjeto, nuevoEstado);
            fail("Se esperaba una excepción por codigo inválido.");
        } catch (ClassCastException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("no se puede convertir"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoCadena() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 6; // Asegúrate que este codigo existe
        String nuevoEstado = "true";

        try {
            inventarioDao.cambiarEstadoProducto(codigo, Boolean.parseBoolean(nuevoEstado));
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoNumero() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 7; // Asegúrate que este codigo existe
        int nuevoEstado = 1;

        try {
            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado == 1);
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoObjeto() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 8; // Asegúrate que este codigo existe
        Object nuevoEstado = new Object();

        try {
            inventarioDao.cambiarEstadoProducto(codigo, (boolean) nuevoEstado);
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (ClassCastException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("no se puede convertir"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoLista() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 9; // Asegúrate que este codigo existe
        Object nuevoEstado = java.util.Arrays.asList(true);

        try {
            inventarioDao.cambiarEstadoProducto(codigo, (boolean) nuevoEstado);
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (ClassCastException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("no se puede convertir"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoMapa() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 10; // Asegúrate que este codigo existe
        Object nuevoEstado = java.util.Collections.singletonMap("estado", true);

        try {
            inventarioDao.cambiarEstadoProducto(codigo, (boolean) nuevoEstado);
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (ClassCastException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("no se puede convertir"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoArreglo() {
        InventarioDao inventarioDao = new InventarioDao();
        int[] idArreglo = {1, 2, 3};
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(idArreglo[0], nuevoEstado);
            fail("Se esperaba una excepción por codigo inválido.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoArreglo() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 11; // Asegúrate que este codigo existe
        boolean[] nuevoEstado = {true, false};

        try {
            inventarioDao.cambiarEstadoProducto(codigo, nuevoEstado[0]);
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoNullString() {
        InventarioDao inventarioDao = new InventarioDao();
        String idNullString = null;
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idNullString), nuevoEstado);
            fail("Se esperaba una excepción por codigo nulo.");
        } catch (NullPointerException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("codigo no puede ser nulo"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoNullString() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 12; // Asegúrate que este codigo existe
        String nuevoEstado = null;

        try {
            inventarioDao.cambiarEstadoProducto(codigo, Boolean.parseBoolean(nuevoEstado));
            fail("Se esperaba una excepción por estado nulo.");
        } catch (NullPointerException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("estado no puede ser nulo"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoDecimalString() {
        InventarioDao inventarioDao = new InventarioDao();
        String idDecimalString = "1.5";
        boolean nuevoEstado = true;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idDecimalString), nuevoEstado);
            fail("Se esperaba una excepción por codigo no numérico.");
        } catch (NumberFormatException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoDecimalString() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 13; // Asegúrate que este codigo existe
        String nuevoEstado = "1.0";

        try {
            inventarioDao.cambiarEstadoProducto(codigo, Boolean.parseBoolean(nuevoEstado));
            fail("Se esperaba una excepción por estado no booleano.");
        } catch (RuntimeException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConIdComoBooleanString() {
        InventarioDao inventarioDao = new InventarioDao();
        String idBooleanString = "true";
        boolean nuevoEstado = false;

        try {
            inventarioDao.cambiarEstadoProducto(Integer.parseInt(idBooleanString), nuevoEstado);
            fail("Se esperaba una excepción por codigo no numérico.");
        } catch (NumberFormatException e) {
            assertTrue("El mensaje de error no es el esperado.",
                    e.getMessage().toLowerCase().contains("formato inválido"));
        }
    }

    @Test
    public void testCambiarEstadoProductoConEstadoComoBooleanString() {
        InventarioDao inventarioDao = new InventarioDao();
        int codigo = 14; // Asegúrate que este codigo existe
        String nuevoEstado = "true";

        try {
            inventarioDao.cambiarEstadoProducto(codigo, Boolean.parseBoolean(nuevoEstado));
            boolean estadoActual = inventarioDao.obtenerEstadoProducto(codigo);
            assertEquals("El estado del producto no se actualizó correctamente.", true, estadoActual);
        } catch (Exception e) {
            fail("Se produjo una excepción inesperada: " + e.getMessage());
        }
    }

}

