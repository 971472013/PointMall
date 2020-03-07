# 积链商城
### 以下仅为本次项目开发的记录信息，涉及服务器内容请以自己使用的服务器为准


## admin
商城B端。其用户为user。基于vue-element-admin框架开发。

admin [部署地址](http://www.pointxmall.com/admin/)

[vue-element-admin参考](https://panjiachen.github.io/vue-element-admin-site/zh/)

```
构建命令：npm run build:prod
```

## mall
商城C端。其用户为customer。基于Vant框架开发。

mall [部署地址](http://www.pointxmall.com/mall/#/index?mallID=5e5fb17d3d02f55f08e25f12)(由于需要载入商城信息，数据库的更新可能导致链接无效）

[vant参考](https://youzan.github.io/vant/#/zh-CN/)

```
构建命令：npm run build
```

## server
服务器上运行的服务端。使用springboot+MongoDB开发，结合阿里云服务实现短信验证码发送、图片上传、图床等功能。

## 数据库
```
mongo启动指令：mongod --bind_ip 0.0.0.0 --auth --fork --logpath=/data/mongodb.log 
（0.0.0.0代表所有ip地址都可以通过MongoDB账号密码的认证访问这个数据库）

server配置文件中的MongoDB信息：spring.data.mongodb.uri=mongodb://root:root@localhost:27017/server?authSource=admin&authMechanism=SCRAM-SHA-1
（即MongoDB账号为root、密码为root、认证数据库为admin）
本项目中使用的MongoDB下数据库为server
```

## 部署信息
server运行在服务器端 8080 端口。

admin、mall都运行在服务器端 80 端口，使用 Nginx 作为静态资源服务器并实现请求的代理转发。

```
Nginx命令：
编辑Nginx服务器配置：sudo vim /etc/nginx/nginx.conf
刷新配置信息：nginx -s reload
查看Nginx运行状态：netstat -ano | grep 9528
```

admin：构建后将用目录下的dist文件夹内的内容替换服务器上 /data/admin/文件夹下的内容即可。

mall：构建后将用目录下的dist文件夹内的内容替换服务器上 /data/mall/文件夹下的内容即可。

server：使用maven构建服务端代码，本例更改使用构建结果文件名为：app.jar
```
运行命令：nohup java -jar app.jar >/dev/null 2>&1 &
```

## 其他
```
icon文件夹为图标处理内容
icon/before为处理前的图片，为阿里矢量图库下载的透明背景图
icon/after为处理后的图标，背景颜色为白色
icon/white.py为图标处理的程序

图标替换：https://www.jianshu.com/p/8379597e3f97

跟随商城主题的图标变色
https://www.cnblogs.com/coco1s/p/8080211.html
https://blog.csdn.net/comeonbabe_/article/details/89266810
```