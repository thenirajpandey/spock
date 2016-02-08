This example loads test data from an embedded [H2](http://www.h2database.com) database (make sure the Jar is on your classpath).

```
import groovy.sql.Sql

import spock.lang.*

class DatabaseDriven extends Specification {
  @Shared sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")
  
  // normally an external database would be used,
  // and the test data wouldn't have to be inserted here
  def setupSpec() {
    sql.execute("create table maxdata (id int primary key, a int, b int, c int)")
    sql.execute("insert into maxdata values (1, 3, 7, 7), (2, 5, 4, 5), (3, 9, 9, 9)")
  }

  def "maximum of two numbers"() {
    expect:
    Math.max(a, b) == c

    where:
    [a, b, c] << sql.rows("select a, b, c from maxdata")
  }
}
```