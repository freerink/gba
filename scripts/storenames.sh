#!/usr/bin/bash
URL=http://localhost:31080/names
echo "Store all names to $URL"
IN=names-20210125.txt
TMP=/dev/null
ID=0
while read -r LINE
do
	#echo "$ID = $LINE"
	curl -s $URL -d "{\"id\": $ID, \"name\": \"$LINE\"}" -H "Content-Type: application/json" > $TMP
	let ID=$ID+1
	#sleep 1
done < "$IN"
