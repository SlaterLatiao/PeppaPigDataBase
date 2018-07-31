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

	// private static final byte TINYINT_SC =
	// DataType.getInstance().nameToSerialCode("tinyint");
	// private static final byte SMALLINT_SC =
	// DataType.getInstance().nameToSerialCode("smallint");
	// private static final byte INT_SC =
	// DataType.getInstance().nameToSerialCode("int");
	// private static final byte BIGINT_SC =
	// DataType.getInstance().nameToSerialCode("bigint");
	// private static final byte FLOAT_SC =
	// DataType.getInstance().nameToSerialCode("float");
	// private static final byte DOUBLE_SC =
	// DataType.getInstance().nameToSerialCode("double");
	// private static final byte DATETIME_SC =
	// DataType.getInstance().nameToSerialCode("datetime");
	// private static final byte DATE_SC =
	// DataType.getInstance().nameToSerialCode("date");
	// private static final byte TEXT_SC =
	// DataType.getInstance().nameToSerialCode("text");
	// private static final byte NULL_SC =
	// DataType.getInstance().nameToSerialCode("null");

	public Page(String filePath) {
		pNum = 0;
		records = new ArrayList<Record>();
		tableFile = new File(filePath);

		try {
			// table exists, retrieve data
			if (tableFile.exists()) {
				raf = new RandomAccessFile(tableFile, "r");
				raf.seek(0);
				readContent();
				raf.close();
				// table doesn't exist, create a new root
			} else {
				raf = new RandomAccessFile(tableFile, "rw");

				type = Constants.LEAF_TABLE_PAGE;

				if (tableFile.createNewFile()) {

					raf = new RandomAccessFile(tableFile, "rw");
					raf.setLength(Constants.PAGE_SIZE);
					raf.seek(0);

					// write page type = table leaf into both page and file
					type = Constants.LEAF_TABLE_PAGE;
					raf.writeByte(type);
					// number of records is 0
					nRecords = 0;
					raf.writeByte(nRecords);
					// list of record start addresses is empty
					rStarts = new ArrayList<Short>();
					// start of content is end of page
					raf.writeShort(Constants.PAGE_SIZE);
					// root is rightmost page in initialization
					rPointer = Constants.RIGET_MOST_PAGE;
					raf.writeInt(rPointer);
					raf.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Page(byte type, int pno, File tableFile) {
		pNum = pno;

		this.tableFile = tableFile;
		records = new ArrayList<Record>();

		try {
			raf = new RandomAccessFile(tableFile, "rw");
			raf.setLength(Constants.PAGE_SIZE);
			raf.seek(0);

			// write page type into both page and file
			this.type = type;
			raf.writeByte(type);
			// number of records is 0
			nRecords = 0;
			raf.writeByte(nRecords);
			// list of record start addresses is empty
			rStarts = new ArrayList<Short>();
			// start of content is end of page
			raf.writeShort(Constants.PAGE_SIZE);
			// new node is rightmost page in initialization
			rPointer = Constants.RIGET_MOST_PAGE;
			raf.writeInt(rPointer);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		type = Constants.LEAF_TABLE_PAGE;

	}

	public Page getNewPage(byte type) {
		Page page = new Page(type, getMaxPnum() + 1, tableFile);
		page.pNum = this.pNum + 1;
		return page;
	}

	public boolean isLeaf() {
		if (type == Constants.LEAF_INDEX_PAGE || type == Constants.LEAF_TABLE_PAGE)
			return true;
		return false;
	}

	public int getEmptySpace() {
		int space = 0;
		space = (int) startAddr - Constants.PAGE_HEADER_LENGTH - 2 * rStarts.size();
		return space;
	}

	public int getMaxPnum() {
		try {
			raf = new RandomAccessFile(tableFile, "r");
			int size = (int) raf.length();
			raf.close();
			return size / Constants.PAGE_SIZE - 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void setRPointer(Page p) {
		int pnum = p.getPNum();
		try {
			raf = new RandomAccessFile(tableFile, "wr");
			// set right pointer to page p
			rPointer = pnum;
			// move to r pointer position
			raf.seek(getFileAddr(4));
			raf.writeInt(rPointer);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getPNum() {
		return pNum;
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

	private long getFileAddr(int offset) {
		return pNum * Constants.PAGE_SIZE + offset;
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

	public List<Record> getRecordList() {
		return records;
	}

	public List<Page> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(int row_id) {
		// TODO Auto-generated method stub
		
	}

	public void update(int k, Record r) {
		// TODO Auto-generated method stub
		
	}

	public void addRecord(Record r) {
		// TODO Auto-generated method stub
		
	}

	public void addInner(int key) {
		// TODO Auto-generated method stub
		
	}

	public void setPNum(int n) {
		// TODO Auto-generated method stub
		
	}

	public void exchangeContent(Node node) {
		// TODO Auto-generated method stub
		
	}

	public int getMaxRowID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
