package AbstractSyntaxTree.statement;

import AbstractSyntaxTree.expression.ExpressionNode;
import InternalRepresentation.Enums.BranchOperation;
import InternalRepresentation.Enums.BuiltInFunction;
import InternalRepresentation.Enums.Register;
import InternalRepresentation.Enums.SystemBuiltInFunction;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.SymbolTable;

import java.util.List;

public class PrintLineStatementNode extends StatementNode {

  /* expression:  ExpressionNode corresponding to the expression 'println' was called with */
  private final ExpressionNode expression;
  private SymbolTable currSymTable = null;

  public PrintLineStatementNode(ExpressionNode expression) {
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
    // TODO: ABSTRACT IN A FUNCTION IN VISITOR
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
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              SystemBuiltInFunction.PUTCHAR.getMessage()));
          break;
        case STRING:
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_STRING));
          break;
        case BOOL:
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_BOOL));
          break;
        case INT:
          BuiltInFunction.PRINT_INT.setUsed();
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_INT));
          break;
      }
    }

    internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
        BuiltInFunction.PRINT_LN));
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}