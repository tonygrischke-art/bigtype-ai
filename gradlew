#!/bin/sh
PRG="$0"
APP_HOME=`dirname "$PRG"`/gradle/wrapper
CLASSPATH=$APP_HOME/gradle-wrapper.jar
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
