#!/bin/bash

# Script para ejecutar el visualizador de árboles de derivación
# Uso: ./tree_visualizer.sh <archivo.maya>

# Verificar que se haya proporcionado un archivo
if [ $# -lt 1 ]; then
    echo "Uso: $0 <archivo.maya>"
    exit 1
fi

# Verificar que el archivo exista
if [ ! -f "$1" ]; then
    echo "Error: El archivo '$1' no existe."
    exit 1
fi

# Compilar el proyecto si es necesario
echo "Compilando el proyecto..."
mvn compile

# Ejecutar el visualizador de árboles
echo "Generando árbol de derivación para $1..."
mvn exec:java -Dexec.mainClass="mayascript.main.TreeVisualizer" -Dexec.args="$1"

# Verificar si se generó el archivo XML con el árbol
TREE_FILE="${1%.*}_tree.xml"
if [ -f "$TREE_FILE" ]; then
    echo "Árbol de derivación generado exitosamente en: $TREE_FILE"
    
    # Preguntar si se desea abrir el archivo
    read -p "¿Desea abrir el archivo? (s/n): " respuesta
    if [ "$respuesta" = "s" ] || [ "$respuesta" = "S" ]; then
        # Intentar abrir con el visor predeterminado del sistema
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            open "$TREE_FILE"
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            xdg-open "$TREE_FILE"
        elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
            # Windows
            start "$TREE_FILE"
        else
            echo "No se pudo determinar cómo abrir el archivo en este sistema operativo."
            echo "Por favor, ábralo manualmente desde: $TREE_FILE"
        fi
    fi
else
    echo "Error: No se pudo generar el árbol de derivación."
fi 