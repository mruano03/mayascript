package mayascript.symbols;

/**
 * Enumeración que representa los tipos posibles para un símbolo
 */
public enum SymbolType {
    // Tipos de datos
    INTEGER("hunab"),     // Entero
    FLOAT("kin"),         // Flotante
    STRING("nawal"),      // Cadena de texto
    BOOLEAN("bool"),      // Booleano
    
    // Tipos de símbolos
    FUNCTION("kaban"),    // Función
    VARIABLE("tzolk"),    // Variable
    CONSTANT("tun"),      // Constante
    
    // Tipo especial para errores
    UNKNOWN;
    
    private final String keyword;
    
    SymbolType() {
        this.keyword = null;
    }
    
    SymbolType(String keyword) {
        this.keyword = keyword;
    }
    
    /**
     * Devuelve la palabra clave asociada al tipo
     * @return Palabra clave
     */
    public String getKeyword() {
        return keyword;
    }
    
    /**
     * Encuentra un tipo de símbolo a partir de su palabra clave
     * @param keyword Palabra clave
     * @return Tipo de símbolo o UNKNOWN si no se encuentra
     */
    public static SymbolType fromKeyword(String keyword) {
        for (SymbolType type : values()) {
            if (type.keyword != null && type.keyword.equals(keyword)) {
                return type;
            }
        }
        return UNKNOWN;
    }
} 