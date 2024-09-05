# Maven Dependencies

The purpose of this task is to practice writing the Maven POM file.
Duration: _30 minutes_

## Description

In this task, you will create a project within this one and write a POM file that describes this project.
You must specify this project's coordinates (groupId, artifactId, version), list of developers, and some dependencies,
as described below.

You need to create a directory named `project` in this root project directory and create a Maven POM file
within.

## Requirements

* `pom.xml` is located in the `project` directory
* Model version must `4.0.0`
* `groupId` must be `com.vanderlinde`
* `artifactId` must be `awesome-plan`
* `version` must be `0.0.1-SNAPSHOT`
* Must be specified 2 developers of this project:
  * John Marston, with `johnmarston@linde.der` email, `johnnyboy` id, `Van der Linde` organization, and -7 timezone
  * Leigh Johnson, with `leighjohnson@armadillo.com` email, `marshall` id, `Town of Armadillo` organization, and -7 timezone
* This project must depend on the following projects:
  * `junit-jupiter` of version `5.7.2`, `test` scope
  * `commons-lang3` of version `3.12.0`, with no specified scope
  * `lombok` of version `1.18.20`, `provided` scope.

All dependencies groupId you can find in Maven Central.