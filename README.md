# CoolMallKMP

CoolMallKMP 是由 `CoolMallKotlin` 迁移而来的 Kotlin Multiplatform 电商应用，使用 Compose Multiplatform 共享 Android 与 iOS 的业务层和 UI。

## 项目结构

```text
androidApp/                  Android 应用入口和 Android 平台能力
iosApp/                      iOS 应用入口、Info.plist、AppIcon
shared/src/commonMain/
├── core/
│   ├── data/                DataSource、Repository、本地数据
│   ├── database/            Room 数据库和 DAO
│   ├── model/               请求、响应、实体模型
│   ├── network/             Ktor、鉴权、错误处理
│   ├── platform/            expect/actual 平台能力
│   ├── session/             Token 和登录会话
│   └── ui/                  主题、公共组件、骨架、弹窗
├── feature/
│   ├── launch/              Splash、引导
│   ├── main/                首页、分类、购物车、我的
│   ├── auth/                登录、注册、短信登录、重置密码
│   ├── goods/               分类、搜索、商品详情、评价
│   ├── order/               确认订单、订单、物流、退款
│   ├── user/                资料、地址、足迹
│   ├── market/              优惠券
│   ├── feedback/            意见反馈
│   ├── cs/                  在线客服
│   └── common/              设置、关于、协议、WebScreen
└── ui/                      全局主题和根导航
```

Feature 内部按职责组织：

```text
feature/<name>/
├── component/
├── model/
├── navigation/
├── view/
│   ├── screen/
│   ├── skeleton/
│   ├── sheet/
│   └── platform/
└── viewmodel/
```

## 运行和构建

构建 Android Debug APK：

```bash
./gradlew :androidApp:assembleDebug
```

编译共享 Android 源集：

```bash
./gradlew :shared:compileAndroidMain
```

iOS 工程使用 Xcode 打开 `iosApp/` 运行。

## 核心架构

```text
Screen
  -> Navigation
  -> ViewModel
  -> Repository
  -> Ktor DataSource / Room / DataStore
  -> UiState
  -> Screen
```

- `AppContainer` 负责共享依赖组装，替代 Android 单端 Hilt 入口。
- `AuthSession` 持有登录状态，网络层自动添加 `Authorization`。
- `Repository` 隔离业务逻辑和 API，页面不直接访问 Ktor。
- `Room` 保存结构化本地数据，例如购物车和搜索历史。
- `DataStore` 保存轻量配置，例如引导状态、启动页开关和主题设置。
- `expect/actual` 处理通知、权限、图片选择、WebView、应用信息等平台差异。

## 业务入口

完整业务链路见 [`README_BUSINESS_FLOW.md`](./README_BUSINESS_FLOW.md)。

根入口：

```text
shared/src/commonMain/kotlin/com/joker/coolmall/ui/CoolMallApp.kt
```

核心基础设施：

```text
shared/src/commonMain/kotlin/com/joker/coolmall/core/network/
shared/src/commonMain/kotlin/com/joker/coolmall/core/data/
shared/src/commonMain/kotlin/com/joker/coolmall/core/database/
shared/src/commonMain/kotlin/com/joker/coolmall/core/session/
shared/src/commonMain/kotlin/com/joker/coolmall/core/ui/
```

## API 基础配置

默认 API 基地址：

```text
https://mall.dusksnow.top/app/
```

请求通常由网络层统一完成：

1. Repository 组装请求。
2. Ktor 添加鉴权和公共请求配置。
3. 解析服务端 `code`、`message`、`data`。
4. 转换为 ViewModel 使用的 `UiState`。
5. 页面处理加载、成功、空数据和错误状态。

## 明确限制

以下能力不在当前迁移范围内，不伪造成功行为：

- 微信登录和绑定 SDK。
- QQ 登录和绑定 SDK。
- 微信、支付宝等支付 SDK。
- 重置密码最终提交接口，等待后端确认正式 API。

其余核心业务入口，包括登录、商品浏览、搜索、购物车、地址、订单、反馈、客服和设置，均由共享层实现。
