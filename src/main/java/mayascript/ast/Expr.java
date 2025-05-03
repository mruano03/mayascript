package mayascript.ast;

import mayascript.lexer.Token;
import java.util.List;

/**
 * Clase base para todas las expresiones en el AST
 */
public abstract class Expr {
    
    /**
     * Método abstracto para implementar el patrón visitor
     */
    public abstract <T> T accept(Visitor<T> visitor);
    
    /**
     * Expresión literal (número, cadena, booleano)
     */
    public static class Literal extends Expr {
        private final Object value;
        
        public Literal(Object value) {
            this.value = value;
        }
        
        public Object getValue() {
            return value;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }
    
    /**
     * Expresión de agrupación con paréntesis
     */
    public static class Grouping extends Expr {
        private final Expr expression;
        
        public Grouping(Expr expression) {
            this.expression = expression;
        }
        
        public Expr getExpression() {
            return expression;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }
    
    /**
     * Expresión de operación unaria (!, -)
     */
    public static class Unary extends Expr {
        private final Token operator;
        private final Expr right;
        
        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
        
        public Token getOperator() {
            return operator;
        }
        
        public Expr getRight() {
            return right;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }
    
    /**
     * Expresión de operación binaria (+, -, *, /, etc.)
     */
    public static class Binary extends Expr {
        private final Expr left;
        private final Token operator;
        private final Expr right;
        
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        
        public Expr getLeft() {
            return left;
        }
        
        public Token getOperator() {
            return operator;
        }
        
        public Expr getRight() {
            return right;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }
    
    /**
     * Expresión de operación lógica (&&, ||)
     */
    public static class Logical extends Expr {
        private final Expr left;
        private final Token operator;
        private final Expr right;
        
        public Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        
        public Expr getLeft() {
            return left;
        }
        
        public Token getOperator() {
            return operator;
        }
        
        public Expr getRight() {
            return right;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }
    
    /**
     * Expresión de variable
     */
    public static class Variable extends Expr {
        private final Token name;
        
        public Variable(Token name) {
            this.name = name;
        }
        
        public Token getName() {
            return name;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }
    
    /**
     * Expresión de asignación
     */
    public static class Assign extends Expr {
        private final Token name;
        private final Expr value;
        
        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }
        
        public Token getName() {
            return name;
        }
        
        public Expr getValue() {
            return value;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }
    
    /**
     * Expresión de llamada a función
     */
    public static class Call extends Expr {
        private final Expr callee;
        private final Token paren;
        private final List<Expr> arguments;
        
        public Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }
        
        public Expr getCallee() {
            return callee;
        }
        
        public Token getParen() {
            return paren;
        }
        
        public List<Expr> getArguments() {
            return arguments;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitCallExpr(this);
        }
    }
    
    /**
     * Expresión de entrada de usuario
     */
    public static class Input extends Expr {
        private final Token keyword;
        
        public Input(Token keyword) {
            this.keyword = keyword;
        }
        
        public Token getKeyword() {
            return keyword;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitInputExpr(this);
        }
    }
} 