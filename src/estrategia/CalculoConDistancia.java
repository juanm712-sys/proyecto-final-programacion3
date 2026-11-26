package estrategia;

public abstract class CalculoConDistancia implements EstrategiaCalculo {
    protected final double distanciaKm;

    protected CalculoConDistancia(double distanciaKm) {
        if (distanciaKm < 0) throw new IllegalArgumentException("La distancia no puede ser negativa");
        this.distanciaKm = distanciaKm;
    }

    public double getDistanciaKm() { return distanciaKm; }
}
