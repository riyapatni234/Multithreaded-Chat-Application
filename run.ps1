$ErrorActionPreference = "Stop"

$fxLib = "C:\Program Files\Java\javafx-sdk-26\lib"
$modules = "javafx.controls,javafx.fxml"

Set-Location $PSScriptRoot

$pgJarFile = Get-ChildItem -Path . -Filter "postgresql-*.jar" -File | Select-Object -First 1
if ($pgJarFile) {
    $cp = ".;$($pgJarFile.Name)"
    Write-Host "Using JDBC jar: $($pgJarFile.Name)" -ForegroundColor Cyan
} else {
    $cp = "."
    Write-Host "PostgreSQL JDBC jar not found, running without DB driver on classpath." -ForegroundColor Yellow
}

javac --module-path $fxLib --add-modules $modules -cp $cp Main.java LoginPage.java SignupPage.java ChatDashboard.java DatabaseConnection.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

java --module-path $fxLib --add-modules $modules -cp $cp Main
exit $LASTEXITCODE
