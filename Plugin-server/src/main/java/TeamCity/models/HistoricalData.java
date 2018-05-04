package TeamCity.models;

import TeamCity.powershell.DeployStatus;
import lombok.Data;

import java.sql.Clob;
import java.sql.Types;
import java.time.LocalDateTime;


@Data
public class HistoricalData {
    private Long id;
    private LocalDateTime localDateTime;
    private Clob output;
    private Deploy deploy;

    public Object[] listOValues() {
        return new Object[]{getId(), getLocalDateTime(), deploy.getUserId(), getOutput(), deploy.getEnvironment(),
                deploy.getProjectName(), deploy.getBuildId(), deploy.getPhase(), deploy.getDeployStatus()};
    }

    public int[] listOfTypes() {
        return new int[]{Types.BIGINT, Types.TIMESTAMP, Types.BIGINT, Types.BLOB, Types.NVARCHAR,
                Types.NVARCHAR, Types.INTEGER, Types.NVARCHAR, Types.NVARCHAR};
    }
}
