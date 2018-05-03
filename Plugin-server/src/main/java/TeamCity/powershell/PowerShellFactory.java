package TeamCity.powershell;

import TeamCity.Models.Deploy;
import jetbrains.buildServer.users.SUser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingDeque;


public class PowerShellFactory {


    public PowerShellWrapper createPowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {
        Files.copy(Paths.get(scriptPath), new FileOutputStream(pathPrefix + "/" + deploy.getFileNameFromDeploy()));
        PowerShellWrapper powerShellWrapper = new PowerShellWrapper();
        powerShellWrapper.setDeploy(deploy);
        powerShellWrapper.setBlockingQueue(new LinkedBlockingDeque<>());
        powerShellWrapper.setPowerShellRunner(new PowerShellRunner());
        powerShellWrapper.setScriptPath(pathPrefix + deploy.getFileNameFromDeploy());
        return powerShellWrapper;
    }
}