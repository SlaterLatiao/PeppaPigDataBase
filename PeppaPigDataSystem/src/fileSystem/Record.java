package fileSystem;

import java.util.ArrayList;
import Common.DataType;

/**
 * @author Jinru Shi & Li Liu 2018-07-23
 */
public class Record {

	private int pageNumOfRec;
	private short payLoad;
	protected int rowId;
	private byte numOfColumn;
	protected ArrayList<Byte> dataTypes;
	protected ArrayList<String> valuesOfColumns;
	// only for inner page
	private short childrenRecord;
	private DataType data;

	public Record(ArrayList<String> valuesOfColumns){
        this.valuesOfColumns = valuesOfColumns;
    }

	public Record(int pageNumOfRec, byte numOfColumn, ArrayList<Byte> dataTypes, ArrayList<String> valuesOfColumns) {
		this.pageNumOfRec = pageNumOfRec;
		this.numOfColumn = numOfColumn;
		this.dataTypes = dataTypes;
		this.valuesOfColumns = valuesOfColumns;
		data = DataType.getInstance();
		calculatePayLoad();
	}

	public Record(int rowId, short payLoad, byte numOfColumn, ArrayList<Byte> dataTypes,
			ArrayList<String> valuesOfColumns) {
		this.rowId = rowId;
		this.payLoad = payLoad;
		this.numOfColumn = numOfColumn;
		this.dataTypes = dataTypes;
		this.valuesOfColumns = valuesOfColumns;
		data = DataType.getInstance();
	}

	public Record(int rowId, int child) {
		this.rowId = rowId;
		payLoad = 6;
		numOfColumn = 1;
		dataTypes = new ArrayList<Byte>();
		dataTypes.add((byte) 3);
		valuesOfColumns = new ArrayList<String>();
		valuesOfColumns.add(Integer.toString(child));
		data = DataType.getInstance();
	}

	/*
	 * public Record() { payLoad = 0; data = DataType.getInstance(); }
	 */

	public int getPageNumOfRec() {
		return pageNumOfRec;
	}

	public void setPageNumOfRec(int pageNumOfRec) {
		this.pageNumOfRec = pageNumOfRec;
	}

	public int getSpace() {
		return this.payLoad + 6;
	}

	public short getPayLoad() {
		return payLoad;
	}

	public void calculatePayLoad() {
		for (int i = 0; i < this.numOfColumn; i++) {
			if (this.dataTypes.get(i) == data.nameToSerialCode("null")) {
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("tinyint")) {
				this.payLoad += 1;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("smallint")) {
				this.payLoad += 2;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("int")) {
				this.payLoad += 4;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("bigint")) {
				this.payLoad += 8;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("float")) {
				this.payLoad += 4;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("double")) {
				this.payLoad += 8;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("datetime")) {
				this.payLoad += 8;
			}
			if (this.dataTypes.get(i) == data.nameToSerialCode("date")) {
				this.payLoad += 8;
			}
			if (this.dataTypes.get(i) > data.nameToSerialCode("text")) {
				byte length = (byte) (this.getDataTypes().get(i) - data.nameToSerialCode("text"));
				this.payLoad += length;
			}
		}
		this.payLoad += 1 + this.getNumOfColumn();
	}

	public void setPayLoad(short payLoad) {
		this.payLoad = payLoad;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public byte getNumOfColumn() {
		return numOfColumn;
	}

	public void setNumOfColumn(byte numOfColumn) {
		this.numOfColumn = numOfColumn;
	}

	public ArrayList<Byte> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(ArrayList<Byte> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public ArrayList<String> getValuesOfColumns() {
		return valuesOfColumns;
	}

	public void setValuesOfColumns(ArrayList<String> valuesOfColumns) {
		this.valuesOfColumns = valuesOfColumns;
	}

	public short getChildrenRecord() {
		return this.childrenRecord;
	}

	public void setChildrenRecord(short childrenRecord) {
		this.childrenRecord = childrenRecord;
	}


}
