#Togyzkumalak Game (Training Data Pipeline)

Policy, state evaluation, strategies, and simulation.

The core of the Java package is Apache Beam pipeline that generates Monte Carlo Search Tree, the tree of most promissing moves and their outcomes:

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

