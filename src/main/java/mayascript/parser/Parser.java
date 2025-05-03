package mayascript.parser;

import mayascript.errors.ErrorHandler;
import mayascript.errors.SyntaxError;
import mayascript.lexer.Token;
import mayascript.lexer.TokenType;
import mayascript.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador sintáctico para MayaScript.
 * Convierte una secuencia de tokens en un árbol de sintaxis abstracta.
 */
public class Parser {
    private final List<Token> tokens;
    private final ErrorHandler errorHandler;
    private int current = 0;
    
    public Parser(List<Token> tokens, ErrorHandler errorHandler) {
        this.tokens = tokens;
        this.errorHandler = errorHandler;
    }
    
    /**
     * Inicia el análisis sintáctico para generar el AST
     * @return El nodo raíz del árbol de sintaxis abstracta
     */
    public Stmt.Program parse() {
        try {
            return program();
        } catch (ParserError error) {
            return new Stmt.Program(new ArrayList<>());
        }
    }
    
    /**
     * Regla de producción para un programa completo
     * program -> mayab statement* ahau
     */
    private Stmt.Program program() {
        List<Stmt> statements = new ArrayList<>();
        
        // Verificar que el programa comience con 'mayab'
        consume(TokenType.MAYAB, "Se esperaba 'mayab' para iniciar el programa.");
        
        // Analizar todas las declaraciones
        while (!check(TokenType.AHAU) && !isAtEnd()) {
            statements.add(declaration());
        }
        
        // Verificar que el programa termine con 'ahau'
        consume(TokenType.AHAU, "Se esperaba 'ahau' para finalizar el programa.");
        
        return new Stmt.Program(statements);
    }
    
    /**
     * Regla de producción para una declaración
     * declaration -> varDeclaration | constDeclaration | functionDecl | statement
     */
    private Stmt declaration() {
        try {
            if (match(TokenType.TZOLK)) return varDeclaration();
            if (match(TokenType.TUN)) return constDeclaration();
            if (match(TokenType.KABAN)) return function();
            
            return statement();
        } catch (ParserError error) {
            synchronize();
            return null;
        }
    }
    
