package TeamCity.dao;


import TeamCity.Models.HistoricalData;

import java.util.List;

public interface HistoricalDataDao {

    //TODO provide Right query to insert
    String INSERT_QUERY = "";

    //TODO provide Right query to select
    String SELECT_QUERY = "";

    int save(HistoricalData historicalData);

    List<HistoricalData> getAll();
}
