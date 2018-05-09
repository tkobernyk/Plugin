package TeamCity.powershell;

import TeamCity.cache.CacheWrapper;
import TeamCity.models.Deploy;
import jetbrains.buildServer.users.SUser;
import lombok.Getter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;

@Getter
public class PowerShellFactory {

    private static final com.intellij.openapi.diagnostic.Logger LOGGER =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellFactory.class.getName());

    private CacheWrapper cacheWrapper;

    public PowerShellFactory(CacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }

    public PowerShellWrapper getOrCreatePowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {

        if (cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode()) != null) {
            LOGGER.info("*****--------------Getting from cache");
            return cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode());
        }
        return createPowerShellRunner(deploy, sUser, scriptPath, pathPrefix);
    }

    private PowerShellWrapper createPowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {
        LOGGER.info("*****--------------Creating new one");
        String path = pathPrefix + "\\" + deploy.getFileNameFromDeploy();
        Files.copy(Paths.get(scriptPath), new FileOutputStream(path));
        PowerShellWrapper powerShellWrapper = new PowerShellWrapper();
        deploy.setUserId(sUser.getId());
        powerShellWrapper.setDeploy(deploy);
        powerShellWrapper.setQueue(new ArrayDeque<>());
        powerShellWrapper.setData(new StringBuilder());
        powerShellWrapper.setPowerShellRunner(new PowerShellRunner());
        powerShellWrapper.setScriptPath(path);
        cacheWrapper.getPowerShellOutputCache().put(deploy.getFileNameFromDeploy().hashCode(), powerShellWrapper);
        return powerShellWrapper;
    }
}