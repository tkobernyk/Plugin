param([String]$projectName,[Int32]$buildId,[String]$environment)
$localPath = "C:\Deployment"
Write-Host "projectName: $projectName"
Write-Host "Delay: 10000"
Start-Sleep -Milliseconds 10000
Write-Host "buildId: $buildId"
Write-Host "Delay: 10000"
Start-Sleep -Milliseconds 10000
Write-Host "environment: $environment"
Switch($projectName)
{
    "eSignal.ETF" { $projectFolder = "esignaletf"; break}
}
$fullPath = "$localPath\$projectFolder"
#Invoke-Command -ComputerName webhlxdeploynja.production.ofs -ScriptBlock { param($pBuildId, $pEnvironment, $pFullPath) & ("$pFullPath\Run.ps1") -buildId $pBuildId -environment $pEnvironment } -ArgumentList $buildId, $environment, $fullPath