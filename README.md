# Mixin Client Java SDK

这里是 Mixin Client Java SDK，其它语言的 Mixin SDK：

- NodeJS: https://github.com/virushuo/mixin-node
- Go: https://github.com/MixinMessenger/bot-api-go-client
- Python: https://github.com/myrual/mixin_client_demo

更多 Mixin 开发资源：

- mixin_dev_resource: https://github.com/myrual/mixin_dev_resource
- MiXin_Player：https://github.com/albertschr/MiXin_Player

# User Guide  
## 本地打包  
1. 修改`src/main/java/one/mixin/api/Config.java`，填入你自己的帐号配置。
2. 在`src/main/resources`目录下新建`rsa_private_key.txt`文件，写入你的rsa私钥内容。
3. 切换到项目根目录，运行命令:
    > mvn clean package -Dmaven.test.skip=true
    
    在`target`目录下会生成`sdk-0.1-SNAPSHOT.jar`。　　　
4. 安装到本地maven库，运行命令:
    > mvn clean install -Dmaven.test.skip=true
    
    此时可以在本地工程中引用Mixin Client Java SDK。

## 如何使用　　
1. 添加Mixin Client Java SDK引用。以maven工程为例,修改`pom.xml`, 添加:
```
    <dependencies>
        <!--..省略...-->
        <dependency>
            <groupId>one.minxin</groupId>
            <artifactId>sdk</artifactId>
            <version>0.1-SNAPSHOT</version>
        </dependency>
        <!--..省略...-->
    <dependencies>
```

2. 在代码中使用: 
```
    Config config = new Config(); 
    MixinClient mixinClient = new MixinClient(config);
    
    // 读取assets:
    List<Asset> assets = mixinClient.readAssets();
```

# Release Note 
## Java SDK v0.3
当前版本v0.3，添加了[MixinNetwork api](https://developers.mixin.one/api/)实现。

## Java SDK v0.2

版本v0.2，主要功能是 Mixin 机器人：

- 发送文本消息、回复文本消息；
- 发送表情消息、回复表情消息；
- 确认收到文本、表情等消息（使得对方界面的消息确认由单钩变双钩）；
- 发送、共享联系人，发送、共享小程序及机器人。
- 获钱包余额、转账给他人。

下一版本 v0.3 即将添加的功能：

- 群聊

# 使用协议

- 本 Java SDK 开源、免费、无偿使用
- 唯一要求是当你用本 SDK 开发了 Mixin 小程序或机器人时，通过回复[这里](https://github.com/qige-one/mixin_java_sdk/issues/2)告知我一声。

# Powered By

以下小程序或机器人是基于本 SDK 开发（按回复给我的时间）：

- ![imgs/7000100071.png](imgs/7000100071.png)，7000100071，小冰MX，作者七哥。
