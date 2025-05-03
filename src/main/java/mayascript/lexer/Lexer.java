package mayascript.lexer;

import mayascript.errors.ErrorHandler;
import mayascript.errors.SyntaxError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Analizador léxico para el lenguaje MayaScript.
 * Convierte el código fuente en una secuencia de tokens.
 */
public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final ErrorHandler errorHandler;
    
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;
    
    // Mapa de palabras reservadas
    private static final Map<String, TokenType> keywords;
    
    // Expresiones regulares para validación
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    
    static {
        keywords = new HashMap<>();
        
        // Registrar todas las palabras reservadas
        for (TokenType type : TokenType.values()) {
            if (type.getLexeme() != null) {
                keywords.put(type.getLexeme(), type);
            }
        }
    }
    
    public Lexer(String source, ErrorHandler errorHandler) {
        this.source = source;
        this.errorHandler = errorHandler;
    }
    
    /**
     * Analiza el código fuente completo y genera todos los tokens
     * @return Lista de tokens encontrados
     */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // Comenzamos un nuevo lexema
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", null, line, column));
        return tokens;
    }
    
    /**
     * Analiza un único token
     */
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            // Caracteres individuales
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case '[': addToken(TokenType.LEFT_BRACKET); break;
            case ']': addToken(TokenType.RIGHT_BRACKET); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '%': addToken(TokenType.MODULO); break;
            
            // Operadores que pueden tener dos caracteres
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            
            // División o comentario
            case '/':
                if (match('/')) {
                    // Comentario de una línea, ignorar hasta el final de la línea
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    multilineComment();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            
            // Operador lógico AND
            case '&':
                if (match('&')) {
                    addToken(TokenType.AND);
                } else {
                    errorHandler.error(line, column, "Esperaba '&' después de '&'", SyntaxError.ErrorType.LEXICAL);
                }
                break;
            
            // Operador lógico OR
            case '|':
                if (match('|')) {
                    addToken(TokenType.OR);
                } else {
                    errorHandler.error(line, column, "Esperaba '|' después de '|'", SyntaxError.ErrorType.LEXICAL);
                }
                break;
                
            // Espacios en blanco, ignorar
            case ' ':
            case '\r':
            case '\t':
                break;
                
            // Salto de línea
            case '\n':
                line++;
                column = 1;
                break;
                
            // Cadenas
            case '"': string(); break;
                
            // Caracteres no reconocidos
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    errorHandler.error(line, column, "Carácter inesperado: '" + c + "'", SyntaxError.ErrorType.LEXICAL);
                }
                break;
        }
    }
    
    /**
     * Procesa un comentario multilinea
     */
    private void multilineComment() {
        int nestedLevel = 1;
        int startLine = line;
        int startColumn = column;
        
        while (nestedLevel > 0 && !isAtEnd()) {
            if (peek() == '/' && peekNext() == '*') {
                advance();
                advance();
                nestedLevel++;
            } else if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                nestedLevel--;
            } else if (peek() == '\n') {
                advance();
                line++;
                column = 1;
            } else {
                advance();
            }
        }
        
        if (nestedLevel > 0) {
            errorHandler.error(startLine, startColumn, "Comentario multilinea sin cerrar", SyntaxError.ErrorType.LEXICAL);
        }
    }
    
    /**
     * Procesa un identificador (palabra reservada o identificador de usuario)
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        
        // Verificar si es una palabra reservada
        TokenType type = keywords.get(text);
        if (type == null) {
            // Verificar que el identificador sea válido según el patrón
            if (!IDENTIFIER_PATTERN.matcher(text).matches()) {
                errorHandler.error(line, column - text.length(),
                    "Identificador inválido: '" + text + "'", SyntaxError.ErrorType.LEXICAL);
                return;
            }
            
            type = TokenType.IDENTIFIER;
        }
        
        addToken(type);
    }
    
    /**
     * Procesa un número (entero o decimal)
     */
    private void number() {
        // Verificar si es un entero
        while (isDigit(peek())) advance();
        
        // Verificar si es un decimal
        if (peek() == '.' && isDigit(peekNext())) {
            // Consumir el punto
            advance();
            
            // Consumir la parte decimal
            while (isDigit(peek())) advance();
        }
        
        // Verificar que el número sea válido
        String numberText = source.substring(start, current);
        if (!NUMBER_PATTERN.matcher(numberText).matches()) {
            errorHandler.error(line, column - numberText.length(), 
                "Número inválido: '" + numberText + "'", SyntaxError.ErrorType.LEXICAL);
            return;
        }
        
        // Convertir a un valor numérico
        addToken(TokenType.NUMBER_LITERAL, Double.parseDouble(numberText));
    }
    
    /**
     * Procesa una cadena de texto
     */
    private void string() {
        int startLine = line;
        int startColumn = column - 1;
        
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }
        
        if (isAtEnd()) {
            errorHandler.error(startLine, startColumn, "Cadena sin terminar", SyntaxError.ErrorType.LEXICAL);
            return;
        }
        
        // Consumimos el cierre de comillas
        advance();
        
        // Extrae el valor de la cadena sin las comillas
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING_LITERAL, value);
    }
    
    /**
     * Comprueba si el siguiente carácter coincide con el esperado
     */
    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) {
            return false;
        }
        
        current++;
        column++;
        return true;
    }
    
    /**
     * Devuelve el carácter actual sin consumirlo
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    /**
     * Devuelve el carácter siguiente sin consumirlo
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    
    /**
     * Comprueba si un carácter es una letra o guión bajo
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }
    
    /**
     * Comprueba si un carácter es un dígito
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    
    /**
     * Comprueba si un carácter es alfanumérico
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    /**
     * Comprueba si hemos llegado al final del código fuente
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }
    
    /**
     * Consume el carácter actual y lo devuelve
     */
    private char advance() {
        column++;
        return source.charAt(current++);
    }
    
    /**
     * Agrega un token sin valor literal
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    
    /**
     * Agrega un token con su valor literal
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, column - text.length()));
    }
} 