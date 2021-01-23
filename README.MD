# HamsterService

JavaDoc 将在之后不久提供

# 项目简介

## HamsterService-Bukkit

HamsterService 的 Bukkit 组件，该组件用于安装在 Bukkit 服务端

## HamsterService-Proxy

HamsterService 的 Proxy 组件，该组件用于安装在 BungeeCord 服务端

## HamsterService-Server

HamsterService 的 转发中心 组件，该组件应该单独启动  
为其他安装了该插件的服务端提供跨服通信的支持

# 开发者

## 1.依赖导入

开发者可以使用以下代码快速导入 HamsterService-BungeeCord 依赖

### Maven

```xml

<project>
    <repositories>
        <repository>
            <id>hamster3</id>
            <url>http://maven.hamster3.cn/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>cn.hamster3</groupId>
            <artifactId>HamsterService-Bukkit</artifactId>
            <version>2.0.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>

```

### Gradle

```groovy
    repositories {
    maven { url 'http://maven.hamster3.cn/repository/maven-public/' }
}
dependencies {
    implementation 'cn.hamster3:HamsterService-Bukkit:2.0.0-SNAPSHOT'
}

```

## 2.订阅标签

一般情况下，所有消息将会发送至服务中心，然后由服务中心负责分发这些消息  
当子服订阅了某个 Tag 时，若其他服务器发送了该Tag的标签，则该子服将会收到

```java
public class Test extends JavaPlugin {
    public void onEnable() {
        // 订阅消息
        ServiceMessageAPI.subscribeTag("TestTag");

        // 取消订阅消息
        ServiceMessageAPI.unsubscribeTag("TestTag");
    }
}

```

## 3.监听消息事件

当订阅了该标签之后，你可以使用一个监听器来监听 MessageReceivedEvent 事件  
值得注意的是，所有 HamsterService 消息都会触发该事件，所以你应该在自己的插件内过滤一下 Tag  
**只有订阅了标签才有可能收到该标签的事件！**

```java
public class TestListener implements Listener {
    @EventHandler
    public void onMessageReceived(MessageReceivedEvent event) {
        ServiceMessageInfo info = event.getMessageInfo();
        if (!"TestTag".equals(info.getTag())) {
            // 不是我们想要的标签
            return;
        }
        switch (info.getAction()) {
            case "kickAllPlayer": {
                String message = info.getContent().getAsString();
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.disconnect(message);
                }
            }
            case "kickPlayer": {
                JsonObject content = info.getContent().getAsJsonObject();
                UUID uuid = UUID.fromString(content.getAsString());
                String message = content.get("message").getAsString();
                ProxyServer.getInstance().getPlayer(uuid).disconnect(message);
            }
            case "tpPlayer": {
                String[] args = info.getContent().getAsString().split(" ");
                UUID player = UUID.fromString(args[0]);
                UUID toPlayer = UUID.fromString(args[1]);
                //执行传送操作
            }
        }
    }
}

```

## 4.发送消息

要想使用 HamsterService 发送跨服消息很简单，你只需要使用 ServiceMessageAPI 类即可

```java
public class Test {
    public void test() {
        // 可以发送字符串
        ServiceMessageAPI.sendMessage("TestTag", "kickAllPlayer", "你已被服务器踢出!");
        // 也可以使用占位符替换
        ServiceMessageAPI.sendMessage("TestTag", "tpPlayerTo", "%s %s", "PlayerA", "PlayerB");
        // 还可以使用JsonObject
        JsonObject object = new JsonObject();
        object.addProperty("uuid", "4ce4a7ff-f9e7-32af-9c15-ad176dbecf5a");
        object.addProperty("reason", "你已被服务器踢出!");
        ServiceMessageAPI.sendMessage("TestTag", "kickPlayer", object);
    }
}

```

消息发送后，所有订阅了该标签的服务器将会收到事件，并由对应的监听器处理

## 5.其他内容

插件的主要API都集中在 ServiceInfoAPI 和 ServiceMessageAPI 两个类，具体内容请查阅开发文档  
在使用 ServiceMessageAPI.sendMessage() 时，tag一般使用插件名称，action一般用于动作判断（即收到该消息后该如何处理），content一般用于附加信息（具体看上面的例子就知道了）  
content可以存储任何Json支持的数据：int、long、字符串、JsonObject、JsonArray等等  
由于插件为了方便快捷（以及为了照顾强迫症患者）去除了很多繁琐的代码判断，所有的类型判断由人为检查  
因此请注意收发信息时的content格式，若使用不当则可能会引发事件报错  
（↑比如你明明发消息的时候写的是个字符串，读取的时候却`getAsJsonObject()`）  