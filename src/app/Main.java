package app;

import estado.EstadoPedido;
import estrategia.*;
import modelo.*;
import observador.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<Producto> catalogo = new ArrayList<>();
    private static final List<Pedido> pedidos = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        cargarCatalogoDemo();
        int opcion;
        do {
            System.out.println("\n===== CICLO VICTORIA =====");
            System.out.println("1. Registrar producto/servicio");
            System.out.println("2. Crear pedido");
            System.out.println("3. Cambiar estado de un pedido");
            System.out.println("4. Listar pedidos por estado");
            System.out.println("5. Resumen del día");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(sc.nextLine());
            try {
                switch (opcion) {
                    case 1 -> registrarProducto();
                    case 2 -> crearPedido();
                    case 3 -> cambiarEstado();
                    case 4 -> listarPorEstado();
                    case 5 -> resumenDia();
                    case 0 -> System.out.println("Hasta pronto.");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void cargarCatalogoDemo() {
        catalogo.add(new Producto("Cadena MTB", 45000, Categoria.REPUESTO));
        catalogo.add(new Producto("Bicicleta GW Alloy", 850000, Categoria.BICICLETA));
        catalogo.add(new Producto("Mantenimiento básico", 60000, Categoria.MANTENIMIENTO));
    }

    private static void registrarProducto() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Precio: ");
        double precio = Double.parseDouble(sc.nextLine());
        System.out.println("Categorías: 1) REPUESTO 2) BICICLETA 3) MANTENIMIENTO 4) ARREGLO");
        int c = Integer.parseInt(sc.nextLine());
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
        int m = Integer.parseInt(sc.nextLine());

        EstrategiaCalculo estrategia;
        switch (m) {
            case 1 -> estrategia = new CalculoTienda();
            case 2 -> {
                System.out.print("Distancia (km): ");
                double km = Double.parseDouble(sc.nextLine());
                estrategia = new CalculoDomicilio(km);
            }
            case 3 -> estrategia = new CalculoRecogida();
            case 4 -> {
                System.out.print("Distancia (km): ");
                double km = Double.parseDouble(sc.nextLine());
                System.out.print("¿Urgente? (s/n): ");
                boolean urg = sc.nextLine().equalsIgnoreCase("s");
                estrategia = new CalculoServicioDomicilio(km, urg);
            }
            default -> throw new IllegalArgumentException("Modalidad inválida");
        }

        Pedido pedido = new Pedido(estrategia);
        pedido.agregarObservador(new ObservadorTaller());
        pedido.agregarObservador(new ObservadorCaja());
        pedido.agregarObservador(new ObservadorCliente("Cliente" + pedido.getId()));

        boolean seguir = true;
        while (seguir) {
            for (int i = 0; i < catalogo.size(); i++) {
                System.out.println((i + 1) + ". " + catalogo.get(i));
            }
            System.out.print("Elija producto (0 para terminar): ");
            int idx = Integer.parseInt(sc.nextLine());
            if (idx == 0) break;
            if (idx < 1 || idx > catalogo.size()) { System.out.println("Ítem inválido."); continue; }
            System.out.print("Cantidad: ");
            int cant = Integer.parseInt(sc.nextLine());
            pedido.agregarItem(new ItemPedido(catalogo.get(idx - 1), cant));
        }

        try {
            System.out.printf("TOTAL estimado: $%.2f%n", pedido.calcularTotal());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }
        pedidos.add(pedido);
        System.out.println("Pedido #" + pedido.getId() + " creado.");
    }

    private static void cambiarEstado() {
        if (pedidos.isEmpty()) { System.out.println("No hay pedidos."); return; }
        System.out.print("ID del pedido: ");
        int id = Integer.parseInt(sc.nextLine());
        Pedido p = pedidos.stream().filter(x -> x.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        System.out.println("Estado actual: " + p.getEstado());
        System.out.println("Nuevo estado: 1) RECIBIDO 2) EN_DIAGNOSTICO 3) EN_REPARACION 4) LISTO 5) ENTREGADO");
        int e = Integer.parseInt(sc.nextLine());
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
        int e = Integer.parseInt(sc.nextLine());
        EstadoPedido filtro = EstadoPedido.values()[e - 1];
        pedidos.stream().filter(p -> p.getEstado() == filtro).forEach(System.out::println);
    }

    private static void resumenDia() {
        double total = 0;
        int[] porModalidad = new int[4];
        for (Pedido p : pedidos) {
            total += p.calcularTotal();
            if (p.getEstrategia() instanceof CalculoTienda) porModalidad[0]++;
            else if (p.getEstrategia() instanceof CalculoDomicilio) porModalidad[1]++;
            else if (p.getEstrategia() instanceof CalculoRecogida) porModalidad[2]++;
            else if (p.getEstrategia() instanceof CalculoServicioDomicilio) porModalidad[3]++;
        }
        System.out.printf("Total vendido: $%.2f%n", total);
        System.out.println("Pedidos tienda: " + porModalidad[0]);
        System.out.println("Pedidos domicilio: " + porModalidad[1]);
        System.out.println("Pedidos recogida: " + porModalidad[2]);
        System.out.println("Pedidos servicio a domicilio: " + porModalidad[3]);
    }
}