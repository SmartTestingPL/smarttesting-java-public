#!/bin/bash

set -o errexit

currentFolder="$( pwd )"

# Wersja Spring Cloud Contract Stub Runner
SC_CONTRACT_DOCKER_VERSION="4.0.4"
# Konfiguracja Spring Cloud Contract Stub Runner
STUBRUNNER_PORT="8083"
# Koordynaty zaślepek 'groupId:artifactId:version:classifier:port'
STUBRUNNER_IDS="pl.smarttesting:loan-issuance:0.0.1-SNAPSHOT:stubs:9876"
# Uruchom obraz Dockerowy ze Spring Cloud Contract Stub Runner
docker run  --rm -e "STUBRUNNER_IDS=${STUBRUNNER_IDS}" \
-e "STUBRUNNER_STUBS_MODE=LOCAL" \
-e 'THIN_ROOT=file:///home/scc/.m2/' \
-p "${STUBRUNNER_PORT}:${STUBRUNNER_PORT}" \
-p "9876:9876"  \
-v "${HOME}/.m2/:/home/scc/.m2/:rw" \
--detach \
springcloud/spring-cloud-contract-stub-runner:"${SC_CONTRACT_DOCKER_VERSION}"
