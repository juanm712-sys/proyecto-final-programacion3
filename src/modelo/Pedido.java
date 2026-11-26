package modelo;

import estado.EstadoPedido;
import estrategia.EstrategiaCalculo;
import excepcion.CambioEstadoInvalidoException;
import excepcion.PedidoVacioException;
import observador.ObservadorPedido;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static int contador = 1;

    private final int id;
    private final List<ItemPedido> items;
    private EstrategiaCalculo estrategia;
    private EstadoPedido estado;
    private final List<ObservadorPedido> observadores;

    public Pedido(EstrategiaCalculo estrategia) {
        this.id = contador++;
        this.items = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.estrategia = estrategia;
        this.estado = EstadoPedido.RECIBIDO;
    }

    public int getId() { return id; }
    public EstadoPedido getEstado() { return estado; }
    public EstrategiaCalculo getEstrategia() { return estrategia; }

    public void setEstrategia(EstrategiaCalculo estrategia) { this.estrategia = estrategia; }

    public void agregarItem(ItemPedido item) { items.add(item); }
    public List<ItemPedido> getItems() { return items; }

    public double getSubtotal() {
        return items.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    public double calcularTotal() {
        if (items.isEmpty()) throw new PedidoVacioException("El pedido #" + id + " está vacío");
        return estrategia.calcularTotal(this);
    }

    public void agregarObservador(ObservadorPedido o) { observadores.add(o); }
    public void quitarObservador(ObservadorPedido o) { observadores.remove(o); }

    private void notificar(EstadoPedido anterior, EstadoPedido nuevo) {
        for (ObservadorPedido o : observadores) {
            o.actualizar(this, anterior, nuevo);
        }
    }

    public void cambiarEstado(EstadoPedido nuevo) {
        if (!esTransicionValida(estado, nuevo)) {
            throw new CambioEstadoInvalidoException("Transición inválida: " + estado + " -> " + nuevo);
        }
        EstadoPedido anterior = this.estado;
        this.estado = nuevo;
        notificar(anterior, nuevo);
    }

    private boolean esTransicionValida(EstadoPedido actual, EstadoPedido nuevo) {
        return switch (actual) {
            case RECIBIDO -> nuevo == EstadoPedido.EN_DIAGNOSTICO || nuevo == EstadoPedido.LISTO;
            case EN_DIAGNOSTICO -> nuevo == EstadoPedido.EN_REPARACION;
            case EN_REPARACION -> nuevo == EstadoPedido.LISTO;
            case LISTO -> nuevo == EstadoPedido.ENTREGADO;
            case ENTREGADO -> false;
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(id)
          .append(" | Estado: ").append(estado)
          .append(" | Modalidad: ").append(estrategia.getNombreModalidad()).append("\n");
        items.forEach(i -> sb.append("  - ").append(i).append("\n"));
        sb.append("  Subtotal: $").append(getSubtotal()).append("\n");
        sb.append("  TOTAL: $").append(calcularTotal());
        return sb.toString();
    }
}