#!/bin/bash

set -euo pipefail

function installTravisTools {
  mkdir ~/.local
  curl -sSL https://github.com/SonarSource/travis-utils/tarball/v27 | tar zx --strip-components 1 -C ~/.local
  source ~/.local/bin/install
}

installTravisTools

case "$TARGET" in

CI)
  #regular_mvn_build_deploy_analyze
  
  # Do not deploy a SNAPSHOT version but the release version related to this build
  #set_maven_build_version $TRAVIS_BUILD_NUMBER 
  echo TRAVIS_BUILD_NUMBER:$TRAVIS_BUILD_NUMBER
  CURRENT_VERSION=`mvn help:evaluate -Dexpression="project.version" | grep -v '^\[\|Download\w\+\:'`
  echo CURRENT_VERSION:$CURRENT_VERSION
  RELEASE_VERSION=`echo $CURRENT_VERSION | sed "s/-.*//g"`
  echo RELEASE_VERSION:$RELEASE_VERSION
  NEW_VERSION="$RELEASE_VERSION-build$TRAVIS_BUILD_NUMBER"
  echo NEW_VERSION:$NEW_VERSION 
  mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$NEW_VERSION
  echo repo:$ARTIFACTORY_DEPLOY_REPO::default::$ARTIFACTORY_URL/$ARTIFACTORY_DEPLOY_REPO
  # the profile "deploy-sonarsource" is defined in parent pom v28+
  mvn deploy -DaltDeploymentRepository="$ARTIFACTORY_DEPLOY_REPO::default::$ARTIFACTORY_URL/$ARTIFACTORY_DEPLOY_REPO" \
    -Pdeploy-sonarsource \
    -B -e -V
  ;;


*)
  echo "Unexpected TARGET value: $TARGET"
  exit 1
  ;;

esac
