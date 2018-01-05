# sbt-nomic

[![Build Status](https://travis-ci.org/zladovan/sbt-nomic.svg?branch=master)](https://travis-ci.org/zladovan/sbt-nomic)
[![Download](https://api.bintray.com/packages/sbt/sbt-plugin-releases/sbt-nomic/images/download.svg) ](https://bintray.com/sbt/sbt-plugin-releases/sbt-nomic/_latestVersion)

Sbt plugin for [Nomic](https://github.com/sn3d/nomic/) - applications deployer for hadoop ecosystem.

With sbt-nomic plugin it is possible to generate and control deployment of nomic boxes right from sbt build.  

## Setup

### Declaring plugin

Nomic plugin is published for **sbt 0.13.6** and **sbt 1.0.4**. 

Create file ``project/nomic.sbt`` under base directory with following content:

```scala
addSbtPlugin("com.zlad" % "sbt-nomic" % "0.1.34")
```

### Configure nomic

To use controller tasks you need to have local installation of nomic.
Follow instructions in [Nomic documentation](http://nomic.readthedocs.io/en/develop/installation.html).

Ensure you have added directory with nomic executable to PATH on your operating system.

## Usage

After declaring plugin you can start use two types of tasks - controller and generator.
They share some common settings.

```scala
name in nomic := "simple-workflow" // default to name.value
version in nomic := "1.0.0" // default to version.value
```

In the simplest usecase you can specify nomic box as a text

```scala
nomicDescribe :=
  """
    |name = 'simple-workflow'
    |version = '1.0.0'
    |
    |hdfs {
    |   resource "workflow.xml"
    |}
  """.stripMargin
```  
Add some file, e.g. from project resources

```scala
mappings in nomic ++= resourceDirectory.value / "workflow.xml" pair flat
```

Finally deploy your box to hdfs from commandline

```
sbt nomicInstall
```

## Generator

TODO

## Controller

There are few tasks which result in calling nomic commands from command line. 
They can be used to control deployment of nomic box(es) from build.
See [Nomic documentation](http://nomic.readthedocs.io/en/latest/gettingstarted.html#deploying-and-removing) for details. 

* nomicInstall - ``nomic install ${path to target/nomic}``
* nomicRemove - ``nomic remove $group:$name:*`` 
* nomicUpgrade - shorthand for calling  nomicRemove and nomicInstall
* nomicList - ``nomic list``
* nomicConfig - ``nomic conf``
 