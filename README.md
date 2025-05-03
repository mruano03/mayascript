# MayaScript

<div align="center">
  <h3>Un lenguaje de programación inspirado en la cultura maya</h3>
</div>

## 📜 Descripción

MayaScript es un minicompilador con una sintaxis única inspirada en términos de la cultura maya, diseñado como un proyecto educativo para demostrar los principios fundamentales de compilación y análisis de lenguajes.
```
mayab
    pakal "Hola Mundo Maya";
ahau
```

## 🌟 Características

- ✅ **Análisis Léxico**: Tokenización completa del código fuente
- ✅ **Análisis Sintáctico**: Construcción de árboles de derivación
- ✅ **Tabla de Símbolos**: Gestión eficiente de identificadores
- ✅ **Manejo de Errores**: Detección y reporte de errores léxicos y sintácticos
- ✅ **Visualización de Árboles**: Representación gráfica de la estructura sintáctica

## 🛠️ Tecnologías

- Java 11+
- Maven
- Análisis sintáctico personalizado
- Técnicas de compilación moderna

## ⚙️ Instalación

### Requisitos previos

- Java JDK 11 o superior
- Maven 3.6 o superior

### Pasos para instalar

```bash
# Clonar el repositorio
git clone https://github.com/mruano03/mayascript.git
cd mayascript

# Compilar el proyecto
mvn clean package
```

## 📋 Uso

### Ejecutar un archivo de código

```bash
./mayascript.sh ruta/al/archivo.maya
```

O directamente:

```bash
java -jar target/mayascript-1.0-SNAPSHOT.jar ruta/al/archivo.maya
```

### Modo Interactivo

```bash
./mayascript.sh
```

## 🎯 Ejemplos de Código

### Hola Mundo
```
mayab
    pakal "Hola Mundo Maya";
ahau
```

### Uso de Booleanos
```
mayab
    pakal "El valor es: " haab;  // haab representa verdadero
    pakal "El otro valor es: " manik;  // manik representa falso
ahau
```

## 📚 Documentación

La documentación completa está disponible en la carpeta `docs/`:

- 📘 Manual de Usuario: `docs/Manual_Usuario.md`


## 🧪 Ejemplos de Prueba

En la carpeta `src/main/resources/ejemplos/` encontrarás varios ejemplos ordenados por complejidad:

1. Programas básicos de introducción
2. Ejemplos de formato y mensajes
3. Demostraciones de valores booleanos
4. Uso de comentarios
5. Programas completos
6. Ejemplos de errores comunes
7. Visualización de árboles de sintaxis

## 🌱 Estado del Proyecto

Proyecto Educativo(Curso: Compiladores)

## 👥 Autores

- **[Mynor David Ruano Cabrera]** - *Trabajo inicial* - (https://github.com/mruano03)

