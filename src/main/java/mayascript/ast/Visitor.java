package mayascript.ast;

/**
 * Interfaz Visitor para recorrer el árbol de sintaxis abstracta (AST).
 * Implementa el patrón de diseño Visitor para separar los algoritmos de la estructura del AST.
 */
public interface Visitor<T> {
    // Métodos para visitar expresiones
    T visitLiteralExpr(Expr.Literal expr);
    T visitGroupingExpr(Expr.Grouping expr);
    T visitUnaryExpr(Expr.Unary expr);
    T visitBinaryExpr(Expr.Binary expr);
    T visitLogicalExpr(Expr.Logical expr);
    T visitVariableExpr(Expr.Variable expr);
    T visitAssignExpr(Expr.Assign expr);
    T visitCallExpr(Expr.Call expr);
    T visitInputExpr(Expr.Input expr);
    
    // Métodos para visitar sentencias
    T visitProgramStmt(Stmt.Program stmt);
    T visitExpressionStmt(Stmt.Expression stmt);
    T visitPrintStmt(Stmt.Print stmt);
    T visitVarStmt(Stmt.Var stmt);
    T visitConstStmt(Stmt.Const stmt);
    T visitBlockStmt(Stmt.Block stmt);
    T visitIfStmt(Stmt.If stmt);
    T visitWhileStmt(Stmt.While stmt);
    T visitReturnStmt(Stmt.Return stmt);
    T visitFunctionStmt(Stmt.Function stmt);
} 