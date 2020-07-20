# ip代理池
本项目其实就是个简单的代理服务器，经过我小小的修改。加了个代理池进来。
渗透、爬虫的时候很容易就会把自己ip给ban了所以就需要ip代理池了。
![ippool][1]
## 使用

1、启动ip代理池
先把这个项目跑起来
https://github.com/jhao104/proxy_pool  

2、启动代理服务器

默认监听8080  
java -jar proxyServer.jar

自定义监听端口  
java -jar proxyServer.jar 9090

3、设置代理    
设置好代理后你就发现每次请求的ip都不一样。

## 配置
简单说一下proxy_pool需要做的一些配置。   
1、如果有更好的代理网站，或者是你买了代理ip。可以在根目录的/fetcher/proxyFetcher.py里面自己写个方法去爬该方法需要以生成器(yield)形式返回host:ip，然后在根目录
的setting.py文件里面把方法名添加进去。  
2、redis默认装好是没密码的，需要修改setting.py,改为DB_CONN='redis://@127.0.0.1:6379/0'一般全部默认即可   
3、修改超时，把setting.py中的VERIFY_TIMEOUT改小点(3)。默认10秒，这种ip基本用不成。

## 最后
博客：http://www.safe6.cn/      
公众号：safe6安全的成长日记        
项目地址：     
整合好的我已经打包放公众号，需要的自取(回复：ip代理池)    
![safe6][2]

## 感谢开源项目
https://github.com/casparhuan/proxyServer             
https://github.com/jhao104/proxy_pool


  [1]: http://qiniu.safe6.cn/ipPool.jpg
  [2]: http://qiniu.safe6.cn/qr2.png
