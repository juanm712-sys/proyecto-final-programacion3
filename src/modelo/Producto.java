package modelo;

public class Producto {
    private final String nombre;
    private final double precio;
    private final Categoria categoria;

    public Producto(String nombre, double precio, Categoria categoria) {
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public Categoria getCategoria() { return categoria; }

    @Override
    public String toString() {
        return String.format("%s [%s] - $%.2f", nombre, categoria, precio);
    }
}