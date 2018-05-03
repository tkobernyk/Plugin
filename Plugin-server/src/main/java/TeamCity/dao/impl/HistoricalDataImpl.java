package TeamCity.dao.impl;

import TeamCity.Models.HistoricalData;
import TeamCity.dao.HistoricalDataDao;
import TeamCity.dao.HistoricalDataRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


public class HistoricalDataImpl implements HistoricalDataDao {

    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(HistoricalData historicalData) {
        return jdbcTemplate.update(INSERT_QUERY, historicalData.listOValues(), historicalData.listOfTypes());
    }

    @Override
    public List<HistoricalData> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, new HistoricalDataRowMapper());
    }


}
