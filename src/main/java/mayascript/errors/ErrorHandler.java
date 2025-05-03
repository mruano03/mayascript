package mayascript.errors;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar los errores encontrados durante el análisis léxico y sintáctico
 */
public class ErrorHandler {
    private final ErrorTable errorTable = new ErrorTable();
    private final List<SyntaxError> errors = new ArrayList<>();
    private boolean hadError = false;
    
    /**
     * Registra un error de sintaxis
     * @param line Línea donde ocurrió el error
     * @param column Columna donde ocurrió el error
     * @param message Mensaje descriptivo del error
     */
    public void error(int line, int column, String message) {
        error(line, column, message, SyntaxError.ErrorType.SYNTAX);
    }
    
    /**
     * Registra un error con un tipo específico
     * @param line Línea donde ocurrió el error
     * @param column Columna donde ocurrió el error
     * @param message Mensaje descriptivo del error
     * @param type Tipo de error (léxico, sintáctico, semántico)
     */
    public void error(int line, int column, String message, SyntaxError.ErrorType type) {
        SyntaxError error = new SyntaxError(line, column, message, type);
        errors.add(error);
        hadError = true;
        
        // Agregar a la tabla de errores
        errorTable.addError(line, column, message, type);
        
        // Imprimir información del error
        System.err.println(error);
    }
    
    /**
     * Indica si hubo errores durante el análisis
     * @return true si hubo errores, false en caso contrario
     */
    public boolean hadError() {
        return hadError;
    }
    
    /**
     * Restablece el estado de error
     */
    public void reset() {
        hadError = false;
        errors.clear();
        errorTable.clear();
    }
    
    /**
     * Devuelve la lista de errores registrados
     * @return Lista de errores
     */
    public List<SyntaxError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Obtiene la tabla de errores
     * @return La tabla de errores
     */
    public ErrorTable getErrorTable() {
        return errorTable;
    }
    
    /**
     * Imprime todos los errores registrados
     */
    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("No se encontraron errores.");
            return;
        }
        
        System.err.println("Errores encontrados:");
        for (SyntaxError error : errors) {
            System.err.println(error);
        }
    }
    
    /**
     * Imprime la tabla de errores
     */
    public void printErrorTable() {
        System.out.println(errorTable.generateErrorTable());
    }
} 