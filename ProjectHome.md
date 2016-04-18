## _I hope you guys enjoy this gift. Happy new year!_ ##

## _本项目不定期更新，欢迎测试和使用！_ ##

**This project, [lange-monkey project](http://code.google.com/p/lange-monkey/) and [bingo-monkey project](http://code.google.com/p/bingo-monkey/) share one nick name: _Mr. Monkey_**

## 本项目主要采用Java开发，目前分为标准版和绿色版： ##
运行\*标准版\*程序需要在您的计算机上安装Java！而\*绿色版\*无需安装Java，但程序体积较大。

**点此图片安装Java  ===>**[![](http://java.com/en/img/everywhere/getjava_sm.gif)](http://java.com/zh_CN/download/index.jsp)

[点此链接检查是否安装了Java最新版本](http://java.com/zh_CN/download/installed.jsp)

---

### 最新通知 ###
1.**目前作者已暂停此项目的维护**，信息反馈渠道已被暂时关闭，其他功能仍可正常使用。由于作者精力有限，原有的一些已知Bug未能修复，如果再次出现类似问题，请自行寻找解决办法。谢谢大家的理解与支持！

2.本程序完全通过正常的方式获取服务器页面，发送的报文中均伪装成了常用浏览器，所有自动挂机行为均按随机时间间隔完成跳转，所以隐匿程度较高，服务器日志中不易于发现痕迹。不过在正确率计算上，由于偏向于高正确率，所以在较高正确率设置下，容易造成大面积100%，这比较危险！所以建议调低正确率设置！

3.再次高调声明：**本程序纯属个人娱乐或研究的作品，本无意外传**，现在的状况与本人毫无干系，所以请大家自负责任！

4.作者保留启动\*Monkey应急机制\*的权力，在极端情况下会封锁Monkey数据下载接口、上传答案接口、提交报告接口，甚至会考虑执行自我清除，敬请留意！

---

### 声明 ###
本程序的编写目的仅仅用于作者个人的技术研究与兴趣扩展，具体说是对Java网络编程、HTML解析等方面的实践探索，请不要滥用它或将它用于不正当的活动中！对由此造成的后果，作者本人概不负责！
### 版权 ###
为了更好地与大家交流技术，本程序遵循GPL V3协议，完全开放源代码，并提供有限的编译支持。任何人都可以从此处免费获取本程序的源代码及二进制程序。
### 参与 ###
如果您有兴趣，可以参与到本项目中，协助本项目的发展，具体请联系作者。
### 反馈 ###
希望您能及时将发现的BUG或自己的意见与建议通过程序内的提交窗口或邮件反馈至作者，作者会及时回复并积极改进程序。

---

# Latest Version : 2.6b #
## Download Now: [Win-32](http://nhce-monkey.googlecode.com/files/Mr.%20Monkey%202.6b.exe) | [Win-64](http://nhce-monkey.googlecode.com/files/Mr.%20Monkey%202.6b%28x64%29.exe) | [Win-32 Green](http://nhce-monkey.googlecode.com/files/Mr.%20Monkey%202.6b%20green.exe) ##
### File Size:  14.4 MB | 14.3 MB | 36.4 MB ###
注：1.目前Win-64版仅支持核心功能，部分新加入的扩展功能可能暂时不被支持
> 2.带有Green标签的为绿色版，绿色版的程序由于自带JVM，所以无需安装Java亦可使用，不过程序包体积较大！
# Data Packages Available : [10](http://code.google.com/p/nhce-monkey/downloads/list) #

---

## `===`Release Note`===` ##
### 2.6b ###
【注意！绿色版用户们，单击Monkey的升级方式存在bug，请至官网手动下载！否则无法找到下载的最新版程序文件！】

【本版仍为测试版，请及时上报遇到的bug！】

1.增加以下功能模块：

（1）Bug询问机制，在遇到问题时分析问题原因，主动询问是否为程序Bug，

> 并自动生成调试信息，添加到Bug报告中，以便进一步分析，更正Bug。

> （目前仅在NHCE中试用）

2.修复以下Bug：

（1）提交bug或建议时，报告发送失败。原因是Google的陌生IP验证导致发送被阻止，现已增加验证应对机制。

（2）NHCE Monkey的Quiz倒计时计算出错。原因是函数传参颠倒，导致计算结果为负数，现已更正。

（3）NHCE Monkey的Quiz托管模式中，提交中文为乱码。原因是POST请求前未进行字符集转换，现已更正。

3.尝试解决的Bug：

（1）NHCE Monkey的登陆问题。现已尝试修改登陆信息的解析方式，
> 针对用户名称和班级名称降低解析级别，以增强通用性。
### 2.6a ###
【本版为测试版，请及时上报遇到的bug！】

1.增加以下功能模块：

（1）NHCE Monkey增加Quiz托管模式，可以定时完成Quiz

（2）NHCE Monkey增加简易的断线重连机制，从而在网络环境较恶劣时，提供一定的挂机恢复能力

（3）增加源码浏览器，嵌入程序源代码，方便用户参考程序源码

（4）增强的主面板：

> a.增加“增强功能状态”按钮，图标的颜色指示增强功能的状态

> b.增加“版本信息”按钮，图标的颜色指示程序是否是绿色版

> c.增加“项目主页”按钮，可以打开项目主页

> d.增加“清理缓存”按钮，可以清理Monkey的缓存目录

> e.增加“更新日志”按钮，可以显示完整的更新日志

> f.增加“查看源码”按钮，可以打开源码浏览器

（5）增加绿色版的Monkey，自带JRE，不装Java照样使用！

2.修复以下bug：

（1）NHCE Monkey导出答案的路径定位出错

（2）NHCE Monkey上传Quiz答案时线程处理有误，导致上传失败

（3）NHCE Monkey从托盘状态恢复时，可能仍保留最小化状态

（4）Bingo Monkey提交记录列表中的内容预览存在数组越界漏洞

3.进行以下功能改进：

（1）NHCE Monkey在班级列表中列出Quiz时，如果Quiz未开始，显示出距离Quiz开始的时间；

> 如果Quiz正在进行，显示距离Quiz结束的时间

（2）NHCE Monkey在Quiz没有检测到答案时，提示自行完成并分享答案

（3）Lange Monkey挂机脚本更新，增加“超时强制恢复”特性，以保证持续有效挂机

（4）Lange Monkey挂机开始的提示窗口略作改变，显示更清晰，并增加“挂机技巧”提示

（5）Lange Monkey退出时检验冰果窗口状态，以防止当前所挂机时丢失

（6）Lange Monkey在进入带有互评作业的学习计划时，提示自行完成互评作业

（7）Bingo Monkey增加“紧急提交”按钮，但目前仅仅给出简单的提示

（8）所有Monkey缓存从%TEMP%转移到%APPDATA%，防止被其他软件清理。

（9）所有Monkey在初始化迅雷引擎和连接更新服务器时，显示“正在初始化”，以防止用户多次点击更新按钮

（10）所有Monkey中按下“显示许可”按钮时，提示正在读取，防止发生重复加载

（11）更新了程序加载画面，动态显示加载状态，带来更好体验
### 2.5c ###
1.增加以下功能模块：

（1）万众瞩目的NHCE Monkey的Quiz功能！

> （包括检测，进入，分析，答案的提取、分享、查询、一键提交！）

2.修复以下bug：

（1）Win-64由于32位dll加载失败而导致程序崩溃

（2）记录文件不匹配时的意外崩溃

（3）Lange Monkey由于蓝鸽系统升级导致的登陆失败

（4）Bingo Monkey显示的提交记录顺序颠倒

（5）Bingo Monkey不响应批改过的作文，不显示批改结果

（6）源码中泄露隐私信息

（7）升级打包工具到最新版，修复旧版的漏洞

（8）Win-64自动升级时匹配下载地址失败

3.进行以下功能改进：

（1）所有Monkey增加问候语，会说更多话，亲！

（2）NHCE Monkey增加禁止弹出托盘气泡的设置选项

（3）Lange Monkey可以利用有道api进行实时翻译并发音

（4）Bingo Monkey显示提交记录改为采用弹出对话框的方式，防止覆盖已输入的内容

（5）Bingo Monkey在提交后自动检查并显示提交记录

（6）Bingo Monkey增加编辑记忆功能，自动记录最近输入内容，程序崩溃后仍可恢复

（7）Bingo Monkey左侧“题目要求”放大了字体

（8）Bingo Monkey编辑框字体换成了微软雅黑

（9）所有Monkey的字典工具支持回车键查词

（10）使用打包工具支持的splash图片来美化启动过程

（11）提交bug时提示采用加密连接

> （同志们，发现Bug请及时提交，SSL加密传输，不会暴露你的身份的！）

（12）组件模块化处理，将功能性组件分拆为独立jar

> （包括JThunder,JMSC,JSoundTouch,JLanguageTool,JHanspell）


### 2.5beta ###
【该版本为测试版，测试已经结束，下载内容已被删除，请下载新版！】

【注意：2.4-6中发现存在迅雷调用失败导致无法自动更新的Bug】

【如果您目前版本是2.4-6，请去官网`http://code.google.com/p/nhce-monkey`手动下载！】

1.增加以下功能模块：

（1）许可证及引用项目信息专用窗口

（2）Bingo Monkey增加来自Open Office的拼写检查

（3）Bingo Monkey增加简单的语法检查

（4）集成了最新科技的“萌属性”（语音功能）

（5）NHCE Monkey增加Quiz模块（未全部完成，目前仅用于测试！）

2.修复以下bug：

（1）迅雷开放下载引擎调用失败（由于dll缺少定向）

3.进行以下功能改进：

（1）入口界面采用简单特效进行了优化

（2）Bingo Moneky提交部分增加作文字数检查
### 2.4-6 ###
【该版本由于bug级别较高，下载内容已被删除，请下载新版或保留旧版！】

1.修复以下bug

（1）修复了几个极其微小的bug

2.进行以下功能改进

（1）程序升级时调用迅雷开放下载平台，以加速升级

> （需要安装迅雷7，如果没有安装将使用旧版方式升级）

（2）已将生成exe的提示信息文本翻译为中文
### 2.4-5 ###
1.修复以下bug

（1）下载数据包时阻塞Swing线程

（2）提交报告的输入框没有自动换行

2.进行以下功能改进

（1）支持兼容旧版（2.0）的数据记录

（2）提交报告的内容添加信息头，包括：程序名、版本、Java版本、操作系统版本
### 2.4-4 ###
1.修复以下bug

（1）11级登录失败
### 2.4-3 ###
1.进行以下功能改进

（1）增加报告提交按钮，以方便用户提交Bug或建议
### 2.4-2 ###
1.修复以下bug

（1）升级中断时临时文件没有删除，反而命名为目标文件名

（2）默认focus设置有误

（3）第2单元Reading Skills标准答案解析失败

（4）提示升级的内容在日志里的格式不正确

2.进行以下功能改进

（1）密码框回车登陆
### 2.4-1 ###
1. Add binding with [bingo-monkey project](http://code.google.com/p/bingo-monkey/)

2. fixed some tiny bugs
### 2.4 ###
1. Add auto-update function

2. fixed some tiny bugs
### 2.3-1 ###
1. Some tiny but important bugs fixed.
### 2.3 ###
1. Add binding with [lange-monkey project](http://code.google.com/p/lange-monkey/)

2. Many important bugs fixed
### 2.2-1 ###
1. Some tiny but important bugs fixed.
### 2.2 ###
1. Support Data Sharing based on the **"Cloud"** concept! Now you can submit what you have got and get what other users have got!

2. The default right-answer-rate is changed to 70%-100%

3. Some bugs fixed.
### 2.1 ###
1. Support auto-making mistakes! 4 types of mistakes available: abc->ab; abc->abd; abc->(empty); abc def-> def abc.

2. Some bugs fixed.
### 2.0 ###
1. First release with basic functions available: log in; view forms; fill answers; import/export answers; submit forms; view user info; look up online dictionaries; check update