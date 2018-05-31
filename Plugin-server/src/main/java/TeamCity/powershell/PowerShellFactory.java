package TeamCity.powershell;

import TeamCity.cache.CacheWrapper;
import TeamCity.models.Deploy;
import jetbrains.buildServer.users.SUser;
import lombok.Getter;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingDeque;

@Getter
public class PowerShellFactory {

    private static final com.intellij.openapi.diagnostic.Logger logger =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellFactory.class.getName());

    private CacheWrapper cacheWrapper;
    private PowerShellRunner powerShellRunner;

    public PowerShellFactory(CacheWrapper cacheWrapper, PowerShellRunner powerShellRunner) {
        this.cacheWrapper = cacheWrapper;
        this.powerShellRunner = powerShellRunner;
    }

    public PowerShellWrapper getOrCreatePowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) {
        if (cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode()) != null) {
            return cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode());
        } else if (deploy.isEnabledDeploy()) {
            return createPowerShellRunner(deploy, sUser, scriptPath, pathPrefix);
        }
        throw new RuntimeException("Nothing to Process");
    }

    private PowerShellWrapper createPowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) {
        PowerShellWrapper powerShellWrapper = new PowerShellWrapper();
        powerShellWrapper.setPowerShellRunner(powerShellRunner);
        deploy.setUserId(sUser.getId());
        powerShellWrapper.setDeploy(deploy);
        powerShellWrapper.setData(new StringBuilder());
        powerShellWrapper.setQueue(new LinkedBlockingDeque<>());
        powerShellWrapper.setScriptPath(createCopyOfPowerShellWrapperScript(pathPrefix, scriptPath, deploy));
        cacheWrapper.getPowerShellOutputCache().put(deploy.getFileNameFromDeploy().hashCode(), powerShellWrapper);
        return powerShellWrapper;
    }

    private String createCopyOfPowerShellWrapperScript(String pathPrefix, String scriptPath, Deploy deploy) {
        String path = pathPrefix + "\\" + deploy.getFileNameFromDeploy();
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            Files.copy(Paths.get(scriptPath), fileOutputStream);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return path;
    }
}