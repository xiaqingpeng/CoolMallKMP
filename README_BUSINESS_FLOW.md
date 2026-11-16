# CoolMallKMP 业务流转说明

CoolMallKMP 是由 `CoolMallKotlin` 迁移而来的 Android/iOS 双端 Compose Multiplatform 项目。

业务页面主要在 `shared/src/commonMain` 中复用，Android 和 iOS 只保留入口、平台能力以及平台 SDK 适配。当前支付、微信/QQ 登录绑定等第三方 SDK 能力按迁移约定暂不接入。

## 1. 项目分层

```text
androidApp/                         Android 应用入口、系统主题、通知和平台实现
iosApp/                             iOS 应用入口、Info.plist、AppIcon 和平台容器
shared/src/commonMain/
  core/
    data/                            网络、Repository、本地存储、Room、DataStore
    database/                        Room 数据库和 DAO
    model/                           请求、响应、实体模型
    network/                         Ktor HttpClient、鉴权、日志、错误处理
    platform/                        expect/actual 平台能力
    session/                         登录会话和 Token 状态
    ui/                              公共组件、主题、骨架、弹窗、按钮
  feature/
    launch/                           Splash 和引导页
    main/                             首页和底部导航
    auth/                             登录、注册、短信登录、重置密码
    goods/                            分类、搜索、商品详情、评论
    cart/                             购物车
    order/                            确认订单、订单列表、详情、物流、支付
    user/                             个人中心、资料、地址、足迹
    market/                           优惠券
    feedback/                         意见反馈
    cs/                               在线客服
    common/                           设置、关于、协议、WebScreen
  ui/CoolMallApp.kt                   全局主题和 NavHost
```

## 2. 应用启动链路

```text
系统启动
  ├─ Android: MainActivity
  │    ├─ installSplashScreen()
  │    ├─ enableEdgeToEdge()
  │    ├─ 初始化 AppContainer、网络和平台能力
  │    └─ setContent { CoolMallApp() }
  │
  └─ iOS: CoolMallApp
       └─ ContentView -> ComposeView -> MainViewController()

CoolMallApp
  └─ Splash
       ├─ restoreSession()
       ├─ 读取 showSplash
       ├─ 等待启动动画最小展示时间
       └─ 读取 guide_shown
            ├─ 未展示过 -> Guide
            └─ 已展示过 -> Main
```

### 2.1 Android 启动页

Android 存在两层启动表现：

1. Android 12 及以上系统 SplashScreen API。
2. Compose 共享层中的 `feature/launch/SplashScreen.kt`。

系统 Splash 负责应用进程启动期间的首帧，Compose Splash 负责品牌 Logo、文字和动画。系统启动页资源位于：

```text
androidApp/src/main/res/values-v31/themes.xml
androidApp/src/main/res/values-night-v31/themes.xml
androidApp/src/main/res/drawable/ic_launcher_background.xml
androidApp/src/main/res/drawable-v24/ic_launcher_foreground.xml
```

### 2.2 iOS 启动页

iOS 的应用入口在：

```text
iosApp/iosApp/iOSApp.swift
iosApp/iosApp/ContentView.swift
```

iOS 进入共享 Compose 的 `MainViewController()` 后，和 Android 使用同一套 Splash、引导和主业务路由。iOS 的 AppIcon、应用名称和包信息仍由 Xcode 工程及 `Info.plist` 管理。

## 3. 引导页链路

```text
Splash
  └─ Guide
       ├─ 左右滑动浏览 4 个引导页
       ├─ 点击“继续”进入下一页
       ├─ 点击“跳过”结束引导
       └─ 最后一页点击“开始体验”
            ├─ 正常首次启动 -> Main
            └─ 从设置页重新查看 -> 返回 Settings
```

引导完成后会持久化 `guide_shown=true`。下次冷启动会跳过引导直接进入首页。

相关代码：

```text
shared/src/commonMain/kotlin/com/joker/coolmall/feature/launch/
shared/src/commonMain/kotlin/com/joker/coolmall/core/data/local/GuidePreferences.kt
shared/src/commonMain/kotlin/com/joker/coolmall/core/data/local/SplashPreferences.kt
```

## 4. 主框架和底部导航

```text
Main
  ├─ 首页
  ├─ 分类
  ├─ 购物车
  └─ 我的
```

首页提供以下业务入口：

```text
搜索
分类/专题
商品详情
优惠券
订单
收货地址
足迹
在线客服
意见反馈
关于
设置
用户协议
隐私协议
GitHub/Web 页面
```

主页面通过 `AuthSession.token` 判断是否已登录。未登录访问需要账号的功能时，统一跳转登录页。

## 5. 登录和会话链路

