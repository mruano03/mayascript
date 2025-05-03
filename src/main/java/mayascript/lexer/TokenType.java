package mayascript.lexer;

/**
 * Enumeración de todos los tipos de tokens soportados por el lenguaje MayaScript
 */
public enum TokenType {
    // Palabras reservadas 
    MAYAB("mayab"),          // Inicio de programa
    TZOLK("tzolk"),          // Declaración de variable
    HUNAB("hunab"),          // Tipo entero
    KIN("kin"),              // Tipo flotante
    NAWAL("nawal"),          // Tipo string
    BOOL("bool"),            // Tipo booleano
    CHAC("chac"),            // Condicional if
    IMIX("imix"),            // Condicional else
    MULUC("muluc"),          // Bucle while
    UINAL("uinal"),          // Bucle for
    KABAN("kaban"),          // Función
    NIK("nik"),              // Retorno de función
    PAKAL("pakal"),          // Imprimir
    WAAJ("waaj"),            // Entrada de usuario
    TUN("tun"),              // Constante
    AHAU("ahau"),            // Fin de programa
    
    // Literales
    IDENTIFIER,              // Identificador
    INT_LITERAL,             // Literal entero
    FLOAT_LITERAL,           // Literal flotante
    STRING_LITERAL,          // Literal cadena
    TRUE("haab"),            // Literal verdadero
    FALSE("manik"),          // Literal falso
    
    // Operadores aritméticos
    PLUS("+"),               // Suma
    MINUS("-"),              // Resta
    MULTIPLY("*"),           // Multiplicación
    DIVIDE("/"),             // División
    MODULO("%"),             // Módulo
    
    // Operadores relacionales
    EQUALS("=="),            // Igual a
    NOT_EQUALS("!="),        // No igual a
    GREATER_THAN(">"),       // Mayor que
    LESS_THAN("<"),          // Menor que
    GREATER_EQUALS(">="),    // Mayor o igual que
    LESS_EQUALS("<="),       // Menor o igual que
    
    // Operadores lógicos
    AND("&&"),               // Y lógico
    OR("||"),                // O lógico
    NOT("!"),                // Negación
    
    // Operadores de asignación
    ASSIGN("="),             // Asignación
    
    // Delimitadores
    LEFT_PAREN("("),         // Paréntesis izquierdo
    RIGHT_PAREN(")"),        // Paréntesis derecho
    LEFT_BRACE("{"),         // Llave izquierda
    RIGHT_BRACE("}"),        // Llave derecha
    LEFT_BRACKET("["),       // Corchete izquierdo
    RIGHT_BRACKET("]"),      // Corchete derecho
    SEMICOLON(";"),          // Punto y coma
    COMMA(","),              // Coma
    DOT("."),                // Punto
    
    // Especiales
    EOF,                     // Fin de archivo
    ERROR;                   // Error léxico
    
    private final String lexeme;
    
    TokenType() {
        this.lexeme = null;
    }
    
    TokenType(String lexeme) {
        this.lexeme = lexeme;
    }
    
    public String getLexeme() {
        return lexeme;
    }
} 