# netty-chat中文注释版

添加注释和笔记，供自己阅读学习之用。(@[vectoryao](https://github.com/VectorYao))

### Protocol:

| PacketLen  | Header  | Actual Content |
| :----: |:-------:| :-------------:|
| 4byte  | 12byte   |   data  |

###  Packet:
- PacketLen
- HeaderLen
- Version
- Operation
- SeqId
- Body

### Architecture:

<img src="https://github.com/VectorYao/netty-chat/blob/master/docs/architecture.png" width="500">
<img src="https://github.com/VectorYao/netty-chat/blob/master/docs/proto.png" width="800">

### Operation

    AuthOperation -> decode<AuthToken> -> AuthService （登录授权的流程）
    MessageOperation -> decode<Message> -> MessageService （消息服务的流程，其中涉及到MQMessageListener的消息转发功能）

### MessageQueue

    MQMessage
    MQProducer
    MQConsumer
    MQMessageListener

### Run & Test

个人推荐使用Intellij IDEA来导入项目，直接可以运行。

首先，程序主入口在`wiki.tony.chat.comet.ChatApplication.java`中，运行main函数即可开启支持tcp协议和webSocket协议的服务器；

然后，分别打开客户端1（`wiki.tony.chat.comet.client.ChatClientOne.java`）和客户端2（`wiki.tony.chat.comet.client.ChatClientTwo.java`）;

最后，根据消息格式[消息接收方id:消息内容content]，输入消息的接收方id和消息内容。

服务器端控制台截图：

<img src="https://github.com/VectorYao/netty-chat/blob/master/docs/nettychat1.jpg" >

客户端1控制台截图：

<img src="https://github.com/VectorYao/netty-chat/blob/master/docs/nettychat2.jpg">

客户端2控制台截图：

<img src="https://github.com/VectorYao/netty-chat/blob/master/docs/nettychat3.jpg">
    
实现了客户端1和客户端2的类似于QQ机制的双工通信。
    
### License
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.