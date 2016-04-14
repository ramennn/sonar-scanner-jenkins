#!/bin/bash
set -euo pipefail
echo "Running with SQ=$SQ_VERSION"

JENKINS_VERSION=1.580.3
 
  mvn package -B -e -V -DskipTests
  
  cd its
  mvn -Djenkins.runtimeVersion="$JENKINS_VERSION" -Dsonar.runtimeVersion="$SQ_VERSION" -Dmaven.test.redirectTestOutputToFile=false verify 
