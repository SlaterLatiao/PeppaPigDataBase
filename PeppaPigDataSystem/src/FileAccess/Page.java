package FileAccess;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import Common.Constants;
import fileSystem.Record;
import Common.DataType;
/**
 *
 */
public class Page{
    private byte pageType;
    private byte numOfRecords;
    private short startAddr;
    private int rightNodeAddr;
    private ArrayList<Short> recordAddrList;
    private ArrayList<Record> RecordList;
    private int pageNum;
    private String filePath;
    private DataType data = new DataType();
    //Constructor for root page
    public Page(String filePath){
        super();
        setPageType(Constants.INTERIOR_TABLE_PAGE);
        setNumOfRecords((byte)0x00);
        setStartAddr((short)(Constants.PAGE_SIZE - 1));
        setRightNodeAddr(Constants.RIGET_MOST_PAGE);
        this.RecordList = new ArrayList<Record>();
        setPageNum(0);
    }

    //###################################################################
    //Constructor for a page(need page type)
    public Page(byte pageType){
        super();
        setPageType(pageType);
        setNumOfRecords((byte)0x00);
        setStartAddr((short)(Constants.PAGE_SIZE - 1));
//        setRightNodeAddr(constants.RIGET_MOST_PAGE);
        this.RecordList = new ArrayList<Record>();
//        setPageNum(0);
    }

    // get page by index
    public Page(int key) {
        super();
        readPage(key);
    }
    //###################################################################

    //check is it is a leaf page
    public boolean isLeaf() {
        if(this.pageType==Constants.LEAF_INDEX_PAGE||this.pageType==Constants.LEAF_TABLE_PAGE){
            return true;
        }
        return false;
    }

//    public List<Record> getRecords(){
//        ArrayList<Record> records = new  ArrayList<Record>();
//        for(int i = 0;i<RecordList.size();i++) {
//           Record r = RecordList.get(i);
//            records.add(r);
//        }
//        return records;
//    }

    //################################################################
    //needs fileName

    /**
     *
     * @return childrenList
     */
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

    /**
     *
     * @param key read page by index
     */
    private void readPage(int key) {
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "r");
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
                record.setRowId(rAFile.readShort());
                record.setNumOfColumn(rAFile.readByte());
                ArrayList<Byte> dataTypeList= new ArrayList<Byte>();
                for(int j=0;j<record.getNumOfColumn();j++) {
                    dataTypeList.add(rAFile.readByte());
                }
                record.setDataTypes(dataTypeList);

                ArrayList<Object> valuesOfColumns= new ArrayList<Object>();
                for(int j=0;j<record.getNumOfColumn();j++) {
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("null")) {
                        valuesOfColumns.add("null");
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("tinyint")) {
                        valuesOfColumns.add(rAFile.readByte());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("smallint")) {
                        valuesOfColumns.add(rAFile.readShort());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("int")) {
                        valuesOfColumns.add(rAFile.readInt());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("bigint")) {
                        valuesOfColumns.add(rAFile.readLong());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("float")) {
		        		 valuesOfColumns.add(rAFile.readFloat());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("double")) {
                        valuesOfColumns.add(rAFile.readDouble());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("datatime")) {
                        valuesOfColumns.add(rAFile.readLong());
                    }
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("date")) {
                        valuesOfColumns.add(rAFile.readLong());
                    }
                    if(record.getDataTypes().get(i)>data.nameToSerialCode("text")){
                        byte length = (byte) (record.getDataTypes().get(i) - data.nameToSerialCode("text"));
                        char[] text = new char[length];
                        for (byte k = 0; k < length; k++) {
                            text[k] = (char) rAFile.readByte();
                        }
                        valuesOfColumns.add(new String(text));
                     }
                }
                record.setValuesOfColumns(valuesOfColumns);
                addRecordList(record);
            }
            rAFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte getMaxIndex(){
        return 0;
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
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }
    public String getFilePath(){
        return this.filePath;
    }
}

