[TOC]

# 前言

## 1-5 整合Sleuth追踪调用链路

- 创建 sleuth-traceA 和 sleuth-traceB, 添加 Sleuth 依赖
- 调用请求链路, 查看log中的信息
- 采样率设置
- 启动测试
  - EurekaServerApplication :20000/
  - SleuthTrace1Application :62000/
  - SleuthTrace2Application :62001/
  - GET http://localhost:62001/traceB  | 查看log
  - GET http://localhost:62000/traceA  | 查看log, 会出现 [里面某些字符串一致]

<br>

[GitHub](https://github.com/eddie-code) <br>
[博客园](https://www.cnblogs.com/EddieBlog) <br>
[CSDN](https://blog.csdn.net/eddielee9217) <br>
[自建博客](https://blog.eddilee.cn/s/about) <br>