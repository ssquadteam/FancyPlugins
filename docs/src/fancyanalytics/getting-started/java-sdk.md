---
icon: dot
order: 300
---

# Java SDK

This guide will help you set up FancyAnalytics in your general Java application.

## Include the Java SDK

### Gradle

```kotlin
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}
```

```kotlin
dependencies {
    implementation("de.oliver.fancyanalytics:java-sdk:VERSION")
}
```

### Maven

```xml
<repository>
    <id>fancyplugins-releases</id>
    <name>FancyPlugins Repository</name>
    <url>https://repo.fancyinnovations.com/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>de.oliver.FancyAnalytics</groupId>
    <artifactId>java-sdk</artifactId>
    <version>VERSION</version>
</dependency>
```

!!! warning
Make sure to shade the API into your app! You can use the [Shade plugin](https://imperceptiblethoughts.com/shadow/) for this.
!!!

## Use the API

### Initialize the ApiClient

First you need to create an instance of the `ApiClient` class.

```java
ApiClient fancyAnalytics = new ApiClient("https://api.fancyanalytics.net", "", "YOUR API TOKEN");
```

### Send metric data

You can send metric data to the server using the record service.

```java
Record record = new Record("unique sender id", "project id", timestamp, new HashMap<>());
record.withEntry("metric name", "metric value");

fancyAnalytics.getRecordService().createRecord("project id", record);
```

### Send events

You can also send events to the server using the event service.

```java
Event event = new Event("event name", new HashMap<>());
event.withProperty("prop key", "prop value");

fancyAnalytics.getEventService().createEvent("project id", event);
```