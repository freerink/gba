#!/usr/bin/bash
URL=https://nl.geneanet.org/surname/list
echo "Get all names from $URL"
OUT=names.txt
TMP=names.tmp
MATCHURL=https://nl.geneanet.org/familienamen/
BLOCK=1
curl -s $URL | grep -e "familienamen/[A-Z]*[A-Z]\"" | sed s,\<a\ href=\"$MATCHURL,, | sed s,\"\>,, | sed "s/ *//" > $TMP
while test -s $TMP
do
	echo "Block $BLOCK"
	cat $TMP >> $OUT
	let BLOCK=$BLOCK+1
	curl -s $URL/$BLOCK | grep -e "familienamen/[A-Z]*[A-Z]\"" | sed s,\<a\ href=\"$MATCHURL,, | sed s,\"\>,, | sed "s/ *//" > $TMP
done
