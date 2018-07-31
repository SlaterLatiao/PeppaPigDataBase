package fileSystem;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import Common.Constants;
import Common.DataType;

public class Page {

	private int pNum;
	private byte type;
	private byte nRecords;
	private short startAddr;
	private int rPointer;
	private List<Short> rStarts;
	private List<Record> records;
	File tableFile;
	private RandomAccessFile raf;

//	private static final byte TINYINT_SC = DataType.getInstance().nameToSerialCode("tinyint");
//	private static final byte SMALLINT_SC = DataType.getInstance().nameToSerialCode("smallint");
//	private static final byte INT_SC = DataType.getInstance().nameToSerialCode("int");
//	private static final byte BIGINT_SC = DataType.getInstance().nameToSerialCode("bigint");
//	private static final byte FLOAT_SC = DataType.getInstance().nameToSerialCode("float");
//	private static final byte DOUBLE_SC = DataType.getInstance().nameToSerialCode("double");
//	private static final byte DATETIME_SC = DataType.getInstance().nameToSerialCode("datetime");
//	private static final byte DATE_SC = DataType.getInstance().nameToSerialCode("date");
//	private static final byte TEXT_SC = DataType.getInstance().nameToSerialCode("text");
//	private static final byte NULL_SC = DataType.getInstance().nameToSerialCode("null");

	public Page(String filePath) {
		pNum = 0;
		records = new ArrayList<Record>();
		tableFile = new File(filePath);

		try {
			if (tableFile.exists()) {
				raf = new RandomAccessFile(tableFile, "r");
				raf.seek(0);
				readContent();

				raf.close();
			} else {
//				setPageType(Constants.LEAF_TABLE_PAGE);
//				setNumOfRecords((byte) 0x00);
//				setStartAddr((short) (Constants.PAGE_SIZE - 1));
//				setRightNodeAddr(Constants.RIGET_MOST_PAGE);
//				this.recordAddrList = new ArrayList<Short>();
//				this.RecordList = new ArrayList<Record>();
//				setPageNum(1);
//				createFile(filePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readContent() {
		try {
			// start of this page
			raf.seek(pNum * Constants.PAGE_SIZE);
			// set type attribute based on data from file
			type = raf.readByte();
			// this is a leaf page
			if (type == Constants.LEAF_TABLE_PAGE || type == Constants.LEAF_INDEX_PAGE) {
				// read number of records
				nRecords = raf.readByte();
				startAddr = raf.readShort();
				rPointer = raf.readInt();

				rStarts = new ArrayList<Short>();
				// construct list of start addresses
				for (int i = 0; i < nRecords; i++)
					rStarts.add(raf.readShort());

				for (int i = 0; i < nRecords; i++) {
					Record r = new Record();
					// goes to start of record content
					raf.seek(getFileAddr(rStarts.get(i)));
					// read payload
					r.setPayLoad(raf.readShort());
					// read row id
					r.setRowId(raf.readInt());
					// read number of columns
					r.setNumOfColumn(raf.readByte());
					ArrayList<Byte> dataTypes = new ArrayList<Byte>();
					// construct list of data types
					for (int j = 0; i < r.getNumOfColumn(); j++)
						dataTypes.add(raf.readByte());
					r.setDataTypes(dataTypes);
					ArrayList<String> values = new ArrayList<String>();
					// read each record value and append to list
					for (int j = 0; j < r.getNumOfColumn(); j++) {
						byte dataType = dataTypes.get(j);
						values = readDataByType(values, dataType);
						r.setValuesOfColumns(values);
						records.add(r);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long getFileAddr(Short offset) {
		return pNum * Constants.PAGE_SIZE;
	}

	private ArrayList<String> readDataByType(ArrayList<String> values, byte dataType) {
		try {
			switch (dataType) {
			case 1:
				values.add(String.valueOf(raf.readByte()));
				break;
			case 2:
				values.add(String.valueOf(raf.readShort()));
				break;
			case 3:
				values.add(String.valueOf(raf.readInt()));
				break;
			case 4:
				values.add(String.valueOf(raf.readLong()));
				break;
			case 5:
				values.add(String.valueOf(raf.readFloat()));
				break;
			case 6:
				values.add(String.valueOf(raf.readDouble()));
				break;
			case 7:
				values.add(String.valueOf(raf.readLong()));
				break;
			case 8:
				values.add(String.valueOf(raf.readLong()));
				break;
			case 0:
				values.add("");
				break;
			// none of above, it is a string
			default:
				byte length = (byte) (dataType - 10);
				char[] str = new char[length];
				for (int i = 0; i < length; i++) {
					str[i] = (char) raf.readByte();
				}
				values.add(new String(str));
				break;
			}
			return values;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
