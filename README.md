# play-zio-example
This is a simple demo of the Play Framework playing along with the ZIO type.

I took some of this code from https://github.com/larousso/play-zio, but it specifically does not go into using ZIO modules 
or generally integrating ZIO more deeply into Play (ZEnv, application lifecycle hooks, etc.)

I'm still learning ZIO and it changes a lot so I might add more in as I keep learning.

I welcome PRs! I only ask that additions are well-commented since I want to keep
learning from this.

## Overview
This example lets you return a ZIO type within a controller action instead of a Future.
It's by no means a complete integration of ZIO into Play, but it's a good start
and allows you to use basic ZIO features that make it a solid library,
such as type-enforced error handling.

- `HomeController` has examples of how to use ZIO in your controllers.
- `app/package.scala` has the actual definitions for how ZIO actions are built.
- Some really simple tests for `HomeController` are included as well.

## Running the example
Run the server, then:
```
curl localhost:9000
```

This should output:
```
1 + 2 = 3
```

## A more complex example
Here's an example with error handling, something ZIO enforces via its type system.

For a successful run:
```
curl localhost:9000 -H "Content-Type: application/json" -d '{"input": "testing"}'
```

You should get the output:
```json
{"output":"Hello, testing"}
```

To trigger an error, provide an unexpected JSON input:
```
curl localhost:9000 -H "Content-Type: application/json" -d '{"foo": "bar"}'
```

You should get the output:
```json
{"error":"Invalid JSON input."}
```
