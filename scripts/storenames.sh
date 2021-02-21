#!/usr/bin/bash
URL=http://192.168.1.245:30913/names
echo "Store all names to $URL"
IN=names-20210125.txt
TMP=/dev/null
#
# Get the current number of stored names
#
OUT=`curl -s "$URL?count=true"`
echo "Count out = $OUT"
if test $OUT = "[]"
then
	START=0
else
	START=`echo $OUT | cut --delimiter=: -f 2 | cut --delimiter=, -f 1`
	let START=$START+1
fi
ID=0
echo "Starting from $START"
#
# Read the names
#
while read -r LINE
do
	if test $ID -ge $START
	then
		# echo "$ID = $LINE"
		curl -s $URL -d "{\"id\": $ID, \"name\": \"$LINE\"}" -H "Content-Type: application/json" > $TMP
	fi
	# echo "Sleep $ID"
	# sleep 0.1s
	let ID=$ID+1
done < "$IN"
