/*param([String]$projectName,
      [int]$buildId,
      [String]$environment)
Write-Host "projectName: $projectName <br />"
Write-Host "buildId: $buildId  <br />"
Write-Host "environment: $environment  <br />"
$OFS = "`r`n"
'projectName: ' + $projectName + $OFS + 'buildId: ' + $buildId + $OFS + 'environment: ' + $environment | out-file "D:\test.txt"*/
For ($i=0; $i -le 10; $i++) {
     Write-Output $i
  }