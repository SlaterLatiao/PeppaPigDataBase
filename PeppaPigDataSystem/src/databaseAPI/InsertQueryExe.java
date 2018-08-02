package databaseAPI;

import Common.Column;
import Common.Constants;
import Common.DataType;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.QueriesInfo.InsertQueryInfo;

import java.util.ArrayList;

public class InsertQueryExe {
    public static void executeQuery(InsertQueryInfo info){

        String tablePath;
        tablePath = Constants.SYSTEM_USER_PATH;

        Table currTable = new Table(tablePath+"/"+info.tableName+Constants.DEFAULT_FILE_EXTENSION);

        ArrayList<Column> tableColumns = databaseAPI.General.getColumns(info.tableName);

        //1) Every record needs set num of columns

        ArrayList<String> userColumnsNames = info.columns;
        ArrayList<String> userValues = info.values;

        String[] colVal = new String[tableColumns.size()-1];

        for(String u_c : userColumnsNames){
            boolean isSuccess = false;
            for(int i = 0; i < colVal.length; i++){
                if(u_c.equals(tableColumns.get(i+1).getColumnName())){
                    if(colVal[i] == null) {
                        colVal[i] = userValues.get(i);
                        isSuccess = true;
                    }
                }
            }
            if (!isSuccess){
                System.out.println("Inconsistent user column with table column syntax");
                return;
            }
        }

        //2) Every record needs to set datatypes
        ArrayList<Byte> dataTypes = new ArrayList<>();
        for(int i=1;i<tableColumns.size();i++){
            if(tableColumns.get(i).getDataType().dataTypeName.equals("text")){
                dataTypes.add((byte)(tableColumns.get(i).getDataType().serialCode+info.values.get(i-1).length()));
            }else{
                dataTypes.add(tableColumns.get(i).getDataType().serialCode);
            }

        }

        //3) Every record needs to set values
        ArrayList<String> values = new ArrayList<>();
        for(String colValStr : colVal) {
            values.add(colValStr);
        }

        Record newRec = new Record((byte)tableColumns.size()-1,(byte)dataTypes.size(),dataTypes,values);
        currTable.insert(newRec);

    }

}
