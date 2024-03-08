# Phecda 设备驱动SDK

> phecda-device-sdk 是 Phecda 设备驱动SDK，提供设备驱动开发所需的接口和工具。该方案参考了edgexfoundary


### 添加依赖
```text
<dependency>
    <groupId>com.trionesdev.phecda</groupId>
    <artifactId>phecda-device-sdk</artifactId>
    <version>latest</version>
</dependency>
```

### 使用方法
1. 配置文件
2. 实现一个设备驱动
```java
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver;

public class VirtualDriver implements ProtocolDriver{
    
}
```
2. 注册设备驱动
```java
public void main(String[] args){
    ProtocolDriver virtualDriver = new VirtualDriver();
    DeviceDriver.bootstrap(args, "device-virtual", "0.0.1", virtualDriver);
}
```

### 上报数据格式
```json
{
  "version":"",
  "id":"",
  "deviceName":"", //设备名称（唯一）
  "productKey":"", //产品名称/ID
  "sourceName":"", //资源标记
  "ts": null, //时间戳
  "readings": {
    "identifier": {
      "valueType": "",
      "value": null,
      "units": "",
      "ts": null //时间戳
    }
  },
  "tags":{ //其他标签

  }
}
```