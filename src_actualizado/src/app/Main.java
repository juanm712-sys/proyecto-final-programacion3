package app;

import estado.EstadoPedido;
import estrategia.*;
import excepcion.PedidoVacioException;
import modelo.*;
import observador.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final List<Producto> catalogo = new ArrayList<>();
    private static final List<Pedido> pedidos = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    // Un único taller compartido, para que pueda listar los pendientes de todos los pedidos
    private static final ObservadorTaller taller = new ObservadorTaller();

    public static void main(String[] args) {
        cargarCatalogoDemo();
        int opcion = -1;
        do {
            System.out.println("\n===== CICLO VICTORIA =====");
            System.out.println("1. Registrar producto/servicio");
            System.out.println("2. Crear pedido");
            System.out.println("3. Cambiar estado de un pedido");
            System.out.println("4. Listar pedidos por estado");
            System.out.println("5. Resumen del día");
            System.out.println("6. Agregar/quitar observador de un pedido");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
                switch (opcion) {
                    case 1 -> registrarProducto();
                    case 2 -> crearPedido();
                    case 3 -> cambiarEstado();
                    case 4 -> listarPorEstado();
                    case 5 -> resumenDia();
                    case 6 -> gestionarObservadores();
                    case 0 -> System.out.println("Hasta pronto.");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: ingrese un número válido.");
                opcion = -1;
            } catch (NoSuchElementException e) {
                System.out.println("Entrada finalizada. Hasta pronto.");
                opcion = 0;
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un número entero válido.");
        }
    }

    private static double leerDecimal(String mensaje) {
        System.out.print(mensaje);
        try {
            return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un número válido.");
        }
    }

    private static void cargarCatalogoDemo() {
        catalogo.add(new Producto("Cadena MTB", 45000, Categoria.REPUESTO));
        catalogo.add(new Producto("Bicicleta GW Alloy", 850000, Categoria.BICICLETA));
        catalogo.add(new Producto("Mantenimiento básico", 60000, Categoria.MANTENIMIENTO));
        catalogo.add(new Producto("Arreglo de pinchazo", 15000, Categoria.ARREGLO));
    }

    private static void registrarProducto() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío");
        double precio = leerDecimal("Precio: ");
        System.out.println("Categorías: 1) REPUESTO 2) BICICLETA 3) MANTENIMIENTO 4) ARREGLO");
        int c = leerEntero("Categoría: ");
        Categoria cat = switch (c) {
            case 1 -> Categoria.REPUESTO;
            case 2 -> Categoria.BICICLETA;
            case 3 -> Categoria.MANTENIMIENTO;
            case 4 -> Categoria.ARREGLO;
            default -> throw new IllegalArgumentException("Categoría inválida");
        };
        catalogo.add(new Producto(nombre, precio, cat));
        System.out.println("Producto registrado.");
    }

    private static void crearPedido() {
        if (catalogo.isEmpty()) { System.out.println("Catálogo vacío."); return; }

        System.out.println("Seleccione modalidad:");
        System.out.println("1) Tienda  2) Domicilio  3) Recogida  4) Servicio a domicilio");
        int m = leerEntero("Modalidad: ");

        EstrategiaCalculo estrategia;
        switch (m) {
            case 1 -> estrategia = new CalculoTienda();
            case 2 -> {
                double km = leerDecimal("Distancia (km): ");
                estrategia = new CalculoDomicilio(km);
            }
            case 3 -> estrategia = new CalculoRecogida();
            case 4 -> {
                double km = leerDecimal("Distancia (km): ");
                System.out.print("¿Urgente? (s/n): ");
                boolean urg = sc.nextLine().trim().equalsIgnoreCase("s");
                estrategia = new CalculoServicioDomicilio(km, urg);
            }
            default -> throw new IllegalArgumentException("Modalidad inválida");
        }

        // Los productos se juntan primero; el Pedido solo se crea si hay al menos uno
        List<ItemPedido> seleccionados = new ArrayList<>();
        while (true) {
            for (int i = 0; i < catalogo.size(); i++) {
                System.out.println((i + 1) + ". " + catalogo.get(i));
            }
            int idx = leerEntero("Elija producto (0 para terminar): ");
            if (idx == 0) break;
            if (idx < 1 || idx > catalogo.size()) { System.out.println("Ítem inválido."); continue; }
            try {
                int cant = leerEntero("Cantidad: ");
                seleccionados.add(new ItemPedido(catalogo.get(idx - 1), cant));
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        if (seleccionados.isEmpty()) {
            throw new PedidoVacioException("No se puede crear un pedido sin productos");
        }

        Pedido pedido = new Pedido(estrategia);
        seleccionados.forEach(pedido::agregarItem);
        pedido.agregarObservador(taller);
        pedido.agregarObservador(new ObservadorCaja());
        pedido.agregarObservador(new ObservadorCliente("Cliente" + pedido.getId()));
        pedidos.add(pedido);

        System.out.printf("TOTAL estimado: $%.2f%n", pedido.calcularTotal());
        System.out.println("Pedido #" + pedido.getId() + " creado.");
        // El estado inicial (RECIBIDO) también se avisa al taller
        taller.actualizar(pedido, null, EstadoPedido.RECIBIDO);
    }

    private static Pedido buscarPedido() {
        int id = leerEntero("ID del pedido: ");
        return pedidos.stream().filter(x -> x.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    private static void cambiarEstado() {
        if (pedidos.isEmpty()) { System.out.println("No hay pedidos."); return; }
        Pedido p = buscarPedido();
        System.out.println("Estado actual: " + p.getEstado());
        System.out.println("Nuevo estado: 1) RECIBIDO 2) EN_DIAGNOSTICO 3) EN_REPARACION 4) LISTO 5) ENTREGADO");
        int e = leerEntero("Nuevo estado: ");
        EstadoPedido nuevo = switch (e) {
            case 1 -> EstadoPedido.RECIBIDO;
            case 2 -> EstadoPedido.EN_DIAGNOSTICO;
            case 3 -> EstadoPedido.EN_REPARACION;
            case 4 -> EstadoPedido.LISTO;
            case 5 -> EstadoPedido.ENTREGADO;
            default -> throw new IllegalArgumentException("Estado inválido");
        };
        p.cambiarEstado(nuevo);
        System.out.println("Estado actualizado.");
    }

    private static void listarPorEstado() {
        System.out.println("Filtrar por: 1) RECIBIDO 2) EN_DIAGNOSTICO 3) EN_REPARACION 4) LISTO 5) ENTREGADO");
        int e = leerEntero("Estado: ");
        if (e < 1 || e > EstadoPedido.values().length) throw new IllegalArgumentException("Estado inválido");
        EstadoPedido filtro = EstadoPedido.values()[e - 1];
        List<Pedido> filtrados = pedidos.stream().filter(p -> p.getEstado() == filtro).toList();
        if (filtrados.isEmpty()) {
            System.out.println("No hay pedidos en estado " + filtro + ".");
        } else {
            filtrados.forEach(System.out::println);
        }
    }

    private static void gestionarObservadores() {
        if (pedidos.isEmpty()) { System.out.println("No hay pedidos."); return; }
        Pedido p = buscarPedido();
        System.out.println("1) Agregar Taller   2) Agregar Caja   3) Agregar Cliente");
        System.out.println("4) Quitar Taller    5) Quitar Caja    6) Quitar Cliente");
        int o = leerEntero("Opción: ");
        switch (o) {
            case 1 -> { if (!p.tieneObservador(ObservadorTaller.class)) p.agregarObservador(taller); }
            case 2 -> { if (!p.tieneObservador(ObservadorCaja.class)) p.agregarObservador(new ObservadorCaja()); }
            case 3 -> { if (!p.tieneObservador(ObservadorCliente.class)) p.agregarObservador(new ObservadorCliente("Cliente" + p.getId())); }
            case 4 -> p.quitarObservadorPorTipo(ObservadorTaller.class);
            case 5 -> p.quitarObservadorPorTipo(ObservadorCaja.class);
            case 6 -> p.quitarObservadorPorTipo(ObservadorCliente.class);
            default -> throw new IllegalArgumentException("Opción inválida");
        }
        System.out.println("Observadores del pedido #" + p.getId() + " actualizados: "
                + "Taller=" + p.tieneObservador(ObservadorTaller.class)
                + ", Caja=" + p.tieneObservador(ObservadorCaja.class)
                + ", Cliente=" + p.tieneObservador(ObservadorCliente.class));
    }

    private static void resumenDia() {
        double total = 0;
        Map<String, Integer> porModalidad = new LinkedHashMap<>();
        for (Pedido p : pedidos) {
            if (p.getEstado() == EstadoPedido.ENTREGADO) {
                total += p.calcularTotal();
            }
            porModalidad.merge(p.getEstrategia().getNombreModalidad(), 1, Integer::sum);
        }
        System.out.printf("Total vendido (pedidos entregados): $%.2f%n", total);
        System.out.println("Pedidos registrados: " + pedidos.size());
        if (porModalidad.isEmpty()) {
            System.out.println("Aún no hay pedidos por modalidad.");
        } else {
            porModalidad.forEach((modalidad, cantidad) ->
                    System.out.println("Pedidos " + modalidad + ": " + cantidad));
        }
    }
}
