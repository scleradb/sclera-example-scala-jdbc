# Sclera - JDBC Example (Scala version)

This example application shows how an application can interface with Sclera using the standard [JDBC API](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html).

Sclera's JDBC driver [sclera-jdbc](https://github.com/scleradb/sclera/tree/master/modules/interfaces/jdbc) provides a [JDBC](http://en.wikipedia.org/wiki/Java_Database_Connectivity) type 4 interface.

To use Sclera through JDBC, the application needs to:

- specify the Sclera home directory by setting the `SCLERA_ROOT` environment variable (if not set, the default is `$HOME/.sclera`)
- add the following dependencies:
    - Sclera Configuration Manager, [sclera-config](https://github.com/scleradb/sclera/tree/master/modules/config),
    - Sclera Core Engine, [sclera-core](https://github.com/scleradb/sclera/tree/master/modules/core),
    - Sclera JDBC Driver, [sclera-jdbc](https://github.com/scleradb/sclera/tree/master/modules/interfaces/jdbc), and
    - Sclera plugins needed (if any).
- connect to Sclera's JDBC driver using the JDBC URL `jdbc:scleradb`, and execute commands and queries using the standard [JDBC API](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html).

The example application described below is a command line tool to initialize Sclera, and execute queries. See [here](#executable-script) for details on the usage.

## Specify Sclera Root Directory

We need to specify a directory where Sclera can keep its configuration, metadata, and internal database. This is done by setting the environment variable `SCLERA_ROOT`. If not specified, the default is `$HOME/.sclera`.

## Add Package Dependencies

This example uses [SBT](https://scala-sbt.org) as the build tool, and the build file is [`build.sbt`](build.sbt).

The required dependencies are added as:

```scala
    libraryDependencies ++= Seq(
        "com.scleradb" %% "sclera-config" % "4.0-SNAPSHOT",
        "com.scleradb" %% "sclera-core" % "4.0-SNAPSHOT",
        "com.scleradb" %% "sclera-jdbc" % "4.0-SNAPSHOT"
    )
```

This is a minimal example, and does not include any Sclera plugins. If your example needs a Sclera Plugin, it should be added to the `libraryDependencies` as well.

## Interface with Sclera using the JDBC API

This application consists of a single source file, [`JdbcExample.scala`](src/main/scala/JdbcExample.scala).

There are two procedures:

- `initialize()`: This initializes Sclera's schema (metadata). This is called when `--init` is specified on the command line.
- `runQueries()`: This executes queries provided on the command line and displays the results.

### Code Details: `initialize()`

- Links with Sclera's JDBC driver and gets a JDBC `Connection`.
- Creates a JDBC `Statement` using the JDBC connection.
- Executes the statement `create schema` on Sclera using the JDBC `Statement`.

When a connection is initialized, Sclera first checks the sanity of its Schema and issues a warning if anything is wrong. Since we are initializing the schema, we bypass this step by passing a flag `checkSchema` in the properties while creating a connection.

### Code Details: `runQueries(...)`

- Links with Sclera's JDBC driver and gets a JDBC `Connection`.
- Creates a JDBC `Statement` using the JDBC connection.
- For each query in the list passed as the parameter,
    - Executes the query using the JDBC `Statement`, getting the JDBC `ResultSet`
    - Get the JDBC `ResultSetMetadata` for the `ResultSet` -- this provides the number of columns in the result and their names.
    - Output the column names, followed by the result values one row at a time.

## Executable Script

The build file contains a task `mkscript` that generates an executable script for the application, called `scleraexample` in the `bin` subdirectory. You can generate the script using the command:

    > sbt mkscript

The script is run as follows:

    > bin/scleraexample --init

    > bin/scleraexample "select 'Hello' as greeting1, 'World!' as greeting2"
    GREETING1, GREETING2
    Hello, World!

