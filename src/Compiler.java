import AbstractSyntaxTree.ASTNode;
import SemanticAnalysis.SymbolTable;
import antlr.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;

public class Compiler {
  private static final int SYNTAX_ERROR_CODE = 100;
  private static final int SEMANTIC_ERROR_CODE = 200;


  public static void main(String[] args){

    if(args.length != 1) {
      throw new IllegalArgumentException("Incorrect number of arguments received");
    }

    CharStream input = null;
    try{
      input = CharStreams.fromFileName(args[0]);

    } catch (IOException e) {
      System.out.println("File not found");
    }

    WACCLexer lexer = new WACCLexer(input);
    lexer.removeErrorListeners();
    // add our own error  ??
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    WACCParser parser = new WACCParser(tokens);

    parser.removeErrorListeners();
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
    parser.addErrorListener(syntaxErrorListener);
    // add our own error listener
    ParseTree tree = parser.program();


    if(syntaxErrorListener.hasSyntaxErrors()){
      //TODO
      syntaxErrorListener.printAllErrors();
      System.exit(SYNTAX_ERROR_CODE);
    }

    ASTVisitor visitor = new ASTVisitor();
    ASTNode prog = visitor.visit(tree);
    System.out.println(tree.toStringTree(parser));

    // check if error listener encountered any errors and if so exit with an error code
    // otherwise continue with semantics checking

    List<String> semanticErrors = new ArrayList<>();
    SymbolTable topSymbolTable = new SymbolTable(null);

    // TODO: Create our own semantic listener
    prog.semanticAnalysis(topSymbolTable, semanticErrors);

    if (!(semanticErrors.isEmpty())) {
      for (String error : semanticErrors) {
        System.out.println(error);
      }
      System.exit(SEMANTIC_ERROR_CODE);
    }
  }
}
