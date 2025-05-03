package mayascript.symbols;

/**
 * Clase que representa un símbolo en la tabla de símbolos
 */
public class Symbol {
    private final String name;
    private final SymbolType type;
    private final boolean mutable;
    private Object value;
    
    /**
     * Constructor de un símbolo
     * @param name Nombre del símbolo
     * @param type Tipo del símbolo
     * @param mutable Indica si el símbolo puede ser modificado
     */
    public Symbol(String name, SymbolType type, boolean mutable) {
        this.name = name;
        this.type = type;
        this.mutable = mutable;
        this.value = null;
    }
    
    /**
     * Constructor de un símbolo con valor inicial
     * @param name Nombre del símbolo
     * @param type Tipo del símbolo
     * @param mutable Indica si el símbolo puede ser modificado
     * @param value Valor inicial del símbolo
     */
    public Symbol(String name, SymbolType type, boolean mutable, Object value) {
        this.name = name;
        this.type = type;
        this.mutable = mutable;
        this.value = value;
    }
    
    /**
     * Devuelve el nombre del símbolo
     * @return Nombre del símbolo
     */
    public String getName() {
        return name;
    }
    
    /**
     * Devuelve el tipo del símbolo
     * @return Tipo del símbolo
     */
    public SymbolType getType() {
        return type;
    }
    
    /**
     * Indica si el símbolo puede ser modificado
     * @return true si el símbolo puede ser modificado, false en caso contrario
     */
    public boolean isMutable() {
        return mutable;
    }
    
    /**
     * Devuelve el valor actual del símbolo
     * @return Valor del símbolo
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * Actualiza el valor del símbolo
     * @param value Nuevo valor
     */
    public void setValue(Object value) {
        if (!mutable) {
            throw new IllegalStateException("No se puede modificar un símbolo constante: " + name);
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("Symbol[name=%s, type=%s, mutable=%s, value=%s]",
                name, type, mutable, value);
    }
} 