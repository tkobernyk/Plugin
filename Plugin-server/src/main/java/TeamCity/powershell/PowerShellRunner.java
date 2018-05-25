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
    private static final com.intellij.openapi.diagnostic.Logger LOGGER =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellRunner.class.getName());


    private ProcessFactory processFactory;


    public PowerShellRunner(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    private static Predicate<String> predicate = s -> !s.contains("Windows PowerShell")
            && !s.contains("Copyright (C) 2016 Microsoft Corporation. All rights reserved.") && !s.isEmpty();

    //TODO: rewrite to separate methods
    public Deploy run(BlockingQueue<String> queue, String scriptPath, Deploy deploy) {
        LOGGER.info("CURRENT THREAD PowerShellRunner " + Thread.currentThread().getId());
        Process powerShellProcess = processFactory.getOrCreateProcess(deploy.getFileNameFromDeploy(), scriptPath, deploy.getParametersAsString());
        LOGGER.info("Procces object " + powerShellProcess);
        LOGGER.info("PROCCESS " + powerShellProcess);
        deploy.setDeployStatus(DeployStatus.IN_PROGRESS);
        processOutput(powerShellProcess, queue, deploy);
        deploy.setDeployStatus(DeployStatus.SUCCESS);
        LOGGER.info("Deploy Status " + "SUCCESS");
        processFactory.getCacheWrapper().getJavaProccessCache().remove(deploy.getFileNameFromDeploy().hashCode());
        powerShellProcess.destroy();
        LOGGER.info("FINISHED PROCESSING RUN METHOD");
        return deploy;
    }


    private void processOutput(Process powerShellProcess, BlockingQueue<String> queue, Deploy deploy) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            LOGGER.info("PROCESS OUTPUT");
            LOGGER.info("QUEUE " + queue.hashCode());
            String line;
            while (!((line = bufferedReader.readLine()).contains("FINISHED"))) {
                addStringToBlockingQueue(queue, line);
            }
            LOGGER.info("FINISHED OUTPUT");

        } catch (Exception e) {
            deploy.setDeployStatus(DeployStatus.FAILED);
            LOGGER.error(e.getMessage(), e);
        }


    }

    private boolean addStringToBlockingQueue(BlockingQueue<String> queue, String line) {
        return queue.add(line + (!line.isEmpty() ? " <br />" : ""));
    }

}