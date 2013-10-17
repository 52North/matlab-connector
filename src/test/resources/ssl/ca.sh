#!/bin/bash -e
DIR="${1:-$(pwd)}"
KEYSIZE=2048
CAROOT="$DIR/ca"

rm -rf ${DIR}/{server,client}*

function log() {
    echo -e "\e[0;33m$1\e[0m"
}

function create_ca() {
    log 'Configuring CA'
    mkdir -p "$CAROOT/ca.db.certs"
    touch "$CAROOT/ca.db.index"
    echo 01 > "$CAROOT/ca.db.serial"

    # Configuration
    cat > "$CAROOT/ca.conf" <<EOF
[ ca ]
default_ca = ca_default
[ ca_default ]
dir = ${CAROOT}
certs = ${CAROOT}
new_certs_dir = ${CAROOT}/ca.db.certs
database = ${CAROOT}/ca.db.index
serial = ${CAROOT}/ca.db.serial
RANDFILE = ${CAROOT}/ca.db.rand
certificate = ${CAROOT}/ca.crt
private_key = ${CAROOT}/ca.key
default_days = 365
default_crl_days = 30
default_md = md5
preserve = no
policy = generic_policy
[ generic_policy ]
countryName = optional
stateOrProvinceName = optional
localityName = optional
organizationName = optional
organizationalUnitName = optional
commonName = supplied
emailAddress = optional
EOF

    sed -i "s|REPLACE_LATER|$CAROOT|" "$CAROOT/ca.conf"

    log 'Generating CA private key'
    openssl genrsa -out "$CAROOT/ca.key" $KEYSIZE

    log 'Creating CA CSR'
    openssl req -new \
                -key "$CAROOT/ca.key" \
                -out "$CAROOT/ca.csr" \
                -subj '/C=DE/L=Muenster/O=SelfCA/CN=CA'

    log 'Creating self-signed CA certificate'
    openssl x509 -req -days 10000 \
                 -in "$CAROOT/ca.csr" \
                 -out "$CAROOT/ca.crt" \
                 -signkey "$CAROOT/ca.key"
}

function create_ca_signed() {
    log "Creating private key for $1"
    openssl genrsa -out "$DIR/$1.key" $KEYSIZE

    log "Creating CSR for $1"
    openssl req -new               \
                -key "$DIR/$1.key" \
                -out "$DIR/$1.csr" \
                -subj "/C=DE/L=Muenster/O=$1/CN=$1"

    log "Signing certificate for $1 with CA"
    openssl ca -batch -days 10000 \
               -config "$CAROOT/ca.conf" \
               -cert "$CAROOT/ca.crt" \
               -keyfile "$CAROOT/ca.key" \
               -in "$DIR/$1.csr" \
               -out "$DIR/$1.crt"
}


function create_self_signed() {
    log "Creating private key for $1"
    openssl genrsa -out "$DIR/$1.key" $KEYSIZE

    log "Creating CSR for $1"
    openssl req -new               \
                -key "$DIR/$1.key" \
                -out "$DIR/$1.csr" \
                -subj "/C=DE/L=Muenster/O=$1/CN=$1"

    log "Creating self-signed certificate for $1"
    openssl x509 -req -days 10000 \
                 -in "$DIR/$1.csr" \
                 -signkey "$DIR/$1.key" \
                 -out "$DIR/$1.crt"
}

create_ca
create_ca_signed server
create_ca_signed client1
create_self_signed client2
create_self_signed client3


mkdir "$DIR/server"
cat "$CAROOT/ca.crt" "$DIR/client2.crt" > "$DIR/server/trust.pem"
cat "$CAROOT/ca.crt" "$DIR/server.crt"  > "$DIR/server/cert.pem"
cat "$DIR/server.key"                   > "$DIR/server/key.pem"

mkdir "$DIR/client1"
cat "$DIR/server.crt"                   > "$DIR/client1/trust.pem"
cat "$DIR/client1.crt"                  > "$DIR/client1/cert.pem"
cat "$DIR/client1.key"                  > "$DIR/client1/key.pem"

mkdir "$DIR/client2"
cat "$DIR/server.crt"                   > "$DIR/client2/trust.pem"
cat "$DIR/client2.crt"                  > "$DIR/client2/cert.pem"
cat "$DIR/client2.key"                  > "$DIR/client2/key.pem"

mkdir "$DIR/client3"
cat "$DIR/server.crt"                   > "$DIR/client3/trust.pem"
cat "$DIR/client3.crt"                  > "$DIR/client3/cert.pem"
cat "$DIR/client3.key"                  > "$DIR/client3/key.pem"

rm -r "$CAROOT" ${DIR}/*.{crt,csr,key}
