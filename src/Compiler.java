import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import antlr.*;
public class Compiler {

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
    // add our own error listener
    ParseTree tree = parser.program();

    System.out.println(tree.toStringTree(parser));

    // check if error listener encountered any errors and if so exit with an error code
    // otherwise continue with semantics checking
  }
}
