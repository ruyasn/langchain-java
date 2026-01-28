@echo off
setlocal
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set "MAVEN_OPTS=%MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%"

if not exist "%WRAPPER_JAR%" (
  echo Maven Wrapper jar not found: %WRAPPER_JAR%
  exit /b 1
)

set "JAVA_CMD=java"
if not "%JAVA_HOME%"=="" (
  if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java"
)

"%JAVA_CMD%" %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
endlocal
