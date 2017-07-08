#!/bin/bash

inspect_inventory_for() {
  WISHLIST_ITEM=$1
  if grep ">$WISHLIST_ITEM<" -A 6 inventory | grep -q "Temporarily out of stock"; then
    echo "$WISHLIST_ITEM is not in stock";
  else
    echo "$WISHLIST_ITEM is in stock! Sending notification";
  fi
}

echo "Querying inventory"
# curl --cookie ./.login-cookies "https://www.senegence.com/SeneGenceWeb/WebOrdering/ProductList.aspx?d=332764&c=1&ot=1" > inventory

if grep -q 'DistributorIdMismatchError.aspx' inventory; then
  echo "Unable to query inventory - login has expired"
  exit 1
fi


echo "Comparing to wishlist"
grep -v "^#" wishlist.txt | while read WISHLIST_ITEM; do
  inspect_inventory_for "$WISHLIST_ITEM";
done
