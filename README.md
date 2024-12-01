# Spigot Framework by Fronsky®

| Latest Version |
|----------------|
| v1.2.0         |

## Description

Boost Spigot plugin development with the Fronsky Spigot Framework. Packed with tools and libraries, it streamlines plugin creation and management, empowering you to bring your Minecraft ideas to life.

## Table of Contents

- [Installation](#installation)
    - [Setup JDK](#setup-jdk)
- [Deployment](#deployment)

### Installation

To get started, follow these steps to install the necessary software:

1. Ensure that you have Git installed on your system. If you're not sure, you can check by running the following command in your terminal:

  ```bash
  git --version
  ```

If Git is not installed, you can download and install it from the official website: [https://git-scm.com/](https://git-scm.com/)

2. Go to the directory where you want to install the Spigot Framework, and open the terminal here.
3. Clone this repository, by executing the following command in your terminal:

  ```bash
  git clone -b <branch-name> https://github.com/fronskydev/spigot-framework.git
  ```

Replace `<branch_name>` with the name of the branch you want to clone.

4. Ensure that you have IntelliJ IDEA and Java JDK 8 installed on your system. If you're not sure, you can check by running the following commands in your terminal:

  ```bash
  java -version
  where /R C:\ idea64.exe
  ```

If either of these tools is not installed, you can download and install them from the following links:

* Java JDK 8: [https://www.oracle.com/nl/java/technologies/javase/javase8-archive-downloads.html](https://www.oracle.com/nl/java/technologies/javase/javase8-archive-downloads.html)
* Intellij IDEA: [https://www.jetbrains.com/idea/](https://www.jetbrains.com/idea/)

8. Look in the [Setup JDK](#setup-jdk) to setup the jdk correctly in the project if you get any errors.

#### Setup JDK

1. Open IntelliJ IDEA and open your project.
2. In the projects tab, right-click on the root of your project name and choose `Open Module Settings` from the context menu.
3. In the Module Settings dialog, navigate to the `Project` tab and ensure that the SDK is set to JDK 8.
4. On the right side of the IntelliJ IDEA window, locate the Gradle tab and click on it. Then, click on the tools icon `⚙️` (settings), followed by `Gradle Settings`.
5. In the Gradle Settings dialog, verify that the Gradle JVM is configured to use JDK 8.

### Deployment

To create the jar file for the Spigot Framework, run the following command inside IntelliJ IDEA.

```bash
.\gradlew clean build
```
