package TeamCity.service;

import TeamCity.models.Deploy;
import TeamCity.powershell.PowerShellWrapper;
import jetbrains.buildServer.users.SUser;

/**
 * Created by Mykhailo_Moskura on 5/25/2018.
 */
public interface Service {

    PowerShellWrapper getPowerShellWrapper(Deploy deploy, SUser sUser, String psScriptPath, String prefixPath);
}
