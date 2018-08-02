package userInterface.QueriesInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Common.Column;
import Common.Constants;
import Common.DataType;

import userInterface.Utils.Errors;

/**
 * @author LI LIU 2018-07-16
 * */
public class CreateTableQueryInfo {

    public String tableName;
    public ArrayList<Column> columns;


    public CreateTableQueryInfo(String tabelName, ArrayList<String> columns){
        this.tableName = tabelName;
        this.columns = getColumns(columns);
    }

    /**
     * initial all columns of the table
     * */
    public ArrayList<Column> getColumns(ArrayList<String> col){
        //columns list
        ArrayList<Column> columns = new ArrayList<>();
        /*Column rowid = new Column("rowid",new DataType("int"),false,true);
        columns.add(rowid);*/

        boolean isFirst = true;
        for(String c:col){

            boolean isPrimary = false;
            boolean isNotNull = false;

            if(isFirst){
                if(c.contains("primary key")){ //user set first column as primary key
                    isPrimary = true;
                    isNotNull = true;
                }
            }else{
                if(c.contains("primary key")){
                    System.out.println(Errors.PRIMARY_KEY);
                }
            }

            if(c.contains("not null")){
                isNotNull = true;
            }

            String[] subStrings = c.split(" ");// e.g. substrings = {"id","int","primary","key"}

            //get column
            String columnName = subStrings[0].toLowerCase();
            if(!isValidType(subStrings[1].toLowerCase())){
                System.out.println(Errors.INVALID_TYPE.replace("%1",subStrings[1]));
                return columns;
            }
            DataType dataType = new DataType(subStrings[1].toLowerCase());
            Column column = new Column(columnName,dataType,isPrimary,isNotNull);
            columns.add(column);
            isFirst = false;
        }
        return columns;
    }

    public boolean isValidType(String s){
        List<String> tempList = Arrays.asList(Constants.DATATYPE_LIST);
        return (tempList.contains(s));
    }

}
