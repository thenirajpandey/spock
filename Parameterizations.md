### Simple parameterizations ###

```
where:
x << dataProvider
```

Here, `x` is an unbound (i.e. undefined) variable, and `dataProvider` is an object that is iterated over according to Groovy semantics. Each value returned by `dataProvider` is bound to variable `x`, and the feature method is subsequently executed once (including `setup()`and `cleanup()`). An execution of a feature method for a particular binding of values to variables is called _iteration_.

If you prefer static typing, `x` can also be defined as a parameter of the surrounding feature method:

```
def "test me"(int x) {
  // ...
  where: x << [1,2,3]
}
```

If a feature method has multiple simple parameterizations, the number of iterations is the minimum number of values returned by a data provider, and any left over values are ignored:

```
where:
x << [1, 2]
y << [4, 5, 6]
```

These parameterizations will trigger two iterations - one with binding `x=1, y=4`, and another one with binding `x=2, y=5`.

Besides iterating over a data provider, Spock might invoke two other methods on a data provider: If a data provider offers a `size()` method, Spock will use the value returned by this method to calculate the estimated number of iterations for the feature method. If a data provider offers a `close()` method, Spock will call this method once the data provider is no longer needed.

It is very easy to write a custom data provider. Just implement the methods `next()` and `hasNext()` (as defined in `java.util.Iterator`) and optionally `size()` and `close()`, and you are ready to go!

### Derived parameterizations ###

```
where:
x = expression
```

Derived parameterizations compute new values from existing ones. They are evaluated once for every iteration, but do not by themselves trigger additional iterations. Usually, derived parameterizations are used together with simple parameterizations:

```
where:
row << sql.execute("select * from customer")
firstName = row["first_name"]
secondName = row["second_name"]
name = firstName + " " + secondName
```

Derived parameterizations can also be used to introduce symbolic names for fixed values:

```
when: stack.push(item)
then: stack.pop() == item
where: item = new Car("Porsche", Color.RED)
```

Here `item` is an alias for `new Car("Porsche", Color.RED)`, and cleanly separates the feature method's logic from the concrete data used.

### Multi-parameterizations ###

Especially when working with external data (e.g. from a spreadsheet or a database), it's sometimes handy to assign multiple values at once. Therefore, Spock provides a notation similar to Groovy's multi-assignment:

```
where:
[name, age, gender] = sql.execute("select name, age, sex from customer")
```

Here Spock iterates (again according to Groovy's iteration semantics) over each row returned by the query and binds the first element to `name`, the second to `age`, and the third to `gender`. Any subsequent elements in a row are ignored. To skip over intermediary elements, placeholders can be used:

```
where:
[name, _, gender] = sql.execute("select name, age, sex from customer")
```

(Of course, in this particular example the preferred solution would be to adapt the SQL query to only return `name` and `sex`.)