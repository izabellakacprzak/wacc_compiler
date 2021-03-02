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

import static InternalRepresentation.Enums.ConditionCode.VS;

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
        BuiltInFunction.PRINT_STRING.setUsed();
        internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
            BuiltInFunction.PRINT_STRING.getLabel()));
      } else {
        BuiltInFunction.PRINT_REFERENCE.setUsed();
        internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
            BuiltInFunction.PRINT_REFERENCE.getLabel()));
      }
    } else if (type instanceof PairType) {
      BuiltInFunction.PRINT_REFERENCE.setUsed();
      internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
          BuiltInFunction.PRINT_REFERENCE.getLabel()));
    } else if (type instanceof BaseType) {
      BaseType.Type baseType = ((BaseType) type).getBaseType();
      switch (baseType) {
        case CHAR:
        case STRING:
          BuiltInFunction.PRINT_STRING.setUsed();
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_STRING.getLabel()));
          break;
        case BOOL:
          BuiltInFunction.PRINT_BOOL.setUsed();
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_BOOL.getLabel()));
          break;
        case INT:
          BuiltInFunction.PRINT_INT.setUsed();
          internalState.addInstruction(new BranchInstruction(BranchOperation.BL,
              BuiltInFunction.PRINT_INT.getLabel()));
          break;
      }
    }
  }

  @Override
  public SymbolTable getCurrSymTable() {
    return currSymTable;
  }
}