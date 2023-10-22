#!/bin/bash

# Exit immediately if a simple command exits with a non-zero status
set -o errexit
# When errtrace is enabled, the ERR trap is also triggered when the error (a command returning a nonzero code) occurs inside a function or a subshell.
set -o errtrace
# If set, the return value of a pipeline is the value of the last (rightmost) command to
# exit with a non-zero status, or zero if all commands in the pipeline exit successfully.
# By default, pipelines only return a failure if the last command errors.
# When used in combination with set -e, pipefail will make a script exit if any command
# in a pipeline errors.
set -o pipefail

# synopsis {{{
# Receives a request from an external system.
# }}}

export CURL_BIN="${CURL_BIN:-curl}"
export URL="${URL:-https://reqres.in/api/users/2}"

# FUNCTION: request {{{
# Will call an external URL to retrieve a user.
function request() {
	"${CURL_BIN}" "${URL}" || echo "Failed to retrieve the request"
} # }}}

request
