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