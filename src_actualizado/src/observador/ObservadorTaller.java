package observador;

import estado.EstadoPedido;
import modelo.Pedido;

import java.util.Set;
import java.util.TreeSet;

public class ObservadorTaller implements ObservadorPedido {
    private final Set<Integer> pendientes = new TreeSet<>();

    @Override
    public void actualizar(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo) {
        if (nuevo == EstadoPedido.LISTO || nuevo == EstadoPedido.ENTREGADO) {
            pendientes.remove(pedido.getId());
        } else {
            pendientes.add(pedido.getId());
        }

        if (nuevo == EstadoPedido.RECIBIDO || nuevo == EstadoPedido.EN_DIAGNOSTICO) {
            System.out.println("[TALLER] Pedido #" + pedido.getId() + " pendiente por preparar/diagnosticar.");
        } else {
            System.out.println("[TALLER] Pedido #" + pedido.getId() + " ahora está: " + nuevo);
        }
        System.out.println("[TALLER] Pedidos pendientes en el taller: " + (pendientes.isEmpty() ? "ninguno" : pendientes));
    }
}
