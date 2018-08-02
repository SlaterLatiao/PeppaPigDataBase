package fileSystem;

import java.util.ArrayList;
import Common.DataType;

public class IndexRecord extends Record implements Comparable<IndexRecord> {

	public IndexRecord(ArrayList<String> valuesOfColumns) {
		super(valuesOfColumns);
	}

	public IndexRecord(int pageNumOfRec, byte numOfColumn, ArrayList<Byte> dataTypes,
			ArrayList<String> valuesOfColumns) {
		super(pageNumOfRec, numOfColumn, dataTypes, valuesOfColumns);
	}

	public IndexRecord(int rowId, short payLoad, byte numOfColumn, ArrayList<Byte> dataTypes,
			ArrayList<String> valuesOfColumns) {
		super(rowId, payLoad, numOfColumn, dataTypes, valuesOfColumns);
	}

	public IndexRecord(int rowId, int child) {
		super(rowId, child);
	}

	@Override
	public int compareTo(IndexRecord that) {
		switch (dataTypes.get(0)) {
		case 1:
			return Byte.valueOf(this.valuesOfColumns.get(0)).compareTo(Byte.valueOf(that.valuesOfColumns.get(0)));
		case 2:
			return Short.valueOf(this.valuesOfColumns.get(0)).compareTo(Short.valueOf(that.valuesOfColumns.get(0)));
		case 3:
			return Integer.valueOf(this.valuesOfColumns.get(0)).compareTo(Integer.valueOf(that.valuesOfColumns.get(0)));
		case 4:
			return Long.valueOf(this.valuesOfColumns.get(0)).compareTo(Long.valueOf(that.valuesOfColumns.get(0)));
		case 5:
			return Float.valueOf(this.valuesOfColumns.get(0)).compareTo(Float.valueOf(that.valuesOfColumns.get(0)));
		case 6:
			return Double.valueOf(this.valuesOfColumns.get(0)).compareTo(Double.valueOf(that.valuesOfColumns.get(0)));
		case 7:
			return Long.valueOf(this.valuesOfColumns.get(0)).compareTo(Long.valueOf(that.valuesOfColumns.get(0)));
		case 8:
			return Long.valueOf(this.valuesOfColumns.get(0)).compareTo(Long.valueOf(that.valuesOfColumns.get(0)));
		case 0:
			return -1;
		// none of above, it is a string
		default:
			return this.valuesOfColumns.get(0).compareTo(that.valuesOfColumns.get(0));
		}
	}


}
