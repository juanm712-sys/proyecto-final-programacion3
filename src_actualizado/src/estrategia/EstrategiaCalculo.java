package estrategia;

import modelo.Pedido;

public interface EstrategiaCalculo {
    double calcularTotal(Pedido pedido);
    String getNombreModalidad();
}