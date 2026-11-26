package excepcion;

public class PedidoVacioException extends RuntimeException {
    public PedidoVacioException(String msg) { super(msg); }
}