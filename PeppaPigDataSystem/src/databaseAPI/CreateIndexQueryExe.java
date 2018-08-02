package databaseAPI;

import Common.Constants;
import fileSystem.IndexTable;
import fileSystem.Table;
import userInterface.QueriesInfo.CreateIndexQueryInfo;

public class CreateIndexQueryExe {
    public static void executeQuery(CreateIndexQueryInfo info){

        String tablePath;

        tablePath = Constants.SYSTEM_USER_PATH;
        // Create new table file
        IndexTable newIndexTable = new IndexTable(tablePath+"/"+info.indexName+Constants.DEFAULT_FILE_EXTENSION);

        //Update davisColumnTable
    }

}
