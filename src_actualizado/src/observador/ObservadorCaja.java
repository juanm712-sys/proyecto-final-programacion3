package observador;

import estado.EstadoPedido;
import modelo.Pedido;

public class ObservadorCaja implements ObservadorPedido {
    @Override
    public void actualizar(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo) {
        if (nuevo == EstadoPedido.ENTREGADO) {
            System.out.printf("[CAJA] Pago registrado del pedido #%d por $%.2f%n",
                    pedido.getId(), pedido.calcularTotal());
        }
    }
}