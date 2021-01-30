package ic.doc.semantic_analysis.data_types;

import ic.doc.semantic_analysis.DataType;

public class CharType extends DataType {

    private static final int MIN_ASCII = 32;
    private static final int MAX_ASCII = 126;

    public CharType() {
        super();
    }

    public boolean validChar(char character) {
        return (MIN_ASCII <= character && character <= MAX_ASCII);
    }

    @Override
    public String toString() {
        return "char";
    }
}
