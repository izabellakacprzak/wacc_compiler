package ic.doc.semantic_analysis.data_types;

import ic.doc.semantic_analysis.DataType;

public class IntType extends DataType {

    private final static int MAX_INT = (1 << 31) - 1;
    private final static int MIN_INT = -(1 << 31);

    public IntType() {
        super();
    }


//    Not sure if we need this - ask in labs?
//    public boolean inBounds(int number){
//        return (MIN_INT < number && number < MAX_INT);
//    }
//

    @Override
    public String toString() {
        return "int";
    }
}
