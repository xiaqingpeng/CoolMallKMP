# CoolMallKMP 核心业务流转

## 1. 启动和引导

```text
Android MainActivity / iOS App
  -> CoolMallApp
  -> Splash
  -> 恢复登录会话
  -> 读取 guide_shown
       ├── 未完成引导 -> Guide
       └── 已完成引导 -> Main
```

- Android 系统 Splash 负责进程启动阶段的首帧。
- 共享 `SplashScreen` 负责品牌展示和启动路由。
- `GuideScreen` 完成后写入 `guide_shown=true`。
- 设置页重新查看引导时，完成后返回设置页。

## 2. 主框架

```text
Main
├── 首页
├── 分类
├── 购物车
└── 我的
```

首页和分类进入商品列表或商品详情；我的页面根据登录状态展示登录入口或用户业务入口。

未登录访问受保护功能时，统一跳转认证流程：

```text
受保护入口 -> Login -> 登录成功 -> 返回目标业务
```

## 3. 登录和会话

```text
Login
├── 账号密码登录
│   └── user/login/password
├── 手机验证码登录
│   ├── user/login/smsCode
│   └── user/login/phone
├── 注册
│   └── user/login/register
└── 重置密码
    ├── 图形验证码
    ├── 短信验证码
    └── 最终提交接口待后端确认
```

登录成功后：

```text
API 响应
  -> AuthSession 保存 token / refreshToken
  -> DataStore 持久化
  -> 后续 Ktor 请求自动添加 Authorization
  -> 页面刷新为已登录状态
```

退出登录会清理会话和用户状态。微信、QQ 登录与绑定暂未接入第三方 SDK。

## 4. 商品浏览和搜索

```text
首页 / 分类
  -> 商品列表
  -> 商品详情
```

搜索链路：

```text
搜索入口
  -> 输入关键词
  -> 搜索历史 / 热门搜索
  -> 分类、价格、排序筛选
  -> 商品分页结果
  -> 商品详情
```

搜索历史使用 Room 保存，旧版本 DataStore 数据仅用于兼容导入。搜索页面分别处理历史、热门搜索和商品结果的加载、空数据、错误与分页。

## 5. 商品详情

```text
商品详情
├── 图片轮播
├── 商品信息和价格
├── 优惠券弹窗
├── 规格选择 Sheet
├── 商品评价
├── 加入购物车
└── 立即购买
    └── 确认订单
```

商品详情会记录浏览足迹。加入购物车更新购物车数据；立即购买携带当前规格和数量进入确认订单。

## 6. 购物车和地址

```text
购物车
├── 单选 / 全选
├── 数量调整
├── 编辑和删除
└── 结算
    └── 确认订单
        └── 选择地址
            └── 地址列表（选择模式）
```

地址列表有两种模式：

- 普通模式：管理、新增、编辑、删除和设置默认地址。
- 选择模式：点击地址卡片后返回确认订单，并更新收货地址。

地址编辑字段：

```text
收货人、手机号、省、市、区、详细地址、默认地址
```

地区选择由 `RegionPickerModal` 完成。

## 7. 订单

```text
确认订单
  -> 提交订单
  -> 订单结果
  -> 订单详情 / 订单列表
```

订单相关能力：

```text
订单列表
├── 全部
├── 待付款
├── 待发货
├── 待收货
├── 待评价
└── 售后 / 退款

订单详情
├── 物流
├── 评价
├── 退款
├── 取消订单
└── 确认收货
```

支付 SDK 按迁移约定暂不接入。订单列表各状态 Tab 保留独立分页状态。

## 8. 我的、资料和设置

```text
我的
├── 个人资料
├── 我的订单
├── 收货地址
├── 优惠券
├── 足迹
├── 在线客服
├── 意见反馈
├── 设置
└── 关于
```

设置包含主题、语言展示、引导开关、缓存清理、用户协议、隐私协议、意见反馈和关于入口。

协议和通用网页：

```text
WebScreen
├── Android WebView
└── iOS WKWebView
```

页面入口和导航由共享层管理，WebView 容器由平台实现。

## 9. 意见反馈

```text
意见反馈列表
  -> 登录校验
  -> feedback/page
  -> 提交反馈
       ├── 输入内容
       ├── 选择图片
       ├── 获取上传凭证
       ├── 上传到 COS
       ├── feedback/submit
       └── 返回列表并刷新
```

图片上传是两阶段流程：

```text
base/comm/upload
  -> 获取腾讯云 COS 临时凭证
  -> multipart 上传图片
  -> 得到图片 URL
  -> 提交反馈或聊天消息
```

## 10. 在线客服

```text
客服页面
  -> REST 加载历史消息
  -> 建立 WebSocket
  -> 接收实时消息
  -> 发送文本或图片
  -> 离开页面释放连接
```

客服消息使用倒序分页，不能直接复用普通列表的分页方向。

## 11. 统一数据流

```text
Screen
  -> ViewModel
  -> Repository
  -> Ktor DataSource / Room / DataStore
  -> UiState
  -> Screen
```

网络请求的公共处理包括：

- base URL 和公共请求配置。
- Authorization 注入。
- 服务端 `code/message/data` 解析。
- Token 过期处理。
- 加载、成功、空数据、错误、重试和分页状态。

## 12. 代码阅读顺序

```text
shared/src/commonMain/kotlin/com/joker/coolmall/ui/CoolMallApp.kt
  -> feature/launch/
  -> feature/main/
  -> feature/auth/
  -> feature/goods/
  -> feature/order/
  -> feature/user/
  -> core/network/
  -> core/data/
  -> core/session/
```

单个功能按以下路径追踪：

```text
Screen
  -> Navigation
  -> ViewModel
  -> Repository
  -> DataSource / API
  -> UiState
```
