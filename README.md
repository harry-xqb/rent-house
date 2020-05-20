# Rent-House(开源租房网站后端模板)

## 本项目现处于开发阶段，后期预计实现如下功能:
1. 管理端: 房源CRUD
2. 用户端: 地铁找房，地图找房， 区间价格找房，在线看房，VR看房等。
3. 管理员与用户实时聊天
4. 预约看房

### 所用框架: 
1. spring boot
2. spring security （权限控制）
3. spring data jpa 
4. spring data elasticsearch （全文搜索）
5. kafka (消息队列->进行异步索引)
6. hadoop + spark (大数据框架，实现推荐房源)
### 环境
数据库: mysql  
缓存: redis  
全文搜索引擎: elasticsearch
elasticsearch可视化工具: kibana
jdk: 1.8  
### [前端源码点这里](https://github.com/night-233/rent-house-admin)
