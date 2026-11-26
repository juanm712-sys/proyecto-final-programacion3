package observador;

import estado.EstadoPedido;
import modelo.Pedido;

public interface ObservadorPedido {
    void actualizar(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo);
}