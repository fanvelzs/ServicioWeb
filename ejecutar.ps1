param([switch]$Pruebas)

$ErrorActionPreference = 'Stop'
$javaHomeAnterior = $env:JAVA_HOME
$jdkCandidatos = @($env:JAVA_HOME)
$carpetaJdks = Join-Path $env:USERPROFILE '.jdks'

if (Test-Path -LiteralPath $carpetaJdks) {
    $jdkCandidatos += @(Get-ChildItem -LiteralPath $carpetaJdks -Directory |
        Select-Object -ExpandProperty FullName)
}

$jdkProyecto = $jdkCandidatos | Where-Object {
    $_ -and
    (Test-Path -LiteralPath (Join-Path $_ 'bin\javac.exe')) -and
    (Test-Path -LiteralPath (Join-Path $_ 'release')) -and
    (Select-String -LiteralPath (Join-Path $_ 'release') -Pattern '^JAVA_VERSION="25([.+-]|")' -Quiet)
} | Select-Object -First 1

if (-not $jdkProyecto) {
    throw 'No se encontro un JDK 25. Instala el JDK 25 en .jdks o configura JAVA_HOME con su carpeta.'
}

Push-Location $PSScriptRoot
try {
    $env:JAVA_HOME = $jdkProyecto
    Write-Host "Usando JDK: $jdkProyecto"
    if ($Pruebas) {
        & .\mvnw.cmd test
    } else {
        & .\mvnw.cmd javafx:run
    }
    if ($LASTEXITCODE -ne 0) {
        throw "Maven termino con el codigo $LASTEXITCODE."
    }
} finally {
    $env:JAVA_HOME = $javaHomeAnterior
    Pop-Location
}
