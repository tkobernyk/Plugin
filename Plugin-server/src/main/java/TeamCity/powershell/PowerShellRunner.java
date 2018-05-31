package TeamCity.powershell;

import TeamCity.models.Deploy;
import TeamCity.powershell.process.ProcessFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

@Getter
@Setter
public class PowerShellRunner {

    private static final com.intellij.openapi.diagnostic.Logger logger =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellRunner.class.getName());
    private static final String FINISHED_CONSTANT_NAME = "FINISHED";


    private final ProcessFactory processFactory;


    public PowerShellRunner(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    private static Predicate<String> predicate = s -> !s.contains("Windows PowerShell")
            && !s.contains("Copyright (C) 2016 Microsoft Corporation. All rights reserved.") && !s.isEmpty();

    Deploy run(BlockingQueue<String> queue, String scriptPath, Deploy deploy) {
        Process powerShellProcess = processFactory.getOrCreateProcess(deploy.getFileNameFromDeploy(), scriptPath, deploy.getParametersAsString());
        deploy.setDeployStatus(DeployStatus.IN_PROGRESS);
        processOutput(powerShellProcess, queue, deploy);
        deploy.setDeployStatus(DeployStatus.SUCCESS);
        processFactory.getCacheWrapper().getJavaProccessCache().remove(deploy.getFileNameFromDeploy().hashCode());
        powerShellProcess.destroy();
        logger.info("FINISHED PROCESS");
        return deploy;
    }


    private boolean processOutput(Process powerShellProcess, BlockingQueue<String> queue, Deploy deploy) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            return bufferedReader.lines().filter(predicate).anyMatch(line -> addStringToBlockingQueue(queue, line));
        } catch (Exception e) {
            deploy.setDeployStatus(DeployStatus.FAILED);
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean addStringToBlockingQueue(BlockingQueue<String> queue, String line) {
        queue.add(line + (!line.isEmpty() ? " <br />" : ""));
        return line.equals(FINISHED_CONSTANT_NAME);
    }
}