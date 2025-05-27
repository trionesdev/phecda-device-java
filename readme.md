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

public class VirtualDriver implements ProtocolDriver {

}
```

2. 注册设备驱动

```java
public void main(String[] args) {
    ProtocolDriver virtualDriver = new VirtualDriver();
    DeviceDriver.bootstrap(args, "device-virtual", "0.0.1", virtualDriver);
}
```

### 上报数据格式

##### 范例

```json
{
  "deviceName": "device_0001",
  "id": "9cb5fe7f-64e9-4c89-b2ab-c90053f69166",
  "productKey": "trionesdev-test001",
  "readings": {
    "temperature": {
      "ts": 1731814271938,
      "value": "1",
      "valueType": "Int"
    }
  },
  "tags": {
    "env": "test"
  },
  "ts": 1731814271938,
  "type": "property",
  "version": "v1"
}
```

##### 字段说明

| 属性                           | 说明                      | 类型                             | 默认值        |
|------------------------------|-------------------------|--------------------------------|------------|
| version                      | 版本号                     | String                         |            |
| id                           | 信息ID                    | String                         |            |
| type                         | 消息类型`property`          | String                         | `property` |
| deviceName                   | 设备名称(全局唯一)              | String                         |            |
| productKey                   | 产品KEY                   | String                         |            |
| productKey                   | 产品KEY                   | String                         |            |
| ts                           | 时间戳                     | Long                           |            |
| readings                     | 内容 Map 类型，key 就是属性的name | Map<String,Map<String,Object>> |            |
| readings\[key\]              | 属性name                  | String                         |            |
| readings\[key\]\[ts\]        | 属性时间戳                   | Long                           |            |
| readings\[key\]\[value\]     | 属性值                     | String                         |            |
| readings\[key\]\[valueType\] | 属性值类型 `Int`\|`String`   | String                         |            |


#### 互相吹捧，共同进步
> 留言回复不及时，可以通过关注公众号联系我们
<div style="width: 100%;text-align: center;">
   <img src="images/shuque_wx.jpg" width="200px" alt="">
</div>
