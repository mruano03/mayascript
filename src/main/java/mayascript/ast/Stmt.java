package mayascript.ast;

import mayascript.lexer.Token;
import java.util.List;

/**
 * Clase base para todas las sentencias en el AST
 */
public abstract class Stmt {

    /**
     * Método abstracto para implementar el patrón visitor
     */
    public abstract <T> T accept(Visitor<T> visitor);

    /**
     * Representa un programa completo
     */
    public static class Program extends Stmt {
        private final List<Stmt> statements;
        
        public Program(List<Stmt> statements) {
            this.statements = statements;
        }
        
        public List<Stmt> getStatements() {
            return statements;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitProgramStmt(this);
        }
    }
    
    /**
     * Sentencia de expresión
     */
    public static class Expression extends Stmt {
        private final Expr expression;
        
        public Expression(Expr expression) {
            this.expression = expression;
        }
        
        public Expr getExpression() {
            return expression;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }
    
    /**
     * Sentencia de impresión
     */
    public static class Print extends Stmt {
        private final Expr expression;
        
        public Print(Expr expression) {
            this.expression = expression;
        }
        
        public Expr getExpression() {
            return expression;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }
    
    /**
     * Declaración de variable
     */
    public static class Var extends Stmt {
        private final Token name;
        private final Token type;
        private final Expr initializer;
        
        public Var(Token name, Token type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }
        
        public Token getName() {
            return name;
        }
        
        public Token getType() {
            return type;
        }
        
        public Expr getInitializer() {
            return initializer;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitVarStmt(this);
        }
    }
    
    /**
     * Declaración de constante
     */
    public static class Const extends Stmt {
        private final Token name;
        private final Token type;
        private final Expr initializer;
        
        public Const(Token name, Token type, Expr initializer) {
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }
        
        public Token getName() {
            return name;
        }
        
        public Token getType() {
            return type;
        }
        
        public Expr getInitializer() {
            return initializer;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitConstStmt(this);
        }
    }
    
    /**
     * Bloque de código
     */
    public static class Block extends Stmt {
        private final List<Stmt> statements;
        
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }
        
        public List<Stmt> getStatements() {
            return statements;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }
    
    /**
     * Sentencia condicional if
     */
    public static class If extends Stmt {
        private final Expr condition;
        private final Stmt thenBranch;
        private final Stmt elseBranch;
        
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }
        
        public Expr getCondition() {
            return condition;
        }
        
        public Stmt getThenBranch() {
            return thenBranch;
        }
        
        public Stmt getElseBranch() {
            return elseBranch;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
    }
    
    /**
     * Bucle while
     */
    public static class While extends Stmt {
        private final Expr condition;
        private final Stmt body;
        
        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }
        
        public Expr getCondition() {
            return condition;
        }
        
        public Stmt getBody() {
            return body;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }
    
    /**
     * Sentencia de retorno
     */
    public static class Return extends Stmt {
        private final Token keyword;
        private final Expr value;
        
        public Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }
        
        public Token getKeyword() {
            return keyword;
        }
        
        public Expr getValue() {
            return value;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }
    
    /**
     * Declaración de función
     */
    public static class Function extends Stmt {
        private final Token name;
        private final Token returnType;
        private final List<Parameter> parameters;
        private final List<Stmt> body;
        
        public Function(Token name, Token returnType, List<Parameter> parameters, List<Stmt> body) {
            this.name = name;
            this.returnType = returnType;
            this.parameters = parameters;
            this.body = body;
        }
        
        public Token getName() {
            return name;
        }
        
        public Token getReturnType() {
            return returnType;
        }
        
        public List<Parameter> getParameters() {
            return parameters;
        }
        
        public List<Stmt> getBody() {
            return body;
        }
        
        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }
    
    /**
     * Parámetro de función
     */
    public static class Parameter {
        private final Token name;
        private final Token type;
        
        public Parameter(Token name, Token type) {
            this.name = name;
            this.type = type;
        }
        
        public Token getName() {
            return name;
        }
        
        public Token getType() {
            return type;
        }
    }
} 