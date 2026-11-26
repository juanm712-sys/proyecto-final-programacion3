package estrategia;

import modelo.Pedido;

public class CalculoServicioDomicilio implements EstrategiaCalculo {
    private final double distanciaKm;
    private final boolean urgente;

    public CalculoServicioDomicilio(double distanciaKm, boolean urgente) {
        if (distanciaKm < 0) throw new IllegalArgumentException("La distancia no puede ser negativa");
        this.distanciaKm = distanciaKm;
        this.urgente = urgente;
    }

    @Override
    public double calcularTotal(Pedido pedido) {
        double total = pedido.getSubtotal() + 5000 + (distanciaKm * 1000);
        if (urgente) total *= 1.15;
        return total;
    }

    @Override
    public String getNombreModalidad() { return "Servicio técnico a domicilio"; }
}