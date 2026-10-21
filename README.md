# BAPP

Aplicación móvil Android para el levantamiento de infraestructura tecnológica en Data Centers (caso CMC360 Ciberseguridad Industrial SpA, DSY1105).

## Funcionalidades

- Registro e inicio de sesión de usuarios con validación de campos, persistidos con Room.
- Pantalla de carga animada de 3 segundos al iniciar sesión.
- Levantamiento de rack: ID, fecha (calendario modal) y rol.
- Captura de fotografía con la cámara o selección de imagen desde la galería.
- Procesamiento de imagen simulado con animación y catálogo de componentes.
- Revisión y validación de componentes, con trazabilidad en la base de datos.
- Listado de racks registrados, edición y eliminación con confirmación.

## Arquitectura

MVVM con Jetpack Compose.

- `data`: entidades, DAOs, base de datos Room y repositorio.
- `model`: catálogo de componentes.
- `viewmodel`: estado y lógica de cada flujo.
- `ui`: tema, componentes reutilizables y pantallas.
- `navigation`: rutas y transiciones entre pantallas.

## Tecnologías

Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room, KSP.
