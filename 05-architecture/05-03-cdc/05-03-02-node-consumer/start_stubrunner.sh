#!/usr/bin/env bash

set -o errexit

./run_stubrunner_as_docker.sh
echo "Wait for 60 seconds for the container to start"
sleep 60