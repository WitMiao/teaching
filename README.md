# Teaching 在线教学平台 v2.8

===============

## 项目介绍

Teaching针对机构、学校提供STEAM在线教育解决方案， 提供一个低成本试错的机会。

平台集成CRM系统、教务系统、作业系统、题库系统、赛事系统、社区系统、文章系统。并封装了常用的工具，如各种工具类、微信生态对接、支付对接等等。

[前往官网](http://teaching.vip)

[查看本开源版DEMO](http://open.teaching.vip)

## QQ交流群 ：  191723983

## 开源版功能

```
├─首页
├─社区
├─创作
│  ├─Scratch
│  ├─Python
│  ├─ScratchJr
│  ├─Blockly
├─学生中心
│  ├─我的作品
│  ├─班级作业
│  ├─个人设置
│   ├─我的课程
│   │  ├─卡片形式
│   │  ├─地图形式
├─作业管理
│  ├─作品管理
│  ├─布置班级作业
├─课程管理
│  ├─课程管理
│  ├─课程单元管理
├─资讯管理
├─系统管理
│  ├─用户管理
|  ├─角色管理
│  ├─权限设置
│  ├─班级管理
│  ├─字典管理
│  ├─后台菜单管理
│  ├─前台菜单管理
│  ├─网站配置
│  ├─Scratch素材库
├─系统监控
│  ├─ ……
```

## 教学工具对接

- [Scratch2.0](https://github.com/open-scratch/scratch2) (已淘汰)

- [Scratch3.0](https://github.com/open-scratch/scratch3)

- [ScratchJr](https://github.com/open-scratch/scratchjr)

- [Python Turtle](https://github.com/open-scratch/teaching-python)

- [Blockly](https://github.com/google/blockly)
  
## 技术架构

后端技术： SpringBoot_2.1.3.RELEASE + Mybatis-plus_3.1.2 + Shiro_1.4.0 + Jwt_3.7.0 + Swagger-ui + Redis 

前端技术： Ant-design-vue + Vue + Webpack

其他技术： Druid（数据库连接池）、Logback（日志工具） 、poi（Excel工具）、Quartz（定时任务）、lombok（简化代码）、七牛云存储、Scratch二次开发

项目构建： Maven、Jdk8、npm或yarn

## 编译和部署教程

### [点击查看宝塔面板部署教程](https://www.213.name/%e5%ae%9d%e5%a1%94%e9%9d%a2%e6%9d%bf%e5%bf%ab%e9%80%9f%e9%83%a8%e7%bd%b2teaching%e5%bc%80%e6%ba%90%e6%95%99%e5%ad%a6%e5%b9%b3%e5%8f%b0/)

### 环境准备

以CentOS服务器为例，其他系统操作流程基本一样。

#### 安装mysql5.6

1. 略

2. 设置数据库表名忽略大小写（重要）

   `lower_case_table_names=1`

3. 导入api/db文件夹的sql文件。如果是升级，需要依次按版本号执行升级sql。

#### 安装 redis 6.0

略

#### 安装Java

CentOS系统可执行命令一键安装
`yum install -y java-1.8.0-openjdk`

#### 安装Nginx

略

#### 注册配置七牛云

- 登录 www.qiniu.com 注册后实名认证
- 新建对象存储Kodo，访问控制设为：开放，记录bucket名字和存储区域以备后续配置
- 绑定域名（免费分配的测试域名一个月后过期）
- 获取accessKey，secretKey以备后续配置

### 后端编译和部署

#### 修改配置

修改application.yml切换测试环境和线上环境，默认为线上环境，即使用application-prod.yml。

修改application-prod.yml

需要修改的地方：

```yml
domain: 您的站点域名
# 本地：local 七牛云：qiniu
uploadType: 文件存储目标
# 数据库连接配置
datasource:
    master:
      url: jdbc:mysql://127.0.0.1:3306/teachingopen?characterEncoding=UTF-8&useUnicode=true&useSSL=false&tinyInt1isBit=false
      username: teachingopen
      password: teachingopen
#Redis连接配置
redis:  
    database: 1
    host: 127.0.0.1
    password: ''
    port: 6379
#七牛配置
qiniu:
  accessKey: 您的七牛accessKey
  secretKey: 您的七牛secretKey
  bucketName: 您的七牛bucketName
  staticDomain: 您的七牛域名
  area: 您的七牛存储区域（z0：华东 z1：华北 z2：华南 na0：北美 as0：东南亚 cn-east-2：华东-浙江2）
```

配置文件可以编译后修改，推荐将.yml配置文件放到jar包同级目录，java将优先使用同级目录的配置，这样方便后续升级。

#### 编译项目（若使用已编译好的jar文件，本步骤可以跳过）

配置maven源

```xml
<mirrors>
       <mirror>
            <id>nexus-aliyun</id>
            <mirrorOf>*,!jeecg,!jeecg-snapshots,!getui-nexus</mirrorOf>
            <name>Nexus aliyun</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public</url>
        </mirror> 
 </mirrors>
```

在api目录执行

`mvn clean package`

编译成功后得到jar文件：\target\teaching-open-xxx.jar
(xxx为版本号)

- 上传jar文件到服务器，建议同时将yml配置文件也上传到jar同级目录

- 启动后端api并放入后台执行

  `nohup java -jar teaching-open-xxx.jar &`

  或者上传并接执行启动脚本
  `bash start-teaching.sh`


### 前端编译（若使用已编译好的前端，本步骤可以跳过）

- 安装nodejs版本v12
  
- 安装依赖
  `npm install` 或 `yarn install`

- 编译
  `npm run build` 或 `yarn run build`

### 前端部署

将编译后的dist文件夹上传至服务器网站根目录

- 配置Nginx

参考配置:

```
server
{
    listen 80 default_server;
    server_name open.teaching.vip; # 改为你网站的域名
    root /www/wwwroot/teaching-open; # 改为你网站目录的路径
    location / {
      index index.html index.htm;
      root /www/wwwroot/teaching-open; # 改为你网站目录的路径
      if (!-e $request_filename) {
          rewrite ^(.*)$ /index.html?s=$1 last;
          break;
      }
      gzip on;
      gzip_min_length 1k;
      gzip_comp_level 9;
      gzip_types text/plain application/javascript application/x-javascript text/css application/xml text/javascript application/x-httpd-php image/jpeg image/gif image/png;
      gzip_vary on;
      gzip_disable "MSIE [1-6]\.";
    }
    
    location ^~ /api
    {
        expires 0;
        proxy_pass              http://127.0.0.1:8080/api/;
        proxy_set_header        Host 127.0.0.1;
        proxy_set_header        X-Real-IP $remote_addr;
        proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
        add_header X-Cache $upstream_cache_status;
        add_header Cache-Control no-cache;
    }
}
```

### 测试账号

- admin —— 超级管理员
- teacher —— 老师
- student —— 学生
默认密码均为123456

## 常见问题

### 页面一直加载中

有两种可能：

1. api未启动，尝试访问http://ip地址:8080看是否有内容输出
2. nginx反向代理配置错误，特征是接口报502或504错误

### 后台上传的Scratch素材库不显示
自定义素材库，必须使用七牛云存储。默认使用的是本地素材库。
解决方案：
将本地素材库上传至七牛云，素材库位置在scratch3/static/internalapi，需要原来的保持目录结构，选择internalapi目录上传。

然后修改scratch3/index.html中的素材库配置地址切换为七牛云

```js
assets:{
  assetHost: getSysConfig('qiniuDomain')
}
```

### Scratch提交作品卡住

1. 七牛云配置错误，此时系统内其他上传也会失败
2. 页面停留时间过长导致登录失效，可以将Scratch文件保存到本地，刷新页面后再次提交
3. 网络问题

### 切换为本地存储

强烈建议使用云存储，极大减少服务器的宽带压力。但是有些朋友不想用七牛云存储，或者局域网部署，则可以使用本地存储模式。

修改application-prod.yml

```yml
jeecg:
  uploadType: local
  path :
  #文件上传根目录 设置
  upload: D://opt//upFiles
  # 文件访问地址
  staticDomain: /api/sys/common/static
```

切换云存储后，之前上传的文件请自行迁移。

### 升级系统的步骤

0. 准备编译好的api和web项目，可自行编译，也可下载编译好的

1. 执行升级sql

文件在api/db。如当前版本为2.4，需要依次执行update2.5.sql、update2.6.sql

2. 上传最新版jar包

如果yml配置文件放在jar包外，一般情况无需修改api配置。如果配置文件使用的是jar包内的，需要替换jar包内的配置文件。

3. 停掉原来的api程序

如果您的系统内没有其他正在运行的java程序，那么只需执行`pkill java`即可停止

4. 重启新版api

参考命令：

`nohup java -jar teaching-open-xxx.jar &`  

注意xxx是版本号，不要照抄！！

5. 上传覆盖前端文件