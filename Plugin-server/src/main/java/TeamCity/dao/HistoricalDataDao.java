package TeamCity.dao;


import TeamCity.models.HistoricalData;

import java.util.List;

public interface HistoricalDataDao {

    //TODO provide Right query to select
    String SELECT_QUERY = "SELECT [Id]\n" +
            "      ,[BuildId]\n" +
            "      ,[ProjectName]\n" +
            "      ,[Environment]\n" +
            "      ,[Phase]\n" +
            "      ,[DeployDateTime]\n" +
            "      ,[DeployStatus]\n" +
            "      ,[DeployOutput]\n" +
            "      ,[UserId]\n" +
            "  FROM [TeamCity].[dbo].[historical_deployment_data]";

    //TODO provide Right query to insert
    String INSERT_QUERY = "";

    int save(HistoricalData historicalData);

    List<HistoricalData> getAll();
}
