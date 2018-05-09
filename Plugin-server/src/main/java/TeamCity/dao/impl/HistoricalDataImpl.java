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
    public int save(HistoricalData historicalData) {
        Log.info("SAVING HISTORICAL DATA " + historicalData);
        int a = 0;
        try {
             a = jdbcTemplate.update(INSERT_QUERY, historicalData.listOValues(), historicalData.listOfTypes());
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
        return a;
    }

    @Override
    public List<HistoricalData> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, new HistoricalDataRowMapper());
    }
}
