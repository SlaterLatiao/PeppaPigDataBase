package userInterface.QueriesInfo;

import userInterface.Utils.Condition;
import java.util.ArrayList;

/**
 * @author LI LIU 2018-07-16
 * */
public class UpdateQueryInfo {
    public String tableName;
    public String columnName;
    public String value;
    public ArrayList<Condition> conditions;
    public String logiOper;

    public UpdateQueryInfo(String tableName, String columnName, String value, ArrayList<Condition> conditions, String logiOper){
        this.tableName =tableName;
        this.columnName =columnName;
        this.value = value;
        this.conditions = conditions;
        this.logiOper = logiOper;
    }

}
