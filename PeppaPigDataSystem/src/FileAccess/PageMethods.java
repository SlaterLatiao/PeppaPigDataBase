package FileAccess;

import Common.Constants;
import Common.DataType;
import fileSystem.Page;
import fileSystem.Record;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class PageMethods {
    private int pNum;
    private byte type;
    private byte nRecords;
    private short startAddr;
    private int rPointer;
    private List<Short> rStarts;
    private List<Record> records;
    File tableFile;
    private RandomAccessFile raf;
    public PageMethods(String filePath) {
        pNum = 0;
        records = new ArrayList<Record>();
        tableFile = new File(filePath);
    }

//##################################################################################################################
//FOR FUNCTION getMaxRowID()

    /**
         *
         * @return -1 means no page in this file or can not find a leaf page in this file
         */
    public int getMaxRowID(){
        File file = tableFile;
        boolean getMax = false;
        int maxPageNum=(int)(file.length()/(long)Constants.PAGE_SIZE)-1;
        if(maxPageNum<0){
            return -1;
        }
        byte maxPageType;
        while(!getMax){
            maxPageType =readPageType(file,maxPageNum);
            if (maxPageType==Constants.LEAF_TABLE_PAGE||maxPageNum==Constants.LEAF_INDEX_PAGE){
                getMax=true;
                return readMaxRowId(file,maxPageNum);
            }
            else{
                maxPageNum-=1;
            }
        }
        return -1;
    }

    /**
     *
     * @param file
     * @param maxPageNum max page number in this file, not necessary to be max leaf page
     * @return -1 means read failed
     */
    private byte readPageType(File file,int maxPageNum){
        try {
            RandomAccessFile randomAccessFile=null;
            randomAccessFile = new RandomAccessFile(file, "r");
            //initial root page
            randomAccessFile.seek(maxPageNum*Constants.PAGE_SIZE);
            byte maxPageType = randomAccessFile.readByte();
            randomAccessFile.close();
            return maxPageType;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     *
     * @param file
     * @param maxPageNum
     * @return 0 means no record in this file, -1 mneas read failed
     */
    private int readMaxRowId(File file,int maxPageNum){
        int maxRowId = 0;
        try {
            RandomAccessFile randomAccessFile=null;
            randomAccessFile.seek(maxPageNum*Constants.PAGE_SIZE+1);
            byte recordNum = randomAccessFile.readByte();
            if(recordNum == 0){
                return maxRowId;
            }
            else{
                randomAccessFile.seek(maxPageNum*Constants.PAGE_SIZE+Constants.PAGE_HEADER_LENGTH+2*(maxPageNum-1));
                byte maxRecordAddr=randomAccessFile.readByte();
                randomAccessFile.seek(maxPageNum*Constants.PAGE_SIZE+maxRecordAddr+1);
                maxRowId=randomAccessFile.readInt();
            }
            randomAccessFile.close();
            return maxRowId;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
//##################################################################################################################


//##################################################################################################################
//FOR FUNCTION remove()

    /**
     *
     * @param row_id
     */
    public void remove(int row_id) {
        // TODO Auto-generated method stub
        RandomAccessFile raf=null;
        try {
            raf = new RandomAccessFile(tableFile, "rw");

            raf.seek(getFileAddr(1));
            raf.writeByte(nRecords-1);

            int index_rowId=-1;
            for (int i =0;i<nRecords;i++){
                raf.seek(getFileAddr(rStarts.get(i)));
                if(raf.readInt()==row_id) {
                    index_rowId = i;
                    break;
                }
            }
            rStarts.remove(index_rowId);
            nRecords-=1;

            raf.seek(getFileAddr(Constants.PAGE_HEADER_LENGTH));
            for (int i =0;i<nRecords;i++){
                raf.writeShort(rStarts.get(i));
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
//##################################################################################################################

//##################################################################################################################
//FOR FUNCTION update()

    public void update(int k, Record r) {
        try {
            raf = new RandomAccessFile(tableFile, "rw");
            //get the position(address or index in this page) of the record with row_id k.
            for (int i = 0;i<nRecords;i++){
                raf.seek(getFileAddr(rStarts.get(i))+1);
                if(raf.readInt()==k){
                    raf.seek(getFileAddr(rStarts.get(i)));
                    raf.writeShort(r.getPayLoad());
                    raf.writeInt(r.getRowId());
                    raf.writeByte(r.getNumOfColumn());
                    for (int j = 0; j < r.getNumOfColumn(); j++)
                        raf.writeByte(r.getDataTypes().get(j));
                    // write column contents
                    for (int m = 0; m< r.getNumOfColumn(); m++) {
                        String content = r.getValuesOfColumns().get(m);
                        byte dataType = r.getDataTypes().get(m);
                        writeDataByType(content, dataType);
                    }
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//##################################################################################################################
//FOR FUNCTION setPNum()

    public void setPNum(int n) {
       pNum=n;
    }

//##################################################################################################################
//FOR FUNCTION exchangeContent()

    public void exchangeContent(PageMethods page) {
        try {
            //write page into root(0th page)
            raf = new RandomAccessFile(tableFile, "rw");
            raf.seek(getFileAddr(0));
            raf.writeByte(page.type);
            raf.writeByte(page.nRecords);
            raf.writeShort(page.startAddr);
            raf.writeInt(page.rPointer);

            for(int i =0;i<page.nRecords;i++){
                raf.writeShort(page.rStarts.get(i));
            }
            List<Record> recordsBuffer = page.records;
            Record rBuffer;
            List<Byte> tBuffer;
            List<String> cBuffer;
            for(int j=0;j<nRecords;j++){
                rBuffer = recordsBuffer.get(j);
                raf.writeShort(rBuffer.getPayLoad());
                raf.writeInt(rBuffer.getRowId());
                raf.writeByte(rBuffer.getNumOfColumn());

                tBuffer = rBuffer.getDataTypes();
                for(int k=0;k<rBuffer.getNumOfColumn();k++){
                    raf.writeByte(tBuffer.get(k));
                }

                cBuffer = rBuffer.getValuesOfColumns();
                for(int m=0;m<rBuffer.getNumOfColumn();m++){
                    writeDataByType(cBuffer.get(m), tBuffer.get(m));
                }
            }
            //write previous 0th page into page.position
            raf = new RandomAccessFile(tableFile, "rw");
            raf.seek(page.getFileAddr(0));
            raf.writeByte(type);
            raf.writeByte(nRecords);
            raf.writeShort(startAddr);
            raf.writeInt(rPointer);

            for(int i =0;i<nRecords;i++){
                raf.writeShort(rStarts.get(i));
            }

            for(int j=0;j<nRecords;j++){
                rBuffer = records.get(j);
                raf.writeShort(rBuffer.getPayLoad());
                raf.writeInt(rBuffer.getRowId());
                raf.writeByte(rBuffer.getNumOfColumn());

                tBuffer = rBuffer.getDataTypes();
                for(int k=0;k<rBuffer.getNumOfColumn();k++){
                    raf.writeByte(tBuffer.get(k));
                }

                cBuffer = rBuffer.getValuesOfColumns();
                for(int m=0;m<rBuffer.getNumOfColumn();m++){
                    writeDataByType(cBuffer.get(m), tBuffer.get(m));
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//##################################################################################################################


//##################################################################################################################
//FOR FUNCTION getChildren()

    public List<Page> getChildren() {

        return null;
    }
//##################################################################################################################

//##################################################################################################################

//##################################################################################################################








    //hou mian shi mei sha yong de dong xi
    private long getFileAddr(int offset) {
        return pNum * Constants.PAGE_SIZE + offset;
    }
    private void writeDataByType(String content, byte dataType) {}
    public List<Record> getRecordList() {
        return records;
    }
}
