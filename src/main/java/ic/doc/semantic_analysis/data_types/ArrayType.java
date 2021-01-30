package ic.doc.semantic_analysis.data_types;

import ic.doc.semantic_analysis.DataType;

public class ArrayType extends DataType {

    private final int size;
    private final DataType arrayType;

    public ArrayType(int size, DataType arrayType){
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
