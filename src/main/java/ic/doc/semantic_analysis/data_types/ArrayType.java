package ic.doc.semantic_analysis.data_types;

import ic.doc.semantic_analysis.DataTypeId;

public class ArrayType extends DataTypeId {

    private final int size;
    private final DataTypeId arrayType;

    public ArrayType(int size, DataTypeId arrayType) {
        super();
        this.size = size;
        this.arrayType = arrayType;
    }

    public int getSize(){
        return size;
    }

    @Override
    public String toString() {
        return arrayType.toString() + "[]";
    }
}
