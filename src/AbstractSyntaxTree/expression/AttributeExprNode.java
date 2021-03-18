package AbstractSyntaxTree.expression;

import static InternalRepresentation.Instructions.BranchInstruction.BranchOperation.BL;
import static InternalRepresentation.Utils.BuiltInFunction.CustomBuiltIn.NULL_POINTER;
import static InternalRepresentation.Utils.Register.DEST_REG;

import AbstractSyntaxTree.ASTNode;
import InternalRepresentation.Instructions.BranchInstruction;
import InternalRepresentation.Instructions.LdrInstruction;
import InternalRepresentation.Instructions.LdrInstruction.LdrType;
import InternalRepresentation.Instructions.MovInstruction;
import InternalRepresentation.InternalState;
import InternalRepresentation.Utils.Register;
import SemanticAnalysis.DataTypeId;
import SemanticAnalysis.DataTypes.ClassType;
import SemanticAnalysis.DataTypes.PairType;
import SemanticAnalysis.Identifier;
import SemanticAnalysis.SemanticError;
import SemanticAnalysis.SymbolTable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AttributeExprNode extends ExpressionNode {

  private static final int ADDRESS_BYTE_SIZE = 4;
  private final IdentifierNode objectName;
  private final IdentifierNode attributeName;

  public AttributeExprNode(int line, int charPositionInLine, IdentifierNode objectName,
      IdentifierNode attributeName) {
    super(line, charPositionInLine);
    this.objectName = objectName;
    this.attributeName = attributeName;
  }

  public String getObjectName() {
    return objectName.getIdentifier();
  }

  public int getAttributeIndex(SymbolTable symbolTable) {
      ClassType classType =  (ClassType) objectName.getType(symbolTable);
      return classType.findIndexAttribute(attributeName);
  }

  @Override
  public DataTypeId getType(SymbolTable symbolTable) {
    DataTypeId objectType = objectName.getType(symbolTable);
    if (!(objectType instanceof ClassType)) {
      return null;
    } else {
      ClassType classType = (ClassType) objectType;
      SymbolTable classSymbolTable = classType.getAttributes().get(0).getCurrSymTable();

      /* Check if such an attribute exists for this class */
      Identifier attribute = classSymbolTable.lookup("attr*" + attributeName.getIdentifier());
      if(attribute == null) {
        return null;
      } else {
        return attribute.getType();
      }
    }
  }


  @Override
  public String toString() {
    return null;
  }

  @Override
  public void semanticAnalysis(SymbolTable symbolTable, List<SemanticError> errorMessages,
      List<ASTNode> uncheckedNodes, boolean firstCheck) {
    /* Check class of object */
    DataTypeId objectType = objectName.getType(symbolTable);
    if (!(objectType instanceof ClassType)) {
      errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
              "Cannot get attribute of a non-object."   + " Expected: CLASS TYPE "
              + " Actual: " + objectType));
    } else {
      ClassType classType = (ClassType) objectType;
      SymbolTable classSymbolTable = classType.getAttributes().get(0).getCurrSymTable();

      /* Check if such an attribute exists for this class */
      if(classSymbolTable.lookup(attributeName.getIdentifier()) == null) {
        errorMessages.add(new SemanticError(objectName.getLine(), objectName.getCharPositionInLine(),
                "Attribute with name '" + attributeName.getIdentifier() + "' has not been declared for class '"
                + classType.getClassName()
                + " Expected: [" + classType.getAttributes().stream().map(Objects::toString).collect(Collectors.toList())
                + "] Actual: " + attributeName.getIdentifier()));
      }
    }
  }


  // get offset of object from symbolTable add to that index of attriburw from attribute list of class
  // load from that offset + index * ADDRESS_SIZE
  @Override
  public void generateAssembly(InternalState internalState) {
    SymbolTable currSymbolTable = getCurrSymTable();

    /* Visit and generate assembly code for the attribute identifier */
    objectName.generateAssembly(internalState);

    /* Get register where offset of object will be stored */
    Register objectReg = internalState.peekFreeRegister();

    internalState.addInstruction(new MovInstruction(DEST_REG, objectReg));

    /* Check for null pointer exception */
    internalState.addInstruction(new BranchInstruction(BL, NULL_POINTER));

    /* Get index of attribute in list of class attributes and load it */
    ClassType classType = (ClassType) this.getType(currSymbolTable);
    int attributeIndex = classType.findIndexAttribute(attributeName);
    internalState.addInstruction(
        new LdrInstruction(LdrType.LDR, objectReg, objectReg, attributeIndex * ADDRESS_BYTE_SIZE));

    /* Calculate type of Ldr instruction based on the size of the attribute */
    DataTypeId type = attributeName.getType(currSymbolTable);
    int elemSize = type.getSize();
    LdrType ldrInstr = (elemSize == 1) ? LdrType.LDRSB : LdrType.LDR;

    internalState.addInstruction(new LdrInstruction(ldrInstr, objectReg, objectReg));
  }
}