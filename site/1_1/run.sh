#!/usr/bin/env bash
#Number of rounds the crawler will run
rounds=2
#readseg options
dumpOptions="-nogenerate -nofetch -nocontent -noparse -noparsedata"
#!/usr/bin/env bash
# note that some of the options listed here could be set in the
# corresponding hadoop site xml param file 
commonOptions="-D mapred.reduce.tasks=$numTasks -D mapred.child.java.opts=-Xmx1000m -D mapred.reduce.tasks.speculative.execution=false -D mapred.map.tasks.speculative.execution=false -D mapred.compress.map.output=true"


# set the number of slaves nodes
numSlaves=1

# number of urls to fetch in one iteration
# 250K per task?
sizeFetchlist=`expr $numSlaves \* 50000`

# time limit for feching
timeLimitFetch=120

# num threads for fetching
numThreads=50

# enable the skipping of records for the parsing so that a dodgy document 
# so that it does not fail the full task
skipRecordsOptions="-D mapred.skip.attempts.to.start.skipping=2 -D mapred.skip.map.max.skip.records=1"

cd $HOME/crawler

touch STARTED

bin/nutch inject micrawl/crawldb/ urls
i=0
while test $rounds -gt $i ; do
	bin/nutch generate $commonOptions micrawl/crawldb/ micrawl/segments -topN $sizeFetchlist -numFetchers $numSlaves -noFilter
	s1=$(ls -d micrawl/segments/2* |tail -1)
	bin/nutch fetch $commonOptions -D fetcher.timelimit.mins=$timeLimitFetch $(echo $s1) -noParsing -threads $numThreads
	bin/nutch parse $(echo $s1)
	bin/nutch updatedb $commonOptions micrawl/crawldb/ $(echo $s1)
	bin/nutch invertlinks micrawl/linkdb $(echo $s1)
  	bin/nutch dedup micrawl/crawldb
	bin/nutch readseg -dump $(echo $s1) salida/$(echo $s1) $dumpOptions
sh juntarSalidas.sh
java -jar $HOME/101CrawlersWeb/butler.jar index
touch IndexPending
	i=$(($i+1))
done

rm STARTED
touch FIN
