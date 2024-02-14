<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>${basePackage}</groupId>
  <artifactId>${name}</artifactId>
  <version>${version}</version>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <hutool.version>5.8.25</hutool.version>
    <picocli.version>4.7.5</picocli.version>
    <freemarker.version>2.3.32</freemarker.version>
    <apache.commons-collections4.version>4.4</apache.commons-collections4.version>
    <lombok.version>1.18.28</lombok.version>
    <junit.version>4.13.2</junit.version>
    <maven-checkstyle-plugin.version>3.2.0</maven-checkstyle-plugin.version>
  </properties>

  <dependencies>
    <!-- https://doc.hutool.cn -->
    <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-all</artifactId>
      <version><#noparse>${hutool.version}</#noparse></version>
    </dependency>

    <!-- https://picocli.info -->
    <dependency>
      <groupId>info.picocli</groupId>
      <artifactId>picocli</artifactId>
      <version><#noparse>${picocli.version}</#noparse></version>
    </dependency>
    <!-- https://freemarker.apache.org/index.html -->
    <dependency>
      <groupId>org.freemarker</groupId>
      <artifactId>freemarker</artifactId>
      <version><#noparse>${freemarker.version}</#noparse></version>
    </dependency>

    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-collections4</artifactId>
      <version><#noparse>${apache.commons-collections4.version}</#noparse></version>
    </dependency>

    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version><#noparse>${lombok.version}</#noparse></version>
    </dependency>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version><#noparse>${junit.version}</#noparse></version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.3.0</version>
        <configuration>
          <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
          <archive>
            <manifest>
              <!-- 替换主类 -->
              <mainClass>${basePackage}.Main</mainClass>
            </manifest>
          </archive>
        </configuration>
        <executions>
          <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
              <goal>single</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>