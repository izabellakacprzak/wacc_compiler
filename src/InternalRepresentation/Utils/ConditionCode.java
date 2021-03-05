package InternalRepresentation.Utils;

/* Condition codes with the following meaning:
 * L: Link
 * EQ: Equal
 * NE: Not equal
 * CS: Unsigned higher or same, carry set
 * VS: Overflow
 * GE Signed greater or equal
 * LT Signed less than
 * GT Signed greater than
 * LE Signed less than or equal
*/
public enum ConditionCode {
    L, EQ, NE, CS, VS, GE, LT, GT, LE;

    public String getCondName() {
        return name();
    }
}
