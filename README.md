# NablarchでOpenAIを使う

## 概要

インテグレーションするライブラリがあるわけではないので単純にOpenAIクライアントを使うだけ。

OpenAIクライアントは https://platform.openai.com/docs/libraries で紹介されている。
このリポジトリでは[Azure OpenAI client library for Java](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/openai/azure-ai-openai)を使う。

## 設計メモ

OpenAIクライアントはNablarchのコンポーネントとして登録して`SystemRepository`から取得して使用する。

Azure OpenAI client library for Javaではクライアントをビルダーで構築するため、Nablarchでコンポーネント登録する際は[ファクトリクラス](https://nablarch.github.io/docs/LATEST/doc/application_framework/application_framework/libraries/repository.html#repository-factory-injection)を用いている。

## コード例の動作確認

### 準備

1. https://platform.openai.com/settings/organization/api-keys でAPIキーを作成する。
1. 作成したAPIキーを環境変数`OPENAI_APIKEY`へ設定する

### 実行・動作確認

サーバーを起動する。

```
mvn jetty:run
```

[HTTPie](https://httpie.io/)で動作確認する。

```
$ http GET localhost:9080/openai query=="Nablarchの概要を100文字で。"
HTTP/1.1 200 OK
Cache-Control: no-store
Content-Length: 462
Content-Type: application/json;charset=utf-8
Date: Fri, 03 Jan 2025 01:47:27 GMT
Referrer-Policy: strict-origin-when-cross-origin
Server: Jetty(12.0.12)
X-Content-Type-Options: nosniff

{
    "contentFilterResults": null,
    "delta": null,
    "enhancements": null,
    "finishReason": "stop",
    "index": 0,
    "logprobs": null,
    "message": {
        "content": "Nablarchは、日本の企業向けに開発されたフレームワークで、業務システムの構築を効率化します。特に Javaベースで、柔軟な設計や高い生産性を提供し、保守性にも優れています。",
        "context": null,
        "functionCall": null,
        "refusal": null,
        "role": "assistant",
        "toolCalls": null
    }
}
```

### コンテナ化

[Cloud Native Buildpacks](https://buildpacks.io/)でコンテナイメージを作成する。

```
pack build -e BP_MAVEN_ACTIVE_PROFILES=prod -e BP_MAVEN_BUILT_ARTIFACT='target/*-prod.war' -e BP_TOMCAT_VERSION=10.1.34 nablarch-openai-example
```

<details>
<summary>ビルドのログ</summary>

```
latest: Pulling from paketobuildpacks/builder-jammy-base
Digest: sha256:c88ac6549786c68de00c68d1a149b36f33c0c6d12f9943765afb0c259374533e
Status: Image is up to date for paketobuildpacks/builder-jammy-base:latest
latest: Pulling from paketobuildpacks/run-jammy-base
Digest: sha256:e260db5855ca069e423a2037af484a0f83ca5fd27ab7dbf00529c6c3cdd11f16
Status: Image is up to date for paketobuildpacks/run-jammy-base:latest
===> ANALYZING
Restoring data for SBOM from previous image
===> DETECTING
target distro name/version labels not found, reading /etc/os-release file
10 of 26 buildpacks participating
paketo-buildpacks/ca-certificates   3.9.0
paketo-buildpacks/bellsoft-liberica 11.0.1
paketo-buildpacks/syft              2.6.1
paketo-buildpacks/maven             6.19.2
paketo-buildpacks/executable-jar    6.12.0
paketo-buildpacks/apache-tomcat     8.4.1
paketo-buildpacks/apache-tomee      1.11.0
paketo-buildpacks/liberty           4.4.1
paketo-buildpacks/dist-zip          5.9.0
paketo-buildpacks/spring-boot       5.32.0
===> RESTORING
Restoring metadata for "paketo-buildpacks/ca-certificates:helper" from app image
Restoring metadata for "paketo-buildpacks/bellsoft-liberica:java-security-properties" from app image
Restoring metadata for "paketo-buildpacks/bellsoft-liberica:jre" from app image
Restoring metadata for "paketo-buildpacks/bellsoft-liberica:helper" from app image
Restoring metadata for "paketo-buildpacks/apache-tomcat:catalina-base" from app image
Restoring metadata for "paketo-buildpacks/apache-tomcat:helper" from app image
Restoring metadata for "paketo-buildpacks/apache-tomcat:tomcat" from app image
===> BUILDING
target distro name/version labels not found, reading /etc/os-release file

Paketo Buildpack for CA Certificates 3.9.0
  https://github.com/paketo-buildpacks/ca-certificates
  Build Configuration:
    $BP_EMBED_CERTS                    false  Embed certificates into the image
    $BP_ENABLE_RUNTIME_CERT_BINDING    true   Deprecated: Enable/disable certificate helper layer to add certs at runtime
    $BP_RUNTIME_CERT_BINDING_DISABLED  false  Disable certificate helper layer to add certs at runtime
  Launch Helper: Reusing cached layer

Paketo Buildpack for BellSoft Liberica 11.0.1
  https://github.com/paketo-buildpacks/bellsoft-liberica
  Build Configuration:
    $BP_JVM_JLINK_ARGS           --no-man-pages --no-header-files --strip-debug --compress=1  configure custom link arguments (--output must be omitted)
    $BP_JVM_JLINK_ENABLED        false                                                        enables running jlink tool to generate custom JRE
    $BP_JVM_TYPE                 JRE                                                          the JVM type - JDK or JRE
    $BP_JVM_VERSION              21                                                           the Java version
  Launch Configuration:
    $BPL_DEBUG_ENABLED           false                                                        enables Java remote debugging support
    $BPL_DEBUG_PORT              8000                                                         configure the remote debugging port
    $BPL_DEBUG_SUSPEND           false                                                        configure whether to suspend execution until a debugger has attached
    $BPL_HEAP_DUMP_PATH                                                                       write heap dumps on error to this path
    $BPL_JAVA_NMT_ENABLED        true                                                         enables Java Native Memory Tracking (NMT)
    $BPL_JAVA_NMT_LEVEL          summary                                                      configure level of NMT, summary or detail
    $BPL_JFR_ARGS                                                                             configure custom Java Flight Recording (JFR) arguments
    $BPL_JFR_ENABLED             false                                                        enables Java Flight Recording (JFR)
    $BPL_JMX_ENABLED             false                                                        enables Java Management Extensions (JMX)
    $BPL_JMX_PORT                5000                                                         configure the JMX port
    $BPL_JVM_HEAD_ROOM           0                                                            the headroom in memory calculation
    $BPL_JVM_LOADED_CLASS_COUNT  35% of classes                                               the number of loaded classes in memory calculation
    $BPL_JVM_THREAD_COUNT        250                                                          the number of threads in memory calculation
    $JAVA_TOOL_OPTIONS                                                                        the JVM launch flags
    Using buildpack default Java version 21
  BellSoft Liberica JDK 21.0.5: Contributing to layer
    Downloading from https://github.com/bell-sw/Liberica/releases/download/21.0.5+11/bellsoft-jdk21.0.5+11-linux-amd64.tar.gz
    Verifying checksum
    Expanding to /layers/paketo-buildpacks_bellsoft-liberica/jdk
    Adding 147 container CA certificates to JVM truststore
    Writing env.build/JAVA_HOME.override
    Writing env.build/JDK_HOME.override
  BellSoft Liberica JRE 21.0.5: Reusing cached layer
  Launch Helper: Reusing cached layer
  Java Security Properties: Reusing cached layer

Paketo Buildpack for Syft 2.6.1
  https://github.com/paketo-buildpacks/syft
    Downloading from https://github.com/anchore/syft/releases/download/v1.18.1/syft_1.18.1_linux_amd64.tar.gz
    Verifying checksum
    Writing env.build/SYFT_CHECK_FOR_APP_UPDATE.default

Paketo Buildpack for Maven 6.19.2
  https://github.com/paketo-buildpacks/maven
  Build Configuration:
    $BP_EXCLUDE_FILES                                                                            colon separated list of glob patterns, matched source files are removed
    $BP_INCLUDE_FILES                                                                            colon separated list of glob patterns, matched source files are included
    $BP_JAVA_INSTALL_NODE                 false                                                  whether to install Yarn/Node binaries based on the presence of a package.json or yarn.lock file
    $BP_MAVEN_ACTIVE_PROFILES             prod                                                   the active profiles (comma separated: such as: p1,!p2,?p3) to pass to Maven
    $BP_MAVEN_ADDITIONAL_BUILD_ARGUMENTS                                                         the additionnal arguments (appended to BP_MAVEN_BUILD_ARGUMENTS) to pass to Maven
    $BP_MAVEN_BUILD_ARGUMENTS             -Dmaven.test.skip=true --no-transfer-progress package  the arguments to pass to Maven
    $BP_MAVEN_BUILT_ARTIFACT              target/*-prod.war                                      the built application artifact explicitly.  Supersedes $BP_MAVEN_BUILT_MODULE
    $BP_MAVEN_BUILT_MODULE                                                                       the module to find application artifact in
    $BP_MAVEN_DAEMON_ENABLED              false                                                  use maven daemon
    $BP_MAVEN_POM_FILE                    pom.xml                                                the location of the main pom.xml file, relative to the application root
    $BP_MAVEN_SETTINGS_PATH                                                                      the path to a Maven settings file
    $BP_MAVEN_VERSION                     3                                                      the Maven version
    $BP_NODE_PROJECT_PATH                                                                        configure a project subdirectory to look for `package.json` and `yarn.lock` files
  Apache Maven 3.9.9: Contributing to layer
    Downloading from https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.tar.gz
    Verifying checksum
    Expanding to /layers/paketo-buildpacks_maven/maven
    Creating cache directory /home/cnb/.m2
  Compiled Application: Contributing to layer
    Executing mvn --batch-mode -Dmaven.test.skip=true --no-transfer-progress package -P prod
      [INFO] Scanning for projects...
      [INFO] 
      [INFO] ----------------< com.example:nablarch-openai-example >-----------------
      [INFO] Building nablarch-openai-example 0.1.0-SNAPSHOT
      [INFO]   from pom.xml
      [INFO] --------------------------------[ war ]---------------------------------
      [INFO] 
      [INFO] --- resources:3.3.1:resources (default-resources) @ nablarch-openai-example ---
      [INFO] Copying 7 resources from src/main/resources to target/classes
      [INFO] Copying 1 resource from src/env/prod/resources to target/classes
      [INFO] Copying 0 resource from src/main/java to target/classes
      [INFO] 
      [INFO] --- compiler:3.13.0:compile (default-compile) @ nablarch-openai-example ---
      [INFO] Recompiling the module because of added or removed source files.
      [INFO] Compiling 5 source files with javac [debug target 17] to target/classes
      [WARNING] system modules path not set in conjunction with -source 17
      [INFO] 
      [INFO] --- resources:3.3.1:testResources (default-testResources) @ nablarch-openai-example ---
      [INFO] Not copying test resources
      [INFO] 
      [INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ nablarch-openai-example ---
      [INFO] Not compiling test sources
      [INFO] 
      [INFO] --- surefire:3.5.0:test (default-test) @ nablarch-openai-example ---
      [INFO] Tests are skipped.
      [INFO] 
      [INFO] --- war:3.4.0:war (default-war) @ nablarch-openai-example ---
      [INFO] Packaging webapp
      [INFO] Assembling webapp [nablarch-openai-example] in [/workspace/target/nablarch-openai-example-0.1.0-SNAPSHOT]
      [INFO] Processing war project
      [INFO] Copying webapp resources [/workspace/src/main/webapp]
      [INFO] deleting outdated resource WEB-INF/lib/nablarch-core-dataformat-2.0.2.jar
      [INFO] deleting outdated resource WEB-INF/lib/nablarch-core-jdbc-2.2.0.jar
      [INFO] deleting outdated resource WEB-INF/lib/nablarch-core-transaction-2.1.0.jar
      [INFO] deleting outdated resource WEB-INF/lib/nablarch-fw-web-extension-2.0.0.jar
      [INFO] deleting outdated resource WEB-INF/lib/nablarch-fw-web-hotdeploy-2.0.0.jar
      [INFO] deleting outdated resource WEB-INF/lib/slf4j-nablarch-adaptor-2.1.0.jar
      [INFO] Building war: /workspace/target/nablarch-openai-example-0.1.0-SNAPSHOT-prod.war
      [INFO] ------------------------------------------------------------------------
      [INFO] BUILD SUCCESS
      [INFO] ------------------------------------------------------------------------
      [INFO] Total time:  11.933 s
      [INFO] Finished at: 2025-01-03T01:54:30Z
      [INFO] ------------------------------------------------------------------------
      
  Removing source code
  Restoring application artifact

Paketo Buildpack for Apache Tomcat 8.4.1
  https://github.com/paketo-buildpacks/apache-tomcat
  Build Configuration:
    $BP_JAVA_APP_SERVER                               the application server to use
    $BP_TOMCAT_CONTEXT_PATH                           the application context path
    $BP_TOMCAT_ENV_PROPERTY_SOURCE_DISABLED  false    Disable Tomcat's EnvironmentPropertySource
    $BP_TOMCAT_EXT_CONF_SHA256                        the SHA256 hash of the external Tomcat configuration archive
    $BP_TOMCAT_EXT_CONF_STRIP                0        the number of directory components to strip from the external Tomcat configuration archive
    $BP_TOMCAT_EXT_CONF_URI                           the download location of the external Tomcat configuration
    $BP_TOMCAT_EXT_CONF_VERSION                       the version of the external Tomcat configuration
    $BP_TOMCAT_VERSION                       10.1.34  the Tomcat version
  Launch Configuration:
    $BPL_TOMCAT_ACCESS_LOGGING_ENABLED                the Tomcat access logging state
  Apache Tomcat 10.1.34: Reusing cached layer
  Launch Helper: Reusing cached layer
  Apache Tomcat Support: Contributing to layer
    Copying context.xml to /layers/paketo-buildpacks_apache-tomcat/catalina-base/conf
    Copying logging.properties to /layers/paketo-buildpacks_apache-tomcat/catalina-base/conf
    Copying server.xml to /layers/paketo-buildpacks_apache-tomcat/catalina-base/conf
    Copying web.xml to /layers/paketo-buildpacks_apache-tomcat/catalina-base/conf
  Apache Tomcat Access Logging Support 3.4.0
    Downloading from https://repo1.maven.org/maven2/org/cloudfoundry/tomcat-access-logging-support/3.4.0.RELEASE/tomcat-access-logging-support-3.4.0.RELEASE.jar
    Verifying checksum
    Copying to /layers/paketo-buildpacks_apache-tomcat/catalina-base/lib
  Apache Tomcat Lifecycle Support 3.4.0
    Downloading from https://repo1.maven.org/maven2/org/cloudfoundry/tomcat-lifecycle-support/3.4.0.RELEASE/tomcat-lifecycle-support-3.4.0.RELEASE.jar
    Verifying checksum
    Copying to /layers/paketo-buildpacks_apache-tomcat/catalina-base/lib
  Apache Tomcat Logging Support 3.4.0
    Downloading from https://repo1.maven.org/maven2/org/cloudfoundry/tomcat-logging-support/3.4.0.RELEASE/tomcat-logging-support-3.4.0.RELEASE.jar
    Verifying checksum
    Copying to /layers/paketo-buildpacks_apache-tomcat/catalina-base/bin
    Writing /layers/paketo-buildpacks_apache-tomcat/catalina-base/bin/setenv.sh
  Mounting application at ROOT
    Writing env.launch/CATALINA_BASE.default
    Writing env.launch/CATALINA_OPTS.default
    Writing env.launch/CATALINA_TMPDIR.default
  Process types:
    task:   sh /layers/paketo-buildpacks_apache-tomcat/tomcat/bin/catalina.sh run (direct)
    tomcat: sh /layers/paketo-buildpacks_apache-tomcat/tomcat/bin/catalina.sh run (direct)
    web:    sh /layers/paketo-buildpacks_apache-tomcat/tomcat/bin/catalina.sh run (direct)
===> EXPORTING
Reusing layer 'paketo-buildpacks/ca-certificates:helper'
Reusing layer 'paketo-buildpacks/bellsoft-liberica:helper'
Reusing layer 'paketo-buildpacks/bellsoft-liberica:java-security-properties'
Reusing layer 'paketo-buildpacks/bellsoft-liberica:jre'
Reusing layer 'paketo-buildpacks/apache-tomcat:catalina-base'
Reusing layer 'paketo-buildpacks/apache-tomcat:helper'
Reusing layer 'paketo-buildpacks/apache-tomcat:tomcat'
Reusing layer 'buildpacksio/lifecycle:launch.sbom'
Added 1/1 app layer(s)
Reusing layer 'buildpacksio/lifecycle:launcher'
Reusing layer 'buildpacksio/lifecycle:config'
Reusing layer 'buildpacksio/lifecycle:process-types'
Adding label 'io.buildpacks.lifecycle.metadata'
Adding label 'io.buildpacks.build.metadata'
Adding label 'io.buildpacks.project.metadata'
Setting default process type 'web'
Saving nablarch-openai-example...
*** Images (caa6ee3cfbac):
      nablarch-openai-example
Adding cache layer 'paketo-buildpacks/bellsoft-liberica:jdk'
Adding cache layer 'paketo-buildpacks/syft:syft'
Adding cache layer 'paketo-buildpacks/maven:application'
Adding cache layer 'paketo-buildpacks/maven:cache'
Adding cache layer 'paketo-buildpacks/maven:maven'
Adding cache layer 'buildpacksio/lifecycle:cache.sbom'
Successfully built image nablarch-openai-example
```

</details>

コンテナを起動する。

```
docker run -it --rm -p 8080:8080 -e OPENAI_APIKEY=$OPENAI_APIKEY nablarch-openai-example
```

<details>
<summary>コンテナの起動ログ</summary>

```
ARNING: The requested image's platform (linux/amd64) does not match the detected host platform (linux/arm64/v8) and no specific platform was requested
Calculating JVM memory based on 15831148K available memory
For more information on this calculation, see https://paketo.io/docs/reference/java-reference/#memory-calculator
Calculated JVM Memory Configuration: -XX:MaxDirectMemorySize=10M -Xmx15244173K -XX:MaxMetaspaceSize=74974K -XX:ReservedCodeCacheSize=240M -Xss1M (Total Memory: 15831148K, Thread Count: 250, Loaded Class Count: 10823, Headroom: 0%)
Enabling Java Native Memory Tracking
Adding 147 container CA certificates to JVM truststore
Using CATALINA_BASE:   /layers/paketo-buildpacks_apache-tomcat/catalina-base
Using CATALINA_HOME:   /layers/paketo-buildpacks_apache-tomcat/tomcat
Using CATALINA_TMPDIR: /tmp
Using JRE_HOME:        /layers/paketo-buildpacks_bellsoft-liberica/jre
Using CLASSPATH:       /layers/paketo-buildpacks_apache-tomcat/catalina-base/bin/tomcat-logging-support-3.4.0.RELEASE.jar:/layers/paketo-buildpacks_apache-tomcat/tomcat/bin/bootstrap.jar:/layers/paketo-buildpacks_apache-tomcat/tomcat/bin/tomcat-juli.jar
Using CATALINA_OPTS:   -Dorg.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource
Picked up JAVA_TOOL_OPTIONS: -Djava.security.properties=/layers/paketo-buildpacks_bellsoft-liberica/java-security-properties/java-security.properties -XX:+ExitOnOutOfMemoryError -XX:MaxDirectMemorySize=10M -Xmx15244173K -XX:MaxMetaspaceSize=74974K -XX:ReservedCodeCacheSize=240M -Xss1M -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintNMTStatistics
[CONTAINER] org.apache.coyote.http11.Http11NioProtocol         INFO    Initializing ProtocolHandler ["http-nio-8080"]
[CONTAINER] org.apache.catalina.startup.Catalina               INFO    Server initialization in [700] milliseconds
[CONTAINER] org.apache.catalina.core.StandardService           INFO    Starting service [Catalina]
[CONTAINER] org.apache.catalina.core.StandardEngine            INFO    Starting Servlet engine: [Apache Tomcat/10.1.34]
[CONTAINER] org.apache.catalina.startup.HostConfig             INFO    Deploying web application directory [/layers/paketo-buildpacks_apache-tomcat/catalina-base/webapps/ROOT]
[CONTAINER] org.apache.jasper.servlet.TldScanner               INFO    At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.
2025-01-03 01:58:02.885 -INFO- null [null] boot_proc = [] proc_sys = [jaxrs] req_id = [null] usr_id = [null] initialized.
        LOGGER = [DEV] NAME REGEX = [DEV] LEVEL = [INFO]
        LOGGER = [PER] NAME REGEX = [PERFORMANCE] LEVEL = [INFO]
        LOGGER = [SQL] NAME REGEX = [SQL] LEVEL = [INFO]
        LOGGER = [ACC] NAME REGEX = [HTTP_ACCESS] LEVEL = [INFO]
        LOGGER = [ROO] NAME REGEX = [.*] LEVEL = [INFO]
2025-01-03 01:58:02.972 -INFO- nablarch.core.repository.di.config.externalize.OsEnvironmentVariableExternalizedLoader [null] boot_proc = [] proc_sys = [jaxrs] req_id = [null] usr_id = [null] value was overridden by os environment variable. key = openai.apikey
SLF4J(W): No SLF4J providers were found.
SLF4J(W): Defaulting to no-operation (NOP) logger implementation
SLF4J(W): See https://www.slf4j.org/codes.html#noProviders for further details.
2025-01-03 01:58:03.471 -INFO- nablarch.fw.web.servlet.NablarchServletContextListener [null] boot_proc = [] proc_sys = [jaxrs] req_id = [null] usr_id = [null] [nablarch.fw.web.servlet.NablarchServletContextListener#contextInitialized] initialization completed.
[CONTAINER] org.apache.catalina.startup.HostConfig             INFO    Deployment of web application directory [/layers/paketo-buildpacks_apache-tomcat/catalina-base/webapps/ROOT] has finished in [1,839] ms
[CONTAINER] org.apache.coyote.http11.Http11NioProtocol         INFO    Starting ProtocolHandler ["http-nio-8080"]
[CONTAINER] org.apache.catalina.startup.Catalina               INFO    Server startup in [1892] milliseconds
```

</details>

[HTTPie](https://httpie.io/)で動作確認する。

```
$ http GET localhost:8080/openai query=="Nablarchの概要を100文字で。"
HTTP/1.1 200 
Cache-Control: no-store
Connection: keep-alive
Content-Length: 470
Content-Type: application/json;charset=UTF-8
Date: Fri, 03 Jan 2025 01:28:42 GMT
Keep-Alive: timeout=20
Referrer-Policy: strict-origin-when-cross-origin
X-Content-Type-Options: nosniff

{
    "contentFilterResults": null,
    "delta": null,
    "enhancements": null,
    "finishReason": "stop",
    "index": 0,
    "logprobs": null,
    "message": {
        "content": "Nablarchは、Javaベースのアプリケーション開発フレームワークで、業務システムの効率的な構築を支援します。豊富なライブラリやツールを提供し、品質向上と保守性を重視しています。",
        "context": null,
        "functionCall": null,
        "refusal": null,
        "role": "assistant",
        "toolCalls": null
    }
}
```
