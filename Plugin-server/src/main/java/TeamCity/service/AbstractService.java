package TeamCity.service;

import TeamCity.models.Deploy;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellWrapper;
import jetbrains.buildServer.users.SUser;
import lombok.Getter;


@Getter
public abstract class AbstractService implements Service {

    private final PowerShellFactory powerShellFactory;

    public AbstractService(PowerShellFactory powerShellFactory) {
        this.powerShellFactory = powerShellFactory;
    }

    @Override
    public PowerShellWrapper getPowerShellWrapper(Deploy deploy, SUser sUser, String psScriptPath, String prefixPath) {
        return powerShellFactory.getOrCreatePowerShellRunner(deploy, sUser, psScriptPath, prefixPath);
    }
}
