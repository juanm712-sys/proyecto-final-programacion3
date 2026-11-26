# Sistema de Gestión - Ciclo Victoria 🚲

Proyecto final para la asignatura **Programación III** (Universidad de Córdoba).
Sistema de gestión de ventas y servicios para un local de bicicletas, implementando los patrones de diseño **Strategy** y **Observer**.

**Autor:** Juan Elias Lopez Espitia

## Adaptación del Proyecto
El enunciado original planteaba un restaurante. Este proyecto adapta la lógica al dominio de una tienda de bicicletas ("Ciclo Victoria"):
- **Menú** ➔ Catálogo de Repuestos, Bicicletas, Mantenimientos y Arreglos.
- **Modalidades de pedido** ➔ Venta en tienda, Domicilio, Recogida y Servicio técnico a domicilio.
- **Estados del pedido** ➔ Recibido, En diagnóstico, En reparación, Listo, Entregado.
- **Observadores** ➔ Taller (cocina), Caja y Cliente.

## Estructura del proyecto
```
src/
├── app/          Main.java (menú interactivo por consola)
├── estado/       EstadoPedido
├── estrategia/   EstrategiaCalculo, CalculoConDistancia (abstracta),
│                 CalculoTienda, CalculoDomicilio, CalculoRecogida, CalculoServicioDomicilio
├── excepcion/    PedidoVacioException, CambioEstadoInvalidoException
├── modelo/       Pedido, ItemPedido, Producto, Categoria
└── observador/   ObservadorPedido, ObservadorTaller, ObservadorCaja, ObservadorCliente
```
El diagrama de clases UML se encuentra en el archivo `DiagramaUML.png`.

## Patrones de Diseño Implementados

### 1. Strategy (Comportamiento)
Se utiliza para calcular el costo final del pedido según la modalidad seleccionada. Permite agregar nuevas modalidades sin modificar la clase `Pedido`.
- **Interfaz:** `EstrategiaCalculo`
- **Implementaciones:**
  - `CalculoTienda`: subtotal + 10 % de propina sugerida.
  - `CalculoRecogida`: subtotal + $2.000 por empaque.
  - `CalculoDomicilio`: subtotal + $3.000 hasta 3 km, y $1.000 por cada km adicional.
  - `CalculoServicioDomicilio`: subtotal + $5.000 de desplazamiento + $1.000 por km + 15 % de recargo si es urgente.
- `CalculoDomicilio` y `CalculoServicioDomicilio` **heredan** de la clase abstracta `CalculoConDistancia`, que valida y guarda la distancia.

### 2. Observer (Comportamiento)
Se utiliza para notificar automáticamente a los interesados cuando un pedido cambia de estado. Permite suscribir y desuscribir observadores **en tiempo de ejecución** (opción 6 del menú).
- **Interfaz:** `ObservadorPedido`
- **Implementaciones:**
  - `ObservadorTaller`: avisa los pedidos pendientes por preparar o diagnosticar y muestra la lista de pendientes.
  - `ObservadorCaja`: registra el pago cuando el pedido pasa a `ENTREGADO`.
  - `ObservadorCliente`: informa al cliente el nuevo estado de su pedido.

## Requisitos Técnicos Cumplidos
- **Lenguaje:** Java (JDK 17 o superior).
- **POO:** interfaces, herencia (clase abstracta `CalculoConDistancia`), encapsulamiento y polimorfismo.
- **Excepciones personalizadas:** `PedidoVacioException` (pedido sin productos) y `CambioEstadoInvalidoException` (saltos de estado no permitidos).
- **Interfaz:** menú interactivo por consola.

## Flujo de estados
`RECIBIDO → EN_DIAGNOSTICO → EN_REPARACION → LISTO → ENTREGADO`

También se permite `RECIBIDO → LISTO` para ventas de productos que no requieren diagnóstico ni reparación. Cualquier otro salto lanza `CambioEstadoInvalidoException`.

## Cómo ejecutar el proyecto

**Requisito:** tener instalado el JDK 17 o superior (`javac -version` debe funcionar).

**Opción A - VS Code:** abrir la carpeta del proyecto (con la extensión de Java instalada), abrir `src/app/Main.java` y pulsar **Run** (▶).

**Opción B - Terminal**, desde la raíz del proyecto (donde está la carpeta `src`):

```bash
javac -d bin src/estado/*.java src/modelo/*.java src/estrategia/*.java src/observador/*.java src/excepcion/*.java src/app/*.java
java -cp bin app.Main
```

> En Windows PowerShell o CMD, si el comando anterior no funciona, usar barras invertidas: `src\estado\*.java`, etc.

## Guía rápida de pruebas
El catálogo ya trae 4 productos de ejemplo (uno por categoría).

| Qué probar | Cómo hacerlo |
|---|---|
| **Strategy** | Opción **2**: crear pedidos con cada modalidad (Tienda, Domicilio, Recogida, Servicio a domicilio) y comparar el `TOTAL estimado`. |
| **Observer** | Opción **3**: cambiar el estado de un pedido y ver los mensajes de Taller, Cliente y, al llegar a `ENTREGADO`, de Caja. |
| **Observadores en tiempo de ejecución** | Opción **6**: quitar (por ejemplo) el Cliente de un pedido y luego cambiar su estado con la opción 3: ya no aparece el mensaje del cliente. |
| **`CambioEstadoInvalidoException`** | Opción **3**: pasar un pedido de `EN_DIAGNOSTICO` directamente a `LISTO` o `ENTREGADO`. |
| **`PedidoVacioException`** | Opción **2**: elegir modalidad y escribir `0` en el primer producto, sin agregar nada. |
| **Listar por estado** | Opción **4**: elegir un estado y ver los pedidos que están en él. |
| **Resumen del día** | Opción **5**: muestra el total vendido (solo pedidos `ENTREGADO`) y la cantidad de pedidos por modalidad. |
| **Registrar producto** | Opción **1**: agregar un producto nuevo y verlo luego en el listado de la opción 2. |
