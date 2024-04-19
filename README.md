# logback-adapter

It's an adapter for nacos client logger. Detail see [alibaba/nacos#9860](https://github.com/alibaba/nacos/issues/9860).

## Usage:

Import logback-adapter into your application dependency.

For example: 
```xml
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>logback-adapter</artifactId>
            <version>${logback-adapter.version}</version>
        </dependency>
        <!--  nacos.client.version >= 2.2.1 -->
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>${nacos.client.version}</version>
        </dependency>
```

## Version adapter

|Adapter Version|Nacos Client Version|Logback Version|
|---------------|---------------|---------------|
|1.0.X| 2.2.1 ~ 2.3.X | 1.3.0 ~ latest |
|1.1.X| 2.4.0 ~ latest | 1.3.0 ~ latest |