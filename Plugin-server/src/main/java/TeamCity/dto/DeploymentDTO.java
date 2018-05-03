package TeamCity.dto;


import TeamCity.models.Deploy;
import jetbrains.buildServer.users.SUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeploymentDTO {

    private Deploy deploy;
    private SUser sUser;
    private String scriptPath;
    private String pathPrefix;

}
