#!/bin/bash         
# nohup ./iterator.sh &> iterator.log &
echo "Model training iterator started"

for i in 1 2 3 4 5 6 7 8 9
do
	echo "-----------------"
	echo "-----------------"
	echo "-----------------"
	echo "-----------------"
	echo "Starting with version $i:"
	
	python3 training.py $i

	/home/anarbek/Flink/bin/start-cluster.sh

	java -cp /mnt/shared_disk/iter/game-bundled-1.0.jar kz.ninestones.game.learning.training.MonteCarloTreeSearchExplorationPipeline \
	--outputPath=/var/shared_disk/iter/v$((i+1))/training/direct/data \
	--experimentalOutputPath=/var/shared_disk/iter/v$((i+1))/training/experimental/data \
	--numSeeds=64 \
	--numExpanses=50 \
	--numOutputShards=16 \
	--topOff=50 \
	--parallelism=8 \
	--runner=FlinkRunner \
	--fasterCopy=true \
	--flinkMaster=localhost:8081 \
	--filesToStage=game-bundled-1.0.jar

	/home/anarbek/Flink/bin/stop-cluster.sh
done

