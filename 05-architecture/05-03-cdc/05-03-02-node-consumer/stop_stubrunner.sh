#!/usr/bin/env bash

set -o errexit

docker stop "$(docker ps | grep stub-runner | awk -F' ' '{print $1}')" || echo "Failed to stop"