package estrategia;

import modelo.Pedido;

public class CalculoDomicilio implements EstrategiaCalculo {
    private final double distanciaKm;

    public CalculoDomicilio(double distanciaKm) {
        if (distanciaKm < 0) throw new IllegalArgumentException("La distancia no puede ser negativa");
        this.distanciaKm = distanciaKm;
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