package mayascript.main;

import mayascript.lexer.Lexer;
import mayascript.lexer.Token;
import mayascript.parser.Parser;
import mayascript.ast.Stmt;
import mayascript.ast.Expr;
import mayascript.errors.ErrorHandler;
import mayascript.errors.SyntaxError;
import mayascript.parser.ASTVisualizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del minicompilador MayaScript
 */
public class MayaScript {
    private static final ErrorHandler errorHandler = new ErrorHandler();
    private static boolean hadError = false;
    // Siempre mostrar el árbol por defecto
    private static boolean showParseTree = true;
    // Siempre mostrar la tabla de errores
    private static boolean showErrorTable = true;
    
    // Lista para almacenar resultados de ejecución
    private static List<String> executionOutput = new ArrayList<>();
    
    public static void main(String[] args) throws IOException {
        if (args.length > 3) {
            System.out.println("Uso: mayascript [archivo] [--no-tree] [--no-error-table]");
            System.exit(64);
        } else if (args.length >= 1) {
            // Verificar opciones
            for (int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "--no-tree":
                        showParseTree = false;
                        break;
                    case "--no-error-table":
                        showErrorTable = false;
                        break;
                    default:
                        System.out.println("Opción no reconocida: " + args[i]);
                        System.out.println("Uso: mayascript [archivo] [--no-tree] [--no-error-table]");
                        System.exit(64);
                }
            }
            
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
    
    /**
     * Ejecuta un archivo de código MayaScript
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        
        if (hadError) {
            System.exit(65);
        }
    }
    
    /**
     * Inicia un intérprete interactivo
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        
        System.out.println("MayaScript v1.0 - Interprete interactivo");
        System.out.println("Escribe 'salir' para terminar");
        System.out.println("Escribe 'tree' para activar/desactivar el árbol de derivación");
        System.out.println("Escribe 'table' para activar/desactivar la tabla de errores");
        System.out.println("El árbol de derivación está ACTIVADO por defecto");
        System.out.println("La tabla de errores está ACTIVADA por defecto");
        
        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.equals("salir")) break;
            
            // Ignorar líneas vacías o que solo contienen espacios en blanco
            if (line.trim().isEmpty()) {
                continue;
            }
            
            // Activar/desactivar árbol de derivación
            if (line.trim().equals("tree")) {
                showParseTree = !showParseTree;
                System.out.println("Árbol de derivación: " + (showParseTree ? "activado" : "desactivado"));
                continue;
            }
            
            // Activar/desactivar tabla de errores
            if (line.trim().equals("table")) {
                showErrorTable = !showErrorTable;
                System.out.println("Tabla de errores: " + (showErrorTable ? "activada" : "desactivada"));
                continue;
            }
            
            // Limpiar la salida de ejecuciones anteriores
            executionOutput.clear();
            
            run(line);
            hadError = false;
        }
    }
    
    /**
     * Ejecuta el código fuente de MayaScript
     */
    private static void run(String source) {
        // Limpiar la salida de ejecuciones anteriores
        executionOutput.clear();
        
        // Reiniciar el manejador de errores
        errorHandler.reset();
        
        // Análisis léxico
        Lexer lexer = new Lexer(source, errorHandler);
        List<Token> tokens = lexer.scanTokens();
        
        // Análisis sintáctico
        Parser parser = new Parser(tokens, errorHandler);
        Stmt.Program program = parser.parse();
        
        // Si hay errores, no continuar
        if (errorHandler.hadError()) {
            hadError = true;
            System.out.println("\nEl análisis ha detectado errores. No se puede ejecutar el programa.");
            
            // Mostrar la tabla de errores si está activada
            if (showErrorTable) {
                errorHandler.printErrorTable();
            } else {
                errorHandler.printErrors();
            }
            
            // Mostrar estadísticas de errores
            int lexicalErrors = errorHandler.getErrorTable().getErrorCount(SyntaxError.ErrorType.LEXICAL);
            int syntaxErrors = errorHandler.getErrorTable().getErrorCount(SyntaxError.ErrorType.SYNTAX);
            int semanticErrors = errorHandler.getErrorTable().getErrorCount(SyntaxError.ErrorType.SEMANTIC);
            
            System.out.println("\nResumen de errores:");
            System.out.println("- Errores léxicos: " + lexicalErrors);
            System.out.println("- Errores sintácticos: " + syntaxErrors);
            System.out.println("- Errores semánticos: " + semanticErrors);
            System.out.println("Total: " + errorHandler.getErrorTable().getErrorCount() + " errores");
            
            return;
        }
        
        // Ejecutar el programa (simular la ejecución recopilando la salida de los comandos pakal)
        executeProgram(program);
        
        // Mostrar los tokens (para depuración)
        System.out.println("\n=== Tokens ===");
        for (Token token : tokens) {
            System.out.println(token);
        }
        
        // Mostrar el AST (para depuración)
        System.out.println("\n=== Árbol de Sintaxis Abstracta ===");
        System.out.println(program.getStatements().size() + " sentencias encontradas");
        
        // Mostrar el árbol de derivación si está activado (ahora por defecto)
        if (showParseTree) {
            System.out.println("\n=== Árbol de Derivación ===");
            ASTVisualizer visualizer = new ASTVisualizer();
            String parseTree = visualizer.visualize(program);
            System.out.println(parseTree);
        }
        
        // Mostrar el resultado de la ejecución
        System.out.println("\n=== Resultado de la Ejecución ===");
        if (executionOutput.isEmpty()) {
            System.out.println("(No hay salida)");
        } else {
            for (String line : executionOutput) {
                System.out.println(line);
            }
        }
        
        System.out.println("\nAnálisis completado con éxito.");
    }
    
    /**
     * Simula la ejecución del programa
     * @param program El programa a ejecutar
     */
    private static void executeProgram(Stmt.Program program) {
        // Un simple "intérprete" para simular la ejecución de los comandos pakal
        for (Stmt statement : program.getStatements()) {
            if (statement instanceof Stmt.Print) {
                Stmt.Print printStmt = (Stmt.Print)statement;
                String output = evaluateExpression(printStmt.getExpression());
                executionOutput.add(output);
            }
            // Para futura implementación de otras sentencias
        }
    }
    
    /**
     * Evalúa una expresión para obtener su valor como String
     * @param expr La expresión a evaluar
     * @return El valor de la expresión como String
     */
    private static String evaluateExpression(Expr expr) {
        if (expr instanceof Expr.Literal) {
            Object value = ((Expr.Literal)expr).getValue();
            if (value == null) return "null";
            return value.toString();
        }
        // Para futura implementación de otras expresiones
        return "[expresión no evaluable]";
    }
} 