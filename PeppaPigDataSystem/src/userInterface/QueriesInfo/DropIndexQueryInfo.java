package userInterface.QueriesInfo;

public class DropIndexQueryInfo {

    public String tableName;
    public String indexName;

    public DropIndexQueryInfo(String tableName, String indexName){
        this.tableName = tableName;
        this.indexName = indexName;
    }

}
