$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$Workspace = Join-Path (Join-Path $env:USERPROFILE "Documents") "aistudy-docker-workspace"

if ($Root -match '[^\x00-\x7F]') {
  New-Item -ItemType Directory -Force -Path $Workspace | Out-Null
  robocopy $Root $Workspace /E /XD .git .idea .vscode node_modules target dist dist-prod harmony-aistudy /XF deveco-studio-*.exe /NFL /NDL /NJH /NJS /NC /NS | Out-Null
  if ($LASTEXITCODE -ge 8) {
    throw "copy workspace failed with robocopy exit code $LASTEXITCODE"
  }
  Set-Location $Workspace
} else {
  Set-Location $Root
}

docker compose --env-file script/docker/aistudy/docker.env -f docker-compose.aistudy.yml up -d --build
if ($LASTEXITCODE -ne 0) {
  throw "docker compose up failed with exit code $LASTEXITCODE"
}

Write-Host ""
Write-Host "AI Learning Assistance Platform is starting."
Write-Host "Docker workspace: $(Get-Location)"
Write-Host "Admin UI:    http://localhost:8080"
Write-Host "Backend API: http://localhost:48080/admin-api"
Write-Host "MySQL:       localhost:13306 root/root"
Write-Host "Redis:       localhost:16379"
Write-Host ""
Write-Host "For HarmonyOS client, use your LAN IP, for example:"
Write-Host "http://<your-computer-lan-ip>:48080/admin-api"
Write-Host ""
Write-Host "Logs:"
Write-Host "docker compose --env-file script/docker/aistudy/docker.env -f docker-compose.aistudy.yml logs -f server"