    /**
     * Regla de producción para una declaración de variable
     * varDeclaration -> tzolk type identifier (= expression)? ;
     */
    private Stmt varDeclaration() {
        Token type = consume(null, "Se esperaba un tipo de dato.");
        Token name = consume(TokenType.IDENTIFIER, "Se esperaba un nombre de variable.");
        
        Expr initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }
        
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la declaración de variable.");
        return new Stmt.Var(name, type, initializer);
    }
    
    /**
     * Regla de producción para una declaración de constante
     * constDeclaration -> tun type identifier = expression ;
     */
    private Stmt constDeclaration() {
        Token type = consume(null, "Se esperaba un tipo de dato.");
        Token name = consume(TokenType.IDENTIFIER, "Se esperaba un nombre de constante.");
        
        consume(TokenType.ASSIGN, "Se esperaba '=' después del nombre de la constante.");
        Expr initializer = expression();
        
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la declaración de constante.");
        return new Stmt.Const(name, type, initializer);
    }
    
    /**
     * Regla de producción para una declaración de función
     * function -> kaban type identifier ( parameters? ) block
     */
    private Stmt.Function function() {
        Token type = consume(null, "Se esperaba un tipo de retorno.");
        Token name = consume(TokenType.IDENTIFIER, "Se esperaba el nombre de la función.");
        
        consume(TokenType.LEFT_PAREN, "Se esperaba '(' después del nombre de la función.");
        List<Stmt.Parameter> parameters = new ArrayList<>();
        
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                Token paramType = consume(null, "Se esperaba un tipo para el parámetro.");
                Token paramName = consume(TokenType.IDENTIFIER, "Se esperaba un nombre de parámetro.");
                parameters.add(new Stmt.Parameter(paramName, paramType));
            } while (match(TokenType.COMMA));
        }
        
        consume(TokenType.RIGHT_PAREN, "Se esperaba ')' después de los parámetros.");
        
        consume(TokenType.LEFT_BRACE, "Se esperaba '{' antes del cuerpo de la función.");
        List<Stmt> body = block();
        
        return new Stmt.Function(name, type, parameters, body);
    }
    
    /**
     * Regla de producción para una sentencia
     * statement -> exprStmt | printStmt | block | ifStmt | whileStmt | forStmt | returnStmt
     */
    private Stmt statement() {
        if (match(TokenType.PAKAL)) return printStatement();
        if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());
        if (match(TokenType.CHAC)) return ifStatement();
        if (match(TokenType.MULUC)) return whileStatement();
        if (match(TokenType.UINAL)) return forStatement();
        if (match(TokenType.NIK)) return returnStatement();
        
        return expressionStatement();
    }
    
    /**
     * Regla de producción para una sentencia de impresión
     * printStmt -> pakal expression ;
     */
    private Stmt printStatement() {
        Expr value = expression();
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la expresión.");
        return new Stmt.Print(value);
    }
    
    /**
     * Regla de producción para una sentencia de retorno
     * returnStmt -> nik expression? ;
     */
    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la sentencia de retorno.");
        return new Stmt.Return(keyword, value);
    }
    
    /**
     * Regla de producción para un bloque de código
     * block -> { declaration* }
     */
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        
        consume(TokenType.RIGHT_BRACE, "Se esperaba '}' después del bloque.");
        return statements;
    }
    
    /**
     * Regla de producción para una sentencia condicional
     * ifStmt -> chac ( expression ) statement ( imix statement )?
     */
    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Se esperaba '(' después de 'chac'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Se esperaba ')' después de la condición.");
        
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        
        if (match(TokenType.IMIX)) {
            elseBranch = statement();
        }
        
        return new Stmt.If(condition, thenBranch, elseBranch);
    }
    
    /**
     * Regla de producción para un bucle while
     * whileStmt -> muluc ( expression ) statement
     */
    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Se esperaba '(' después de 'muluc'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Se esperaba ')' después de la condición.");
        
        Stmt body = statement();
        
        return new Stmt.While(condition, body);
    }
    
    /**
     * Regla de producción para un bucle for
     * forStmt -> uinal ( (varDecl | exprStmt | ;) expression? ; expression? ) statement
     */
    private Stmt forStatement() {
        consume(TokenType.LEFT_PAREN, "Se esperaba '(' después de 'uinal'.");
        
        // Inicialización
        Stmt initializer;
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.TZOLK)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        
        // Condición
        Expr condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la condición del bucle.");
        
        // Incremento
        Expr increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Se esperaba ')' después de las cláusulas del for.");
        
        // Cuerpo
        Stmt body = statement();
        
        // Transformar a la estructura de while si es necesario
        if (increment != null) {
            body = new Stmt.Block(
                List.of(
                    body,
                    new Stmt.Expression(increment)
                )
            );
        }
        
        if (condition == null) {
            condition = new Expr.Literal(true);
        }
        
        body = new Stmt.While(condition, body);
        
        if (initializer != null) {
            body = new Stmt.Block(List.of(initializer, body));
        }
        
        return body;
    }
    
    /**
     * Regla de producción para una sentencia de expresión
     * exprStmt -> expression ;
     */
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Se esperaba ';' después de la expresión.");
        return new Stmt.Expression(expr);
    }
    
    /**
     * Regla de producción para una expresión
     * expression -> assignment
     */
    private Expr expression() {
        return assignment();
    }
    
    /**
     * Regla de producción para una asignación
     * assignment -> IDENTIFIER = assignment | logic_or
     */
    private Expr assignment() {
        Expr expr = logicOr();
        
        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Expr value = assignment();
            
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).getName();
                return new Expr.Assign(name, value);
            }
            
            error(equals, "Objetivo de asignación inválido.");
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para operaciones lógicas OR
     * logic_or -> logic_and ( || logic_and )*
     */
    private Expr logicOr() {
        Expr expr = logicAnd();
        
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = logicAnd();
            expr = new Expr.Logical(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para operaciones lógicas AND
     * logic_and -> equality ( && equality )*
     */
    private Expr logicAnd() {
        Expr expr = equality();
        
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para operaciones de igualdad
     * equality -> comparison ( ( != | == ) comparison )*
     */
    private Expr equality() {
        Expr expr = comparison();
        
        while (match(TokenType.NOT_EQUALS, TokenType.EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para operaciones de comparación
     * comparison -> term ( ( > | >= | < | <= ) term )*
     */
    private Expr comparison() {
        Expr expr = term();
        
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUALS, 
                     TokenType.LESS_THAN, TokenType.LESS_EQUALS)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para términos
     * term -> factor ( ( - | + ) factor )*
     */
    private Expr term() {
        Expr expr = factor();
        
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para factores
     * factor -> unary ( ( / | * | % ) unary )*
     */
    private Expr factor() {
        Expr expr = unary();
        
        while (match(TokenType.DIVIDE, TokenType.MULTIPLY, TokenType.MODULO)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    /**
     * Regla de producción para operaciones unarias
     * unary -> ( ! | - ) unary | call
     */
    private Expr unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        
        return call();
    }
    
    /**
     * Regla de producción para llamadas a funciones
     * call -> primary ( "(" arguments? ")" )*
     */
    private Expr call() {
        Expr expr = primary();
        
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else {
                break;
            }
        }
        
        return expr;
    }
    
    /**
     * Completa una llamada a función
     */
    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "No puede haber más de 255 argumentos.");
                }
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        
        Token paren = consume(TokenType.RIGHT_PAREN, "Se esperaba ')' después de los argumentos.");
        
        return new Expr.Call(callee, paren, arguments);
    }
    
    /**
     * Regla de producción para expresiones primarias
     * primary -> NUMBER | STRING | "true" | "false" | "(" expression ")" | IDENTIFIER
     */
    private Expr primary() {
        if (match(TokenType.INT_LITERAL, TokenType.FLOAT_LITERAL)) {
            return new Expr.Literal(previous().getLiteral());
        }
        
        if (match(TokenType.STRING_LITERAL)) {
            return new Expr.Literal(previous().getLiteral());
        }
        
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Se esperaba ')' después de la expresión.");
            return new Expr.Grouping(expr);
        }
        
        if (match(TokenType.IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        
        if (match(TokenType.WAAJ)) {
            return new Expr.Input(previous());
        }
        
        throw error(peek(), "Se esperaba una expresión.");
    }
    
    /**
     * Verifica si el token actual es de uno de los tipos especificados
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Consume el token actual si es del tipo esperado, o lanza un error
     */
    private Token consume(TokenType type, String message) {
        if (type != null && check(type)) return advance();
        
        throw error(peek(), message);
    }
    
    /**
     * Verifica si el token actual es del tipo especificado
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }
    
    /**
     * Avanza al siguiente token y devuelve el anterior
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
    
    /**
     * Verifica si se ha llegado al final de los tokens
     */
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }
    
    /**
     * Devuelve el token actual sin avanzar
     */
    private Token peek() {
        return tokens.get(current);
    }
    
    /**
     * Devuelve el token anterior
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
    
    /**
     * Crea un objeto de error y lo reporta
     */
    private ParserError error(Token token, String message) {
        errorHandler.error(token.getLine(), token.getColumn(), message, SyntaxError.ErrorType.SYNTAX);
        return new ParserError();
    }
    
    /**
     * Sincroniza el parser después de un error
     */
    private void synchronize() {
        advance();
        
        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;
            
            switch (peek().getType()) {
                case TZOLK:
                case TUN:
                case KABAN:
                case CHAC:
                case MULUC:
                case UINAL:
                case NIK:
                case PAKAL:
                    return;
            }
            
            advance();
        }
    }
    
    /**
     * Excepción interna para manejo de errores
     */
    private static class ParserError extends RuntimeException {}
} 