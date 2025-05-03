package mayascript.errors;

/**
 * Clase que representa un error de sintaxis en el código fuente
 */
public class SyntaxError {
    private final int line;
    private final int column;
    private final String message;
    private final ErrorType type;
    
    /**
     * Constructor para error con tipo de error específico
     */
    public SyntaxError(int line, int column, String message, ErrorType type) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.type = type;
    }
    
    /**
     * Constructor que asume tipo de error SYNTAX por defecto
     */
    public SyntaxError(int line, int column, String message) {
        this(line, column, message, ErrorType.SYNTAX);
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
    
    public ErrorType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return String.format("Error %s [línea %d, columna %d]: %s", 
                type, line, column, message);
    }
    
    /**
     * Enumeración de tipos de errores posibles
     */
    public enum ErrorType {
        LEXICAL("léxico"),
        SYNTAX("sintáctico"),
        SEMANTIC("semántico");
        
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
} 