#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file to
# you under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when JAVA is not on PATH
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; others: silence the output
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVA_HOME
if [ -n "${JAVA_HOME-}" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
    if [ ! -x "$JAVACMD" ]; then
      die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
  fi
else
  JAVACMD="$(
    'set' +e
    'unset' -f command 2>/dev/null
    'command' -v java
  )" || die "ERROR: Cannot find 'java' command. Please set either JAVA_HOME \
or ensure 'java' is on the PATH."
fi

# wrapperJavaCmd is used to run the wrapper download (optional)
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# Find the project basedir
find_maven_basedir() {
  if [ -z "${1-}" ]; then
    echo "Path not specified to find_maven_basedir" >&2
    return 1
  fi
  basedir="$1"
  wdir="$1"
  while [ "$wdir" != '/' ]; do
    if [ -d "$wdir/.mvn" ]; then
      basedir=$wdir
      break
    fi
    # workaround for JBEAP-8664 (on Solaris 10/Sparc)
    if [ -d "${wdir}" ]; then
      wdir=$(
        cd "$wdir/.."
        pwd
      )
    fi
    # end of workaround
  done
  printf '%s' "$(native_path "$basedir")"
}

# concatenates all lines of a file
concat_lines() {
  if [ -f "$1" ]; then
    # Remove \r in case we run on Windows within Git Bash
    # 'tr' is not on the PATH by default on macOS GitHub Actions runners
    tr -d '\r' < "$1"
  fi
}

log() {
  if [ "${MVNW_VERBOSE-}" = true ]; then
    printf '%s\n' "$1"
  fi
}

BASE_DIR=$(find_maven_basedir "$(dirname "$0")")
if [ -z "$BASE_DIR" ]; then
  exit 1
fi

MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$BASE_DIR"}
log "$MAVEN_PROJECTBASEDIR"

##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
wrapperJarPath="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
if [ -r "$wrapperJarPath" ]; then
  log "Found $wrapperJarPath"
else
  log "Couldn't find $wrapperJarPath, downloading it ..."

  if [ -n "${MVNW_REPOURL-}" ]; then
    wrapperUrl="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  else
    wrapperUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
  fi
  while IFS="=" read -r key value; do
    # Remove '\r' in case we run on Windows within Git Bash
    key=$(echo "$key" | tr -d '\r')
    case "$key" in
    wrapperUrl) wrapperUrl="$value" ;;
    esac
  done <"$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
  log "Downloading from: $wrapperUrl"

  if $JAVACMD -version > /dev/null 2>&1; then
    log "Downloading with wget"
    if command -v wget > /dev/null 2>&1; then
      wget --no-verbose -O "$wrapperJarPath" "$wrapperUrl" \
        ${MVNW_USERNAME:+"--user=${MVNW_USERNAME}"} \
        ${MVNW_PASSWORD:+"--password=${MVNW_PASSWORD}"}
    elif command -v curl > /dev/null 2>&1; then
      curl -o "$wrapperJarPath" "$wrapperUrl" \
        ${MVNW_USERNAME:+"-u ${MVNW_USERNAME}:${MVNW_PASSWORD}"}
    else
      log "Falling back to Java download"
      javaSource="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/MavenWrapperDownloader.java"
      javaClass="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/MavenWrapperDownloader"
      # For those who are not running Java 11+, use MavenWrapperDownloader.java
      if $JAVACMD -version 2>&1 | grep -q "version \"1[^0-9]" || $JAVACMD -version 2>&1 | grep -q "version \"[0-9]\""; then
        if [ ! -e "$javaClass.class" ]; then
          if [ -e "$javaSource" ]; then
            if ! $JAVACMD -version > /dev/null 2>&1; then
              die "ERROR: The java/javac command is not found on the PATH, and JAVA_HOME is not set."
            fi
          fi
        fi
      fi
    fi
  fi
fi

##########################################################################################
# End of extension
##########################################################################################

MAVEN_OPTS="$(concat_lines "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config") ${MAVEN_OPTS-}"

# shellcheck disable=SC2086 # safe to ignore - we want word splitting on MAVEN_OPTS
exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$wrapperJarPath" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"
