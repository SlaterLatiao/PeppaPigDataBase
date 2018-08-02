package userInterface.Utils;

/**
 * @author LI LIU 2018-07-21
 * */
public class Errors{
    public static String SYNTAX_ERROR = "SYNTAX_ERROR:'%1' is not correct davisql statement.\nNote: type 'help;' to check the syntax of the commands.";
    public static String DIRECTORY_EXISTS = "ERROR: Directory '%1' already EXISTS.";
    public static String TABLE_NOT_EXISTS = "ERROR: Table '%1' doesn't exists.";
    public static String TABLE_EXISTS = "ERROR: Table '%1' already exists.";
    public static String COLUMN_NOT_EXISTS = "ERROR: Column '%1' doesn't exists.";
    public static String TABLE_NAME_NEEDED = "ERROR: Table name need to be specified.";
    public static String COLUMNS_NEEDED = "ERROR: Columns and its type need to be specified.";
    public static String INVALID_TYPE = "ERROR: '%1' is not valid data type";
    public static String PRIMARY_KEY ="ERROR: primary key must be specified at first column";


}
