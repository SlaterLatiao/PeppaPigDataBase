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
			raf.setLength((pNum + 1) * Constants.PAGE_SIZE);
			raf.seek(getFileAddr(0));

			// write page type into both page and file
			this.type = type;
			raf.writeByte(type);
			// number of records is 0
			nRecords = 0;
			raf.writeByte(nRecords);
			// list of record start addresses is empty
			rStarts = new ArrayList<Short>();
			// start of content is end of page
			startAddr = Constants.PAGE_SIZE;
			raf.writeShort(startAddr);
			// new node is rightmost page in initialization
			rPointer = Constants.RIGET_MOST_PAGE;
			raf.writeInt(rPointer);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Page(String filePath, int pNum) {
		this.pNum = pNum;
		records = new ArrayList<Record>();
		tableFile = new File(filePath);

		try {
			raf = new RandomAccessFile(tableFile, "r");
			raf.seek(getFileAddr(0));
			readContent();
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Page getNewPage(byte type) {
		Page page = new Page(type, getMaxPnum() + 1, tableFile);
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
			raf = new RandomAccessFile(tableFile, "rw");
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
					// goes to start of record content
					raf.seek(getFileAddr(rStarts.get(i)));

					// read payload
					short payLoad = raf.readShort();
					// read row id
					int rowId = raf.readInt();
					// read number of columns
					byte nColumns = raf.readByte();
					ArrayList<Byte> dataTypes = new ArrayList<Byte>();
					// construct list of data types
					for (int j = 0; j < nColumns; j++)
						dataTypes.add(raf.readByte());

					ArrayList<String> values = new ArrayList<String>();
					// read each record value and append to list
					for (int j = 0; j < nColumns; j++) {
						byte dataType = dataTypes.get(j);
						values = readDataByType(values, dataType);
					}
					records.add(new Record(rowId, payLoad, nColumns, dataTypes, values));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addRecord(Record r) {

		try {
			raf = new RandomAccessFile(tableFile, "rw");

			// move to position of nRecords
			raf.seek(getFileAddr(1));

			// increment nRecords in both file and page
			raf.writeByte(++nRecords);

			// update start address in both file and page
			startAddr = (short) (startAddr - r.getSpace() - 1);
			raf.writeShort(this.startAddr);

			// update rStarts in both file and page
			rStarts.add(startAddr);
			raf.skipBytes(4 + 2 * (nRecords - 1));
			raf.writeShort(this.startAddr);

			// add record in both file and page
			records.add(r);
			// go to start of records
			raf.seek(getFileAddr(startAddr));
			raf.writeShort(r.getPayLoad());
			raf.writeInt(r.getRowId());
			raf.writeByte(r.getNumOfColumn());
			// write column types
			for (int i = 0; i < r.getNumOfColumn(); i++)
				raf.writeByte(r.getDataTypes().get(i));
			// write column contents
			for (int i = 0; i < r.getNumOfColumn(); i++) {
				String content = r.getValuesOfColumns().get(i);
				byte dataType = r.getDataTypes().get(i);
				writeDataByType(content, dataType);
			}
			raf.close();
		} catch (IOException e) {
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

	private void writeDataByType(String content, byte dataType) {
		try {
			switch (dataType) {
			case 1:
				raf.writeByte(new Byte(content));
				break;
			case 2:
				raf.writeShort(new Short(content));
				break;
			case 3:
				raf.writeInt(new Integer(content));
				break;
			case 4:
				raf.writeLong(new Long(content));
				break;
			case 5:
				raf.writeFloat(new Float(content));
				break;
			case 6:
				raf.writeDouble(new Double(content));
				break;
			case 7:
				raf.writeLong(new Long(content));
				break;
			case 8:
				raf.writeLong(new Long(content));
				break;
			case 0:
				// do nothing
				break;
			// none of above, it is a string
			default:
				raf.writeBytes(content);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Record> getRecordList() {
		return records;
	}

	public List<Page> getChildren() {
		Record cRecord;
		ArrayList<Page> children = new ArrayList<Page>();
		Page page;
		int pnBuffer;
		if (nRecords == 0)
			return children;
		page = new Page(tableFile.getAbsolutePath(), rPointer);
		children.add(page);
		for (int i = 0; i < nRecords; i++) {
			cRecord = records.get(i);
			pnBuffer = Integer.valueOf(cRecord.getValuesOfColumns().get(i));
			page = new Page(tableFile.getAbsolutePath(), pnBuffer);
			children.add(page);
		}
		return children;
	}

	public void remove(int row_id) {
		try {
			raf = new RandomAccessFile(tableFile, "rw");

			raf.seek(getFileAddr(1));
			raf.writeByte(nRecords - 1);

			int index_rowId = -1;
			for (int i = 0; i < nRecords; i++) {
				raf.seek(getFileAddr(rStarts.get(i)));
				if (raf.readInt() == row_id) {
					index_rowId = i;
					break;
				}
			}
			rStarts.remove(index_rowId);
			nRecords--;

			raf.seek(getFileAddr(Constants.PAGE_HEADER_LENGTH));
			for (int i = 0; i < nRecords; i++) {
				raf.writeShort(rStarts.get(i));
			}
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update(int k, Record r) {
		try {
			raf = new RandomAccessFile(tableFile, "rw");
			// get the position(address or index in this page) of the record with row_id k.
			raf.seek(getFileAddr(rStarts.get(k)));
			raf.writeShort(r.getPayLoad());
			raf.writeInt(r.getRowId());
			raf.writeByte(r.getNumOfColumn());
			for (int i = 0; i < r.getNumOfColumn(); i++)
				raf.writeByte(r.getDataTypes().get(i));
			// write column contents
			for (int i = 0; i < r.getNumOfColumn(); i++) {
				String content = r.getValuesOfColumns().get(i);
				byte dataType = r.getDataTypes().get(i);
				writeDataByType(content, dataType);
			}
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPNum(int pnum) {
		pNum = pnum;
	}

	public void exchangeContent(Page page) {
		try {
			byte[] b = new byte[Constants.PAGE_SIZE];
			byte[] c = new byte[Constants.PAGE_SIZE];
			// write page into root(0th page)
			raf = new RandomAccessFile(tableFile, "rw");
			raf.seek(getFileAddr(0));
			raf.readFully(b);
			raf.seek( page.getFileAddr(0));
			raf.readFully(c);
			raf.seek(page.getFileAddr(0));
			raf.writeBytes(new String(b));
			raf.seek(getFileAddr(0));
			raf.writeBytes(new String(c));
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMaxRowID() {
		int maxRowId = 0;
		int pnum = (int) (tableFile.length() / (long) Constants.PAGE_SIZE);
		byte pt = -1;
		try {
			raf = new RandomAccessFile(tableFile, "r");
			while (pt != Constants.LEAF_TABLE_PAGE && pt != Constants.LEAF_INDEX_PAGE) {
				pnum--;
				raf.seek(pnum * Constants.PAGE_SIZE);
				pt = raf.readByte();
			}

			raf.seek(pnum * Constants.PAGE_SIZE + 1);
			byte recordNum = raf.readByte();
			if (recordNum != 0) {
				raf.seek(pnum * Constants.PAGE_SIZE + Constants.PAGE_HEADER_LENGTH + 2 * (recordNum - 1));
				short maxRecordAddr = raf.readShort();
				raf.seek(pnum * Constants.PAGE_SIZE + maxRecordAddr + 2);
				maxRowId = raf.readInt();
			}
			raf.close();
			System.out.println("maxRowId:" + maxRowId);
			return maxRowId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	public Page getNext() {
		if (rPointer == -1)
			return null;
		return new Page(tableFile.getAbsolutePath(), rPointer);
	}

	public void addLeftChild(int pNum) {
		try {
			raf = new RandomAccessFile(tableFile, "rw");
			raf.seek(getFileAddr(4));
			raf.writeInt(pNum);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
