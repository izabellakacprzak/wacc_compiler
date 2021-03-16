package AbstractSyntaxTree.assignment;

import AbstractSyntaxTree.ASTNode;
import AbstractSyntaxTree.expression.ExpressionNode;
import AbstractSyntaxTree.expression.IdentifierNode;
import SemanticAnalysis.*;
import SemanticAnalysis.DataTypes.ArrayType;
import SemanticAnalysis.DataTypes.BaseType;

import java.util.ArrayList;
import java.util.List;

import static SemanticAnalysis.DataTypes.BaseType.Type.CHAR;
import static SemanticAnalysis.DataTypes.BaseType.Type.STRING;

public abstract class CallNode extends AssignRHSNode {

    public CallNode(int line, int charPositionInLine) {
        super(line, charPositionInLine);
    }

    /* Returns true when the declared type is a STRING and assigned type is a CHAR[] */
    private boolean stringToCharArray(DataTypeId declaredType, DataTypeId assignedType) {
        if (declaredType.equals(new BaseType(STRING))) {
            return assignedType.equals(new ArrayType(new BaseType(CHAR)));
        }

        return false;
    }

    public void semAnalyseFunctionArgs(SymbolTable symbolTable, List<SemanticError> errorMessages,
                                       IdentifierNode identifier, List<ExpressionNode> arguments,
                                       Identifier functionId, List<ASTNode> uncheckedNodes, boolean firstCheck) {
        List<DataTypeId> argTypes = new ArrayList<>();
        for (ExpressionNode arg : arguments) {
            argTypes.add(arg.getType(symbolTable));
        }

        /* Check that function has been called with the correct number of arguments */
        FunctionId function;
        if (functionId instanceof OverloadFuncId) {
            function = ((OverloadFuncId) functionId).findFunc(argTypes);
            if (function == null) {
                errorMessages.add(new SemanticError(
                        super.getLine(), super.getCharPositionInLine(),
                                "Function call'"
                                + functionId.toString()
                                + "' does not match any of the Overload signatures for function '"
                                + identifier.getIdentifier()
                                + "'"));
                return;
            }
        } else {
            function = (FunctionId) functionId;
        }

        List<DataTypeId> paramTypes = function.getParamTypes();

        if (paramTypes.size() > arguments.size() || paramTypes.size() < arguments.size()) {
            errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
                            "Function '"
                            + identifier.getIdentifier()
                            + "' has been called with the incorrect number of parameters."
                            + " Expected: "
                            + paramTypes.size()
                            + " Actual: "
                            + arguments.size()));
            return;
        }

        /* Check that each parameter's type can be resolved and matches the
         * corresponding argument type */
        for (int i = 0; i < arguments.size(); i++) {
            DataTypeId currArg = arguments.get(i).getType(symbolTable);
            DataTypeId currParamType = paramTypes.get(i);

            if (currParamType == null) {
                break;
            }

            if (currArg == null) {
                errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
                                "Could not resolve type of parameter "
                                + (i + 1)
                                + " in '"
                                + identifier
                                + "' function."
                                + " Expected: "
                                + currParamType));
            } else if (!(currArg.equals(currParamType)) && !stringToCharArray(currParamType, currArg)) {
                errorMessages.add(new SemanticError(super.getLine(), super.getCharPositionInLine(),
                                "Invalid type for parameter "
                                + (i + 1)
                                + " in '"
                                + identifier
                                + "' function."
                                + " Expected: "
                                + currParamType
                                + " Actual: "
                                + currArg));
            }
            arguments.get(i).semanticAnalysis(symbolTable, errorMessages, uncheckedNodes, firstCheck);
        }
    }

    static String getString(StringBuilder str, List<ExpressionNode> arguments) {
        for (ExpressionNode argument : arguments) {
            str.append(argument.toString()).append(", ");
        }

        if (!arguments.isEmpty()) {
            str.delete(str.length() - 2, str.length() - 1);
        }

        return str.append(')').toString();
    }

    static List<DataTypeId> getOverloadDataTypeIds(SymbolTable symbolTable, OverloadFuncId functionId, List<ExpressionNode> arguments) {
        List<DataTypeId> returnTypes;

        List<DataTypeId> argTypes = new ArrayList<>();
        for (ExpressionNode arg : arguments) {
            argTypes.add(arg.getType(symbolTable));
        }
        returnTypes = functionId.findReturnTypes(argTypes);

        return returnTypes;
    }
}
