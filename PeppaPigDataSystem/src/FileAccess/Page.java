package FileAccess;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
//###################################################################
//import Record
//add a getChildrenRecord() in Record
import Common.Constants;

public class Page{
    private byte pageType;
    private byte numOfRecords;
    private short startAddr;
    private int rightNodeAddr;
    private ArrayList<Short> recordAddrList;
    private ArrayList<Record> RecordList;
    private int pageNum;

    //################################################################
    // plan to store fileName, need to discuss
    private String fileName;

    //Constructor for root page
    public Page(String fileName){
        super();
        setPageType(Constants.INTERIOR_TABLE_PAGE);
        setNumOfRecords((byte)0x00);
        setStartAddr((Constants.PAGE_SIZE - 1));
        setRightNodeAddr(Constants.RIGET_MOST_PAGE);
        this.RecordList = new ArrayList<Record>();
        setPageNum(0);
    }

    //Constructor for inner page
    public Page(byte pageType){
        super();
        setPageType(pageType);
        setNumOfRecords((byte)0x00);
        setStartAddr((Constants.PAGE_SIZE - 1));
//        setRightNodeAddr(constants.RIGET_MOST_PAGE);
        this.RecordList = new ArrayList<Record>();
//        setPageNum(0);
    }

    //Constructor for leaf page
    public Page(byte pageType){
        super();
        setPageType(pageType);
        setNumOfRecords((byte)0x00);
        setStartAddr((Constants.PAGE_SIZE - 1));
//        setRightNodeAddr(constants.RIGET_MOST_PAGE);
        this.recordAddrList = new ArrayList<Short>();
        this.RecordList = new ArrayList<Record>();
//        setPageNum(0);
    }

    // get page by index
    public Page(int key) {
        super();
        readPage(key);
    }


    //check is it is a leaf page
    public boolean isLeaf() {
        if(this.pageType==Constants.LEAF_INDEX_PAGE||this.pageType==Constants.LEAF_TABLE_PAGE){
            return true;
        }
        return false;
    }
    //entry
    public List<Entry<Integer, Record>> getEntries(){
        ArrayList<Entry<Integer, Record>> entries = new  ArrayList<Entry<Integer, Record>>();
        for(int i = 0;i<RecordList.size();i++) {
            Entry<Integer, Record> e;
            e.setKey(i);
            e.setValue(RecordList.get(i));
            entries.add(e);
        }
        return entries;
    }

    //################################################################
    //needs fileName

    public List<Page> getChildrenPlus(){
        ArrayList<Page> childrenPlus = new ArrayList<Page>();
        for(int i = 0;i<RecordList.size();i++) {
            short key= RecordList.get(i).getChildrenRecord();
            Page page = new Page(key);
            childrenPlus.add(page);
        }
        return childrenPlus;
    }

    //################################################################
    //needs fileName
    private void readPage(int key) {
        File newFile = new File("userTable" + File.separatorChar + this.fileName);
        RandomAccessFile rAFile;
        rAFile = new RandomAccessFile(newFile, "r");
        rAFile.setLength(Constants.PAGE_SIZE);
        if (newFile.exists()) {
            return;
        }
        try {
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE);
            this.setPageType(rAFile.readByte());
            this.setNumOfRecords(rAFile.readByte());
            this.setStartAddr(rAFile.readShort());
            this.setRightNodeAddr(rAFile.readInt());
            for (int i =0;i<this.getNumOfRecords();i++) {
                this.addRecordAddrList(rAFile.readShort());
            }
            for (int i =0;i<this.getNumOfRecords();i++) {
                Record record=new Record();
                rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE+ this.getRecordAddrList().get(i));
                record.setPayLoad(rAFile.readByte());
                record.setRowid(rAFile.readShort());
                record.setNumOfColumn(rAFile.readByte());
                ArrayList<Byte> dataTypeList= new ArrayList<Byte>();
                for(int j=0;j<record.getNumOfColumn();j++) {
                    dataTypeList.add(rAFile.readByte());
                }
                record.setDataTypes(dataTypeList);


                ArrayList<Object> valuesOfColumns= new ArrayList<Object>();
                for(int j=0;j<record.getNumOfColumn();j++) {
                    //#############################################################################
                    //need datatype byte class, need to read more
                    if(record.getDataTypes().get(i)==(byte) 0x00) {
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x04) {
                        valuesOfColumns.add(rAFile.readByte());
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x05) {
                        valuesOfColumns.add(rAFile.readShort());
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x06) {
                        valuesOfColumns.add(rAFile.readInt());
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x07) {
                        valuesOfColumns.add(rAFile.readLong());
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x08) {
//		        		 valuesOfColumns.add(rAFile.readReal());
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x09) {
                        valuesOfColumns.add(rAFile.readDouble());
                    }
                    if(record.getDataTypes().get(i)==(byte) 0x0A) {

                    }
                    if(record.getDataTypes().get(i)==(byte) 0x0B) {

                    }
                    if(record.getDataTypes().get(i)==(byte) 0x0C) {

                    }
                }
                record.setValuesOfColumns(valuesOfColumns);
                addRecordList(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        rAFile.close();
    }

    public byte getPageType() {
        return pageType;
    }
    public void setPageType(byte pageType) {
        this.pageType = pageType;
    }
    public byte getNumOfRecords() {
        return numOfRecords;
    }
    public void setNumOfRecords(byte numOfRecords) {
        this.numOfRecords = numOfRecords;
    }
    public short getStartAddr() {
        return startAddr;
    }
    public void setStartAddr(short startAddr) {
        this.startAddr = startAddr;
    }
    public int getRightNodeAddr() {
        return rightNodeAddr;
    }
    public void setRightNodeAddr(int rightNodeAddr) {
        this.rightNodeAddr = rightNodeAddr;
    }
    public List<Short> getRecordAddrList() {
        return recordAddrList;
    }

    public int getPageNum() {
        return pageNum;
    }
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    public List<Record> getRecordList() {
        return RecordList;
    }

    public void addRecordList(Record record) {
        RecordList.add(record);
    }

    public void addRecordAddrList(short addr) {
        this.recordAddrList.add(addr);
    }

}

