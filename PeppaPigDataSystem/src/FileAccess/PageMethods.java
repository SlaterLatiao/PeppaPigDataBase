package FileAccess;

import Common.Constants;
import Common.DataType;
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
}
