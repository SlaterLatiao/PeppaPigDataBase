package databaseAPI;

import Common.Column;
import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.QueriesInfo.UpdateQueryInfo;
import userInterface.Utils.Condition;

import java.util.ArrayList;
import java.util.List;

public class UpdateQueryExe {
    public static void executeQuery(UpdateQueryInfo info){
        //find row id's of condition

        Table user_updateTable = new Table(Constants.SYSTEM_USER_PATH+"/"+info.tableName+Constants.DEFAULT_FILE_EXTENSION);

        List<Record> user_tableRecords = user_updateTable.getAllRecord();

        ArrayList<Column> user_tableColumn = General.getColumns(info.tableName);

        ArrayList<Condition> user_tableConditions = info.conditions;



        for(Condition cond : user_tableConditions){
            int indexCol = -1;
            for(int i = 0; i < user_tableColumn.size(); i++){
                if(cond.getColumn().equals(user_tableColumn.get(i).getColumnName())) {
                    indexCol = i;
                }
            }

            ArrayList<Integer> validRowID = null;

            for(Record r : user_tableRecords){
                ArrayList<String> r_valueofCol = r.getValuesOfColumns();
                String valofIndexCol = r_valueofCol.get(indexCol);

                if(cond.getOperator().equals("=")){
                    if(equalsCompare(valofIndexCol, cond.getValue())){
                        validRowID.add(r.getRowId());
                    }
                }
                else if(cond.getOperator().equals(">")){
                    if(greaterCompare(valofIndexCol, cond.getValue())){
                        validRowID.add(r.getRowId());
                    }
                }
                else if(cond.getOperator().equals("<")){
                    if(lessCompare(valofIndexCol, cond.getValue())){
                        validRowID.add(r.getRowId());
                    }
                }
                else if(cond.getOperator().equals(">=")){
                    if(greaterEqualsCompare(valofIndexCol, cond.getValue())){
                        validRowID.add(r.getRowId());
                    }
                }
                else if(cond.getOperator().equals("<=")){
                    if(lessEqualsCompare(valofIndexCol, cond.getValue())){
                        validRowID.add(r.getRowId());
                    }
                }
                else{
                    //error
                }
            }



        }


        //make a new record for each row id

    }

    private static boolean equalsCompare(String valofIndexCol, String value) {



        return false;
    }
}
