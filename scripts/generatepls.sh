#!/usr/bin/bash
URLGEN=http://192.168.1.245:30913/generatePersoonslijst
URLPL=http://192.168.1.245:31886/persoonslijsten
echo "Store all PLs to $URLPL"
TMP=/tmp/pl-ids.txt
OUT=/tmp/pl.json
COUNT=10
ID=0
#
#
while test $ID -lt $COUNT
do
	echo -n "+"
	curl -s $URLGEN -d "{}" -H "Content-Type: application/json" > $OUT
	echo -n "."
	curl -s $URLPL -d "@$OUT" -H "Content-Type: application/json" >> $TMP
	echo -n "."
	echo -ne "\n" >> $TMP
	# echo "Sleep $ID"
	# sleep 1s
	let ID=$ID+1
done 