```text
未登录业务入口
  └─ Login
       ├─ 账号密码登录
       │    └─ POST user/login/password
       ├─ 手机验证码登录
       │    ├─ 获取短信验证码
       │    ├─ Android 请求通知权限并展示验证码通知
       │    └─ POST user/login/phone
       ├─ 注册
       └─ 重置密码
            ├─ 图形验证码
            ├─ 短信验证码
            └─ 最终重置接口待后端接口确认

登录成功
  ├─ 保存 token 和 refreshToken
  ├─ 更新 AuthSession
  ├─ 后续请求自动添加 Authorization
  └─ 返回原业务入口或登录后的目标页面
```

启动时 `SplashViewModel` 会尝试恢复会话。退出登录时应清理 Token、用户信息和相关会话状态。

当前明确未接入：

- 微信登录和绑定 SDK。
- QQ 登录和绑定 SDK。
- 支付 SDK。
- 重置密码最终提交接口。

## 6. 商品浏览和搜索链路

### 6.1 分类

```text
首页/底部导航 -> 分类
  ├─ 分类列表
  ├─ 分类商品列表
  ├─ 推荐/精选商品
  └─ 商品卡片 -> 商品详情
```

### 6.2 搜索

```text
首页搜索入口 -> 搜索页
  ├─ 输入关键词
  ├─ 读取搜索历史
  ├─ 读取热门搜索
  ├─ 选择筛选条件
  │    ├─ 分类
  │    ├─ 价格区间
  │    └─ 排序
  ├─ 请求搜索商品分页
  └─ 商品结果 -> 商品详情
```

搜索历史当前以 Room 为主，保留 DataStore 兼容导入逻辑。搜索结果、历史和热门搜索属于不同数据来源，页面展示时应分别处理加载、空数据和错误状态。

## 7. 商品详情链路

```text
商品卡片 -> 商品详情
  ├─ 商品图片轮播
  ├─ 价格、库存、销量和基础信息
  ├─ 优惠券弹窗
  ├─ 规格选择弹窗
  ├─ 商品评论
  ├─ 加入购物车
  └─ 立即购买
       └─ 确认订单
```

加入购物车完成后回到商品详情并刷新购物车数量。立即购买会携带当前商品、规格和数量进入确认订单页。

## 8. 购物车链路

```text
底部导航 -> 购物车
  ├─ 加载购物车商品
  ├─ 单选/取消选择
  ├─ 全选/取消全选
  ├─ 增加或减少数量
  ├─ 编辑模式
  ├─ 删除商品
  └─ 结算
       └─ 确认订单
```

确认订单只处理已选中的购物车商品。购物车商品数量、选中状态和总价变化后，需要同步更新底部结算栏。

## 9. 地址和确认订单链路

```text
确认订单
  └─ 选择收货地址
       └─ 地址列表（选择模式）
            ├─ 点击地址卡片 -> 返回确认订单并更新地址
            ├─ 点击编辑 -> 地址编辑页
            ├─ 点击删除 -> 删除地址并刷新列表
            └─ 新增地址 -> 地址编辑页
```

普通地址列表和订单选择地址列表共用地址数据，但交互模式不同：

- 普通模式：点击编辑按钮进入编辑页。
- 选择模式：点击整张地址卡片选择地址并返回订单确认页。

地址编辑支持：

```text
收货人
手机号
省/市/区三级地区选择
详细地址
默认地址
新增、修改、删除
```

地区数据使用完整的省、市、区县数据，地区选择由 `RegionPickerModal` 完成。

## 10. 订单链路

```text
购物车/商品详情 -> 确认订单
  ├─ 选择地址
  ├─ 查看商品和优惠
  ├─ 提交订单
  └─ 支付
       ├─ Android 支付能力按约定暂不迁移
       └─ iOS 支付能力按约定暂不迁移

提交订单后
  ├─ 支付结果页
  ├─ 订单详情
  └─ 订单列表
       ├─ 全部
       ├─ 待付款
       ├─ 待发货
       ├─ 待收货
       ├─ 待评价
       └─ 售后/退款
```

订单详情还可以进入：

```text
物流
评价
退款/售后
再次购买或继续支付
```

订单列表的不同 Tab 保留独立分页和缓存状态，不能简单合并成一个普通列表状态机。

## 11. 我的和用户资料链路

```text
底部导航 -> 我的
  ├─ 未登录
  │    └─ 点击个人区域或需要登录的入口 -> Login
  └─ 已登录
       ├─ 个人资料
       ├─ 我的订单
       ├─ 收货地址
       ├─ 优惠券
       ├─ 足迹
       ├─ 在线客服
       ├─ 意见反馈
       └─ 设置
```

个人资料页包含：

