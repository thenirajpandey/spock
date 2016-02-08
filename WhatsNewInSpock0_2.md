## Getting started ##

Getting started with Spock has never been easier: Just download the example project ([spock-example-02.zip](http://spock.googlecode.com/files/spock-example-0.2.zip)), type `./gradlew test` (Windows: `gradlew test`), and seconds later, you will be running your first Spock specifications! The only prerequisite is that you have JDK 5 or higher installed. Everything else will be downloaded automatically. Also included are build scripts for Ant and Maven. For further instructions, see the `README` file.

## Improved visualization of data-driven features ##

Sometimes it would be nice if the iterations of a data-driven feature were listed separately in test reports and test runners. This is achieved with the `@Unroll` annotation. Example:

```
@Unroll
def "name length"() {
  expect:
  name.size() == length

  where:
  name << ["Kirk", "Spock", "Scotty"]
  length << [4, 5, 6]
}
```

Let's have a look at the resulting test report:

![http://svn.spockframework.org/wiki/FeatureUnrollingReport.png](http://svn.spockframework.org/wiki/FeatureUnrollingReport.png)

By default, the name of an iteration is the feature's name followed by a consecutive number. This can be changed by providing a naming pattern after `@Unroll`. A naming pattern may refer to data variables by prepending their names with `#`. Example:

```
@Unroll("'#name' has #length characters")
def "name length"() {
  expect:
  name.size() == length

  where:
  name << ["Kirk", "Spock", "Scotty"]
  length << [4, 5, 6]
}
```

Here is how this feature is displayed in IntelliJ IDEA:

![http://svn.spockframework.org/wiki/FeatureUnrollingScreenshot.png](http://svn.spockframework.org/wiki/FeatureUnrollingScreenshot.png)

As we have seen, `@Unroll` changes the way a data-driven feature is represented in test reports and test runners. However, it is important to note that it does _not_ affect its execution semantics.

## Smart stack trace filtering ##

Spock now employs aggressive stack trace filtering to show you only what's relevant. In particular, internals of the test runner's and Groovy's implementation are no longer displayed. Example:

```
def "my math is terrible"() {
  expect: oneAndOneMakesThree()
}

void oneAndOneMakesThree() {
  assert 1 + 1 == 3
}
```

```
Condition not satisfied:

1 + 1 == 3
  |   |
  2   false

  at StackTraceFilteringExample.oneAndOneMakesThree(StackTraceFilteringExample.groovy:19)
  at StackTraceFilteringExample.my math is terrible(StackTraceFilteringExample.groovy:15)
```

As you can see, only two lines of the stack trace are displayed. By double-clicking on one of them, you can quickly jump to the corresponding source code line. To see the full stack trace (which is over 50 lines long in this example), set the system property `-Dspock.filterStackTrace=false`.

## Accessing "old" values ##

Inside a `then` block, `old(`_expr_`)` refers to the value of `expr` before the preceding `when` block was entered. Example:

```
def "pushing an element on the stack increases its size by one"() {
  def stack = new Stack()
  when: stack.push("element")
  then: stack.size() == old(stack.size()) + 1
}
```

## Calling void methods from expect and then blocks ##

It is now safe to call void methods from `expect` and `then` blocks, without them being treated as implicit conditions. The following code snippet shows some use cases:

```
expect:
println foo // print a message to see what's going on
fooMatchesBar(foo, bar) // condition factored out into helper method with return type void
deepEquals(foo, bar) // call to third-party assertion library
```

## Filtering and sorting feature methods ##

If supported by your JUnit runner, you can now control which and in what order feature methods are run, without editing any source code. For example,  Intellij IDEA 9 EAP lets you run single methods, or just the methods that failed last time.

In case your JUnit runner doesn't offer a convenient way to run selected feature methods, there is another way to accomplish the same thing: Simply annotate the methods that should be run with` @IgnoreRest`, and all other methods will be ignored.

## Custom condition messages ##

Most of the time, it's very helpful that Spock prints out all values of a failed condition. In some cases, however, it would be better to print a custom message. This can now be achieved as follows:

```
assert list.size() < 99999999, "list has ${list.size()} elements" 
```

If this condition fails, it will print the given message instead of the contents of a huge list.

## Linking specifications to issues ##

With the `@Issue` annotation, you can now link specifications and feature methods to issues in your issue tracker.

## Other improvements in a nutshell ##
  * Better JUnit integration
  * Better error handling
  * More user-friendly error messages
  * More powerful extension mechanism
  * Support for specification base classes (experimental)
  * Many smaller improvements and bug fixes