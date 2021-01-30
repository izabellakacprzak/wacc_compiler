package ic.doc.semantic_analysis.data_types;

import ic.doc.semantic_analysis.DataType;

public class PairType extends DataType {

    private final DataType fstType;
    private final DataType sndType;

    public PairType(DataType fstType, DataType sndType) {
        super();
        this.fstType = fstType;
        this.sndType = sndType;
    }

    public DataType getFstType() {
        return fstType;
    }

    public DataType getSndType() {
        return sndType;
    }

    @Override
    public String toString() {
        return "pair(" + fstType.toString() + ", " + sndType.toString() + ")";
    }
}
