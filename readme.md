## Spring LDAP

This is the Atlassian fork of Spring LDAP 2.4.4. See
[Atlassian build and release instructions](ATLASSIAN.md) for the fork's versioning,
build requirements and Maven publication commands.

[![Join the chat at https://gitter.im/spring-projects/spring-ldap](https://badges.gitter.im/spring-projects/spring-ldap.svg)](https://gitter.im/spring-projects/spring-ldap?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://travis-ci.org/spring-projects/spring-ldap.svg?branch=main)](https://travis-ci.org/spring-projects/spring-ldap)

## Atlassian release

This release is `org.springframework.ldap:spring-ldap-core:2.4.4-atlassian-2`,
built from branch `codex/cve-2026-41720`. It includes the
[CVE-2026-41720](https://spring.io/security/cve-2026-41720/) empty-password
authentication fix, backported from upstream commit
[`5cc73a6b`](https://github.com/spring-projects/spring-ldap/commit/5cc73a6b7fb1d2bcb950c4d69221c6d192d79795).
The backport preserves TLS context cleanup when authentication is rejected.
Full FeCru/Crowd integration validation is still required before product adoption.
The previous `2.4.4-atlassian-1` release only established the fork's build and
publication setup and does not contain this fix.

Use **JDK 17** to run Gradle; published library classes target **Java 8**.
The release includes `spring-ldap-core`, `spring-ldap-core-tiger`,
`spring-ldap-ldif-core`, `spring-ldap-odm`, and `spring-ldap-test`, each with its
main, sources and Javadoc JARs, POM and Gradle module metadata.

1. Check out the reviewed branch containing the security backport:

   ```sh
   git switch codex/cve-2026-41720
   ```

2. Set `version=2.4.4-atlassian-2` in `gradle.properties`. Later releases must use
   a new `-atlassian-N` suffix; published versions must not be overwritten.

3. Set `JAVA_HOME` to a JDK 17 installation. Configure the publishing credentials
   in your local `~/.gradle/gradle.properties`:

   ```properties
   atlassianMavenUsername=<your-publishing-username>
   atlassianMavenPassword=<your-publishing-password-or-token>
   ```

   Alternatively, use `ORG_GRADLE_PROJECT_atlassianMavenUsername` and
   `ORG_GRADLE_PROJECT_atlassianMavenPassword` environment variables. Keep these
   values out of Git and command-line arguments. Credentials are not required
   for builds or local publication.

4. Build, test and stage all five libraries locally:

   ```sh
   ./gradlew --no-daemon clean buildFork publishForkToLocal
   git diff --check
   ```

   Inspect the artifacts under
   `build/publications/repos/org/springframework/ldap/`. Verify their release
   version, fork SCM metadata, Java 8 compatibility and inter-module dependency
   versions. The build task excludes samples and standalone LDAP server suites;
   run the integration checks appropriate to the changes being released.

5. Review and commit the release changes, then publish from the clean release
   checkout:

   ```sh
   ./gradlew --no-daemon publishFork
   ```

   This uploads only the five library modules to
   `https://packages.atlassian.com/maven/3rdparty`. CI builds and stages locally;
   it does not publish releases. If publication fails partway through, inspect
   which artifacts were uploaded before retrying.

6. Download all five published files for each module (main, sources and
   Javadoc JARs, POM and Gradle module metadata) through the Atlassian Maven
   repository and compare their SHA-256 hashes with the validated local
   artifacts. For example, the core POM is available at:

   ```sh
   curl --fail --location https://packages.atlassian.com/maven-external/org/springframework/ldap/spring-ldap-core/2.4.4-atlassian-2/spring-ldap-core-2.4.4-atlassian-2.pom
   ```

   After successful publication, tag the exact release commit and push the
   branch and tag to the fork:

   ```sh
   git tag -a 2.4.4-atlassian-2 -m "Release Spring LDAP 2.4.4-atlassian-2"
   git push --atomic -u origin codex/cve-2026-41720 refs/tags/2.4.4-atlassian-2
   ```

7. Continue development on a separate branch with
   `version=2.4.4-atlassian-3-SNAPSHOT`, leaving the release tag unchanged.

See [ATLASSIAN.md](ATLASSIAN.md) for focused build commands and dependency
compatibility notes. The remaining sections describe the upstream project.

## Code of Conduct
This project adheres to the Contributor Covenant [code of conduct](CODE_OF_CONDUCT.adoc).
By participating, you  are expected to uphold this code. Please report unacceptable behavior to spring-code-of-conduct@pivotal.io.

### INTRODUCTION

Spring LDAP is a library to simplify LDAP programming in Java, built on the same
principles as Spring Jdbc. 

The LdapTemplate class encapsulates all the plumbing work involved in traditional LDAP 
programming, such as creating, looping through NamingEnumerations, handling Exceptions
and cleaning up resources. This leaves the programmer to handle the important stuff - 
where to find data (DNs and Filters) and what to do with it (map to and from domain
objects, bind, modify, unbind, etc.), in the same way that JdbcTemplate relieves the 
programmer of all but the actual SQL and how the data maps to the domain model.

In addition to this, Spring LDAP provides Exception translation from NamingExceptions
to an unchecked exception hierarchy, as well as several utilities for working with filters,
LDAP paths and attributes.

For detailed information on the project, please refer to the [reference documentation](https://docs.spring.io/spring-ldap/docs/current/reference/) and [javadocs](https://docs.spring.io/spring-ldap/docs/2.3.x/apidocs/).
See [the changelog](https://github.com/spring-projects/spring-ldap/milestone/28?closed=1) for detailed information on changes in the latest version.

### MAVEN USERS

All major releases of this library are available in the central Maven repository.
The following components are available:

* spring-ldap-core - the core Spring LDAP library
* spring-ldap-core-tiger - the Spring LDAP Java 5 support library
* spring-ldap-test - support classes that help LDAP with integration testing
* spring-ldap-ldif-core - the Spring LDAP LDIF parsing library
* spring-ldap-ldif-batch - the Spring Batch integration layer for the LDIF parsing library
* spring-ldap-odm - The Object-Directory Mapping (ODM) framework

To include e.g. the spring-ldap-core component in your project, use the following dependency tag:

    <dependency>
      <groupId>org.springframework.ldap</groupId>
      <artifactId>spring-ldap-core</artifactId>
      <version>2.3.2.RELEASE</version>
    </dependency>

Milestone releases (such as release candidates) are available from the Spring
framework milestone repo:

    <repository>
      <id>spring-milestone</id>
      <name>Spring Portfolio Milestone Repository</name>
      <url>https://maven.springframework.org/milestone</url>
    </repository>

Snapshot builds produced by the CI builds are available from the Spring framework snapshot repo:

    <repository>
      <id>spring-milestone</id>
      <name>Spring Portfolio Milestone Repository</name>
      <url>https://maven.springframework.org/snapshot</url>
    </repository>

In order to e.g. try out the latest build snapshot of the core module, include the repository above and
use the following dependency tag:

    <dependency>
      <groupId>org.springframework.ldap</groupId>
      <artifactId>spring-ldap-core</artifactId>
      <version>2.0.M1.CI-SNAPSHOT</version>
    </dependency>

Note that while all milestone and snapshot builds have passed the (quite extensive) test suite,
the artifacts here are obviously still work in progress. Feel free to use them for trying out new functionality
and bug fixes for an upcoming version. Please report any problems or bugs in the [issue tracker](https://github.com/spring-projects/spring-ldap/issues).

### ADDITIONAL RESOURCES

* [Official site](https://spring.io/projects/spring-ldap)
* [Stack Overflow](https://stackoverflow.com/questions/tagged/spring-ldap)
* [Issue tracker](https://github.com/spring-projects/spring-ldap/issues)
