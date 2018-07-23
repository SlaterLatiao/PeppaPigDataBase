package Common;

import java.util.HashMap;

/**
 * @author LI LIU 2018-07-21
 * */
public class DataType {
    public static final String TINYINT = "tinyint";
    public static final String SMALLINT = "smallint";
    public static final String INT = "int";
    public static final String BIGINT = "bigint";
    public static final String REAL = "real";
    public static final String DOUBLE = "double";
    public static final String DATETIME = "datetime";
    public static final String DATE = "date";
    public static final String TEXT = "text";
    public static final String NULL ="null";

    public String dataTypeName;
    public byte serialCode;
    public int contentSize;

    public DataType(byte serialCode){
        this.serialCode = serialCode;
    }

    public DataType(String typeName){

        this.dataTypeName = typeName;
        this.serialCode = nameToSerialCode(dataTypeName);
        this.contentSize = nameToSize(dataTypeName);
    }

    public byte nameToSerialCode(String dataTypeName){
        HashMap<String,Byte> serialCodeMap = new HashMap<>();
        serialCodeMap.put(NULL,(byte) 0x00);
        serialCodeMap.put(TINYINT, (byte) 0x04);
        serialCodeMap.put(SMALLINT, (byte) 0x05);
        serialCodeMap.put(INT, (byte) 0x06);
        serialCodeMap.put(BIGINT, (byte) 0x07);
        serialCodeMap.put(REAL, (byte) 0x08);
        serialCodeMap.put(DOUBLE, (byte) 0x09);
        serialCodeMap.put(DATETIME, (byte) 0x0A);
        serialCodeMap.put(DATE, (byte) 0x0B);
        serialCodeMap.put(TEXT, (byte) 0x0C);

        return serialCodeMap.get(dataTypeName);
    }

    public String serialCodeToName(Byte serialCode){
        HashMap<Byte,String> typeNameMap = new HashMap<>();
        typeNameMap.put((byte) 0x00,NULL);
        typeNameMap.put((byte) 0x04,TINYINT);
        typeNameMap.put((byte) 0x05,SMALLINT);
        typeNameMap.put((byte) 0x06,INT);
        typeNameMap.put((byte) 0x07,BIGINT);
        typeNameMap.put((byte) 0x08,REAL);
        typeNameMap.put((byte) 0x09,DOUBLE);
        typeNameMap.put((byte) 0x0A,DATETIME);
        typeNameMap.put((byte) 0x0B,DATE);
        typeNameMap.put((byte) 0x0C,TEXT);

        return typeNameMap.get(serialCode);
    }

    public int nameToSize(String dataTypeName){
        HashMap<String,Integer> sizeMap = new HashMap<>();
        sizeMap.put(NULL, 0);
        sizeMap.put(TINYINT, 1);
        sizeMap.put(SMALLINT, 2);
        sizeMap.put(INT, 4);
        sizeMap.put(BIGINT, 8);
        sizeMap.put(REAL, 4);
        sizeMap.put(DOUBLE, 8);
        sizeMap.put(DATETIME, 8);
        sizeMap.put(DATE, 8);
        sizeMap.put(TEXT, 2); // 0x0C - 0x0A

        return sizeMap.get(dataTypeName);
    }


}