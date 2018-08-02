package databaseAPI;

import Common.Constants;
import fileSystem.IndexTable;
import userInterface.QueriesInfo.DropIndexQueryInfo;

public class DropIndexQueryExe {
    public static void executeQuery(DropIndexQueryInfo info){

        String tablePath;

        tablePath = Constants.SYSTEM_USER_PATH;
        // Create new table file
        IndexTable newIndexTable = new IndexTable(tablePath+"/"+info.indexName+Constants.DEFAULT_FILE_EXTENSION);
        newIndexTable.drop();
        //Update davisColumnTable
    }
}

