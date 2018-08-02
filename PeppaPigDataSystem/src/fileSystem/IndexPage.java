package fileSystem;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import Common.Constants;

public class IndexPage {
    private int pNum;
    private byte nRecords;
    File tableFile;
    private RandomAccessFile raf;
    private List<Short> rStarts;


    private byte type;

    private short startAddr;
    private int rPointer;
    private List<IndexRecord> records;

    public IndexPage(String filePath) {
        pNum = 0;
        records = new ArrayList<IndexRecord>();
        tableFile = new File(filePath);

        try {
            // table exists, retrieve data
            if (tableFile.exists()) {
                raf = new RandomAccessFile(tableFile, "r");
                readContent();
                raf.close();
                // table doesn't exist, create a new root
            } else {

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
                    raf.writeShort(Constants.PAGE_HEADER_LENGTH);
                    // root is child page in initialization
                    rPointer = 0;
                    raf.writeInt(rPointer);
                    raf.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IndexPage(byte type, int pno, File tableFile) {
        pNum = pno;

        this.tableFile = tableFile;
        records = new ArrayList<IndexRecord>();

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

    public IndexPage(String filePath, int pNum) {
        this.pNum = pNum;
        records = new ArrayList<IndexRecord>();
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

    public IndexPage split() {
        IndexPage page = new IndexPage(type, getMaxPnum() + 1, tableFile);
        return page;
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
            raf.seek(0);
            type=raf.readByte();
            raf.skipBytes(1);
            startAddr = raf.readShort();
            rPointer = raf.readInt();

            if(rPointer>0){
                for (int i = 0; i < nRecords; i++) {
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
                    records.add(new IndexRecord(rowId, payLoad, nColumns, dataTypes, values));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLeaf() {
        if (type == Constants.LEAF_INDEX_PAGE || type == Constants.LEAF_TABLE_PAGE)
            return true;
        return false;
    }


    public void addRecord(IndexRecord r) {

        try {

            raf = new RandomAccessFile(tableFile, "rw");

            // update start address in both file and page
            startAddr = (short) (startAddr + r.getSpace());
            if(startAddr>tableFile.length()){
                raf.setLength(tableFile.length()+Constants.PAGE_SIZE);
            }
            raf.seek(Constants.PAGE_HEADER_LENGTH-6);
            raf.writeShort(this.startAddr);
            ++rPointer;
            raf.writeInt(rPointer);

            // add record in both file and page
            records.add(r);
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

    public List<IndexRecord> getRecordList() {
        return records;
    }

    public List<IndexPage> getChildren() {
        Record cRecord;
        ArrayList<IndexPage> children = new ArrayList<IndexPage>();
        IndexPage page;
        int pnBuffer;
        if (nRecords == 0)
            return children;
        page = new IndexPage(tableFile.getAbsolutePath(), rPointer);
        children.add(page);
        for (int i = 0; i < nRecords; i++) {
            cRecord = records.get(i);
            pnBuffer = Integer.valueOf(cRecord.getValuesOfColumns().get(i));
            page = new IndexPage(tableFile.getAbsolutePath(), pnBuffer);
            children.add(page);
        }
        return children;
    }

    public void write(){
        ArrayList<Byte> dt = new ArrayList<>();
        dt.add((byte) 0x0f);
        ArrayList<String> voc = new ArrayList<>();
        ArrayList<String> ts;
        int[] is= {8,1,5,2,4,3,6,7};
        voc.add("test1");
        voc.add("test2");
        voc.add("test3");
        voc.add("test4");
        voc.add("test5");
        voc.add("test6");
        voc.add("test7");
        voc.add("test8");
        for(int i =0;i<8;i++)
        {
            ts = new ArrayList<>();
            ts.add(voc.get(i));
            IndexRecord r = new IndexRecord(is[i], (short)7, (byte)1,dt, ts);
            addRecord(r);
        }
    }


    public List<IndexRecord> getRecords(){
        return records;
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

    public void update(int k,IndexRecord r) {
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

    public void exchangeContent(IndexPage page) {
        try {
            byte[] b = new byte[Constants.PAGE_SIZE];
            byte[] c = new byte[Constants.PAGE_SIZE];
            // write page into root(0th page)
            raf = new RandomAccessFile(tableFile, "rw");
            raf.seek(getFileAddr(0));
            raf.readFully(b);
            raf.seek(page.getFileAddr(0));
            raf.readFully(c);
            raf.seek(page.getFileAddr(0));
            for (int i = 0; i < Constants.PAGE_SIZE; i++) {
                raf.writeByte(b[i]);
            }
            raf.seek(getFileAddr(0));
            for (int j = 0; j < Constants.PAGE_SIZE; j++) {
                raf.writeByte(c[j]);
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Page getNext() {
        if (rPointer == -1)
            return null;
        return new Page(tableFile.getAbsolutePath(), rPointer);
    }

    public void addLeftChiald(int pNum) {
        try {
            raf = new RandomAccessFile(tableFile, "rw");
            raf.seek(getFileAddr(4));
            raf.writeInt(pNum);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropIndex() {
        tableFile.delete();
    }
}
