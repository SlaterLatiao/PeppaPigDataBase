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
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String DATETIME = "datetime";
    public static final String DATE = "date";
    public static final String TEXT = "text";
    public static final String NULL ="null";

    private HashMap<String,Byte> serialCodeMap;
    private HashMap<Byte,String> typeNameMap;
    private HashMap<String,Integer> sizeMap;
    
    public String dataTypeName;
    public byte serialCode;
    public int contentSize;

    private static DataType instance;
    
    public DataType(String typeName){
        this.dataTypeName = typeName;
        DataType dt = DataType.getInstance();
        this.serialCode = dt.nameToSerialCode(dataTypeName);
        this.contentSize = dt.nameToSize(dataTypeName);
    }
    
    private DataType() {
        serialCodeMap = new HashMap<>();
        serialCodeMap.put(NULL,(byte) 0x00);
        serialCodeMap.put(TINYINT, (byte) 0x01);
        serialCodeMap.put(SMALLINT, (byte) 0x02);
        serialCodeMap.put(INT, (byte) 0x03);
        serialCodeMap.put(BIGINT, (byte) 0x04);
        serialCodeMap.put(FLOAT, (byte) 0x05);
        serialCodeMap.put(DOUBLE, (byte) 0x06);
        serialCodeMap.put(DATETIME, (byte) 0x07);
        serialCodeMap.put(DATE, (byte) 0x08);
        serialCodeMap.put(TEXT, (byte) 0x0A);
        
        typeNameMap = new HashMap<>();
        typeNameMap.put((byte) 0x00,NULL);
        typeNameMap.put((byte) 0x01,TINYINT);
        typeNameMap.put((byte) 0x02,SMALLINT);
        typeNameMap.put((byte) 0x03,INT);
        typeNameMap.put((byte) 0x04,BIGINT);
        typeNameMap.put((byte) 0x05,FLOAT);
        typeNameMap.put((byte) 0x06,DOUBLE);
        typeNameMap.put((byte) 0x07,DATETIME);
        typeNameMap.put((byte) 0x08,DATE);
        typeNameMap.put((byte) 0x0A,TEXT);
        
        sizeMap = new HashMap<>();
        sizeMap.put(NULL, 0);
        sizeMap.put(TINYINT, 1);
        sizeMap.put(SMALLINT, 2);
        sizeMap.put(INT, 4);
        sizeMap.put(BIGINT, 8);
        sizeMap.put(FLOAT, 4);
        sizeMap.put(DOUBLE, 8);
        sizeMap.put(DATETIME, 8);
        sizeMap.put(DATE, 8);
        sizeMap.put(TEXT, 10);
    }
    
    public static DataType getInstance() {
    	if (instance == null) {
            instance = new DataType();
        }
        return instance;
    }

    public byte nameToSerialCode(String dataTypeName){
        return serialCodeMap.get(dataTypeName);
    }

    public String serialCodeToName(Byte serialCode){
        return typeNameMap.get(serialCode);
    }

    public int nameToSize(String dataTypeName){
        return sizeMap.get(dataTypeName);
    }

}
