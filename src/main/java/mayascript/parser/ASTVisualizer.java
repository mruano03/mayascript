package mayascript.parser;

import mayascript.ast.Expr;
import mayascript.ast.Stmt;
import mayascript.ast.Visitor;
import mayascript.lexer.Token;
import mayascript.lexer.TokenType;

import java.util.List;

/**
 * Clase para visualizar el árbol de derivación (parse tree) a partir del AST.
 * Implementa el patrón Visitor para recorrer todo el árbol.
 */
public class ASTVisualizer implements Visitor<String> {

    private final StringBuilder builder = new StringBuilder();
    private int indentLevel = 0;
    
    /**
     * Visualiza un programa completo.
     * @param program El programa a visualizar
     * @return La visualización del árbol de derivación
     */
    public String visualize(Stmt.Program program) {
        builder.setLength(0);
        indentLevel = 0;
        
        builder.append("<programa>\n");
        indentLevel++;
        appendIndented("<mayab>\n");
        
        // Procesar todas las declaraciones
        for (Stmt stmt : program.getStatements()) {
            String result = stmt.accept(this);
            appendIndented(result);
        }
        
        appendIndented("<ahau>\n");
        indentLevel--;
        builder.append("</programa>\n");
        
        return builder.toString();
    }
    
    /**
     * Añade texto con la indentación actual
     */
    private void appendIndented(String text) {
        for (int i = 0; i < indentLevel; i++) {
            builder.append("    ");
        }
        builder.append(text);
    }
    
    // Métodos para visitar expresiones
    
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        Object value = expr.getValue();
        if (value == null) {
            return "<literal>waaj</literal>\n";
        } else if (value instanceof Boolean) {
            boolean boolValue = (Boolean) value;
            return "<literal>" + (boolValue ? "haab" : "manik") + "</literal>\n";
        } else if (value instanceof Double || value instanceof Integer) {
            return "<literal><numero>" + value + "</numero></literal>\n";
        } else {
            return "<literal><cadena>" + value + "</cadena></literal>\n";
        }
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        StringBuilder result = new StringBuilder("<agrupacion>\n");
        indentLevel++;
        appendIndented("<parentesis_izquierdo>(</parentesis_izquierdo>\n");
        
        String expression = expr.getExpression().accept(this);
        appendIndented(expression);
        
        appendIndented("<parentesis_derecho>)</parentesis_derecho>\n");
        indentLevel--;
        appendIndented("</agrupacion>\n");
        
        return result.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        StringBuilder result = new StringBuilder("<expresion_unaria>\n");
        indentLevel++;
        
        String operador = "<operador>" + expr.getOperator().getLexeme() + "</operador>\n";
        appendIndented(operador);
        
        String right = expr.getRight().accept(this);
        appendIndented(right);
        
        indentLevel--;
        return result.append("</expresion_unaria>\n").toString();
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        StringBuilder result = new StringBuilder("<expresion_binaria>\n");
        indentLevel++;
        
        String left = expr.getLeft().accept(this);
        appendIndented(left);
        
        String operador = "<operador>" + expr.getOperator().getLexeme() + "</operador>\n";
        appendIndented(operador);
        
        String right = expr.getRight().accept(this);
        appendIndented(right);
        
        indentLevel--;
        return result.append("</expresion_binaria>\n").toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        StringBuilder result = new StringBuilder("<expresion_logica>\n");
        indentLevel++;
        
        String left = expr.getLeft().accept(this);
        appendIndented(left);
        
        String operador = "<operador>" + expr.getOperator().getLexeme() + "</operador>\n";
        appendIndented(operador);
        
        String right = expr.getRight().accept(this);
        appendIndented(right);
        
        indentLevel--;
        return result.append("</expresion_logica>\n").toString();
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return "<variable>" + expr.getName().getLexeme() + "</variable>\n";
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        StringBuilder result = new StringBuilder("<asignacion>\n");
        indentLevel++;
        
