package InternalRepresentation.Utils;

/*
L Link
EQ Equal
NE Not equal
HS/CS Unsigned higher or same, carry set
LO/CC Unsigned lower, carry clear
MI Negative, minus
PL Positive or zero, plus
VS Overflow
VC No overflow
HI Unsigned higher

LS Unsigned lower or same
GE Signed greater or equal
LT Signed less than
GT Signed greater than
LE Signed less than or equal
AL Always
*/
public enum ConditionCode {
    L, EQ, NE, CS, VS, GE, LT, GT, LE;

    public String getCondName() {
        return name();
    }
}
