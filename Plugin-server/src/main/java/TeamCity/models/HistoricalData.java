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
    private Long userId;
    private Clob output;
    private Environment environment;
    private String projectName;
    private String buildId;
    private String phase;
    private DeployStatus deployStatus;


    public Object[] listOValues() {
        return new Object[]{getBuildId(), getProjectName(), getEnvironment(), getPhase(), getDeployStatus(), getOutput(), getUserId()};
    }

    public int[] listOfTypes() {
        return new int[]{Types.BIGINT, Types.NVARCHAR, Types.NVARCHAR, Types.NVARCHAR, Types.NVARCHAR, Types.CLOB, Types.BIGINT};
    }
}
