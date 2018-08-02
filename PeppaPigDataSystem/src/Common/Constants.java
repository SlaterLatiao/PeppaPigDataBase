package Common;

/**
 * @author LI LIU 2018-07-21
 * */

public class Constants {

    public static String DEFAULT_FILE_EXTENSION = ".tbl";
    public static String DEFAULT_DATA_DIRNAME = "data";
    public static String DEFAULT_CATALOG_DIRNAME = "catalog";
    public static String DEFAULT_USERDATA_DIRNAME = "userdata";
    public static String SYSTEM_TABLES_TABLENAME = "davisbase_tables";
    public static String SYSTEM_COLUMNS_TABLENAME = "davisbase_columns";

    public static String SYSTEM_TABLES_PATH = "data/catalog/davisbase_tables.tbl";
    public static String SYSTEM_COLUMNS_PATH = "data/catalog/davisbase_columns.tbl";

    public static String SYSTEM_USER_PATH = "data/userdata";
    public static String SYSTEM_CATALOG_PATH = "data/catalog";
    public static final String USE_HELP_MESSAGE = "Please use 'HELP' to see a list of commands";

    public static final byte DAVIS_TABLES_NUM_OF_COLUMNS = 0x02;
    public static final byte DAVIS_COLUMNS_NUM_OF_COLUMNS = 0x06;

    public static final String[] DATATYPE_LIST = new String[]{"tinyint", "smallint", "int","bigint","float","double","datetime","date","text"};

    public static final short PAGE_SIZE = 512;
    public static final byte INTERIOR_INDEX_PAGE = 0x02;
    public static final byte INTERIOR_TABLE_PAGE = 0x05;
    public static final byte LEAF_INDEX_PAGE = 0x0a;
    public static final byte LEAF_TABLE_PAGE = 0x0d;
    public static final byte RIGET_MOST_PAGE = 0xFFFFFFFF;

    public static final int PAGE_TYPE_OFFSET = 0;
    public static final int NUM_OF_RECORD_OFFSET = 1;
    public static final int START_CONTENT_POS_OFFSET = 2;
    public static final int RIGHT_PAGE_OFFSET = 4;
    public static final int ARRAY_OF_RECORD_OFFSET = 8;
    public static final int PAGE_HEADER_LENGTH = 8;
    public static final int RECORD_HEADER_LENGTH = 6;


}

