数据权限
---
[用户组](AuthGroup.md)实现了[账户](AuthUser.md)可以进行哪些操作。数据权限实现了账户可以看到数据的多少。

权限系统中已经做了数据权限的实现。每个账户登录权限系统后只能查看由自己创建的数据，
如果账号想要查看到其他账号创建的数据时，就需要使用到数据权限的配置。
数据权限对应的模块是**部门管理**；**部门**是具有层级性，通过这种特性将账号进行了等级的划分。
高等级的**部门**是可以查看低等级**部门**的数据；同级**部门**之间的数据不共享。

业务系统中如果需要实现数据权限时，需要在用户创建数据时记录账号的uuid。
用户在登录时，系统会将他子集部门中的人员的UUID一同返回；业务系统在查询数据时加入返回的uuid到查询条件中，
达到数据权限的目的

### 更多
>   **权限管理**
>   *   [账户](docs/AuthUser.md)
>   *   [用户组](docs/AuthGroup.md)
>   *   [权限值](docs/AuthRule.md)
>   *   [数据权限](docs/Department.md)
>
>   **版本控制**
>   *   [版本](docs/Version.md)
>   *   [序列号](docs/License.md)
>
>   **菜单管理**
>   *   [菜单](docs/Menu.md)
>   *   [序列号](docs/License.md)