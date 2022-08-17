#!/bin/bash

set -e

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Use ./update_todo.sh <ENDPOINT> <UUID>"
    exit 0
else
    echo "Endpoint : $1"
    ENDPOINT=$1
    echo "UUID : $2"
    UUID=$2
fi

TIMESTAMP=`date +%Y-%m-%dT%H:%M:%S:%3N`

time curl -X PUT $ENDPOINT/$UUID \
	-H 'Accept: application/json' \
	-H 'Content-Type: application/json' \
	-d '{"subject":"Hello from Quarkus (Update) - BASH!","body":"Content - Timestamp : '$TIMESTAMP'"}' | jq