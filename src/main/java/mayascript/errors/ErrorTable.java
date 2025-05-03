package mayascript.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Clase que implementa una tabla de errores para el análisis léxico y sintáctico
 */
public class ErrorTable {
    private final List<ErrorEntry> errorEntries = new ArrayList<>();
    private int errorCount = 0;
    
    /**
     * Agrega un nuevo error a la tabla
     * 
     * @param line Línea donde ocurrió el error
     * @param column Columna donde ocurrió el error
     * @param message Mensaje descriptivo del error
     * @param type Tipo de error (léxico, sintáctico, semántico)
     * @return El código de error asignado
     */
    public int addError(int line, int column, String message, SyntaxError.ErrorType type) {
        int errorCode = ++errorCount;
        ErrorEntry entry = new ErrorEntry(errorCode, line, column, message, type);
        errorEntries.add(entry);
        return errorCode;
    }
    
    /**
     * Obtiene todas las entradas de error
     * 
     * @return Una lista de todas las entradas de error
     */
    public List<ErrorEntry> getAllErrors() {
        return Collections.unmodifiableList(errorEntries);
    }
    
    /**
     * Obtiene las entradas de error de un tipo específico
     * 
     * @param type El tipo de error a filtrar
     * @return Una lista de entradas de error del tipo especificado
     */
    public List<ErrorEntry> getErrorsByType(SyntaxError.ErrorType type) {
        List<ErrorEntry> filtered = new ArrayList<>();
        for (ErrorEntry entry : errorEntries) {
            if (entry.getType() == type) {
                filtered.add(entry);
            }
        }
        return filtered;
    }
    
    /**
     * Verifica si hay errores en la tabla
     * 
     * @return true si hay al menos un error, false en caso contrario
     */
    public boolean hasErrors() {
        return !errorEntries.isEmpty();
    }
    
    /**
     * Verifica si hay errores de un tipo específico
     * 
     * @param type El tipo de error a verificar
     * @return true si hay al menos un error del tipo especificado, false en caso contrario
     */
    public boolean hasErrors(SyntaxError.ErrorType type) {
        for (ErrorEntry entry : errorEntries) {
            if (entry.getType() == type) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Limpia la tabla de errores
     */
    public void clear() {
        errorEntries.clear();
        errorCount = 0;
    }
    
    /**
     * Cuenta el número de errores en la tabla
     * 
     * @return El número total de errores
     */
    public int getErrorCount() {
        return errorEntries.size();
    }
    
    /**
     * Cuenta el número de errores de un tipo específico
     * 
     * @param type El tipo de error a contar
     * @return El número de errores del tipo especificado
     */
    public int getErrorCount(SyntaxError.ErrorType type) {
        int count = 0;
        for (ErrorEntry entry : errorEntries) {
            if (entry.getType() == type) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Genera una representación tabular de los errores
     * 
     * @return Una cadena con la tabla de errores formateada
     */
    public String generateErrorTable() {
        if (errorEntries.isEmpty()) {
            return "No se encontraron errores.";
        }
        
        // Ordenar errores por línea y columna
        List<ErrorEntry> sortedErrors = new ArrayList<>(errorEntries);
        Collections.sort(sortedErrors, Comparator.comparingInt(ErrorEntry::getLine)
                                               .thenComparingInt(ErrorEntry::getColumn));
        
        StringBuilder table = new StringBuilder();
        table.append("=========================== TABLA DE ERRORES ===========================\n");
        table.append(String.format("%-5s | %-7s | %-5s | %-8s | %-8s | %s\n", 
                "CÓDIGO", "LÍNEA", "COL", "TIPO", "CATEGORÍA", "MENSAJE"));
        table.append("-----------------------------------------------------------------------\n");
        
        for (ErrorEntry entry : sortedErrors) {
            table.append(String.format("E%03d  | %-7d | %-5d | %-8s | %-8s | %s\n",
                    entry.getErrorCode(),
                    entry.getLine(),
                    entry.getColumn(),
                    entry.getType(),
                    getCategoryName(entry.getErrorCode()),
                    entry.getMessage()));
        }
        
        table.append("=======================================================================\n");
        table.append(String.format("Total: %d errores (%d léxicos, %d sintácticos, %d semánticos)\n",
                getErrorCount(),
                getErrorCount(SyntaxError.ErrorType.LEXICAL),
                getErrorCount(SyntaxError.ErrorType.SYNTAX),
                getErrorCount(SyntaxError.ErrorType.SEMANTIC)));
        
        return table.toString();
    }
    
    /**
     * Obtiene la categoría de error basada en el código
     * 
     * @param errorCode El código de error
     * @return El nombre de la categoría
     */
    private String getCategoryName(int errorCode) {
        // Categorización simple basada en rangos de códigos
        if (errorCode <= 100) {
            return "CRITICO";
        } else if (errorCode <= 500) {
            return "GRAVE";
        } else {
            return "LEVE";
        }
    }
    
    /**
     * Clase interna que representa una entrada en la tabla de errores
     */
    public static class ErrorEntry {
        private final int errorCode;
        private final int line;
        private final int column;
        private final String message;
        private final SyntaxError.ErrorType type;
        
        public ErrorEntry(int errorCode, int line, int column, String message, SyntaxError.ErrorType type) {
            this.errorCode = errorCode;
            this.line = line;
            this.column = column;
            this.message = message;
            this.type = type;
        }
        
        public int getErrorCode() {
            return errorCode;
        }
        
        public int getLine() {
            return line;
        }
        
        public int getColumn() {
            return column;
        }
        
        public String getMessage() {
            return message;
        }
        
        public SyntaxError.ErrorType getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return String.format("E%03d - Error %s [línea %d, columna %d]: %s", 
                    errorCode, type, line, column, message);
        }
    }
} 