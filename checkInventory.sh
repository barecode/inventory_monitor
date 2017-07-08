#!/bin/bash
#
# Required Environment Variables
# AUTOMATION_EMAIL_ID - automation e-mail account
# AUTOMATION_EMAIL_PASSWORD - automation e-mail account password
# NOTIFICATION_EMAIL - the notification target (e-mail address)
#
#

DEBUG=
MISSING_SECURE_SH=
MISSING_REQUIRED_ENVIRONMENT_VARIABLES=

# Switch to the script directory before execution
cd `dirname $0`

if [[ -r ./secure.sh ]]; then
  MISSING_SECURE_SH=1
  source ./secure.sh
fi

if [[ -z $AUTOMATION_EMAIL_ID ]]; then
  echo "Automation e-mail ID not set - specify AUTOMATION_EMAIL_ID environment variable"
  MISSING_REQUIRED_ENVIRONMENT_VARIABLES=1
fi

if [[ -z $AUTOMATION_EMAIL_PASSWORD ]]; then
  echo "Automation e-mail password not set - specify AUTOMATION_EMAIL_PASSWORD environment variable"
  MISSING_REQUIRED_ENVIRONMENT_VARIABLES=1
fi

if [[ -n $MISSING_REQUIRED_ENVIRONMENT_VARIABLES ]]; then
  if [[ -n $MISSING_SECURE_SH ]]; then
    echo "Define the variables in the shell or set in ./secure.sh"
  else
    echo "./secure.sh available but environment variables not set"
  fi
  exit 1;
fi


# Wrap an echo in debug enabled logic
log() {
  if [[ -n $DEBUG ]]; then
    echo $1;
  fi
}

send_notification_for() {
  WISHLIST_ITEM=$1
  echo "$WISHLIST_ITEM is in stock!" | mailx -r "$AUTOMATION_EMAIL_ID" -s "$WISHLIST_ITEM is in stock!" -S smtp="smtp.gmail.com:587" -S smtp-use-starttls -S smtp-auth=login -S smtp-auth-user="$AUTOMATION_EMAIL_ID" -S smtp-auth-password="$AUTOMATION_EMAIL_PASSWORD" "$NOTIFICATION_EMAIL"
}

inspect_inventory_for() {
  WISHLIST_ITEM=$1
  if grep ">$WISHLIST_ITEM<" -A 6 inventory | grep -q "Temporarily out of stock"; then
    log "$WISHLIST_ITEM is not in stock";
  else
    echo "$WISHLIST_ITEM is in stock! Sending notification";
    send_notification_for "$WISHLIST_ITEM"
  fi
}

echo "Querying inventory"
curl --cookie ./.login-cookies "https://www.senegence.com/SeneGenceWeb/WebOrdering/ProductList.aspx?d=332764&c=1&ot=1" > inventory

if grep -q 'DistributorIdMismatchError.aspx' inventory; then
  echo "Unable to query inventory - login has expired"
  exit 1
fi


echo "Comparing to wishlist"
grep -v "^#" wishlist.txt | while read WISHLIST_ITEM; do
  inspect_inventory_for "$WISHLIST_ITEM";
done

rm inventory

