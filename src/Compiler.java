import static AbstractSyntaxTree.ASTNode.DEFAULT_TYPE;
import static SemanticAnalysis.DataTypes.BaseType.Type.INT;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.ProgramNode;
import AbstractSyntaxTree.type.FunctionNode;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.ParameterId;
import SemanticAnalysis.SymbolTable;
import antlr.WACCLexer;
import antlr.WACCParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Compiler {

  private static final boolean FIRST_CHECK = true;
  private static final int SYNTAX_ERROR_CODE = 100;
  private static final int INPUT_FILE_ERROR = 1;
  private static final int SEMANTIC_ERROR_CODE = 200;

  public static void main(String[] args) {

    /* Checks the number of arguments of the program and gets as input
     * the path to the .wacc file to compile. */
    if (args.length > 2) {
      throw new IllegalArgumentException("Incorrect number of arguments received");
    }

    /* if mode is -p then perform syntax check
     * if mode is -s then perform syntax and semantic check
     * if mode is -a then perform syntax and semantic check, generate assembly and save to .s file
     * if mode is -h then print help message */
    String mode = "-a";
    if (args.length == 2) {
      mode = args[1];
    }

    if(mode.equals("-h")) {
      System.out.println(
          "if mode is -p then perform syntax check\n"
              + "if mode is -s then perform syntax and semantic check\n"
              + "if mode is -a then perform syntax and semantic check, "
              + "generate assembly and save to .s file\n"
              + "if mode is -h then print help message");
      return;
    }

    CharStream input = null;
    try {
      input = CharStreams.fromFileName(args[0]);

    } catch (IOException e) {
      System.out.println("File not found");
      System.exit(INPUT_FILE_ERROR);
    }

    /* Creates a new lexer using the WACCLexer.g4 defined grammar and
     * removes the default antlr lexer syntax error listener. */
    WACCLexer lexer = new WACCLexer(input);
    lexer.removeErrorListeners();
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    /* Creates a new parser using the WACCParser.g4 defined rules and
     * removes the default antlr parser syntax error listener. */
    WACCParser parser = new WACCParser(tokens);
    parser.removeErrorListeners();
    SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();

    /* Adds our new defined syntax error listener. */
    parser.addErrorListener(syntaxErrorListener);

    ParseTree tree = parser.program();

    /* If the  syntax error listener has been notified of error during parsing, then the
     * accumulated errors are printed after parsing and the program exits with the syntax
     * error code. */
    if (syntaxErrorListener.hasSyntaxErrors()) {
      syntaxErrorListener.printAllErrors();
      System.out.println(
          syntaxErrorListener.syntaxErrorsCount() + " syntax errors detected, exiting...");
      System.exit(SYNTAX_ERROR_CODE);
    }

    ASTVisitor visitor = new ASTVisitor();
    ProgramNode prog = (ProgramNode) visitor.visit(tree);

    /* If the syntax error listener has been notified of errors regarding missing exit or return statements
     * during visiting the AST, then the accumulated errors are printed after and the program exits with
     * the syntax error code. */
    List<String> statementErrors = prog.checkSyntaxErrors();
    if (!statementErrors.isEmpty()) {
      for (String e : statementErrors) {
        System.out.println(e);
      }
      System.out.println(statementErrors.size() + " syntax errors detected, exiting");
      System.exit(SYNTAX_ERROR_CODE);
    }

    if (mode.equals("-s") || mode.equals("-a")) {
      /* If there are no syntax errors, it proceeds to semantic error checking and creates a new
       * semanticErrorListener object. */
      SemanticErrorListener semanticErrorListener = new SemanticErrorListener();
      SymbolTable topSymbolTable = new SymbolTable(null);
      List<ASTNode> uncheckedNodes = new ArrayList<>();

      prog.semanticAnalysis(topSymbolTable, semanticErrorListener.getList(), uncheckedNodes,
          FIRST_CHECK);

      int i = 0;
      while (!(i == uncheckedNodes.size())) {
        ASTNode node = uncheckedNodes.get(i);
        node.semanticAnalysis(node.getCurrSymTable(), semanticErrorListener.getList(),
            uncheckedNodes, !FIRST_CHECK);
        i++;
      }

      List<FunctionNode> functionNodes = prog.getFunctionNodes();

      for (FunctionNode function : functionNodes) {
        List<ParameterId> params = function.getParams().getParameterIds();
        for (ParameterId param : params) {
          if (param.getType() == null) {
            param.setType(DEFAULT_TYPE);
          }

          if (param.isUnsetArray()) {
            param.setBaseElemType(DEFAULT_TYPE);
          }
        }
      }

      /* If the  semantic error listener has been notified of error during semantic checking ,
       * then the accumulated errors are printed after and the program exits with the semantic
       * error code. */
      if (semanticErrorListener.hasSemanticErrors()) {
        semanticErrorListener.printAllErrors();
        System.out.println(
            semanticErrorListener.semanticErrorsCount() + " semantic errors detected, exiting...");
        System.exit(SEMANTIC_ERROR_CODE);
      }

      if (mode.equals("-a")) {
        String[] path = args[0].split("/");

        File assemblyFile = new File(path[path.length - 1].replaceFirst(".wacc", ".s"));
        try {
          InternalState state = new InternalState();
          assemblyFile.delete();

          if (assemblyFile.createNewFile()) {
            state.generateAssembly(assemblyFile, prog);
          }
        } catch (IOException e) {
          System.out.println("Could not create new file");
        }
      }
    }
  }
}
