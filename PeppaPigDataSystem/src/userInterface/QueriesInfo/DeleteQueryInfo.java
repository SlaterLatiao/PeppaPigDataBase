package userInterface.QueriesInfo;

import userInterface.Utils.Condition;
import java.util.ArrayList;

/**
 * @author LI LIU 2018-07-16
 * */
public class DeleteQueryInfo {
    public String tableName;
    public ArrayList<Condition> conditions;
    public boolean isDeleteAll = false;
    public String logiOper;

    public DeleteQueryInfo(String tableName,ArrayList<Condition> conditions, String logiOper){
        this.tableName = tableName;
        this.conditions = conditions;
        if(conditions == null){
            isDeleteAll = true;
        }
        this.logiOper = logiOper;
    }


}
