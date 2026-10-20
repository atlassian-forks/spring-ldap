# Atlassian Spring LDAP fork

This fork starts from upstream tag `2.4.4` and retains the upstream Maven group
`org.springframework.ldap`. Fork releases use `2.4.4-atlassian-N`; development
versions append `-SNAPSHOT`. The version is defined in `gradle.properties`.

## Build and inspect

Use **JDK 17** to run the checked-in Gradle 7.4.2 wrapper. The build plugins need
JDK 17 and the tests use newer JDK APIs. Published library classes still target
**Java 8**. Set `JAVA_HOME` to a JDK 17 installation before running these commands.

```sh
./build-local.sh
./gradlew --no-daemon publishForkToLocal
```

`buildFork` builds and checks `spring-ldap-core`, `spring-ldap-core-tiger`,
`spring-ldap-ldif-core`, `spring-ldap-odm`, and `spring-ldap-test`. Samples,
standalone server integration suites, and the reference documentation are outside
this task. Their upstream tasks remain available for additional validation.

`publishForkToLocal` writes the libraries, sources, Javadoc, POMs and Gradle module
metadata under `build/publications/repos/org/springframework/ldap/`. It does not
contact a deployment repository or require publishing credentials. Check these
artifacts before a release. For a focused core build:

```sh
./gradlew --no-daemon :spring-ldap-core:check \
  :spring-ldap-core:publishMavenJavaPublicationToLocalRepository
```

The main build resolves dependencies from Maven Central first, then Atlassian's
public Maven repository. This avoids relying on the mirror for artifacts available
in Central: once Gradle selects a repository's metadata, it also fetches the JAR
from that repository. Upstream module conventions retain Spring and Shibboleth
repositories as fallbacks. The library dependency versions remain
those of upstream 2.4.4; consumer-specific overrides, including FeCru's Spring/Crowd combination, require
separate integration validation.

To check dependency downloads as well as compilation and tests, use a fresh
Gradle user home and disable reuse of task outputs:

```sh
./gradlew --no-daemon --gradle-user-home "$(mktemp -d)" \
  --no-build-cache --rerun-tasks buildFork publishForkToLocal
```

This downloads the wrapper and dependencies again and requires no publishing
credentials.

## Publish a release

Follow the [release procedure in the README](readme.md#atlassian-release) for
branching, credentials, validation, publication and tagging. The release target
is `https://packages.atlassian.com/maven/3rdparty`, matching the Atlassian Spring
Security fork.

`publishFork` publishes only the five library modules. Module-level
`publishArtifacts` and `deployArtifacts` tasks use the same destination. Remote
publication requires an `-atlassian-N` version suffix. Upstream Spring/OSSRH
release automation is not enabled.
