# Entity views

Annotation processor for JPA Entity views generation.

# Description

JPA 2.0 brought Entity graphs which allows you to specify multiple named fetch plans on entities. 
This solution has one very serious shortcoming. You can not tell your JPA provider, 
what fetch strategy (JOIN, SELECT, SUBSELECT) it should use for associations.
 
This project tries to address this problem by generating multiple Entities for the same SQL tables, 
which can be used in different cases for partial entity graph retrieval. 

## Requirements

- Java 11
- JPA Provider

## Release Notes

See [CHANGELOG.md](CHANGELOG.md)

## Install

Add the following dependencies to your Maven project:

```xml
<dependencies>

    <dependency>
        <groupId>cz.inqool.entityviews</groupId>
        <artifactId>entity-views-api</artifactId>
        <version>0.1.0</version>
    </dependency>
    
    <dependency>
        <groupId>cz.inqool.entityviews</groupId>
        <artifactId>entity-views-processor</artifactId>
        <version>0.1.0</version>
        <scope>compile</scope>
    </dependency>

</dependencies>
```

And now, configure the annotation processor:

```xml
<build>
    <plugins>
 
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5.1</version>
            <configuration>
                <annotationProcessors>
                    <annotationProcessor>
                        cz.inqool.entityviews.EntityViewsProcessor
                    </annotationProcessor>
                </annotationProcessors>
            </configuration>
        </plugin>
 
    </plugins>
</build>
```

# Usage

To start generating views for entities, add `@ViewableClass` annotation to your entity. 
Use the attribute `views` to specify all the views you want to generate.
 
If you want to skip some entity fields from specific views, add `@ViewableProperty` annotation
to the field and use `views` attribute to specify in which views, the field should be present.

The values of `views` attribute should be in snake case (e.g.: 'detail', 'person_detail').

```java
package cz.inqool.entityviews.simple;

import javax.persistence.Entity;

import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableProperty;

@ViewableClass(views = {"list", "detail"})
@Entity
public class Person {
    private String firstName;
    
    @ViewableProperty(views = "detail")
    private String address;
}
```

More complex examples can be found in unit tests.

## Authors

- **Matus Zamborsky** - Initial work

## Licence

The source codes are licenced with GNU Lesser General Public License v3.0 - 
see the [LICENCE.md](LICENCE.md) file for details.

