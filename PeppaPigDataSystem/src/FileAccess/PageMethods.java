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
//FOR FUNCTION remove()

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

    public void exchangeContent(Page page) {
        try {
            byte[] b = new byte[512];
            //write page into root(0th page)
            raf = new RandomAccessFile(tableFile, "rw");
            raf.readFully(b,(int)getFileAddr(0),512);
            raf.seek(page.getFileAddr(0));
            raf.writeBytes(new String(b));

            //write previous 0th page into page.position\
            raf = new RandomAccessFile(tableFile, "rw");
            raf.readFully(b,(int)page.getFileAddr(0),512);
            raf.seek(getFileAddr(0));
            raf.writeBytes(new String(b));
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//##################################################################################################################


//##################################################################################################################
//FOR FUNCTION getChildren()

    public List<Page> getChildren() {
        Record cRecord;
        ArrayList<Page> children = new ArrayList<Page>();
        Page page;
        int pnBuffer;
        page = new Page(tableFile.getAbsolutePath(),rPointer);
        children.add(page);
        for(int i =1;i<nRecords;i++){
            cRecord=records.get(i);
            pnBuffer=Integer.valueOf(cRecord.getValuesOfColumns().get(0));
            page = new Page(tableFile.getAbsolutePath(),pnBuffer);
            children.add(page);
        }
        return children;
    }
//##################################################################################################################

//##################################################################################################################
//FOR CONSTRUCTOR page()
    public Page(String filePath,int pNum){
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
