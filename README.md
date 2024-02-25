# Nine Pebbles (Togyzkumalak) Training Data Pipeline

The core package contains game state, policy, state evaluation, strategies, and simulation.

Provides Apache Beam pipeline that generates Monte Carlo Search Tree (the tree of most promissing moves and their outcomes):
```java
    ExplorationPipelineOptions options =
        PipelineOptionsFactory.fromArgs(args).withValidation().as(ExplorationPipelineOptions.class);

    Pipeline pipeline = Pipeline.create(options);

    List<Integer> instances =
        IntStream.range(0, options.getNumSeeds()).boxed().collect(Collectors.toList());

    pipeline
        .apply("Create seeds", Create.of(instances))
        .apply(
            "Generate Random Game States",
            MapElements.into(BeamTypes.stateProtos).via(i -> GameSimulator.randomState().toProto()))
        .apply("Expand Monte Carlo Search Tree", ParDo.of(new ExpandFn()))
        .apply(
            "Enrich Less Visited Nodes",
            MapElements.into(BeamTypes.stateNodes)
                .via(MonteCarloTreeSearchExplorationPipeline::enrich))
        .apply(
            "Encode As TensorFlow Example",
            MapElements.into(BeamTypes.examples)
                .via(MonteCarloTreeSearchExplorationPipeline::encode))
        .apply(
            "Map To ByteArrays", MapElements.into(BeamTypes.byteArrays).via(Example::toByteArray))
        .apply(
            "Write TFRecords",
            TFRecordIO.write()
                .withNumShards(options.getNumOutputShards())
                .to(options.getOutputPath()));

    pipeline.run().waitUntilFinish();
```

```shell
mvn -Pdataflow-runner compile exec:java \
    -Dexec.mainClass=org.apache.beam.examples.WordCount \
    -Dexec.args="--project=ninestones \
    --gcpTempLocation=gs://game-default-bucket/temp/ \
    --output=gs://game-default-bucket/output \
    --runner=DataflowRunner"
    
mvn -Pdataflow-runner compile exec:java \
    -Dexec.mainClass=kz.ninestones.game.learning.training.MonteCarloTreeSearchExplorationPipeline \
    -Dexec.args="--project=ninestones \
    --gcpTempLocation=gs://game-default-bucket/temp/ \
    --outputPath=gs://game-default-bucket/game_output \
    --numSeeds=10 \
    --numExpanses=100 \
    --numOutputShards=10 \
    --runner=DataflowRunner \
    --region=us-central1"
    

mvn -Pflink-runner compile exec:java \
    -Dexec.mainClass=kz.ninestones.game.learning.training.MonteCarloTreeSearchExplorationPipeline \
    -Dexec.args="--outputPath=/var/shared_disk/training/v2/data \
    --numSeeds=100 \
    --numExpanses=50 \
    --numOutputShards=10 \
    --parallelism=8
    --runner=FlinkRunner \
    --fasterCopy=true \
    --flinkMaster=192.168.0.80:8081 \
    --filesToStage=target/game-bundled-1.0.jar"
    
mvn -Pflink-runner compile exec:java \
    -Dexec.mainClass=kz.ninestones.game.learning.training.MonteCarloTreeSearchExplorationPipeline \
    -Dexec.args="--outputPath=/var/shared_disk/training/v3/data \
    --numSeeds=4 \
    --numExpanses=10 \
    --numOutputShards=10 \
    --topOff=10 \
    --parallelism=8
    --runner=FlinkRunner \
    --fasterCopy=true \
    --flinkMaster=192.168.0.80:8081 \
    --filesToStage=target/game-bundled-1.0.jar"
    
mvn -Pflink-runner compile exec:java \
    -Dexec.mainClass=kz.ninestones.game.learning.training.MonteCarloTreeSearchExplorationPipeline \
    -Dexec.args="--outputPath=/var/shared_disk/training/v6/direct/data \
    --experimentalOutputPath=/var/shared_disk/training/v6/experimental/data \
    --numSeeds=192 \
    --numExpanses=150 \
    --numOutputShards=16 \
    --topOff=50 \
    --parallelism=8
    --runner=FlinkRunner \
    --fasterCopy=true \
    --flinkMaster=192.168.0.80:8081 \
    --filesToStage=target/game-bundled-1.0.jar"
    
```