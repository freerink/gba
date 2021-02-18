#!/usr/bin/bash
URL=http://192.168.1.245:30913/anummers
echo "Store all A-nummers to $URL"
IN=anummers-20210125.txt
TMP=/tmp/anummer.tmp
#
# Read the generated A-nummers from the file
# Check if the A nummer exists
# If not -> store it with gemeenteCode = 0
#

#
# Read the A nummers
#
while read -r LINE
do
	curl -s "$URL?anummer=$LINE" > $TMP
	RAW=`cat $TMP | cut -d ':' -f 1`
	#echo "RAW=$RAW"
	if test "$RAW" = "Not found"
	then
		echo "A nummer $LINE not found -> store"
		curl -s $URL -d "{\"anummer\": $LINE, \"gemeenteCode\": 0}" -H "Content-Type: application/json" > $TMP
	else
		# A nummer exists. we are good
		echo "A-nummer $LINE is already stored"
		VALID=`echo "data=$(</tmp/anummer.tmp);console.log(data.valid)" | node`
		GEMEENTECODE=`echo "data=$(</tmp/anummer.tmp);console.log(data.gemeenteCode)" | node`
		SKIPTO=`echo "data=$(</tmp/anummer.tmp);console.log(data.skipTo)" | node`
		# echo "Sleep anummer=$LINE valid=$VALID code=$GEMEENTECODE skipto=$SKIPTO"
	# exit
	fi
	#sleep 0.1s
	#let ID=$ID+1
done < "$IN"
