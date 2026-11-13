# Step by step process to build a java project

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


