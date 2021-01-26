#!/usr/bin/bash
URL=http://localhost:31080/anummers
echo "Get all A-nummers from $URL"
OUT=anummers.txt
TMP=anummers.tmp
#
# Read the last A nummer from the file if it exists
#
if test -s $OUT
then
	LASTANUMMER=`tail -n 1 $OUT`
	let LASTANUMMER=$LASTANUMMER+1
	echo "Continue from $LASTANUMMER"
	curl -s "$URL?startFrom=$LASTANUMMER&maxIterations=1000" > $TMP
else
	curl -s "$URL" > $TMP
fi
#
# Do the loop
#
while test -s $TMP 
do
	SKIPTO=`echo "data=$(<anummers.tmp);console.log(data.skipTo)" | node`
	# echo "SKIPTO = $SKIPTO"
	ANUMMER=`echo "data=$(<anummers.tmp);console.log(data.anummer)" | node`
	# echo "ANUMMER = $ANUMMER"
	VALID=`echo "data=$(<anummers.tmp);console.log(data.valid)" | node`
	# echo "VALID = $VALID"
	if test "true" = $VALID
	then
		echo $ANUMMER
		echo "$ANUMMER" >> $OUT
	else
		echo "$ANUMMER is not valid"
	fi
	curl -s "$URL?startFrom=$SKIPTO&maxIterations=1000" > $TMP
	# sleep 1
done
