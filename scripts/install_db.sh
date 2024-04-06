#!/usr/bin/env bash
echo "••••••••••••••INSTALLING DB••••••••••••••"

psql -U postgres <<EOF
DROP DATABASE IF EXISTS reco;
DROP DATABASE IF EXISTS recotest;

CREATE DATABASE reco;
CREATE DATABASE recotest;

CREATE ROLE reco WITH CREATEDB SUPERUSER LOGIN;

ALTER USER reco WITH PASSWORD 'password';
\q
EOF
psql -d reco -U postgres -f ./scripts/dbsetup.sql
psql -d recotest -U postgres -f ./scripts/dbsetup.sql