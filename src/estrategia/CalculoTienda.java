package estrategia;

import modelo.Pedido;

public class CalculoTienda implements EstrategiaCalculo {
    @Override
    public double calcularTotal(Pedido pedido) {
        return pedido.getSubtotal() * 1.10;
    }

    @Override
    public String getNombreModalidad() { return "Venta en tienda"; }
}