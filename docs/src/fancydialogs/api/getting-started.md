---
icon: dot
---

# Getting started

## Include the API in your project

To include the FancyNPCs API in your project, you need to add the following dependency to your `build.gradle.kts` or `pom.xml` file.

**Gradle:**
```kotlin
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}
```

```kotlin
dependencies {
    compileOnly("de.oliver:FancyDialogs:VERSION")
}
```

**Maven:**
```xml
<repository>
    <id>fancyinnovations-releases</id>
    <name>FancyInnovations Repository</name>
    <url>https://repo.fancyinnovations.com/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>de.oliver</groupId>
    <artifactId>FancyDialogs</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

Replace `VERSION` with the version of the API you want to use. You can find the latest version on the download pages or in the GitHub releases.

## Show a notice dialog

```java
new NoticeDialog("title", "message").show(player);
// or
NoticeDialog.show(player, "message");
```

## Show a confirmation dialog

```java
CompletableFuture<Boolean> confirmed = new ConfirmationDialog("title", "question", "confirmText", "cancelText").ask(player);
// or
boolean confirmed = ConfirmationDialog.ask(player, "question");
```

## JavaDocs and help

You can find the JavaDocs for the FancyNpcs API [here](https://repo.fancyinnovations.com/javadoc/releases/de/oliver/FancyNpcs/latest).

Join the [FancyInnovations Discord](https://discord.gg/ZUgYCEJUEx) for help and support. There is a dedicated channel for help about the api (`#npcs-dev`).
```