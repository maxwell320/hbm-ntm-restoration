param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs
)

$javaHome = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot'

if (-not (Test-Path $javaHome)) {
    throw "Java 17 not found at $javaHome"
}

$env:JAVA_HOME = $javaHome
$javaBin = Join-Path $javaHome 'bin'
if (-not (($env:Path -split ';') -contains $javaBin)) {
    $env:Path = "$javaBin;$env:Path"
}

if (-not $GradleArgs -or $GradleArgs.Count -eq 0) {
    $GradleArgs = @('compileJava')
}

& ".\gradlew.bat" @GradleArgs
exit $LASTEXITCODE
