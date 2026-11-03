package mx.unison;

import mx.unison.modelos.Almacen;
import mx.unison.modelos.Producto;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.Sesion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DominioInventarioTest {
    @AfterEach
    void cerrarSesion() {
        Sesion.usuarioActual = null;
    }

    @Test
    void unaSesionAusenteNoPuedeRealizarAcciones() {
        Sesion.usuarioActual = null;
        assertFalse(Sesion.tieneRol("ADMIN", "PRODUCTO", "ALMACEN"));
        assertThrows(SecurityException.class, () -> Sesion.requerirRol("ADMIN"));
    }

    @Test
    void cadaAccionExigeUnRolAutorizado() {
        Usuario operador = new Usuario("operador", "hash", "PRODUCTO");
        Sesion.usuarioActual = operador;
        assertSame(operador, Sesion.requerirRol("ADMIN", "PRODUCTO"));
        assertFalse(Sesion.tieneRol("ADMIN", "ALMACEN"));
        assertThrows(SecurityException.class, () -> Sesion.requerirRol("ADMIN", "ALMACEN"));

        Sesion.usuarioActual.setRol("DESCONOCIDO");
        assertFalse(Sesion.tieneRol("ADMIN", "PRODUCTO", "ALMACEN"));
        Sesion.usuarioActual.setRol(null);
        assertFalse(Sesion.tieneRol("ADMIN"));
    }

    @Test
    void precioDebeSerFinitoYNoNegativo() {
        Producto producto = new Producto();
        for (double invalido : new double[]{-0.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> producto.setPrecio(invalido));
        }
        producto.setPrecio(0);
        assertEquals(0, producto.getPrecio());
        producto.setPrecio(19.95);
        assertEquals(19.95, producto.getPrecio());
    }

    @Test
    void productoExigeNombreAlmacenYCantidadNoNegativa() {
        Producto producto = new Producto();
        assertThrows(IllegalArgumentException.class, () -> producto.setNombre("  "));
        assertThrows(IllegalArgumentException.class, () -> producto.setNombre(null));
        assertThrows(IllegalArgumentException.class, () -> producto.setAlmacen(null));
        assertThrows(IllegalArgumentException.class, () -> producto.setCantidad(-1));
        producto.setCantidad(0);
        assertEquals(0, producto.getCantidad());
    }

    @Test
    void editarProductoNoMutaOriginalYRegistraAlActorReal() {
        Almacen almacen = new Almacen().conCambios("Principal", "Edificio A", "creador");
        Producto original = new Producto().conCambios("Teclado", 15, 5, almacen, "creador");
        original.setId(7);

        assertThrows(IllegalArgumentException.class,
                () -> original.conCambios("Cambio inválido", Double.NaN, 8, almacen, "operador"));
        assertEquals("Teclado", original.getNombre());
        assertEquals(15, original.getPrecio());
        assertEquals(5, original.getCantidad());

        Producto editado = original.conCambios(" Teclado nuevo ", 20, 8, almacen, "operador");
        assertNotSame(original, editado);
        assertEquals(7, editado.getId());
        assertEquals("Teclado nuevo", editado.getNombre());
        assertEquals("operador", editado.getUltimoUsuario());
        assertEquals("creador", original.getUltimoUsuario());
        assertEquals("Teclado", original.getNombre());
    }

    @Test
    void editarAlmacenConservaCreadorYFechaOriginales() {
        Almacen original = new Almacen().conCambios(" Principal ", " Edificio A ", "almacenista");
        assertEquals("almacenista", original.getCreadoPor());
        assertNotNull(LocalDateTime.parse(original.getFechaHoraCreacion()));
        original.setId(3);

        Almacen editado = original.conCambios("Secundario", "Edificio B", "administrador");
        assertEquals(3, editado.getId());
        assertEquals("almacenista", editado.getCreadoPor());
        assertEquals(original.getFechaHoraCreacion(), editado.getFechaHoraCreacion());
        assertEquals("Principal", original.getNombre());
        assertEquals("Edificio A", original.getUbicacion());
        assertEquals("Secundario", editado.getNombre());
    }

    @Test
    void almacenInvalidoNoModificaOriginal() {
        Almacen original = new Almacen().conCambios("Principal", "Edificio A", "creador");
        original.setId(1);
        assertThrows(IllegalArgumentException.class, () -> original.conCambios("Otro", " ", "operador"));
        assertThrows(IllegalArgumentException.class, () -> original.conCambios(" ", "B", "operador"));
        assertThrows(IllegalArgumentException.class, () -> original.conCambios("Otro", "B", " "));
        assertEquals("Principal", original.getNombre());
        assertEquals("Edificio A", original.getUbicacion());
        assertEquals("creador", original.getCreadoPor());
    }
}
