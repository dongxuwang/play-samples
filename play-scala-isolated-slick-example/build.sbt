import com.github.tototoshi.sbt.slick.CodegenPlugin.autoImport.{slickCodegenDatabasePassword, slickCodegenDatabaseUrl, slickCodegenJdbcDriver}
import _root_.slick.codegen.SourceCodeGenerator
import _root_.slick.{model => m}

lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:h2:./test")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "sa")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "")

val FlywayVersion = "8.5.13"

(ThisBuild / version) := "1.1-SNAPSHOT"

(ThisBuild / libraryDependencies) ++= Seq(
  "javax.inject" % "javax.inject" % "1",
  "joda-time" % "joda-time" % "2.12.2",
  "org.joda" % "joda-convert" % "2.2.3",
  "com.google.inject" % "guice" % "5.1.0"
)

(ThisBuild / scalaVersion) := "2.13.10"
(ThisBuild / scalacOptions) ++= Seq(
  "-encoding", "UTF-8", // yes, this is 2 args
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-numeric-widen"
)
(ThisBuild / javacOptions) ++= Seq("--release", "11")

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += "org.flywaydb" % "flyway-core" % FlywayVersion,
    flywayLocations := Seq("classpath:db/migration"),
    flywayUrl := databaseUrl,
    flywayUser := databaseUser,
    flywayPassword := databasePassword,
    flywayBaselineOnMigrate := true
  )

lazy val api = (project in file("modules/api"))


lazy val slick = (project in file("modules/slick"))
  .enablePlugins(CodegenPlugin)
  .settings(
    libraryDependencies ++= Seq(
<<<<<<< HEAD
      "com.zaxxer" % "HikariCP" % "5.0.1",
      "com.typesafe.slick" %% "slick" % "3.4.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
      "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.2"
=======
      "com.zaxxer" % "HikariCP" % "3.4.5",
      "com.typesafe.slick" %% "slick" % "3.3.3",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
      "com.github.tototoshi" %% "slick-joda-mapper" % "2.6.0"
>>>>>>> 622ed087 (Update slick-joda-mapper to 2.6.0)
    ),

    slickCodegenDatabaseUrl := databaseUrl,
    slickCodegenDatabaseUser := databaseUser,
    slickCodegenDatabasePassword := databasePassword,
    slickCodegenDriver := _root_.slick.jdbc.H2Profile,
    slickCodegenJdbcDriver := "org.h2.Driver",
    slickCodegenOutputPackage := "com.example.user.slick",
    slickCodegenExcludedTables := Seq("schema_version"),

    slickCodegenCodeGenerator := { (model: m.Model) =>
      new SourceCodeGenerator(model) {
        override def Table = new Table(_) {
          override def TableClass = new TableClass {
            override def parents = Seq("com.example.user.slick.JodaSupport")
          }
          override def Column = new Column(_) {
            override def rawType = model.tpe match {
              case "java.sql.Timestamp" => "org.joda.time.DateTime" // kill j.s.Timestamp
              case _ =>
                super.rawType
            }
          }
        }
      }
    },
    (Compile / sourceGenerators) += slickCodegen.taskValue
  )
  .aggregate(api)
  .dependsOn(api)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-isolated-slick-example""",
    TwirlKeys.templateImports += "com.example.user.User",
    libraryDependencies ++= Seq(
      guice,
      "com.h2database" % "h2" % "2.1.214",
      ws % Test,
      "org.flywaydb" % "flyway-core" % FlywayVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test
    ),
    (Test / fork) := true
  )
  .aggregate(slick)
  .dependsOn(slick)
