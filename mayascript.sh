#!/bin/bash

# Script para ejecutar el compilador MayaScript

# Verifica que Java esté instalado
if ! [ -x "$(command -v java)" ]; then
  echo 'Error: Java no está instalado.' >&2
  exit 1
fi

# Verifica la versión de Java
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
required_version="11"

if [[ "$(echo "${java_version}" | cut -d'.' -f1)" -lt "${required_version}" ]]; then
  echo "Error: Se requiere Java ${required_version} o superior. Versión actual: ${java_version}" >&2
  exit 1
fi

# Directorio donde se encuentra el script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Busca el archivo JAR (asume que está en el directorio target)
JAR_FILE="${DIR}/target/mayascript-1.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
  echo "Error: No se encuentra el archivo JAR. Ejecute 'mvn clean package' primero." >&2
  exit 1
fi

# Ejecuta el compilador con los argumentos proporcionados
java -jar "$JAR_FILE" "$@" 