        appendIndented("<identificador>" + expr.getName().getLexeme() + "</identificador>\n");
        appendIndented("<operador>=</operador>\n");
        
        String value = expr.getValue().accept(this);
        appendIndented(value);
        
        indentLevel--;
        return result.append("</asignacion>\n").toString();
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        StringBuilder result = new StringBuilder("<llamada_funcion>\n");
        indentLevel++;
        
        String callee = expr.getCallee().accept(this);
        appendIndented(callee);
        
        appendIndented("<parentesis_izquierdo>(</parentesis_izquierdo>\n");
        
        List<Expr> arguments = expr.getArguments();
        if (!arguments.isEmpty()) {
            appendIndented("<argumentos>\n");
            indentLevel++;
            
            for (int i = 0; i < arguments.size(); i++) {
                String argument = arguments.get(i).accept(this);
                appendIndented(argument);
                
                if (i < arguments.size() - 1) {
                    appendIndented("<separador>,</separador>\n");
                }
            }
            
            indentLevel--;
            appendIndented("</argumentos>\n");
        }
        
        appendIndented("<parentesis_derecho>)</parentesis_derecho>\n");
        
        indentLevel--;
        return result.append("</llamada_funcion>\n").toString();
    }

    @Override
    public String visitInputExpr(Expr.Input expr) {
        return "<entrada>" + expr.getKeyword().getLexeme() + "</entrada>\n";
    }
    
    // Métodos para visitar sentencias

    @Override
    public String visitProgramStmt(Stmt.Program stmt) {
        StringBuilder result = new StringBuilder("<programa>\n");
        indentLevel++;
        
        for (Stmt statement : stmt.getStatements()) {
            String stmtText = statement.accept(this);
            appendIndented(stmtText);
        }
        
        indentLevel--;
        return result.append("</programa>\n").toString();
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        StringBuilder result = new StringBuilder("<sentencia_expresion>\n");
        indentLevel++;
        
        String expr = stmt.getExpression().accept(this);
        appendIndented(expr);
        
        appendIndented("<punto_coma>;</punto_coma>\n");
        
        indentLevel--;
        return result.append("</sentencia_expresion>\n").toString();
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        StringBuilder result = new StringBuilder("<sentencia_impresion>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>pakal</palabra_clave>\n");
        
        String expr = stmt.getExpression().accept(this);
        appendIndented(expr);
        
        appendIndented("<punto_coma>;</punto_coma>\n");
        
        indentLevel--;
        return result.append("</sentencia_impresion>\n").toString();
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        StringBuilder result = new StringBuilder("<declaracion_variable>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>tzolk</palabra_clave>\n");
        appendIndented("<tipo>" + stmt.getType().getLexeme() + "</tipo>\n");
        appendIndented("<identificador>" + stmt.getName().getLexeme() + "</identificador>\n");
        
        if (stmt.getInitializer() != null) {
            appendIndented("<operador>=</operador>\n");
            String initializer = stmt.getInitializer().accept(this);
            appendIndented(initializer);
        }
        
        appendIndented("<punto_coma>;</punto_coma>\n");
        
        indentLevel--;
        return result.append("</declaracion_variable>\n").toString();
    }

    @Override
    public String visitConstStmt(Stmt.Const stmt) {
        StringBuilder result = new StringBuilder("<declaracion_constante>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>tuun</palabra_clave>\n");
        appendIndented("<tipo>" + stmt.getType().getLexeme() + "</tipo>\n");
        appendIndented("<identificador>" + stmt.getName().getLexeme() + "</identificador>\n");
        appendIndented("<operador>=</operador>\n");
        
        String initializer = stmt.getInitializer().accept(this);
        appendIndented(initializer);
        
        appendIndented("<punto_coma>;</punto_coma>\n");
        
        indentLevel--;
        return result.append("</declaracion_constante>\n").toString();
    }

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder result = new StringBuilder("<bloque>\n");
        indentLevel++;
        
        appendIndented("<llave_izquierda>{</llave_izquierda>\n");
        
        for (Stmt statement : stmt.getStatements()) {
            String stmtText = statement.accept(this);
            appendIndented(stmtText);
        }
        
        appendIndented("<llave_derecha>}</llave_derecha>\n");
        
        indentLevel--;
        return result.append("</bloque>\n").toString();
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        StringBuilder result = new StringBuilder("<sentencia_if>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>chac</palabra_clave>\n");
        appendIndented("<parentesis_izquierdo>(</parentesis_izquierdo>\n");
        
        String condition = stmt.getCondition().accept(this);
        appendIndented(condition);
        
        appendIndented("<parentesis_derecho>)</parentesis_derecho>\n");
        
        String thenBranch = stmt.getThenBranch().accept(this);
        appendIndented(thenBranch);
        
        if (stmt.getElseBranch() != null) {
            appendIndented("<palabra_clave>imix</palabra_clave>\n");
            String elseBranch = stmt.getElseBranch().accept(this);
            appendIndented(elseBranch);
        }
        
        indentLevel--;
        return result.append("</sentencia_if>\n").toString();
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        StringBuilder result = new StringBuilder("<sentencia_while>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>muluc</palabra_clave>\n");
        appendIndented("<parentesis_izquierdo>(</parentesis_izquierdo>\n");
        
        String condition = stmt.getCondition().accept(this);
        appendIndented(condition);
        
        appendIndented("<parentesis_derecho>)</parentesis_derecho>\n");
        
        String body = stmt.getBody().accept(this);
        appendIndented(body);
        
        indentLevel--;
        return result.append("</sentencia_while>\n").toString();
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        StringBuilder result = new StringBuilder("<sentencia_retorno>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>nik</palabra_clave>\n");
        
        if (stmt.getValue() != null) {
            String value = stmt.getValue().accept(this);
            appendIndented(value);
        }
        
        appendIndented("<punto_coma>;</punto_coma>\n");
        
        indentLevel--;
        return result.append("</sentencia_retorno>\n").toString();
    }

    @Override
    public String visitFunctionStmt(Stmt.Function stmt) {
        StringBuilder result = new StringBuilder("<declaracion_funcion>\n");
        indentLevel++;
        
        appendIndented("<palabra_clave>kaban</palabra_clave>\n");
        appendIndented("<tipo_retorno>" + stmt.getReturnType().getLexeme() + "</tipo_retorno>\n");
        appendIndented("<identificador>" + stmt.getName().getLexeme() + "</identificador>\n");
        appendIndented("<parentesis_izquierdo>(</parentesis_izquierdo>\n");
        
        List<Stmt.Parameter> parameters = stmt.getParameters();
        if (!parameters.isEmpty()) {
            appendIndented("<parametros>\n");
            indentLevel++;
            
            for (int i = 0; i < parameters.size(); i++) {
                Stmt.Parameter param = parameters.get(i);
                appendIndented("<parametro>\n");
                indentLevel++;
                appendIndented("<tipo>" + param.getType().getLexeme() + "</tipo>\n");
                appendIndented("<identificador>" + param.getName().getLexeme() + "</identificador>\n");
                indentLevel--;
                appendIndented("</parametro>\n");
                
                if (i < parameters.size() - 1) {
                    appendIndented("<separador>,</separador>\n");
                }
            }
            
            indentLevel--;
            appendIndented("</parametros>\n");
        }
        
        appendIndented("<parentesis_derecho>)</parentesis_derecho>\n");
        
        appendIndented("<llave_izquierda>{</llave_izquierda>\n");
        
        for (Stmt statement : stmt.getBody()) {
            String stmtText = statement.accept(this);
            appendIndented(stmtText);
        }
        
        appendIndented("<llave_derecha>}</llave_derecha>\n");
        
        indentLevel--;
        return result.append("</declaracion_funcion>\n").toString();
    }
} 