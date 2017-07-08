#!/bin/bash
#
# Required Environment Variables
# SENEGENCE_DIST_ID - SeneGence distrbutor ID number (login ID)
# SENEGENCE_DIST_PASS - distributor login password
#
#

MISSING_SECURE_SH=
MISSING_REQUIRED_ENVIRONMENT_VARIABLES=

if [[ -r ./secure.sh ]]; then
  MISSING_SECURE_SH=1
  source ./secure.sh
fi

if [[ -z $SENEGENCE_DIST_ID ]]; then
  echo "Distributor ID not set - specify SENEGENCE_DIST_ID environment variable"
  MISSING_REQUIRED_ENVIRONMENT_VARIABLES=1
fi

if [[ -z $SENEGENCE_DIST_PASS ]]; then
  echo "Login password not set - specify SENEGENCE_DIST_PASS environment variable"
  MISSING_REQUIRED_ENVIRONMENT_VARIABLES=1
fi

if [[ -n $MISSING_REQUIRED_ENVIRONMENT_VARIABLES ]]; then
  if [[ -n $MISSING_SECURE_SH ]]; then
    echo "Define the variables in the shell or set in ./secure.sh to be automatically loaded"
  else
    echo "./secure.sh available but environment variables not set"
  fi
  exit 1;
fi

echo "Logging in as distributor $SENEGENCE_DIST_ID"

echo curl -H "Content-Length:42" -H "Content-Type:application/json; charset=UTF-8" -d "{Dist_ID:'$SENEGENCE_DIST_ID',Dist_Pass:'$SENEGENCE_DIST_PASS'}" --dump-header ./login-headers --cookie-jar ./mycookies https://www.senegence.com/senegence/default.aspx/DistributorLogin
