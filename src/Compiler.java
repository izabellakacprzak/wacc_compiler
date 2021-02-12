import AbstractSyntaxTree.ProgramNode;
import java.util.List;
import java.util.ArrayList;

import SemanticAnalysis.SymbolTable;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;
import antlr.*;

public class Compiler {
  private static final int SYNTAX_ERROR_CODE = 100;
  private static final int INPUT_FILE_ERROR = 1;
  private static final int SEMANTIC_ERROR_CODE = 200;


  public static void main(String[] args){


    if (args.length != 1) {
      throw new IllegalArgumentException("Incorrect number of arguments received");
    }

    CharStream input = null;
    try {
      input = CharStreams.fromFileName(args[0]);

    } catch (IOException e) {
      System.out.println("File not found");
      System.exit(INPUT_FILE_ERROR);
    }

    WACCLexer lexer = new WACCLexer(input);
    lexer.removeErrorListeners();
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    WACCParser parser = new WACCParser(tokens);
    parser.removeErrorListeners();
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
    parser.addErrorListener(syntaxErrorListener);

    ParseTree tree = parser.program();

    if (syntaxErrorListener.hasSyntaxErrors()) {
      syntaxErrorListener.printAllErrors();
      System.out.println(syntaxErrorListener.syntaxErrorsCount() + " syntax errors detected, exiting...");
      System.exit(SYNTAX_ERROR_CODE);
    }


    ASTVisitor visitor = new ASTVisitor();
    ProgramNode prog = (ProgramNode) visitor.visit(tree);

    List<String> statementErrors = prog.checkSyntaxErrors();
    if (!statementErrors.isEmpty()) {
      for (String e : statementErrors) {
        System.out.println(e);
      }
      System.out.println(statementErrors.size() + " syntax errors detected, exiting");
      System.exit(SYNTAX_ERROR_CODE);
    }

    System.out.println(tree.toStringTree(parser));

    // check if error listener encountered any errors and if so exit with an error code
    // otherwise continue with semantics checking

    SemanticErrorListener semanticErrorListener = new SemanticErrorListener();
    SymbolTable topSymbolTable = new SymbolTable(null);

    prog.semanticAnalysis(topSymbolTable, semanticErrorListener.getList());

    if (semanticErrorListener.hasSemanticErrors()) {
      semanticErrorListener.printAllErrors();
      System.out.println(semanticErrorListener.semanticErrorsCount() + " semantic errors detected, exiting...");
      System.exit(SEMANTIC_ERROR_CODE);
    }
  }
}
