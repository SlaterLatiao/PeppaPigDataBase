package userInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import Common.Constants;
import databaseAPI.*;
import userInterface.QueriesInfo.*;
import userInterface.Utils.Condition;
import userInterface.Utils.Errors;


/**
 * @author LI LIU 2018-07-21
 * */
public class DatabaseLaunch {

    /* This can be changed to whatever you like */
    static String prompt = "davisql> ";
    static String version = "v1.0";
    static String copyright = "Copyright ©2018 YELLOW TEAM";
    static boolean isExit = false;

    /*
     *  The Scanner class is used to collect user commands from the prompt.
     *  Each time the semicolon (;) delimiter is entered, the userCommand
     *  String is re-populated.
     */
    static Scanner scanner = new Scanner(System.in).useDelimiter(";");

    /** ***********************************************************************
     *  Main method
     */
    public static void main(String[] args){

        /* Display the welcome screen */
        splashScreen();
        initDatabase();

        /* Variable to collect user input from the prompt */
        String userCommand = "";

        while(!isExit) {
            System.out.print(prompt);
            userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
            parseUserCommand(userCommand);
        }
        System.out.println("\nBye!");
    }

    /** ***********************************************************************
     *  Static method definitions
     */

    /**
     *  Display the splash screen
     */
    public static void splashScreen() {
        System.out.println(line("=",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
        System.out.println("DavisBaseLite Version " + getVersion());
        System.out.println(getCopyright());
        System.out.println("\nType \"help;\" to display supported commands.");
        System.out.println(line("=",80));
    }

    public static void initDatabase(){
        File databaseDir = new File(Constants.DEFAULT_DATA_DIRNAME);
        if(!databaseDir.exists()){
            databaseDir.mkdir();
            // create default database directories.
            File catalogDir = new File(Constants.DEFAULT_DATA_DIRNAME+"/"+Constants.DEFAULT_CATALOG_DIRNAME);
            File userdataDir = new File(Constants.DEFAULT_DATA_DIRNAME+"/"+Constants.DEFAULT_USERDATA_DIRNAME);

            if(!catalogDir.exists()){
                catalogDir.mkdir();
                initCatalog();
            }else{
                System.out.println(Errors.DIRECTORY_EXISTS.replace("%1",catalogDir.getName()));
            }
            if(!userdataDir.exists()){
                userdataDir.mkdir();
            }else{
                System.out.println(Errors.DIRECTORY_EXISTS.replace("%1",userdataDir.getName()));
            }
        }
    }

    public static void initCatalog(){
        String cmd_create_tablesTable = "create table davisbase_tables (table_name TEXT NOT NULL)";
        String cmd_create_columnsTable = "create table davisbase_columns (table_name TEXT NOT NULL," +
                "column_name TEXT NOT NULL, data_type TEXT NOT NULL, ordinal_position TINYINT," +
                "is_nullable TINYINT,is_pirmary TINYINT,index_name TEXT)";
        CreateTableQueryExe.initialCreateSystemTables();
        parseUserCommand(cmd_create_tablesTable);
        parseUserCommand(cmd_create_columnsTable);
    }


    public static void parseUserCommand (String userCommand){

        String[] commandTokens = userCommand.split(" ");
        String command = commandTokens[0].toLowerCase();

        switch (command) {
            case "show":
                showTable(userCommand);
                break;
            case "create":
                parseCreate(userCommand);
                break;
            case "drop":
                parseDrop(userCommand);
                break;
            case "insert":
                parseInsert(userCommand);
                break;
            case "delete":
                parseDelete(userCommand);
                break;
            case "update":
                parseUpdate(userCommand);
                break;
            case "select":
                parseSelect(userCommand);
                break;
            case "help":
                help();
                break;
            case "version":
                displayVersion();
                break;
            case "exit":
                isExit = true;
                break;
            default:
                System.out.println("Command not found : \"" + userCommand + "\"");
                break;
        }
    }





    public static void showTable(String userCommand) {
        /**
         * show table;
         *   == select * from davisbase_tables;
         * */
        if(!PartsEqual(userCommand, "show tables")) {
            System.out.println("Unrecognised Command: " + userCommand + "\nType \"help;\" to display supported commands.");
            return;
        }
        ArrayList<String> columnsList = new ArrayList<>();
        columnsList.add("*");
        String tableName = Constants.SYSTEM_TABLES_TABLENAME;
        ArrayList<Condition> selectCondition = new ArrayList<>();
        SelectQueryInfo showTable = new SelectQueryInfo(columnsList,tableName,selectCondition,null);
        SelectQueryExe.executeQuery(showTable);
    }




    public static void parseCreate(String userCommand) {
        /**
         * create table <table_name> (
         *        colunm_name1 dataType1 [not null] [primary key],
         *        colunm_name2 dataType2 [not null],
         *        ...);
         *
         * create index <index_name> on <table_name> (column_name);
         * */

        //get tableName
        String tableName;
        boolean isExist = false;

        //if it is a create table command
        if(PartsEqual(userCommand, "create table")) {
            int startIndex = userCommand.toLowerCase().indexOf("table") + "table".length();
            int endIndex = userCommand.toLowerCase().indexOf("(");
            if(endIndex == -1){
                System.out.println(Errors.COLUMNS_NEEDED);
                return;
            }
            if(endIndex<=startIndex){
                System.out.println(Errors.TABLE_NAME_NEEDED);
                return;
            }
            tableName = userCommand.toLowerCase().substring(startIndex, endIndex).trim();

            // check whether the table is already exist
            isExist = databaseAPI.General.checkTableExists(tableName);
            if(isExist){
                System.out.println(Errors.TABLE_EXISTS.replace("%1",tableName));
                return;
            }

            startIndex = endIndex;
            endIndex = userCommand.toLowerCase().indexOf(")");
            if(endIndex == -1){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",userCommand));
                return;
            }
            if(endIndex<=startIndex+1){
                System.out.println(Errors.COLUMNS_NEEDED);
                return;
            }
            String restString = userCommand.toLowerCase().substring(startIndex+1,endIndex).trim();
            String[] columnToBeCreate = restString.split(",");
            ArrayList<String> columns = getColumnsList(columnToBeCreate);
            CreateTableQueryInfo createTableQueryInfo = new CreateTableQueryInfo(tableName,columns);
            CreateTableQueryExe.executeQuery(createTableQueryInfo);

        }else if(PartsEqual(userCommand, "create index")){
            String indexName;
            String columnName;
            int onIndex = userCommand.toLowerCase().indexOf("on");
            if(onIndex == -1) {
                System.out.println(Errors.SYNTAX_ERROR.replace("%1", userCommand));
                return;
            }
            indexName = userCommand.toLowerCase().substring("create index".length(),onIndex).trim();
            int openBrkIndex = userCommand.toLowerCase().indexOf("(");
            int closeBrkIndex = userCommand.toLowerCase().indexOf(")");
            if(openBrkIndex == -1 || closeBrkIndex == -1){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1", userCommand));
                return;
            }
            tableName = userCommand.toLowerCase().substring(onIndex+1,openBrkIndex).trim();
            columnName = userCommand.toLowerCase().substring(openBrkIndex+1,closeBrkIndex).trim();

            isExist = databaseAPI.General.checkCreateIndex(tableName,columnName,indexName);
            if(isExist){
                System.out.println(Errors.TABLE_EXISTS.replace("%1",tableName));
                return;
            }else{
                CreateIndexQueryInfo createIndexQueryInfo = new CreateIndexQueryInfo(indexName,tableName,columnName);
                CreateIndexQueryExe.executeQuery(createIndexQueryInfo);
            }
        }else{
            System.out.println("Unrecognised Command: " + userCommand + "\nType \"help;\" to display supported commands.");
        }
    }





    public static void parseDrop(String dropString) {
        /**
         * drop table <table_name>;
         *
         * drop index <table_name>.<column_name>;
         * */
        boolean isExist = false;
        String tableName;
        if(PartsEqual(dropString, "drop table")){
            tableName = dropString.toLowerCase().substring("drop table".length()).trim();
            // check whether the table is already exist
            isExist = databaseAPI.General.checkTableExists(tableName);
            if(!isExist){
                System.out.println(Errors.TABLE_NOT_EXISTS.replace("%1",tableName));
                return;
            }
            DropTableQueryInfo dropTableQueryInfo = new DropTableQueryInfo(tableName);
            DropTableQueryExe.executeQuery(dropTableQueryInfo);
        }else if(PartsEqual(dropString, "drop index")){

            int dotIndex = dropString.toLowerCase().indexOf(".");
            if(dotIndex == -1){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",dropString));
            }

            tableName = dropString.toLowerCase().substring("drop index".length(),dotIndex).trim();
            // check whether the table is already exist
            isExist = databaseAPI.General.checkTableExists(tableName);
            if(!isExist){
                System.out.println(Errors.TABLE_NOT_EXISTS);
                return;
            }
            String indexName = dropString.toLowerCase().substring(dotIndex+1).trim();
            DropIndexQueryInfo dropIndexQueryInfo = new DropIndexQueryInfo(tableName,indexName);
            DropIndexQueryExe.executeQuery(dropIndexQueryInfo);
        }else{
            System.out.println("Unrecognised Command: " + dropString + "\nType \"help;\" to display supported commands.");
            return;
        }
    }







    private static void parseInsert(String userCommand) {
        /**
         * insert into table [(<column_list>)] <table_name> values (<value_list>);
         * */
        String tableName;
        boolean isExist = false;
        ArrayList<String> columns = null;
        ArrayList<String> values;

        if(!PartsEqual(userCommand, "insert into table")) {
            System.out.println("Unrecognised Command: " + userCommand + "\nType \"help;\" to display supported commands.");
            return;
        }
        int valuesIndex = userCommand.toLowerCase().indexOf("values");
        if(valuesIndex == -1){
            System.out.println(Errors.SYNTAX_ERROR.replace("%1",userCommand));
            return;
        }
        String table_column_substring = userCommand.toLowerCase().substring("insert into table".length(),valuesIndex).trim();
        int openBrkIndex = table_column_substring.indexOf("(");
        int closeBrkIndex=table_column_substring.indexOf(")");
        if(openBrkIndex == -1) {
            if(closeBrkIndex != -1){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",userCommand));
                return;
            }
            tableName = table_column_substring.trim();
        }else{
            if(closeBrkIndex == -1 || closeBrkIndex <= openBrkIndex){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",userCommand));
                return;
            }
            tableName = table_column_substring.substring(closeBrkIndex+1).trim();
            String[] col = table_column_substring.substring(openBrkIndex+1,closeBrkIndex).trim().split(",");
            columns = getColumnsList(col);
        }
        // check whether the table is already exist
        isExist = databaseAPI.General.checkTableExists(tableName);
        if(!isExist){
            System.out.println(Errors.TABLE_NOT_EXISTS.replace("%1",tableName));
            return;
        }

        String valueSubString = userCommand.toLowerCase().substring(valuesIndex).trim();
        openBrkIndex = valueSubString.indexOf("(");
        closeBrkIndex = valueSubString.indexOf(")");
        if(openBrkIndex == -1 || closeBrkIndex == -1){
            System.out.println(Errors.SYNTAX_ERROR.replace("%1",userCommand));
            return;
        }
        String[] valuesStrings = valueSubString.substring(openBrkIndex+1,closeBrkIndex).trim().split(",");
        values = getColumnsList(valuesStrings);

        InsertQueryInfo insertQueryInfo = new InsertQueryInfo(tableName,columns,values);
        InsertQueryExe.executeQuery(insertQueryInfo);
    }





    private static void parseDelete(String userCommand) {
        /**
         * delete from <table_name> where <condition_list>;
         * */
        String tableName;
        String logiOper;
        boolean isExist = false;

        ArrayList<Condition> conditions = new ArrayList<>();

        if(!PartsEqual(userCommand, "delete from")) {
            System.out.println("Unrecognised Command: " + userCommand + "\nType \"help;\" to display supported commands.");
            return;
        }

        int whereIndex = userCommand.toLowerCase().indexOf("where");
        if(whereIndex == -1){
            tableName = userCommand.toLowerCase().substring("delete from".length()).trim();
            logiOper = null;
        }else{
            String restString = userCommand.toLowerCase().substring(whereIndex+"where".length()).trim();
            logiOper = getLogicalOperator(restString);
            int logiOperIndex = -1;
            if(logiOper != null){
                if(logiOper.equals("error")){
                    System.out.println(Errors.SYNTAX_ERROR.replace("%1",userCommand));
                }
                logiOperIndex = restString.indexOf(logiOper);
            }
            tableName = userCommand.toLowerCase().substring("delete from".length(),whereIndex).trim();
            conditions = getSelectConditionList(tableName,restString,logiOper,logiOperIndex);
            if(conditions == null){
                return;
            }
        }

        // check whether the table is already exist
        isExist = databaseAPI.General.checkTableExists(tableName);
        if(!isExist){
            System.out.println(Errors.TABLE_NOT_EXISTS);
            return;
        }

        DeleteQueryInfo deleteQueryInfo = new DeleteQueryInfo(tableName,conditions,logiOper);
        DeleteQueryExe.executeQuery(deleteQueryInfo);
    }





    public static void parseUpdate(String updateString) {
        /**
         * update table <table_name> set <column_name> = <value> [where <condition_list>];
         * */
        if(!PartsEqual(updateString, "update table")) {
            System.out.println("Unrecognised Command: " + updateString + "\nType \"help;\" to display supported commands.");
            return;
        }
        int setIndex = updateString.toLowerCase().indexOf("set");
        String tableName;
        String columnName;
        String value;
        ArrayList<Condition> conditions;
        boolean isExist = false;

        if(setIndex == -1){
            System.out.println(Errors.SYNTAX_ERROR.replace("%1",updateString));
            return;
        }
        tableName = updateString.toLowerCase().substring("update table".length(),setIndex).trim();
        // check whether the table is already exist
        isExist = databaseAPI.General.checkTableExists(tableName);
        if(!isExist){
            System.out.println(Errors.TABLE_NOT_EXISTS);
            return;
        }
        String restString = updateString.toLowerCase().substring(setIndex+"set".length()).trim();
        int whereIndex = restString.indexOf("where");
        int assignIndex; //index of "="
        if(whereIndex == -1){
            assignIndex = restString.indexOf("=");
            if(assignIndex == -1){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",updateString));
                return;
            }
            columnName = restString.substring(0,assignIndex).trim();
            value = restString.substring(assignIndex+1).trim();
        }else{
            String setString = restString.substring(0,whereIndex).trim();
            assignIndex = setString.indexOf("=");
            if(assignIndex == -1){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",updateString));
                return;
            }
            columnName = setString.substring(0,assignIndex).trim();
            value = setString.substring(assignIndex+1).trim();
        }

        // check whether the column exists
        if(!General.checkColumnExists(tableName,columnName)){
            System.out.println(Errors.COLUMN_NOT_EXISTS.replace("%1",columnName));
            return;
        }

        restString = restString.substring(whereIndex+"where".length()).trim();
        String logiOper = getLogicalOperator(restString);
        int logiOperIndex = -1;
        if(logiOper != null){
            if(logiOper.equals("error")){
                System.out.println(Errors.SYNTAX_ERROR.replace("%1",updateString));
            }
            logiOperIndex = restString.indexOf(logiOper);
        }
        conditions = getSelectConditionList(tableName,restString,logiOper,logiOperIndex);
        if(conditions == null){
            return;
        }
        UpdateQueryInfo updateQueryInfo = new UpdateQueryInfo(tableName,columnName,value,conditions,logiOper);
        UpdateQueryExe.executeQuery(updateQueryInfo);
    }






    public static void parseSelect(String queryString){
        /**
         * select <column_name_list> from <table_name> [where <selectConditions_list>];
         * */
        //get the attributesList
        int fromIndex = queryString.toLowerCase().indexOf("from");
        if(fromIndex == -1) {
            System.out.println(Errors.SYNTAX_ERROR.replace("%1",queryString));
            return;
        }

        //get selected columns
        String[] attributesList = queryString.substring("select".length(), fromIndex).trim().split(",");
        ArrayList<String> columns = getColumnsList(attributesList);

        //get the table name
        boolean isExist = false;
        String tableName;
        String restQueryString = queryString.substring(fromIndex + "from".length());
        int whereIndex = restQueryString.toLowerCase().indexOf("where");// check whether the query has 'where' select condition.
        if(whereIndex == -1){ // query without select condition
            tableName = restQueryString.trim();
            // check whether the table is already exist
            isExist = databaseAPI.General.checkTableExists(tableName);
            if(!isExist){
                System.out.println(Errors.TABLE_NOT_EXISTS.replace("%1",tableName));
            }else{
                ArrayList<Condition> selectCondition = new ArrayList<>();
                selectCondition.add(null);
                SelectQueryInfo selectQueryInfo = new SelectQueryInfo(columns,tableName,selectCondition,null);
                SelectQueryExe.executeQuery(selectQueryInfo);
            }
        }else{// query with select condition
            tableName = restQueryString.substring(0, whereIndex).trim();
            // check whether the table is already exist
            isExist = databaseAPI.General.checkTableExists(tableName);
            if(!isExist){
                System.out.println(Errors.TABLE_NOT_EXISTS.replace("%1",tableName));
            }else{
                // get selectCondition string
                String whereString = restQueryString.substring(whereIndex + "where".length());
                String logiOper = getLogicalOperator(whereString);
                int logiOperIndex = -1;
                if(logiOper != null){
                    if(logiOper.equals("error")){
                        System.out.println(Errors.SYNTAX_ERROR.replace("%1",queryString));
                    }
                    logiOperIndex = whereString.trim().indexOf(logiOper);
                }
                ArrayList<Condition> selectCondition = getSelectConditionList(tableName,whereString,logiOper,logiOperIndex);
                if(selectCondition == null){
                    return;
                }
                SelectQueryInfo selectQueryInfo = new SelectQueryInfo(columns,tableName,selectCondition,logiOper);
                SelectQueryExe.executeQuery(selectQueryInfo);
            }
        }
    }



    public static void help() {
        System.out.println(line("*",160));
        System.out.println(">>>> SUPPORTED COMMANDS <<<< All commands below are case insensitive\n");
        System.out.println("SHOW TABLES        Display the names of all tables.                      SYNTAX: show tables;");
        System.out.println("CREATE TABLE       Create a new table in the database.                   SYNTAX: create table <table_name> (<columns name, columns type, columns isNullable>);");
        System.out.println("DROP TABLE         Remove table data (i.e. all records) and its schema.  SYNTAX: drop table <table_name>;");
        System.out.println("CREATE INDEX       Create a new index on single column of exist table.   SYNTAX: create index <index_name> on <table_name> (column_name);");
        System.out.println("DROP INDEX         Remove a index.                                       SYNTAX: drop index <table_name>.<index_name>;");
        System.out.println("SELECT             Display table records with specified conditions.      SYNTAX: select <column_list> from <table_name> [where <condition>];");
        System.out.println("INSERT INTO        Insert data into table.                               SYNTAX: insert into  <column_list> <table_name > values (<value_list>);");
        System.out.println("UPDATE TABLE       Modify records data with specified conditions.        SYNTAX: update table <table_name> set <column_name> = <value> [where<condition>];");
        System.out.println("VERSION            Display the program version.                          SYNTAX: version;");
        System.out.println("HELP               Display this help information.                        SYNTAX: help;");
        System.out.println("EXIT               Exit the program.                                     SYNTAX: exit;");
        System.out.println(line("*",160));
    }

    public static String getVersion() {
        return version;
    }

    public static String getCopyright() {
        return copyright;
    }

    public static void displayVersion() {
        System.out.println("SimpleBaseLite Version " + getVersion());
        System.out.println(getCopyright());
    }

    /**
     * Print repeated "-" line
     */
    public static String line(String s,int num) {
        String a = "";
        for(int i=0;i<num;i++) {
            a += s;
        }
        return a;
    }

    public static ArrayList<String> getColumnsList(String[] attributesList){
        ArrayList<String> columns = new ArrayList<>();
        for(String attri:attributesList){
            columns.add(attri.trim());
        }
        return columns;
    }

    public static String getLogicalOperator(String whereString){
        if(whereString.contains("not")){
            int notIndex = whereString.trim().indexOf("not");
            if(notIndex != 0){
                return "error";
            }
            String con = whereString.substring(notIndex+"not".length());
            if(con.isEmpty()){
                return "error";
            }
            return "not";
        }else if(whereString.contains("and")){
            int andIndex = whereString.indexOf("and");
            if(andIndex == 0){
                return "error";
            }
            String con1 = whereString.substring(0,andIndex).trim();
            String con2 = whereString.substring(andIndex+"and".length()).trim();
            if(con1.isEmpty() || con2.isEmpty()){
                return "error";
            }
            return "and";
        }else if(whereString.contains("or")){
            int orIndex = whereString.indexOf("or");
            if(orIndex == 0){
                return "error";
            }
            String con1 = whereString.substring(0,orIndex).trim();
            String con2 = whereString.substring(orIndex+"or".length()).trim();
            if(con1.isEmpty() || con2.isEmpty()){
                return "error";
            }
            return "or";
        }else{
            return null;
        }
    }

    public static ArrayList<Condition> getSelectConditionList(String tableName,String whereString,String logiOper,int logiOperIndex){
        ArrayList<Condition> selectConditions = new ArrayList<>();
        ArrayList<String> subCondition = new ArrayList<>();

        if(logiOperIndex == -1){
            subCondition.add(whereString.trim());
        }else if(logiOperIndex == 0) {
            String con = whereString.trim().substring("not".length()).trim();
            subCondition.add(con);
        }else{
            String con1 = whereString.substring(0,logiOperIndex).trim();
            String con2 = whereString.substring(logiOperIndex+logiOper.length()+1).trim();
            subCondition.add(con1);
            subCondition.add(con2);
        }

        for(String s:subCondition){
            Condition con = null;
            if(s.contains("<=")){
                String[] tmp = s.split("<=");
                if(databaseAPI.General.checkColumnExists(tableName,tmp[0].trim())){
                    con = new Condition(tmp[0].trim(),"<=",tmp[1].trim());
                }else{
                    System.out.println(Errors.SYNTAX_ERROR);
                    return null;
                }
            }
            else if(s.contains(">=")){
                String[] tmp = s.split(">=");
                if(databaseAPI.General.checkColumnExists(tableName,tmp[0].trim())){
                    con = new Condition(tmp[0].trim(),">=",tmp[1].trim());
                }else{
                    System.out.println(Errors.SYNTAX_ERROR);
                    return null;
                }
            }
            else if(s.contains(">")){
                String[] tmp = s.split(">");
                if(databaseAPI.General.checkColumnExists(tableName,tmp[0].trim())){
                    con = new Condition(tmp[0].trim(),">",tmp[1].trim());
                }else{
                    System.out.println(Errors.SYNTAX_ERROR);
                    return null;
                }
            }
            else if(s.contains("<")){
                String[] tmp = s.split("<");
                if(databaseAPI.General.checkColumnExists(tableName,tmp[0].trim())){
                    con = new Condition(tmp[0].trim(),"<",tmp[1].trim());
                }else{
                    System.out.println(Errors.SYNTAX_ERROR);
                    return null;
                }
            }
            else if(s.contains("=")){
                String[] tmp = s.split("=");
                if(databaseAPI.General.checkColumnExists(tableName,tmp[0].trim())){
                    con = new Condition(tmp[0].trim(),"=",tmp[1].trim());
                }else{
                    System.out.println(Errors.SYNTAX_ERROR);
                    return null;
                }
            }
            /*else if(s.contains("<>")){
                String[] tmp = s.split("<>");
                con = new Condition(tmp[0],"<>",tmp[1]);
            }*/
            if(con == null){
                return null;
            }else{
                selectConditions.add(con);
            }
        }

        /*System.out.println(selectConditions.get(0).getColumn()+" "+selectConditions.get(0).getValue());
        System.out.println(selectConditions.get(1).getColumn()+" "+selectConditions.get(1).getValue());*/

        return selectConditions;
    }

    static boolean PartsEqual(String userCommand, String expectedCommand) {
        String[] userParts = userCommand.toLowerCase().split(" ");
        String[] actualParts = expectedCommand.toLowerCase().split(" ");
        if(userParts.length < actualParts.length){
            return false;
        }
        for (int i = 0; i < actualParts.length; i++) {
            if (!actualParts[i].equals(userParts[i])) {
                return false;
            }
        }
        return true;
    }

}

