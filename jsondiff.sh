#!/bin/bash

# requires npm package json-diff
echo "You need to install json-diff, by running"
echo "npm install -g json-diff"

if [ -z "$1" ]; then
  echo "Usage: jsondiff.sh <path1> <path2> ..."
  echo "All paths are relative to assets, such as 'examplemod/blockstates'"
fi

assetMain="./src/main/resources/assets"
assetGen="./src/generated/resources/assets"

while [ -n "$1" ]; do
  dir="$1"

  pathMain="$assetMain/$dir"
  pathGen="$assetGen/$dir"
  fileSame="./jsondiff/$dir/same.txt"
  pathDiff="./jsondiff/$dir"
  if [ -f "$fileSame" ]; then
    rm "$fileSame"
  fi
  if [ -d "$pathDiff" ]; then
    rm -r "$pathDiff"
  fi
  mkdir -p "$pathDiff"

  for filename in $(ls -1 "$pathMain"); do
    if [ -f "$pathGen/$filename" ]; then
      diff=$(json-diff "$pathMain/$filename" "$pathGen/$filename")
      if [ -z "$diff" ]; then
        echo "$pathMain/$filename" "$pathGen/$filename" >> "$fileSame"
      else
        echo "$diff" >"$pathDiff/$filename.diff"
      fi
    fi
  done

  shift 1
done
