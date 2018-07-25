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

        Record newRec = new Record();

        ArrayList<Column> tableColumns = databaseAPI.General.getColumns(info.tableName);

        //1) Every record needs set num of columns
        newRec.setNumOfColumn((byte)tableColumns.size());

        ArrayList<String> userColumnsNames = info.columns;
        ArrayList<String> userValues = info.values;

        String[] colVal = new String[tableColumns.size()];

        for(String u_c : userColumnsNames){
            boolean isSuccess = false;
            for(int i = 0; i < tableColumns.size(); i++){
                if(u_c.equals(tableColumns.get(i).getColumnName())){
                    if(colVal[i] == null) {
                        colVal[i] = u_c;
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
        for(Column col:tableColumns){
            if(col.getDataType().dataTypeName.equals("text")){
                dataTypes.add((byte)(col.getDataType().serialCode+col.getColumnName().length()));
            }else{
                dataTypes.add(col.getDataType().serialCode);
            }
        }
        newRec.setDataTypes(dataTypes);

        //3) Every record needs to set values
        ArrayList<String> values = new ArrayList<>();
        for(String colValStr : colVal) {
            values.add(colValStr);
        }
        newRec.setValuesOfColumns(values);

        currTable.insert(newRec);

    }
}
