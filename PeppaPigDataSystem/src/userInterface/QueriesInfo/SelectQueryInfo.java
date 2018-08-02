package userInterface.QueriesInfo;


import userInterface.Utils.Condition;
import java.util.ArrayList;

/**
 * @author LI LIU 2018-07-16
 * */
public class SelectQueryInfo {

    public String tableName;
    public boolean isSelectAll = false;
    public ArrayList<String> columns;
    public ArrayList<Condition> conditions;
    public String logiOper;


    public SelectQueryInfo(ArrayList<String> attributesList, String tabelName, ArrayList<Condition> selectCondition, String logiOper){

        this.columns = attributesList;
        if(columns.size()==1 && columns.get(0).equals("*")){
            isSelectAll = true;
        }
        this.tableName = tabelName;
        this.conditions = selectCondition;
        this.logiOper = logiOper;
    }


}
