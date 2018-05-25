package TeamCity.powershell;

import TeamCity.cache.CacheWrapper;
import TeamCity.models.Deploy;
import jetbrains.buildServer.users.SUser;
import lombok.Getter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Getter
public class PowerShellFactory {

    private static final com.intellij.openapi.diagnostic.Logger LOGGER =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellFactory.class.getName());

    private CacheWrapper cacheWrapper;
    private PowerShellRunner powerShellRunner;

    public PowerShellFactory(CacheWrapper cacheWrapper, PowerShellRunner powerShellRunner) {
        this.cacheWrapper = cacheWrapper;
        this.powerShellRunner = powerShellRunner;
    }

    public PowerShellWrapper getOrCreatePowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {
        LOGGER.info("HASHCODE getOrCreatePowerShellRunner" + deploy.getFileNameFromDeploy().hashCode());
        if (cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode()) != null) {
            LOGGER.info("*****--------------Getting from cache");
            LOGGER.info("CACHE " + cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode()));
            return cacheWrapper.getPowerShellOutputCache().get(deploy.getFileNameFromDeploy().hashCode());
        }else if(deploy.isEnabledDeploy()){
            return createPowerShellRunner(deploy, sUser, scriptPath, pathPrefix);
        }
        throw new RuntimeException("Nothing to Process");
    }

    private PowerShellWrapper createPowerShellRunner(Deploy deploy, SUser sUser, String scriptPath, String pathPrefix) throws IOException {
        LOGGER.info("*****--------------Creating new one");
        String path = pathPrefix + "\\" + deploy.getFileNameFromDeploy();
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)){
            Files.copy(Paths.get(scriptPath), fileOutputStream);
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }

        PowerShellWrapper powerShellWrapper = new PowerShellWrapper();
        powerShellWrapper.setPowerShellRunner(powerShellRunner);
        deploy.setUserId(sUser.getId());
        powerShellWrapper.setDeploy(deploy);
        powerShellWrapper.setData(new StringBuilder());
        powerShellWrapper.setQueue(new LinkedBlockingDeque<>());
        powerShellWrapper.setScriptPath(path);
        LOGGER.info("HASHCODE createPowerShellRunner" + deploy.getFileNameFromDeploy().hashCode());
        cacheWrapper.getPowerShellOutputCache().put(deploy.getFileNameFromDeploy().hashCode(), powerShellWrapper);
        return powerShellWrapper;
    }
}