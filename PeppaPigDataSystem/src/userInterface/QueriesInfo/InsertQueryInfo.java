package userInterface.QueriesInfo;

import java.util.ArrayList;

/**
 * @author LI LIU 2018-07-16
 * */
public class InsertQueryInfo {

    public String tableName;
    public ArrayList<String> columns;
    public ArrayList<String> values;
    public boolean isInsertAll = false;

    public InsertQueryInfo(String tableName, ArrayList<String> columns, ArrayList<String> values){
        this.tableName = tableName;
        this.columns =columns;
        if(columns == null){
            isInsertAll = true;
        }
        this.values = values;
    }

}
