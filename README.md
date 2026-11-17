# Step by step process to build a java project

**I'm trying to build a java project. A backend-only system where customers place orders, stock is reserved, payment is processed, and notifications go out. I will be building this from scratch, listing every single detail, explaining wherever it is needed. If you want to build the same just read this file thoroughly.**

## Part 1 - Prerequisites - MacBook
- Download and install VS Code. [If you are unaware of this step, sort this out first.]

- Install SDKMAN - run below commands in terminal one after the other
    ```
    curl -s "https://get.sdkman.io" | bash

    source "$HOME/.sdkman/bin/sdkman-init.sh"

    sdk version
    ```
    *`SDKMAN` help you switch between multiple versions of java while developing the application*
- You should see output containing the latest script and native versions:
    ```
    SDKMAN!
    script: 5.19.0
    native: 0.5.0
    ````
- Install SDK Java using below commands
    ```
    sdk install java -tem
    sdk current java    
    ```
    - You should see below output 
    `21-tem`

- Install Docker 
    - Navigate to https://www.docker.com/products/docker-desktop/
    - Download docker and install as per your machine
    - Once installed, run `docker run hello-world` in your terminal, it should give some output like below
    `Hello from Docker! This message shows that your installation appears to be working correctly.`

- Install Maven and Git using below commands
    `brew install maven git`

- In VS Code, install Extension Pack for Java, Spring Boot Extension Pack, and Docker.

- Create a git repo online and initialise it with and connect that repo to your local machine.

## Part 2 - Creating Folders
- Create a folder named `shopsteam`, name this at your convinience.
- Create two sub-folders namely, `catalog-service` and `common`. We will get to know about more details shortly. 

## Part 3 - Understanding what we are about to write. 
- We are creating a Java project, we uses Maven and SpringBoot. 
- **Maven:** This is a build automation and dependency management tool for java projects. It does two jobs.
    - **Build Management:** It knows how to turn the standard java code into deliverables and run the standard lifecycle.(compile -> test -> package -> install).
    - **Dependency management:** You have to specify that you need a particular library in the pom file, maven gets that for you from the repository. For example , if you specify the `spring-boot-starter-web`in the pom file, maven downloads the libraries for you from maven central. 
    *Maven is a java program, runs on java, it requires a JDK to run in your local machine.*
- **SpringBoot:** It is a framework; the core library set for building Java applications. Its central idea is the IoC [Inversion of Control] container: you write classes, and Spring creates the objects (beans), wires them together (dependency injection), and manages their lifecycle. Around that core are modules for web (Spring MVC), data access, security, transactions, and so on. 
- The problem Spring Boot solves. Plain Spring needed a lot of manual configuration: XML or Java config for every bean, picking compatible versions of every library, installing and configuring an external Tomcat, and deploying a WAR to it. Spring Boot removes that.

### What Spring Boot actually adds
*this can be skipped, read this while you progress*

- **Starters:** one dependency like spring-boot-starter-web pulls in a tested bundle (Spring MVC, Jackson, embedded Tomcat, validation). This is the “set of libraries” part.
- **Auto-configuration:** at startup, Spring Boot inspects your classpath and configures beans for you. Tomcat is on the classpath, so it creates a web server. That’s why your app started with almost no code.
- **Embedded server:** Tomcat runs inside your jar, so you start the app with java -jar.
- **Version management:** the BOM you imported in the parent POM guarantees all those libraries are compatible.
- **Production features:**Actuator (the /actuator/health you called), externalized configuration (application.yml), and metrics.
- **Executable packaging:** the repackage step that produced your fat jar.

### POM File [Project Object Model]
- A POM is an XML file (`pom.xml`). Maven needs a POM file to build a Java application; without it, Maven doesn't know what to do. A `pom.xml` must be present in the root directory, plus one in each sub-module directory (if it is a multi-module project).
- The POM tells Maven:
  - **Who** the project is
  - **What** it needs
  - **How** to build it
  - **How** this file relates to other POM files

#### Important Tags in `pom.xml`
- **`<project>`**: The root tag containing standard XML namespace attributes (`xmlns`, `xmlns:xsi`, `xsi:schemaLocation`).
- **`<modelVersion>`**: The version of the POM schema itself (always `4.0.0` for Maven 2/3), telling Maven how to parse the file.
- **`<groupId>`**: A reverse-domain name identifying the organization or owner (e.g., `org.apache.maven`). Java package structures conventionally start with this name to ensure global uniqueness.
- **`<artifactId>`**: The unique name of the artifact (JAR/WAR) this project produces within its `groupId`.
- **`<version>`**: The version of your project (e.g., `1.0.0-SNAPSHOT`). During active development, append `-SNAPSHOT` so Maven fetches updates dynamically; without it, Maven treats the version as an immutable release.
- **`<packaging>`**: Defines the output type produced by the POM (`jar`, `war`, or `pom`). Defaults to `jar`. `war` packages a web application for external servers. `pom` produces no code artifact—only configuration and structure (always used for parent POMs).
- **Parent vs. Child POMs**:
  - **Parent/Aggregator POM**: Located in the root directory (`<packaging>pom</packaging>`). Uses `<modules>` to list sub-module folders and centralizes shared configurations.
  - **Child POM**: Located in a sub-module folder. Uses the `<parent>` tag—which **must** include the parent's `<groupId>`, `<artifactId>`, and `<version>`—to inherit settings.
  - **Rule of thumb**: One POM per component that gets built or versioned independently. Put shared configuration in the parent POM, and module-specific settings in the child POM.
- **`<properties>`**: Defines reusable variables and build settings such as Java version, Spring Boot version, and source encoding (`UTF-8`).
- **`<dependencyManagement>`**: Centralizes dependency versions, scopes, and exclusions without actually downloading or adding them to the classpath.
  - Used in parent POMs so child modules can declare dependencies without specifying `<version>`, or in single POMs to override transitive dependency versions.
  - When importing a Bill of Materials (BOM), set `<type>pom</type>` and `<scope>import</scope>`.
- **`<dependencies>`**: Declares the actual libraries required by the current project/module to compile, test, and run.
- **`<build>`**: Controls how the project is compiled, packaged, and tested.
  - Any plugin placed directly inside `<build><plugins>` executes automatically during the build lifecycle of the current project and all its children.
  - Also allows customizing default paths (`<sourceDirectory>`, `<outputDirectory>`), packaging non-Java files like `.yml` or `.properties` (`<resources>`), and setting the output artifact name (`<finalName>`).
- **`<pluginManagement>`**: Nested inside `<build>`, it works like `<dependencyManagement>` but for Maven plugins. It standardizes plugin versions and configurations across modules without running them automatically; a child module only runs the plugin if it explicitly declares the plugin's `groupId` and `artifactId` in its own `<build><plugins>` section.
- **`<executions>`**: Nested inside a `<plugin>`, it defines what tasks a plugin runs and when:
  - **`<id>`**: A unique identifier for the execution block (useful for running a plugin multiple times with different settings or overriding it in a child POM).
  - **`<phase>`**: Binds the execution to a standard Maven lifecycle step (e.g., `compile`, `test`, `package`, `verify`). Uses the goal's default phase if omitted.
  - **`<goals>`**: Lists the specific plugin tasks (Mojos) to run (e.g., `shade`, `copy-resources`, `repackage`).
  - **`<configuration>`**: Optional parameters that apply only to that specific execution, overriding plugin-level settings.

> **Key Takeaway:** In Apache Maven, a **dependency** is a library that your application's code needs to compile and run, while a **plugin** is a tool that Maven itself uses to build, test, and package your project. Simply put, **dependencies are for your code, and plugins are for Maven**.
