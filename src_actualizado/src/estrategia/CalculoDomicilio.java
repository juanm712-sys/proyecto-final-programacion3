package estrategia;

import modelo.Pedido;

public class CalculoDomicilio extends CalculoConDistancia {

    public CalculoDomicilio(double distanciaKm) {
        super(distanciaKm);
    }

    @Override
    public double calcularTotal(Pedido pedido) {
        double envio = 3000;
        if (distanciaKm > 3) {
            envio += (distanciaKm - 3) * 1000;
        }
        return pedido.getSubtotal() + envio;
    }

    @Override
    public String getNombreModalidad() { return "Domicilio"; }
}
