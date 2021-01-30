package ic.doc.semantic_analysis.data_types;

public class StringType extends ArrayType {

    public StringType(int size) {
        super(size, new CharType());
    }

    @Override
    public String toString() {
        return "string";
    }
}
