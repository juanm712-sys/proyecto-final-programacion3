package observador;

import estado.EstadoPedido;
import modelo.Pedido;

public class ObservadorTaller implements ObservadorPedido {
    @Override
    public void actualizar(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo) {
        if (nuevo == EstadoPedido.RECIBIDO || nuevo == EstadoPedido.EN_DIAGNOSTICO) {
            System.out.println("[TALLER] Pedido #" + pedido.getId() + " pendiente por preparar/diagnosticar.");
        }
    }
}