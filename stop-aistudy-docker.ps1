$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$Workspace = Join-Path (Join-Path $env:USERPROFILE "Documents") "aistudy-docker-workspace"

if (Test-Path (Join-Path $Workspace "docker-compose.aistudy.yml")) {
  Set-Location $Workspace
} else {
  Set-Location $Root
}

docker compose --env-file script/docker/aistudy/docker.env -f docker-compose.aistudy.yml down
if ($LASTEXITCODE -ne 0) {
  throw "docker compose down failed with exit code $LASTEXITCODE"
}
