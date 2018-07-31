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
                this.recordAddrList = new ArrayList<Short>();
                this.RecordList = new ArrayList<Record>();
                RandomAccessFile rAFile=null;
                if (!newFile.exists()) {
                    return;
                }
                try {
                    rAFile = new RandomAccessFile(newFile, "rw");
                    rAFile.seek(0);
                    this.setPageNum(rAFile.readInt());
                    this.readPage();
                    rAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                setPageType(Constants.LEAF_TABLE_PAGE);
                setNumOfRecords((byte)0x00);
                setStartAddr((short)(Constants.PAGE_SIZE-1));
                setRightNodeAddr(Constants.RIGET_MOST_PAGE);
                this.recordAddrList = new ArrayList<Short>();
                this.RecordList = new ArrayList<Record>();
                setPageNum(1);
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
        setStartAddr((short)(Constants.PAGE_SIZE-1));
//        setRightNodeAddr(constants.RIGET_MOST_PAGE);
        this.RecordList = new ArrayList<Record>();
//        setPageNum(0);
    }

    // get page by index
    public Page(String filePath, int pageNum) {
        setPageNum(pageNum);
        setFilePath(filePath);
        this.recordAddrList = new ArrayList<Short>();
        this.RecordList = new ArrayList<Record>();
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
                randomAccessFile.seek(this.getPageNum()*Constants.PAGE_SIZE);
                randomAccessFile.writeByte(Constants.LEAF_TABLE_PAGE);
                randomAccessFile.writeByte((byte)0x00);
                randomAccessFile.writeShort((short)(Constants.PAGE_SIZE - 1));
                randomAccessFile.writeInt(Constants.RIGET_MOST_PAGE);
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
            rAFile.writeInt(this.getPageNum());
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
        if(RecordList.size()==0){
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
    private int readMaxRowId() {
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return 0;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "r");
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE);
            byte type = rAFile.readByte();
            this.setPageType(type);
            this.setNumOfRecords(rAFile.readByte());
            this.setStartAddr(rAFile.readShort());
            this.setRightNodeAddr(rAFile.readInt());
            if(this.getNumOfRecords()>0) {
                for (int i = 0; i < this.getNumOfRecords(); i++) {
                    this.addRecordAddrList(rAFile.readShort());
                }
            }
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE+ this.getRecordAddrList().get(this.recordAddrList.size())+2);
            int rowId = rAFile.readInt();
            rAFile.close();
            return rowId;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    //################################################################
    //can only read leaf page
    /**
     *
     *
     */
    private void readPage() {
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "r");
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE);
            byte type = rAFile.readByte();
            this.setPageType(type);
            if(type == Constants.LEAF_TABLE_PAGE||type==Constants.LEAF_INDEX_PAGE){
                this.setNumOfRecords(rAFile.readByte());
                this.setStartAddr(rAFile.readShort());
                this.setRightNodeAddr(rAFile.readInt());
                if(this.getNumOfRecords()>0){
                    for (int i =0;i<this.getNumOfRecords();i++) {
                        this.addRecordAddrList(rAFile.readShort());
                    }
                    for (int i =0;i<this.getNumOfRecords();i++) {
                        Record record=new Record();
                        rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE+ this.getRecordAddrList().get(i));
                        record.setPayLoad(rAFile.readShort());
                        record.setRowId(rAFile.readInt());
                        record.setNumOfColumn(rAFile.readByte());
                        ArrayList<Byte> dataTypeList= new ArrayList<Byte>();
                        for(int j=0;j<record.getNumOfColumn();j++) {
                            dataTypeList.add(rAFile.readByte());
                        }
                        record.setDataTypes(dataTypeList);
                        ArrayList<String> valuesOfColumns= new ArrayList<String>();
//                        rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE+ this.getRecordAddrList().get(i));
                        for(int l = 0; l <record.getNumOfColumn(); l++) {
                            byte dataTypes = record.getDataTypes().get(l);
                            if(dataTypes==data.nameToSerialCode("null")) {
                                valuesOfColumns.add("");
                            }
                            else if(dataTypes==data.nameToSerialCode("tinyint")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readByte()));
                            }
                            else if(dataTypes==data.nameToSerialCode("smallint")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readShort()));
                            }
                            else if(dataTypes==data.nameToSerialCode("int")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readInt()));
                            }
                            else if(dataTypes==data.nameToSerialCode("bigint")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readLong()));
                            }
                            else if(dataTypes==data.nameToSerialCode("float")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readFloat()));
                            }
                            else if(dataTypes==data.nameToSerialCode("double")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readDouble()));
                            }
                            else if(dataTypes==data.nameToSerialCode("datetime")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readLong()));
                            }
                            else if(dataTypes==data.nameToSerialCode("date")) {
                                valuesOfColumns.add(String.valueOf(rAFile.readLong()));
                            }
                            else if(dataTypes>data.nameToSerialCode("text")){
                                byte length = (byte) (record.getDataTypes().get(i) - data.nameToSerialCode("text"));
                                char[] text = new char[length];
                                for (byte k = 0; k < length; k++) {
                                    text[k] = (char) rAFile.readByte();
                                }
                                valuesOfColumns.add(new String(text));
                            }
                        }
                        record.setValuesOfColumns(valuesOfColumns);
                        this.addRecordList(record);
                    }
                }

            }
            else{
                //###############################################################################################
                //need inner page read function
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
            int maxLeafPageNum=(int)newFile.length()/Constants.PAGE_SIZE-1;
            Page page=null;
            for(int i=maxLeafPageNum; i>0;i--)
            {
                page = new Page(this.filePath,i);
                if((page.getPageType()==Constants.LEAF_TABLE_PAGE)||(page.getPageType()==Constants.LEAF_INDEX_PAGE)){
                    break;
                }
            }
            if(page.getNumOfRecords()>0){
                return readMaxRowId();
            }
            else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 286331153;
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
            rAFile = new RandomAccessFile(newFile, "rw");
            rAFile.setLength(Constants.PAGE_SIZE);
            rAFile.seek(this.pageNum * Constants.PAGE_SIZE+4);
            rAFile.writeInt(this.rightNodeAddr);
            rAFile.seek(page.pageNum * Constants.PAGE_SIZE);
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
        record.calculatePayLoad();
        File newFile = new File(this.filePath);
        RandomAccessFile rAFile=null;
        if (!newFile.exists()) {
            return;
        }
        try {
            rAFile = new RandomAccessFile(newFile, "rw");
            rAFile.seek(this.getPageNum()* Constants.PAGE_SIZE+1);
            this.setNumOfRecords((byte)(this.getNumOfRecords()+1));
            rAFile.writeByte(this.numOfRecords);
            this.setStartAddr((short)(this.getStartAddr() - record.getPayLoad() - 6));
            rAFile.writeShort(this.startAddr);

//#################################################################################################
//update all recordaddrlist
//            for (int i =0;i<this.getNumOfRecords();i++) {
//               rAFile.writeShort( this.recordAddrList.get(i));
//            }
            this.addRecordAddrList(this.startAddr);
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE + Constants.PAGE_HEADER_LENGTH+2*(this.recordAddrList.size()-1));
            rAFile.writeShort(this.startAddr);
            this.addRecordList(record);
            int test = this.getPageNum() * Constants.PAGE_SIZE + this.startAddr;
            rAFile.seek(test);
            rAFile.writeShort(record.getPayLoad());
            rAFile.writeInt( record.getRowId());
            rAFile.writeByte(record.getNumOfColumn());
            for(int j=0;j<record.getNumOfColumn();j++) {
                rAFile.writeByte(record.getDataTypes().get(j));
            }

            for(int i=0;i<record.getNumOfColumn();i++) {
                String object = record.getValuesOfColumns().get(i);
                byte dataTypes =record.getDataTypes().get(i);
                if(dataTypes==data.nameToSerialCode("null")) {
                }
                else if(dataTypes==data.nameToSerialCode("tinyint")) {
                    rAFile.writeByte(new Byte(object));
                }

                else if(dataTypes==data.nameToSerialCode("smallint")) {
                    rAFile.writeShort(new Short(object));
                }
                else if(dataTypes==data.nameToSerialCode("int")) {
                    rAFile.writeInt(new Integer(object));
                }
                else if(dataTypes==data.nameToSerialCode("bigint")) {
                    rAFile.writeLong(new Long(object));
                }
                else if(dataTypes==data.nameToSerialCode("float")) {
                    rAFile.writeFloat(new Float(object));
                }
                else if(dataTypes==data.nameToSerialCode("double")) {
                    rAFile.writeDouble(new Double(object));
                }
                else if(dataTypes==data.nameToSerialCode("datetime")) {
                    rAFile.writeLong(new Long(object));
                }
                else if(dataTypes==data.nameToSerialCode("date")) {
                    rAFile.writeLong(new Long(object));
                }
                else if(dataTypes>data.nameToSerialCode("text")){
                    byte length = (byte) (record.getDataTypes().get(i) - data.nameToSerialCode("text"));
                    rAFile.writeBytes(object.trim());
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
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE+1);
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
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE + Constants.PAGE_HEADER_LENGTH+2*(this.recordAddrList.size()-1));
            rAFile.writeShort(this.startAddr);
            this.addRecordList(record);
            rAFile.seek(this.getPageNum() * Constants.PAGE_SIZE + this.startAddr);
            rAFile.writeShort(record.getPayLoad());
            rAFile.writeInt( record.getRowId());
            rAFile.writeByte(record.getNumOfColumn());
            for(int j=0;j<record.getNumOfColumn();j++) {
                rAFile.writeByte(record.getDataTypes().get(j));
            }
            for(int i=0;i<record.getNumOfColumn();i++) {
                String object = record.getValuesOfColumns().get(i);
                byte dataTypes = record.getDataTypes().get(i);
                if(dataTypes==data.nameToSerialCode("null")) {
                }
                else if(dataTypes==data.nameToSerialCode("tinyint")) {
                    rAFile.writeByte(new Byte(object));
                }

                else if(dataTypes==data.nameToSerialCode("smallint")) {
                    rAFile.writeShort(new Short(object));
                }
                else if(dataTypes==data.nameToSerialCode("int")) {
                    rAFile.writeInt(new Integer(object));
                }
                else if(dataTypes==data.nameToSerialCode("bigint")) {
                    rAFile.writeLong(new Long(object));
                }
                else if(dataTypes==data.nameToSerialCode("float")) {
                    rAFile.writeFloat(new Float(object));
                }
                else if(dataTypes==data.nameToSerialCode("double")) {
                    rAFile.writeDouble(new Double(object));
                }
                else if(dataTypes==data.nameToSerialCode("datetime")) {
                    rAFile.writeLong(new Long(object));
                }
                else if(dataTypes==data.nameToSerialCode("date")) {
                    rAFile.writeLong(new Long(object));
                }
                else if(dataTypes>data.nameToSerialCode("text")){
                    byte length = (byte) (record.getDataTypes().get(i) - data.nameToSerialCode("text"));
                    rAFile.writeBytes(object.trim());
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

