package databaseAPI;

import Common.Column;
import Common.Constants;
import fileSystem.Record;
import fileSystem.Table;
import userInterface.Utils.Condition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WhereAPI {
    public static ArrayList<Integer> doWhere_getRowId (String tableName, ArrayList<Condition> conditions, String logiOper){

        //Initial variables
        Table user_updateTable = new Table(Constants.SYSTEM_USER_PATH+"/"+tableName+Constants.DEFAULT_FILE_EXTENSION);
        List<Record> user_tableRecords = user_updateTable.getAllRecord();

        ArrayList<Column> user_tableAllColumn = General.getColumns(tableName);

        ArrayList<Condition> user_Conditions = conditions;

        //String logiOper;

        //a list to store valid row id's
        ArrayList<Integer> validRowID = new ArrayList<>();

        if(logiOper == null){
            //find valid row id's for each condition
            for(Condition cond : user_Conditions){
                //loop each condition column name find the correlating index on the actual column table
                int indexCol = -1;
                for(int i = 0; i < user_tableAllColumn.size(); i++){
                    if(cond.getColumn().equals(user_tableAllColumn.get(i).getColumnName())) {
                        indexCol = i-1;
                    }
                }

                //loop all records
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
        }
        else if(logiOper.equals("not")){
            //find valid row id's for each condition
            for(Condition cond : user_Conditions){
                //loop each condition column name find the correlating index on the actual column table
                int indexCol = -1;
                for(int i = 0; i < user_tableAllColumn.size(); i++){
                    if(cond.getColumn().equals(user_tableAllColumn.get(i).getColumnName())) {
                        indexCol = i-1;
                    }
                }

                //loop all records
                for(Record r : user_tableRecords){
                    ArrayList<String> r_valueofCol = r.getValuesOfColumns();
                    String valofIndexCol = r_valueofCol.get(indexCol);

                    if(cond.getOperator().equals("=")){
                        if(notEqualsCompare(valofIndexCol, cond.getValue())){
                            validRowID.add(r.getRowId());
                        }
                    }
                    else if(cond.getOperator().equals(">")){
                        if(lessEqualsCompare(valofIndexCol, cond.getValue())){
                            validRowID.add(r.getRowId());
                        }
                    }
                    else if(cond.getOperator().equals("<")){
                        if(greaterEqualsCompare(valofIndexCol, cond.getValue())){
                            validRowID.add(r.getRowId());
                        }
                    }
                    else if(cond.getOperator().equals(">=")){
                        if(lessCompare(valofIndexCol, cond.getValue())){
                            validRowID.add(r.getRowId());
                        }
                    }
                    else if(cond.getOperator().equals("<=")){
                        if(greaterCompare(valofIndexCol, cond.getValue())){
                            validRowID.add(r.getRowId());
                        }
                    }
                    else{
                        //error
                    }
                }
            }
        }
        else if(logiOper.equals("and")) {
            //find valid row id's for each condition

            ArrayList<Integer> validRowID_T1 = new ArrayList<>();
            ArrayList<Integer> validRowID_T2 = new ArrayList<>();

            //find valid row id's for CONDITION 1-------------------------------------------------------------------

            //loop each condition column name find the correlating index on the actual column table
            int indexCol = -1;
            for(int i = 0; i < user_tableAllColumn.size(); i++){
                if(user_Conditions.get(0).getColumn().equals(user_tableAllColumn.get(i).getColumnName())) {
                    indexCol = i-1;
                }
            }

            //loop all records
            for(Record r : user_tableRecords){
                ArrayList<String> r_valueofCol = r.getValuesOfColumns();
                String valofIndexCol = r_valueofCol.get(indexCol);

                if(user_Conditions.get(0).getOperator().equals("=")){
                    if(equalsCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals(">")){
                    if(greaterCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals("<")){
                    if(lessCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals(">=")){
                    if(greaterEqualsCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals("<=")){
                    if(lessEqualsCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else{
                    //error
                }
            }

            //find valid row id's for CONDITION 2-------------------------------------------------------------------

            //loop each condition column name find the correlating index on the actual column table
            indexCol = -1;
            for(int i = 0; i < user_tableAllColumn.size(); i++){
                if(user_Conditions.get(1).getColumn().equals(user_tableAllColumn.get(i).getColumnName())) {
                    indexCol = i-1;
                }
            }

            //loop all records
            for(Record r : user_tableRecords){
                ArrayList<String> r_valueofCol = r.getValuesOfColumns();
                String valofIndexCol = r_valueofCol.get(indexCol);

                if(user_Conditions.get(1).getOperator().equals("=")){
                    if(equalsCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals(">")){
                    if(greaterCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals("<")){
                    if(lessCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals(">=")){
                    if(greaterEqualsCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals("<=")){
                    if(lessEqualsCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else{
                    //error
                }
            }

            //AND BOTH TEMP validROWID-------------------------------------------------------------------
            //INTERSECTION
            validRowID_T1.retainAll(validRowID_T2);
            ArrayList<Integer> validRowIDFinal = validRowID_T1;

            validRowID.addAll(validRowIDFinal);
        }
        else if(logiOper.equals("or")) {
            //find valid row id's for each condition

            ArrayList<Integer> validRowID_T1 = new ArrayList<>();
            ArrayList<Integer> validRowID_T2 = new ArrayList<>();

            //find valid row id's for CONDITION 1-------------------------------------------------------------------

            //loop each condition column name find the correlating index on the actual column table
            int indexCol = -1;
            for(int i = 0; i < user_tableAllColumn.size(); i++){
                if(user_Conditions.get(0).getColumn().equals(user_tableAllColumn.get(i).getColumnName())) {
                    indexCol = i-1;
                }
            }

            //loop all records
            for(Record r : user_tableRecords){
                ArrayList<String> r_valueofCol = r.getValuesOfColumns();
                String valofIndexCol = r_valueofCol.get(indexCol);

                if(user_Conditions.get(0).getOperator().equals("=")){
                    if(equalsCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals(">")){
                    if(greaterCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals("<")){
                    if(lessCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals(">=")){
                    if(greaterEqualsCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(0).getOperator().equals("<=")){
                    if(lessEqualsCompare(valofIndexCol, user_Conditions.get(0).getValue())){
                        validRowID_T1.add(r.getRowId());
                    }
                }
                else{
                    //error
                }
            }

            //find valid row id's for CONDITION 2-------------------------------------------------------------------

            //loop each condition column name find the correlating index on the actual column table
            indexCol = -1;
            for(int i = 0; i < user_tableAllColumn.size(); i++){
                if(user_Conditions.get(1).getColumn().equals(user_tableAllColumn.get(i).getColumnName())) {
                    indexCol = i-1;
                }
            }

            //loop all records
            for(Record r : user_tableRecords){
                ArrayList<String> r_valueofCol = r.getValuesOfColumns();
                String valofIndexCol = r_valueofCol.get(indexCol);

                if(user_Conditions.get(1).getOperator().equals("=")){
                    if(equalsCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals(">")){
                    if(greaterCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals("<")){
                    if(lessCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals(">=")){
                    if(greaterEqualsCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else if(user_Conditions.get(1).getOperator().equals("<=")){
                    if(lessEqualsCompare(valofIndexCol, user_Conditions.get(1).getValue())){
                        validRowID_T2.add(r.getRowId());
                    }
                }
                else{
                    //error
                }
            }

            //AND BOTH TEMP validROWID-------------------------------------------------------------------
            //UNION
            validRowID_T1.addAll(validRowID_T2);
            ArrayList<Integer> validRowIDFinal = validRowID_T1;

            //REMOVE DUPLICATES USING SET CONVERSION
            List<Integer> al = new ArrayList<>();
            // add elements to al, including duplicates
            al.addAll(validRowIDFinal);
            Set<Integer> hs = new HashSet<>();
            hs.addAll(validRowID_T1);
            al.clear();
            al.addAll(hs);

            validRowID.addAll(al);
        }
        else{
            //weird
        }
        //FINALLY RETURN validRowID
        return validRowID;
    }

    private static boolean equalsCompare(String valofIndexCol, String value) {
        if(CheckSupportDataType.CheckSupportDataType(valofIndexCol, value)){
            String dataTypeValue = CheckSupportDataType.getDataType(value);
            if(CheckSupportDataType.isInt(value)){
                //convert to int
                int valofIndex_col_int =  Integer.parseInt(valofIndexCol);
                int value_int = Integer.parseInt(value);

                //compare ints
                if(valofIndex_col_int == value_int){
                    return true;
                }
            }
            else if(CheckSupportDataType.isDouble(value)){
                //convert to double
                double valofIndex_col_dbl =  Double.parseDouble(valofIndexCol);
                double value_dbl = Double.parseDouble(value);

                //compare doubles
                if(valofIndex_col_dbl == value_dbl){
                    return true;
                }
            }
            else if(dataTypeValue.equals("datetime")){

            }
            else if (dataTypeValue.equals("date")){
                try {
                    Date D_valofIndexCol=new SimpleDateFormat("yyyy-MM-dd").parse(valofIndexCol);
                    Date D_value=new SimpleDateFormat("yyyy-MM-dd").parse(value);

                    if(D_valofIndexCol.compareTo(D_value) == 0){
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (dataTypeValue.equals("text")){
                if(valofIndexCol.compareTo(value) == 0){
                    return true;
                }
            }
            else{
                //weird
            }
        }
        return false;
    }

    private static boolean notEqualsCompare(String valofIndexCol, String value) {
        if(CheckSupportDataType.CheckSupportDataType(valofIndexCol, value)){
            String dataTypeValue = CheckSupportDataType.getDataType(value);
            if(CheckSupportDataType.isInt(value)){
                //convert to int
                int valofIndex_col_int =  Integer.parseInt(valofIndexCol);
                int value_int = Integer.parseInt(value);

                //compare ints
                if(valofIndex_col_int != value_int){
                    return true;
                }
            }
            else if(CheckSupportDataType.isDouble(value)){
                //convert to double
                double valofIndex_col_dbl =  Double.parseDouble(valofIndexCol);
                double value_dbl = Double.parseDouble(value);

                //compare doubles
                if(valofIndex_col_dbl != value_dbl){
                    return true;
                }
            }
            else if(dataTypeValue.equals("datetime")){

            }
            else if (dataTypeValue.equals("date")){
                try {
                    Date D_valofIndexCol=new SimpleDateFormat("yyyy-MM-dd").parse(valofIndexCol);
                    Date D_value=new SimpleDateFormat("yyyy-MM-dd").parse(value);

                    if(D_valofIndexCol.compareTo(D_value) != 0){
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (dataTypeValue.equals("text")){
                if(valofIndexCol.compareTo(value) != 0){
                    return true;
                }
            }
            else{
                //werid
            }
        }
        return false;
    }

    private static boolean greaterCompare(String valofIndexCol, String value) {
        if(CheckSupportDataType.CheckSupportDataType(valofIndexCol, value)){
            String dataTypeValue = CheckSupportDataType.getDataType(value);
            if(CheckSupportDataType.isInt(value)){
                //convert to int
                int valofIndex_col_int =  Integer.parseInt(valofIndexCol);
                int value_int = Integer.parseInt(value);

                //compare ints
                if(valofIndex_col_int > value_int){
                    return true;
                }
            }
            else if(CheckSupportDataType.isDouble(value)){
                //convert to double
                double valofIndex_col_dbl =  Double.parseDouble(valofIndexCol);
                double value_dbl = Double.parseDouble(value);

                //compare doubles
                if(valofIndex_col_dbl > value_dbl){
                    return true;
                }
            }
            else if(dataTypeValue.equals("datetime")){

            }
            else if (dataTypeValue.equals("date")){
                try {
                    Date D_valofIndexCol=new SimpleDateFormat("yyyy-MM-dd").parse(valofIndexCol);
                    Date D_value=new SimpleDateFormat("yyyy-MM-dd").parse(value);

                    if(D_valofIndexCol.compareTo(D_value) > 0){
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (dataTypeValue.equals("text")){
                if(valofIndexCol.compareTo(value) > 0){
                    return true;
                }
            }
            else{
                //werid
            }
        }
        return false;
    }

    private static boolean lessCompare(String valofIndexCol, String value) {
        if(CheckSupportDataType.CheckSupportDataType(valofIndexCol, value)){
            String dataTypeValue = CheckSupportDataType.getDataType(value);
            if(CheckSupportDataType.isInt(value)){
                //convert to int
                int valofIndex_col_int =  Integer.parseInt(valofIndexCol);
                int value_int = Integer.parseInt(value);

                //compare ints
                if(valofIndex_col_int < value_int){
                    return true;
                }
            }
            else if(CheckSupportDataType.isDouble(value)){
                //convert to double
                double valofIndex_col_dbl =  Double.parseDouble(valofIndexCol);
                double value_dbl = Double.parseDouble(value);

                //compare doubles
                if(valofIndex_col_dbl < value_dbl){
                    return true;
                }
            }
            else if(dataTypeValue.equals("datetime")){

            }
            else if (dataTypeValue.equals("date")){
                try {
                    Date D_valofIndexCol=new SimpleDateFormat("yyyy-MM-dd").parse(valofIndexCol);
                    Date D_value=new SimpleDateFormat("yyyy-MM-dd").parse(value);

                    if(D_valofIndexCol.compareTo(D_value) < 0){
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (dataTypeValue.equals("text")){
                if(valofIndexCol.compareTo(value) < 0){
                    return true;
                }
            }
            else{
                //werid
            }
        }
        return false;
    }

    private static boolean greaterEqualsCompare(String valofIndexCol, String value) {
        if(CheckSupportDataType.CheckSupportDataType(valofIndexCol, value)){
            String dataTypeValue = CheckSupportDataType.getDataType(value);
            if(CheckSupportDataType.isInt(value)){
                //convert to int
                int valofIndex_col_int =  Integer.parseInt(valofIndexCol);
                int value_int = Integer.parseInt(value);

                //compare ints
                if(valofIndex_col_int >= value_int){
                    return true;
                }
            }
            else if(CheckSupportDataType.isDouble(value)){
                //convert to double
                double valofIndex_col_dbl =  Double.parseDouble(valofIndexCol);
                double value_dbl = Double.parseDouble(value);

                //compare doubles
                if(valofIndex_col_dbl >= value_dbl){
                    return true;
                }
            }
            else if(dataTypeValue.equals("datetime")){

            }
            else if (dataTypeValue.equals("date")){
                try {
                    Date D_valofIndexCol=new SimpleDateFormat("yyyy-MM-dd").parse(valofIndexCol);
                    Date D_value=new SimpleDateFormat("yyyy-MM-dd").parse(value);

                    if(D_valofIndexCol.compareTo(D_value) >= 0){
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (dataTypeValue.equals("text")){
                if(valofIndexCol.compareTo(value) >= 0){
                    return true;
                }
            }
            else{
                //werid
            }
        }
        return false;
    }

    private static boolean lessEqualsCompare(String valofIndexCol, String value) {
        if(CheckSupportDataType.CheckSupportDataType(valofIndexCol, value)){
            String dataTypeValue = CheckSupportDataType.getDataType(value);
            if(CheckSupportDataType.isInt(value)){
                //convert to int
                int valofIndex_col_int =  Integer.parseInt(valofIndexCol);
                int value_int = Integer.parseInt(value);

                //compare ints
                if(valofIndex_col_int <= value_int){
                    return true;
                }
            }
            else if(CheckSupportDataType.isDouble(value)){
                //convert to double
                double valofIndex_col_dbl =  Double.parseDouble(valofIndexCol);
                double value_dbl = Double.parseDouble(value);

                //compare doubles
                if(valofIndex_col_dbl <= value_dbl){
                    return true;
                }
            }
            else if(dataTypeValue.equals("datetime")){

            }
            else if (dataTypeValue.equals("date")){
                try {
                    Date D_valofIndexCol=new SimpleDateFormat("yyyy-MM-dd").parse(valofIndexCol);
                    Date D_value=new SimpleDateFormat("yyyy-MM-dd").parse(value);

                    if(D_valofIndexCol.compareTo(D_value) <= 0){
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (dataTypeValue.equals("text")){
                if(valofIndexCol.compareTo(value) <= 0){
                    return true;
                }
            }
            else{
                //werid
            }
        }
        return false;
    }

}
