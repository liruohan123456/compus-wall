校园墙campus-wall实战项目



技术栈：Kotlin + Jetpack Room + Kotlin协程 + Flow + RecyclerView + DiffUtil



实现功能：

1\.账号注册/登录，多账号独立帖子数据

2\.校园墙帖子发布、删除、列表展示

3\.帖子点赞（乐观UI：先更新界面再异步写库）

4\.帖子评论

5.Room本地数据库持久化，App重启数据不丢失



项目架构：

视图层：Activity/Fragment 负责UI展示，通过观察Flow获取数据库实时数据

数据层：Room DAO接口完成本地增删改查，所有数据库操作在IO协程线程执行，不阻塞主线程

列表优化：RecyclerView  + DiffUtil 实现局部刷新，避免全量重绘，提升滑动流畅度

