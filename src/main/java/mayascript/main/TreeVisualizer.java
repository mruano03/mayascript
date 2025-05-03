package mayascript.main;

import mayascript.ast.Stmt;
import mayascript.errors.ErrorHandler;
import mayascript.errors.SyntaxError;
import mayascript.lexer.Lexer;
import mayascript.lexer.Token;
import mayascript.parser.ASTVisualizer;
import mayascript.parser.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal para generar árboles de derivación para cada entrada.
 */
public class TreeVisualizer {
    private static boolean showErrorTable = true;

    /**
     * Punto de entrada para la generación de árboles de derivación.
     * @param args Argumentos de línea de comandos. El primer argumento debe ser la ruta al archivo a analizar.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: java TreeVisualizer <ruta-archivo> [--no-error-table]");
            System.exit(1);
        }
        
        String sourcePath = args[0];
        
        // Verificar opciones
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--no-error-table")) {
                showErrorTable = false;
            } else {
                System.err.println("Opción no reconocida: " + args[i]);
                System.err.println("Uso: java TreeVisualizer <ruta-archivo> [--no-error-table]");
                System.exit(1);
            }
        }
        
        try {
            String sourceCode = readFile(sourcePath);
            generateParseTree(sourceCode, sourcePath);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Lee el contenido de un archivo.
     * @param filePath Ruta al archivo
     * @return Contenido del archivo como String
     * @throws IOException Si hay un error al leer el archivo
     */
    private static String readFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Genera y muestra el árbol de derivación para el código fuente.
     * @param sourceCode Código fuente a analizar
     * @param sourcePath Ruta al archivo para mostrar en mensajes de error
     */
    private static void generateParseTree(String sourceCode, String sourcePath) {
        // Crear el manejador de errores
        ErrorHandler errorHandler = new ErrorHandler();
        
        // Análisis léxico
        Lexer lexer = new Lexer(sourceCode, errorHandler);
        List<Token> tokens = lexer.scanTokens();
        
        // Si hay errores léxicos, mostrarlos y salir
        if (errorHandler.hadError()) {
            System.err.println("Errores léxicos encontrados:");
            if (showErrorTable) {
                errorHandler.printErrorTable();
            } else {
                errorHandler.getErrors().forEach(System.err::println);
            }
            
            // Mostrar estadísticas de errores
            System.err.println("\nResumen de errores léxicos:");
            System.err.println("Total: " + errorHandler.getErrorTable().getErrorCount(SyntaxError.ErrorType.LEXICAL) + " errores");
            return;
        }
        
        // Análisis sintáctico
        Parser parser = new Parser(tokens, errorHandler);
        Stmt.Program program = parser.parse();
        
        // Si hay errores sintácticos, mostrarlos y salir
        if (errorHandler.hadError()) {
            System.err.println("Errores sintácticos encontrados:");
            if (showErrorTable) {
                errorHandler.printErrorTable();
            } else {
                errorHandler.getErrors().forEach(System.err::println);
            }
            
            // Mostrar estadísticas de errores
            int lexicalErrors = errorHandler.getErrorTable().getErrorCount(SyntaxError.ErrorType.LEXICAL);
            int syntaxErrors = errorHandler.getErrorTable().getErrorCount(SyntaxError.ErrorType.SYNTAX);
            
            System.err.println("\nResumen de errores:");
            System.err.println("- Errores léxicos: " + lexicalErrors);
            System.err.println("- Errores sintácticos: " + syntaxErrors);
            System.err.println("Total: " + (lexicalErrors + syntaxErrors) + " errores");
            return;
        }
        
        // Visualizar el árbol de derivación
        ASTVisualizer visualizer = new ASTVisualizer();
        String parseTree = visualizer.visualize(program);
        
        // Generar el archivo de salida con el árbol de derivación
        String outputPath = sourcePath.replaceAll("\\.maya$", "_tree.xml");
        try {
            Files.write(Paths.get(outputPath), parseTree.getBytes(StandardCharsets.UTF_8));
            System.out.println("Árbol de derivación generado exitosamente en: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error al escribir el árbol de derivación: " + e.getMessage());
        }
        
        // Imprimir el árbol de derivación en la consola
        System.out.println("\nÁrbol de derivación:");
        System.out.println(parseTree);
    }
} 