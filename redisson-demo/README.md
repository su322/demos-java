# 本地模拟多实例 验证分布式锁

## 确保终端Java版本
````
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
````
## 第一个终端
````
java -jar redisson-demo/target/redisson-demo-0.0.1-SNAPSHOT.jar --server.port=8081
````
## 第二个终端
````
java -jar redisson-demo/target/redisson-demo-0.0.1-SNAPSHOT.jar --server.port=8082
````
## 用HTTP工具
````
GET http://localhost:8081/lock/test

GET http://localhost:8081/lock/test
````

## 其中一种结果
````
锁已获得，端口: 8081, 线程: http-nio-8081-exec-2, 获取锁耗时: 4 ms, 总耗时: 8004 ms

未获得锁，端口: 8082, 线程: http-nio-8082-exec-2, 获取锁耗时: 3010 ms, 总耗时: 3010 ms
````