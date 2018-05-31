package TeamCity.dao.impl;

import TeamCity.dao.HistoricalDataDao;
import TeamCity.dao.HistoricalDataRowMapper;
import TeamCity.models.HistoricalData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


public class HistoricalDataImpl implements HistoricalDataDao {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(HistoricalDataImpl.class.getName());
    private JdbcTemplate jdbcTemplate;

    public HistoricalDataImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public HistoricalData save(HistoricalData historicalData) {
        try {
            jdbcTemplate.update(INSERT_QUERY, historicalData.listOValues(), historicalData.listOfTypes());
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
        return historicalData;
    }

    @Override
    public List<HistoricalData> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, new HistoricalDataRowMapper());
    }
}
