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
        this.filePath = filePath;
        try {
            if(fileExist(filePath)){
                File newFile = new File(this.filePath);
                recordAddrList = new ArrayList<Short>();
                RecordList = new ArrayList<Record>();
                RandomAccessFile rAFile=null;
                if (!newFile.exists()) {
                    return;
                }
                try {
                    rAFile = new RandomAccessFile(newFile, "rw");
                    rAFile.seek(0);
                    this.readPage(rAFile.readByte());
                    rAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                setPageType(Constants.LEAF_TABLE_PAGE);
                setNumOfRecords((byte)0x00);
                setStartAddr((short)(Constants.PAGE_SIZE - 1));
                setRightNodeAddr(Constants.RIGET_MOST_PAGE);
                this.recordAddrList = new ArrayList<Short>();
                this.RecordList = new ArrayList<Record>();
                setPageNum(0);
                createFile(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //###################################################################
    //Constructor for a page(need page type)
    public Page(byte pageType){

        setPageType(pageType);
        setNumOfRecords((byte)0x00);
        setStartAddr((short)(Constants.PAGE_SIZE - 1));
//        setRightNodeAddr(constants.RIGET_MOST_PAGE);
        this.RecordList = new ArrayList<Record>();
//        setPageNum(0);
    }

    // get page by index
    public Page(String filePath, int key) {
        super();
        setFilePath(filePath);
        readPage(key);
    }
    //###################################################################

    //check is it is a leaf page
    public boolean isLeaf() {
        if(this.pageType==Constants.LEAF_INDEX_PAGE||this.pageType==Constants.LEAF_TABLE_PAGE){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean fileExist(String filePath){
        File newFile = new File(this.filePath);
        if (!newFile.exists()) {
            return false;
        }
        else{
            return true;
        }
    }

    public void createFile(String filePath){
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return;
            }
            RandomAccessFile randomAccessFile=null;
            if (file.createNewFile()) {
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.setLength(2*Constants.PAGE_SIZE);
                randomAccessFile.seek(0);
                randomAccessFile.writeInt(1);
                randomAccessFile.seek(Constants.PAGE_SIZE);
                randomAccessFile.writeByte(Constants.LEAF_TABLE_PAGE);
                randomAccessFile.writeByte((byte)0x00);
                randomAccessFile.writeShort((short)(Constants.PAGE_SIZE - 1));
                randomAccessFile.writeInt(Constants.RIGET_MOST_PAGE);
                setPageNum(1);
            }
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRootPointer(){
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "rw");
            rAFile.seek(0);
            rAFile.writeByte(this.getPageNum());
            rAFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public List<Page> getChildren(){
        ArrayList<Page> children = new ArrayList<Page>();
        if(RecordList==null){
            return children;
        }
        else{
            for(int i = 0;i<RecordList.size();i++) {
                short key= RecordList.get(i).getChildrenRecord();
                Page page = new Page(this.filePath, key);
                children.add(page);
            }
            return children;
        }
    }

    //################################################################
    //can only read leaf page
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
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE);
            this.setPageType(rAFile.readByte());
            this.setNumOfRecords(rAFile.readByte());
            this.setStartAddr(rAFile.readShort());
            this.setRightNodeAddr(rAFile.readInt());
            for (int i =0;i<this.getNumOfRecords();i++) {
                this.addRecordAddrList(rAFile.readShort());
            }
            for (int i =0;i<this.getNumOfRecords();i++) {
                Record record=new Record();
                rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE+ this.getRecordAddrList().get(i));
                record.setPayLoad(rAFile.readShort());
                record.setRowId(rAFile.readInt());
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
                    if(record.getDataTypes().get(i)==data.nameToSerialCode("datetime")) {
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

    //################################################################################
    //if put this function into b+ tree? last node's last key should be max index
    public int getMaxIndex(){
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return 0;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "rw");
            Page page = new Page(this.filePath,(int)(newFile.length()/Constants.PAGE_SIZE-1));
            rAFile.close();
            if(page.getRecordList()!=null){
                return page.getRecordList().get(page.getRecordList().size()-1).getRowId();
            }
            else{
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //#################################################################################
    //tell nongnong, this function can only be called after page(int key)
    public boolean remove(Integer rowId){
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return false;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "rw");
            int index_rowId=-1;
            for (int i =0;i<recordAddrList.size();i++){
                rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE+ this.getRecordAddrList().get(i));
                if(rAFile.readInt()==rowId) {
                    index_rowId = i;
                    break;
                }
            }
            recordAddrList.remove(index_rowId);
            this.numOfRecords-=1;
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE+1);
            rAFile.writeByte(this.getNumOfRecords());
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE+Constants.PAGE_HEADER_LENGTH);
            for (int i =0;i<recordAddrList.size();i++){
                rAFile.writeShort(recordAddrList.get(i));
            }
            rAFile.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getEmptySpace(){
        int space =0;
        space = (int)startAddr - Constants.PAGE_HEADER_LENGTH - 2*recordAddrList.size();
        return space;
    }

    //###################################################################################
    public Page getNewPage(boolean pageType){
//    public Page getNewPage(byte pageType){
        byte PageType;
        if(pageType = true){
            PageType=Constants.LEAF_TABLE_PAGE;
        }
        else{
            PageType=Constants.INTERIOR_TABLE_PAGE;
        }
        Page page = new Page(PageType);
        page.setRightNodeAddr(Constants.RIGET_MOST_PAGE);
        page.pageNum = this.pageNum+1;


        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return null;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "w");
            rAFile.setLength(Constants.PAGE_SIZE);
            rAFile.seek((this.pageNum+1) * Constants.PAGE_SIZE+4);
            rAFile.writeInt(this.rightNodeAddr);
            rAFile.seek((page.pageNum+1) * Constants.PAGE_SIZE);
            rAFile.writeByte(page.pageType);
            rAFile.writeByte(page.numOfRecords);
            rAFile.writeShort(page.startAddr);
            rAFile.writeInt(page.rightNodeAddr);

            rAFile.close();
            return page;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addRecord(Record record){
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "rw");
            rAFile.seek((this.getPageNum() +1)* Constants.PAGE_SIZE+1);
            this.setNumOfRecords((byte)(this.getNumOfRecords()+1));
            rAFile.writeByte(this.numOfRecords);
            this.setStartAddr((short)(this.startAddr - record.getPayLoad() - 6));
            rAFile.writeShort(this.startAddr);

//#################################################################################################
//update all recordaddrlist
//            for (int i =0;i<this.getNumOfRecords();i++) {
//               rAFile.writeShort( this.recordAddrList.get(i));
//            }
            this.addRecordAddrList(this.startAddr);
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE + Constants.PAGE_HEADER_LENGTH+2*(this.recordAddrList.size()-1));
            rAFile.writeShort(this.startAddr);
            this.addRecordList(record);
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE + this.startAddr);
            rAFile.writeShort(record.getPayLoad());
            rAFile.writeInt( record.getRowId());
            rAFile.writeByte(record.getNumOfColumn());
            for(int j=0;j<record.getNumOfColumn();j++) {
                rAFile.writeByte(record.getDataTypes().get(j));
            }
            for(int i=0;i<record.getNumOfColumn();i++) {
                Object object = record.getValuesOfColumns().get(i);
                if(record.getDataTypes().get(i)==data.nameToSerialCode("null")) {
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("tinyint")) {
                    rAFile.writeByte( ((byte[])(object))[0]);
                }

                if(record.getDataTypes().get(i)==data.nameToSerialCode("smallint")) {
                    rAFile.writeShort( (short)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("int")) {
                    rAFile.writeInt( (int)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("bigint")) {
                    rAFile.writeLong( (long)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("float")) {
                    rAFile.writeFloat( (float)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("double")) {
                    rAFile.writeDouble( (double)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("datetime")) {
                    rAFile.writeLong( (long)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("date")) {
                    rAFile.writeLong( (long)(object));
                }
                if(record.getDataTypes().get(i)>data.nameToSerialCode("text")){
                    byte length = (byte) (record.getDataTypes().get(i) - data.nameToSerialCode("text"));
                    for (byte k = 0; k < length; k++) {
                        rAFile.writeByte(((byte[])(object))[k]);
                    }
                }
            }
            rAFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//##################################################################################################
//    public void addChild(Node node){}
    public void addInner(int key){}
//#####################################################################
//need add updateList to record
    public boolean update(int rowId, Record record){
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return false;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "rw");
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE+1);
            this.setNumOfRecords((byte)((int)this.getNumOfRecords()+1));
            rAFile.writeByte(this.numOfRecords);
            this.setStartAddr((short)(this.startAddr - record.getPayLoad() - 6));
            rAFile.writeShort(this.startAddr);

//#################################################################################################
//update all recordaddrlist
//            for (int i =0;i<this.getNumOfRecords();i++) {
//               rAFile.writeShort( this.recordAddrList.get(i));
//            }
            this.addRecordAddrList(this.startAddr);
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE + Constants.PAGE_HEADER_LENGTH+2*(this.recordAddrList.size()-1));
            rAFile.writeShort(this.startAddr);
            this.addRecordList(record);
            rAFile.seek((this.getPageNum()+1) * Constants.PAGE_SIZE + this.startAddr);
            rAFile.writeShort(record.getPayLoad());
            rAFile.writeInt( record.getRowId());
            rAFile.writeByte(record.getNumOfColumn());
            for(int j=0;j<record.getNumOfColumn();j++) {
                rAFile.writeByte(record.getDataTypes().get(j));
            }
            for(int i=0;i<record.getNumOfColumn();i++) {
                Object object = record.getValuesOfColumns().get(i);
                if(record.getDataTypes().get(i)==data.nameToSerialCode("null")) {
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("tinyint")) {
                    rAFile.writeByte( ((byte[])(object))[0]);
                }

                if(record.getDataTypes().get(i)==data.nameToSerialCode("smallint")) {
                    rAFile.writeShort( (short)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("int")) {
                    rAFile.writeInt( (int)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("bigint")) {
                    rAFile.writeLong( (long)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("float")) {
                    rAFile.writeFloat( (float)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("double")) {
                    rAFile.writeDouble( (double)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("datatime")) {
                    rAFile.writeLong( (long)(object));
                }
                if(record.getDataTypes().get(i)==data.nameToSerialCode("date")) {
                    rAFile.writeLong( (long)(object));
                }
                if(record.getDataTypes().get(i)>data.nameToSerialCode("text")){
                    byte length = (byte) (record.getDataTypes().get(i) - data.nameToSerialCode("text"));
                    if(((byte[])(object)).length>length){
                        return false;
                    }
                    for (byte k = 0; k < length; k++) {
                        rAFile.writeByte(((byte[])(object))[k]);
                    }
                }
            }
            rAFile.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
    public void setRecordAddrList( ArrayList<Short> recordAddrList) {
        this.recordAddrList=recordAddrList;
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

