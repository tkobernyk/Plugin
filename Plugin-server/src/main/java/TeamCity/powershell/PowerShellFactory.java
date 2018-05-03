package TeamCity.powershell;

import TeamCity.cache.CacheWrapper;
import TeamCity.models.Deploy;
import jetbrains.buildServer.users.SUser;
import lombok.Getter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class PowerShellFactory {

    private CacheWrapper cacheWrapper;

    public PowerShellWrapper getOrCreatePowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {

        if (cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode()) != null) {
            return cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode());
        }
        return createPowerShellRunner(deploy, sUser, scriptPath, pathPrefix);
    }

    private PowerShellWrapper createPowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {
        Files.copy(Paths.get(scriptPath), new FileOutputStream(pathPrefix + "/" + deploy.getFileNameFromDeploy()));
        PowerShellWrapper powerShellWrapper = new PowerShellWrapper();
        powerShellWrapper.setDeploy(deploy);
        powerShellWrapper.setBlockingQueue(new LinkedBlockingQueue<>());
        powerShellWrapper.setPowerShellRunner(new PowerShellRunner());
        powerShellWrapper.setScriptPath(pathPrefix + deploy.getFileNameFromDeploy());
        powerShellWrapper.setUserId(sUser.getId());
        cacheWrapper.getPowerShellOutputCache().put(deploy.getFileNameFromDeploy().hashCode(), powerShellWrapper);
        return powerShellWrapper;
    }
}