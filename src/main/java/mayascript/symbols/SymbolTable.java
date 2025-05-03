package mayascript.symbols;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabla de símbolos para MayaScript.
 * Almacena información sobre identificadores y sus propiedades.
 */
public class SymbolTable {
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final SymbolTable enclosing;
    
    /**
     * Constructor para una tabla de símbolos global (sin ámbito padre)
     */
    public SymbolTable() {
        this.enclosing = null;
    }
    
    /**
     * Constructor para una tabla de símbolos local con referencia a su ámbito padre
     * @param enclosing Tabla de símbolos del ámbito superior
     */
    public SymbolTable(SymbolTable enclosing) {
        this.enclosing = enclosing;
    }
    
    /**
     * Declara un nuevo símbolo en la tabla actual
     * @param name Nombre del símbolo
     * @param type Tipo del símbolo
     * @param mutable Indica si el símbolo es modificable
     * @return true si se pudo declarar, false si ya existía en el ámbito actual
     */
    public boolean declare(String name, SymbolType type, boolean mutable) {
        if (symbols.containsKey(name)) {
            return false;
        }
        
        symbols.put(name, new Symbol(name, type, mutable));
        return true;
    }
    
    /**
     * Busca un símbolo en la tabla actual y, si no se encuentra, en los ámbitos superiores
     * @param name Nombre del símbolo a buscar
     * @return El símbolo encontrado o null si no existe
     */
    public Symbol resolve(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }
        
        if (enclosing != null) {
            return enclosing.resolve(name);
        }
        
        return null;
    }
    
    /**
     * Actualiza el valor de un símbolo existente
     * @param name Nombre del símbolo
     * @param value Nuevo valor
     * @return true si se actualizó correctamente, false si no existía o no era modificable
     */
    public boolean assign(String name, Object value) {
        if (symbols.containsKey(name)) {
            Symbol symbol = symbols.get(name);
            if (!symbol.isMutable()) {
                return false; // No se puede modificar una constante
            }
            symbol.setValue(value);
            return true;
        }
        
        if (enclosing != null) {
            return enclosing.assign(name, value);
        }
        
        return false;
    }
    
    /**
     * Obtiene todos los símbolos definidos en esta tabla
     * @return Mapa con todos los símbolos
     */
    public Map<String, Symbol> getSymbols() {
        return new HashMap<>(symbols);
    }
    
    /**
     * Verifica si un símbolo existe en el ámbito actual (no en los padres)
     * @param name Nombre del símbolo
     * @return true si existe en el ámbito actual
     */
    public boolean existsInCurrentScope(String name) {
        return symbols.containsKey(name);
    }
} 