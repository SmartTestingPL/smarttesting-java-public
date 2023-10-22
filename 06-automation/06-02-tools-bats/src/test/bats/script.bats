#!/usr/bin/env bats

load 'test_helper'
load 'test_helper/bats-support/load'
load 'test_helper/bats-assert/load'

setup() {
	export TEMP_DIR="$( mktemp -d )"
	cp -a "${SOURCE_DIR}" "${TEMP_DIR}/my-test"
}

function curl_verbose { echo "curl $*"; }
function curl_stub { echo "hello"; }
function curl_exception { return 1; }

export -f curl_verbose
export -f curl_stub
export -f curl_exception

teardown() { rm -rf "${TEMP_DIR}"; }

# Whitebox testing
@test "should curl request to an external website" {
	export CURL_BIN="curl_verbose"
	export URL="https://foo.com/bar"
	
	run "${SOURCE_DIR}/script.sh"

	assert_success
	assert_output "curl https://foo.com/bar"
}

# Blackbox testing
@test "should return a response from an external website" {
	export CURL_BIN="curl_stub"

	run "${SOURCE_DIR}/script.sh"

	assert_success
	assert_output "hello"
}

# Failure testing
@test "should not fail when request retrieval failed" {
	export CURL_BIN="curl_exception"

	run "${SOURCE_DIR}/script.sh"

	assert_success
	assert_output --partial "Failed"
}
