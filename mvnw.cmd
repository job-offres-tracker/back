@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file to
@REM you under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM   JAVA_HOME - location of a JDK home dir, required when JAVA is not on PATH
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@setlocal enabledelayedexpansion
@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0") ELSE SET "BASE_DIR=%__MVNW_ARG0_NAME__%"

@SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" GOTO endDetectBaseDir

@SET EXEC_DIR=%CD%
@SET WDIR=%EXEC_DIR%
:findBaseDir
@IF EXIST "%WDIR%"\.mvn GOTO baseDirFound
@cd ..
@IF "%WDIR%"=="%CD%" GOTO baseDirNotFound
@SET "WDIR=%CD%"
@GOTO findBaseDir

:baseDirFound
@SET "MAVEN_PROJECTBASEDIR=%WDIR%"
@cd "%EXEC_DIR%"
@GOTO endDetectBaseDir

:baseDirNotFound
@SET "MAVEN_PROJECTBASEDIR=%EXEC_DIR%"
@cd "%EXEC_DIR%"

:endDetectBaseDir

@IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" (
  @IF NOT "%MVNW_REPOURL%"=="" (
    @SET "MVNW_REPO_PATTERN=/org/apache/maven/wrapper/maven-wrapper"
    @SET "WRAPPER_JAR_URL=%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  ) ELSE (
    @SET "WRAPPER_JAR_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  )
  @ECHO Downloading !WRAPPER_JAR_URL!
  @IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" (
    MKDIR "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"
  )
  powershell -NoProfile -NonInteractive -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('!WRAPPER_JAR_URL!', '%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar')"
  @IF ERRORLEVEL 1 GOTO cleanupAndExit
)

@SET JAVA_EXE=%JAVA_HOME%/bin/java.exe

@IF NOT EXIST "%JAVA_EXE%" (
  ECHO Error: JAVA_HOME is not set correctly. 1>&2
  ECHO       Please set the JAVA_HOME variable in your environment. 1>&2
  GOTO error
)

@SET "LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"

@SET CLASSPATH="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"

@SET "MAVEN_OPTS=%MAVEN_OPTS% " & @CALL :MvnWrapperJvmConfig
"%JAVA_EXE%" ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %CLASSPATH% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %LAUNCHER% %MAVEN_CONFIG% %*
@IF ERRORLEVEL 1 GOTO error
@GOTO end

:MvnWrapperJvmConfig
@SET "JVM_CONFIG_MAVEN_PROPS="
@SET "JVM_CONFIG_FILE=%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config"
@IF EXIST "%JVM_CONFIG_FILE%" (
  SET /P JVM_CONFIG_MAVEN_PROPS=<"%JVM_CONFIG_FILE%"
)
@SET "MAVEN_OPTS=%MAVEN_OPTS% %JVM_CONFIG_MAVEN_PROPS%"
@EXIT /B 0

:cleanupAndExit
@SET ERRORLEVEL=1
@GOTO error

:error
@SET ERROR_CODE=1

:end
@ENDLOCAL & SET ERROR_CODE=%ERROR_CODE%

@IF NOT "%SENTRY_EXIT_CODE%"=="" EXIT /B %SENTRY_EXIT_CODE%
@EXIT /B %ERROR_CODE%
