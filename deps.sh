#!/bin/bash -e

tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT

function install_matlab_connector {
	local group="com.googlecode.matlabcontrol"
	local artifact="matlabcontrol"
	local version="4.1.0"
	local base_url="https://matlabcontrol.googlecode.com/files/matlabcontrol-$version"
	# matlab connector
	for c in "" "javadoc" "sources"; do
		file="$tmp/$artifact-$version${c:+-$c}.jar"
		wget -q "$base_url${c:+-$c}.jar" -O "$file"
		mvn -q install:install-file \
			-D "groupId=$group"  -D "artifactId=$artifact"  -D "version=$version" \
			-D "packaging=jar" -D "file=$file" -D "classifier=$c"
	done
}

function install_github {
	local slug="$1"
	local branch="${2:-master}"
	git clone --recursive -q -b "$branch" https://github.com/$slug.git "$tmp/$slug"
	mvn -q -f "$tmp/$slug/pom.xml" -D skipTests clean source:jar javadoc:jar install
}

# sockets

install_matlab_connector
install_github "autermann/sockets"
