package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PrintStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'print' was called with */
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public PrintStatementNode(ExpressionNode expression) {
    this.expression = expression;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<String> errorMessages) {
    currSymTable = symbolTable;
    /* Recursively call semanticAnalysis on expression node */
    expression.semanticAnalysis(symbolTable, errorMessages);
  }

  @Override
  public void generateAssembly(InternalState internalState) {
    expression.generateAssembly(internalState);
    Register nextAvailable = internalState.peekFreeRegister();

    internalState.addInstruction(new MovInstruction(Register.R0, nextAvailable));
    DataTypeId type = expression.getType(currSymTable);

    if (type instanceof ArrayType) {
      if (((ArrayType) type).getElemType().equals(new BaseType(BaseType.Type.CHAR))) {
        internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
            BuiltInFunction.PRINT_STRING));
      } else {
        internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
            BuiltInFunction.PRINT_REFERENCE));
      }
    } else if (type instanceof PairType) {
      internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
          BuiltInFunction.PRINT_REFERENCE));
    } else if (type instanceof BaseType) {
      BaseType.Type baseType = ((BaseType) type).getBaseType();
      switch (baseType) {
        case CHAR:
        case STRING:
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_STRING));
          break;
        case BOOL:
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_BOOL));
          break;
        case INT:
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_INT));
          break;
      }
    }
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}