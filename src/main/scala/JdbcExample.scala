package com.example.scleradb.jdbc

import java.util.Properties
import java.sql.{Connection, Statement, ResultSet}
import java.sql.{DriverManager, ResultSetMetaData}

object JdbcExample {
    val jdbcUrl: String = "jdbc:scleradb"

    def main(args: Array[String]): Unit = args match {
        case Array("--init") => initialize() // initialize Sclera
        case queries => runQueries(queries)  // execute queries
    }

    // initialize Sclera
    private def initialize(): Unit = {
        // we are initializing the schema, no need to check and validate
        val props: Properties = new Properties()
        props.setProperty("checkSchema", "false")

        // get a JDBC connection to Sclera
        var conn: Connection = DriverManager.getConnection(jdbcUrl, props)

        // display warnings, if any
        Option(conn.getWarnings()).foreach(println)

        // initialize the schema by executing the statement "create schema"
        try {
            val stmt: Statement = conn.createStatement()
            try stmt.executeUpdate("create schema") finally stmt.close()
        } finally conn.close()
    }

    // run queries on Sclera
    private def runQueries(queries: Array[String]): Unit = {
        // get a JDBC connection to Sclera
        var conn: Connection = DriverManager.getConnection(jdbcUrl)

        // display warnings, if any
        Option(conn.getWarnings()).foreach(println)

        try {
            // create a statement to execute queries
            val stmt: Statement = conn.createStatement()

            // iterate over the input queries
            try queries.foreach { query =>
                // execute query
                val rs: ResultSet = stmt.executeQuery(query)

                // result metadata
                val metaData: ResultSetMetaData = rs.getMetaData()
                val n: Int = metaData.getColumnCount()
                val colNames: Seq[String] = Range.inclusive(1, n).map { i =>
                    metaData.getColumnLabel(i)
                }

                // display column names
                println(colNames.mkString(", "))

                try {
                    // display each row in the result
                    while( rs.next() ) {
                        val rowVals: Seq[String] =
                            Range.inclusive(1, n).map { i => rs.getString(i) }

                        println(rowVals.mkString(", "))
                    }
                } finally rs.close()
            } finally stmt.close()
        } finally conn.close()
    }
}
