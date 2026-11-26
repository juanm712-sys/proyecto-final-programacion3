package estrategia;

import modelo.Pedido;

public class CalculoRecogida implements EstrategiaCalculo {
    @Override
    public double calcularTotal(Pedido pedido) {
        return pedido.getSubtotal() + 2000;
    }

    @Override
    public String getNombreModalidad() { return "Recogida en tienda"; }
}