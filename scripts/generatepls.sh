#!/usr/bin/bash
URLGEN=http://localhost:31080/generatePersoonslijst
URLPL=http://localhost:41080/persoonslijsten
echo "Store all PLs to $URLPL"
TMP=/dev/null
OUT=pl.json
ID=0
#
#
while test $ID -lt 1000
do
	curl -s $URLGEN -d "{}" -H "Content-Type: application/json" > $OUT
	curl -s $URLPL -d "@$OUT" -H "Content-Type: application/json" > $TMP
	echo "Sleep $ID"
	sleep 1s
	let ID=$ID+1
done 
