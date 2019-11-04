#!/bin/bash

while true; do
  result=$(./gradlew clean test)
  if [[ $result == *"BUILD SUCCESSFUL"* ]]; then
    echo "SUCCESS"
  else
    exit 1
  fi
done