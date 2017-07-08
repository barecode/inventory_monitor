#!/bin/bash
#
# Basic wrapper script for login.sh and checkInventory.sh
# It first calls checkInventory.sh
# If RC=0, then exit
# If RC=1, then the login likely expired, so login and re-run
#
# This script is expected to be called from crontab
#

echo "Monitor job requested at `date "+%F @ %R"`"

if ./checkInventory.sh; then
  echo "Job successfully ran";
else
  echo "Inventory check not successful, assuming expired login. Re-trying..."
  if ./login.sh; then
    ./checkInventory.sh
  fi
fi

echo "Monitor job completed at `date "+%F @ %R"`"
