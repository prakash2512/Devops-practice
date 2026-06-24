#!/bin/bash

#################################
URL="https://247healthmedpro.com/universal/"
EMAILS="prakash.k@healthmedpro.com admin.ezhil@healthmedpro.com"
LOCKFILE="/tmp/universal_alert"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL)

if [ "$STATUS" != "200" ]; then
    NOW=$(date +%s)

    if [ ! -f "$LOCKFILE" ] || [ $((NOW - $(cat $LOCKFILE))) -ge 600 ]; then
        echo "Website Down UniversalBilling - Status: $STATUS" | mail -s "Website Alert" $EMAILS
        echo $NOW > $LOCKFILE
    fi
else
    rm -f "$LOCKFILE"
fi

#################################
URL1="https://247healthmedpro.com"
LOCKFILE="/tmp/247health_alert"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL1)

if [ "$STATUS" != "200" ]; then
    NOW=$(date +%s)

    if [ ! -f "$LOCKFILE" ] || [ $((NOW - $(cat $LOCKFILE))) -ge 600 ]; then
        echo "Website Down 247-Website - Status: $STATUS" | mail -s "Website Alert" $EMAILS
        echo $NOW > $LOCKFILE
    fi
else
    rm -f "$LOCKFILE"
fi

#################################
URL2="https://medelitereports.com"
LOCKFILE="/tmp/medelite_alert"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL2)

if [ "$STATUS" != "200" ]; then
    NOW=$(date +%s)

    if [ ! -f "$LOCKFILE" ] || [ $((NOW - $(cat $LOCKFILE))) -ge 600 ]; then
        echo "Website Down Medelite - Status: $STATUS" | mail -s "Website Alert" $EMAILS
        echo $NOW > $LOCKFILE
    fi
else
    rm -f "$LOCKFILE"
fi


#################################
URL3="https://test.medelitereports.com"
LOCKFILE="/tmp/test.medelitereports_alert"

STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL3)

if [ "$STATUS" != "200" ]; then
    NOW=$(date +%s)

    if [ ! -f "$LOCKFILE" ] || [ $((NOW - $(cat $LOCKFILE))) -ge 600 ]; then
        echo "Website Down test.medelitereports - Status: $STATUS" | mail -s "Website Alert" $EMAILS
        echo $NOW > $LOCKFILE
    fi
else
    rm -f "$LOCKFILE"
fi