```text
头像、昵称、手机号
账号安全
微信绑定（SDK 未接入）
QQ 绑定（SDK 未接入）
修改密码
注销账号
```

未登录状态下，“我的”页的用户区域和受保护入口必须跳转登录页，不能进入空白业务页。

## 12. 设置、关于和协议

```text
设置
  ├─ 主题模式
  ├─ 主题颜色
  ├─ 语言展示
  ├─ 用户协议
  ├─ 隐私协议
  ├─ 隐藏/显示品牌启动页
  ├─ 重新查看引导
  ├─ 清除缓存
  ├─ 意见反馈
  └─ 关于
```

协议页使用共享 WebScreen：

```text
用户协议/隐私协议 -> WebScreen
  ├─ Android WebView
  ├─ iOS WKWebView
  ├─ 动态网页标题
  ├─ 加载状态
  ├─ 刷新
  └─ 返回
```

Android 和 iOS 的 WebView 容器不同，但页面入口、URL、标题和返回逻辑由共享层统一。

## 13. 意见反馈链路

```text
我的/设置 -> 意见反馈列表
  ├─ 登录校验
  ├─ POST feedback/page
  ├─ 查看历史反馈
  └─ 提交反馈
       ├─ 输入反馈内容
       ├─ 选择图片
       ├─ 上传图片
       ├─ 提交反馈
       └─ 返回列表并刷新
```

反馈列表必须携带当前登录 Token。未登录时服务端返回 401 属于正常鉴权行为，页面入口应在客户端先拦截。

## 14. 在线客服链路

```text
我的 -> 在线客服
  ├─ 加载历史消息
  ├─ 建立 WebSocket
  ├─ 接收实时消息
  ├─ 发送文本
  ├─ 发送图片
  └─ 断开页面时释放连接
```

客服消息采用倒序分页，和普通上拉列表的分页方向不同，因此使用独立的倒序分页状态。输入栏的部分扩展能力受原项目和平台能力限制。

## 15. 网络请求和数据流

```text
Screen
  -> ViewModel
     -> Repository
        -> Ktor DataSource
           -> API
```

典型请求流程：

```text
ViewModel 发起请求
  └─ Repository 组装 Request
       └─ Ktor HttpClient
            ├─ baseUrl: https://mall.dusksnow.top/app/
            ├─ 自动添加 Authorization
            ├─ 解析 code/message/data
            ├─ 处理 token 过期
            └─ 返回 UiState
```

列表页面通常包含：

```text
初始加载
下拉刷新
分页加载
空数据
加载错误
重试
```

公共 UI 层提供 `RefreshLayout`、`LoadMore`、骨架屏、`AppCard`、`ListItem` 等组件。不同业务页面仍可能保留专用分页状态，例如订单多 Tab 和客服倒序消息。

## 16. 本地数据和会话

```text
DataStore
  ├─ guide_shown
  ├─ show_splash
  ├─ 主题模式
  └─ 主题颜色

Room
  ├─ 搜索历史
  └─ 业务数据库实体

AuthSession
  ├─ token
  ├─ refreshToken
  └─ 当前用户状态
```

平台实现通过 `expect/actual` 分离：

```text
通知
权限
文件和图片选择
WebView/WKWebView
应用信息
系统分享
平台 SDK
```

## 17. 目前明确不在迁移范围内

以下功能不会伪造接口或 SDK 行为：

1. 微信登录和微信绑定 SDK。
2. QQ 登录和 QQ 绑定 SDK。
3. 微信/支付宝等支付 SDK。
4. 重置密码最终提交接口，等待后端确认正式 API。

除此之外，页面入口、鉴权、网络请求、商品浏览、搜索、购物车、地址、订单、反馈、客服和设置等公共业务链路都应在 Android/iOS 共享层保持一致。

## 18. 代码阅读入口

从业务入口开始阅读时，建议按以下顺序：

```text
shared/src/commonMain/kotlin/com/joker/coolmall/ui/CoolMallApp.kt
shared/src/commonMain/kotlin/com/joker/coolmall/ui/navigation/Routes.kt
shared/src/commonMain/kotlin/com/joker/coolmall/feature/launch/
shared/src/commonMain/kotlin/com/joker/coolmall/feature/main/
shared/src/commonMain/kotlin/com/joker/coolmall/feature/auth/
shared/src/commonMain/kotlin/com/joker/coolmall/core/network/
shared/src/commonMain/kotlin/com/joker/coolmall/core/session/
```

单个业务功能按照以下路径追踪：

```text
入口 Screen
  -> Navigation
  -> ViewModel
  -> Repository
  -> DataSource/API
  -> UiState
  -> Screen 重组和导航结果
```

