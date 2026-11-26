# Sistema de Gestión - Ciclo Victoria 🚲

Proyecto final para la asignatura **Programación III**. 
Sistema de gestión de ventas y servicios para un local de bicicletas, implementando los patrones de diseño **Strategy** y **Observer**.

## Adaptación del Proyecto
El enunciado original planteaba un restaurante. Este proyecto adapta la lógica al dominio de una tienda de bicicletas ("Ciclo Victoria"):
- **Menú** ➔ Catálogo de Repuestos, Bicicletas, Mantenimientos y Arreglos.
- **Modalidades de pedido** ➔ Venta en tienda, Domicilio, Recogida y Servicio técnico a domicilio.
- **Estados del pedido** ➔ Recibido, En diagnóstico, En reparación, Listo, Entregado.
- **Observadores** ➔ Taller (cocina), Caja y Cliente.

## Patrones de Diseño Implementados

### 1. Strategy (Comportamiento)
Se utiliza para calcular el costo final del pedido según la modalidad seleccionada. Permite agregar nuevas modalidades sin modificar la clase `Pedido`.
- **Interfaz:** `EstrategiaCalculo`
- **Implementaciones:** 
  - `CalculoTienda` (Subtotal + 10% propina).
  - `CalculoDomicilio` (Subtotal + envío por distancia).
  - `CalculoRecogida` (Subtotal + empaque).
  - `CalculoServicioDomicilio` (Subtotal + desplazamiento + recargo por urgencia).

### 2. Observer (Comportamiento)
Se utiliza para notificar automáticamente a los interesados cuando un pedido cambia de estado. Permite suscribir y desuscribir observadores en tiempo de ejecución.
- **Interfaz:** `ObservadorPedido`
- **Implementaciones:**
  - `ObservadorTaller`: Notifica a la cocina/taller los pedidos pendientes.
  - `ObservadorCaja`: Registra el pago cuando el pedido pasa a "ENTREGADO".
  - `ObservadorCliente`: Informa al cliente sobre el estado de su pedido.

## Requisitos Técnicos Cumplidos
- **Lenguaje:** Java (JDK 17+).
- **POO:** Uso de interfaces, herencia, encapsulamiento y polimorfismo.
- **Excepciones:** Manejo de pedidos vacíos (`PedidoVacioException`) y cambios de estado inválidos (`CambioEstadoInvalidoException`).
- **Interfaz:** Menú interactivo por consola.

## Cómo ejecutar el proyecto
1. Clonar el repositorio.
2. Abrir en VS Code (con la extensión de Java instalada).
3. Abrir el archivo `src/app/Main.java` y darle clic en el botón **Run** (▶).
4. Alternativamente, desde la terminal en la raíz del proyecto:
   ```bash
   javac -d bin src\estado\*.java src\modelo\*.java src\estrategia\*.java src\observador\*.java src\excepcion\*.java src\app\*.java
   java -cp bin app.Main
