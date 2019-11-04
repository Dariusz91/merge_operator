##Description

You can use this project to try reproduce error resulting in failure of Flux merge operator.
When it is merging two monos, one emitting empty element and other one emitting error 
then sometimes it can result in success.

#### Run tests
You can run tests using gradle command:
```./gradlew clean test```

#### Run tests until it fails
Because of nondeterministic behavior of this test you can use attached ``run_multiple_times.sh`` script to run it in loop
until it fails. You can find logs from tests in build folder.

##### Usage:
```./run_multiple_times.sh```