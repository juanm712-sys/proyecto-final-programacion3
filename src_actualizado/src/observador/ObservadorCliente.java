package observador;

import estado.EstadoPedido;
import modelo.Pedido;

public class ObservadorCliente implements ObservadorPedido {
    private final String nombre;

    public ObservadorCliente(String nombre) { this.nombre = nombre; }

    @Override
    public void actualizar(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo) {
        System.out.println("[CLIENTE " + nombre + "] Su pedido #" + pedido.getId()
                + " cambió a: " + nuevo);
    }
}