# logback-adapter

It's an adapter for nacos client logger. Detail see [alibaba/nacos#9860](https://github.com/alibaba/nacos/issues/9860).

## Usage:

Import logback-adapter into your application dependency.

For example: 
```xml
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.5</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>logback-adapter</artifactId>
            <version>1.0.0</version>
        </dependency>
        <!--  nacos.client.version >= 2.2.1 -->
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>${nacos.client.version}</version>
        </dependency>
```