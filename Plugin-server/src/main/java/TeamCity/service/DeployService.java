package TeamCity.service;

import TeamCity.dao.HistoricalDataDao;
import TeamCity.models.Deploy;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellWrapper;
import jetbrains.buildServer.users.SUser;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


public class DeployService extends AbstractService {
    private static final com.intellij.openapi.diagnostic.Logger logger =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployService.class.getName());
    private TransactionTemplate transactionTemplate;
    private HistoricalDataDao historicalDataDao;

    public DeployService(HistoricalDataDao historicalDataDao, PowerShellFactory powerShellFactory) {
        super(powerShellFactory);
        this.historicalDataDao = historicalDataDao;
    }


    public void deployProjectToEnvironment(Deploy deploy, SUser sUser, String psScriptPath, String prefixPath) {
        PowerShellWrapper powerShellWrapper = getPowerShellWrapper(deploy, sUser, psScriptPath, prefixPath);
        CompletableFuture.supplyAsync(powerShellWrapper).whenCompleteAsync(finishProcessing(powerShellWrapper));
    }

    private BiConsumer<Deploy, Throwable> finishProcessing(PowerShellWrapper powerShellWrapper) {
        throw new RuntimeException("Java");
//        return (d, t) -> {
//            removeFileWithCatchingException(Paths.get(powerShellWrapper.getScriptPath()));
//            historicalDataDao.save(PluginUtils.convertFromDeployToHistoricalData(powerShellWrapper.getDeploy(),
//                    powerShellWrapper.getData().toString()));
//            removePowerShellWrapperFromCache(powerShellWrapper);
//
//        };
    }

    private void removePowerShellWrapperFromCache(PowerShellWrapper powerShellWrapper) {
        getPowerShellFactory().getCacheWrapper().getPowerShellOutputCache().
                remove(powerShellWrapper.getDeploy().getFileNameFromDeploy().hashCode());
    }
}
