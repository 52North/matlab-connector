#!/bin/bash -e

group="com.googlecode.matlabcontrol"
artifact="matlabcontrol"
version="4.1.0"
base_url="https://matlabcontrol.googlecode.com/files/matlabcontrol-$version"

tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT
for c in "" "javadoc" "sources"; do
	file="$tmp/$artifact-$version${c:+-$c}.jar"
	wget "$base_url${c:+-$c}.jar" -O "$file"
	mvn install:install-file \
		-D "groupId=$group"  -D "artifactId=$artifact"  -D "version=$version" \
        -D "packaging=jar" -D "file=$file" -D "classifier=$c"
done
