This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…


---

## CoolMall KMP 迁移说明

本工程目标：将 [CoolMallKotlin](../compose/CoolMallKotlin) 电商功能迁移为 **Android + iOS** Compose Multiplatform。

- **差距分析与计划**：见 [`docs/KMP_MIGRATION_GAP.md`](./docs/KMP_MIGRATION_GAP.md)
- **未改造的 Android 原码备份**：`shared/migration/android-dump/`（457 个 `.kt`，不参与编译）
- **参考样板**：`/Applications/qingpengxia/development-playground/android/kmp/netcloudmusic`
- **iOS 不做**：QQ 登录 / 微信登录与支付 / 支付宝（见 `ThirdPartyCapabilities`）

### 当前进度

- [x] P0：Ktor / DataStore / AppContainer / CoolMallApp 双端入口  
  - 已验证：`:androidApp:assembleDebug`、`:shared:compileKotlinIosSimulatorArm64`
- [x] P2 最小闭环：Splash → Guide? → Main(4 Tab) → Home 拉取 `page/home` 并展示 Banner/分类/商品
- [x] P2 继续：分类 Tab（`goods/type/list`）+ 商品详情（`page/goodsDetail`）
- [x] P2 继续：分类商品列表（`goods/info/page`）+ 分页
- [x] P2 继续：账号密码登录 + Auth DataStore 持久化 + 我的 Tab 登录门禁
- [x] P2 继续：内存购物车 + 商详加购 + 购物车 Tab（改量/删除/清空）
- [x] P2 继续：商详规格选择弹窗（`goods/spec/list`）+ 按规格加购
- [x] P2 继续：用户资料（`user/info/person`）+ 收货地址列表（`user/address/list`）
- [x] P2 继续：购物车结算 → 确认订单（`page/confirmOrder` + `order/info/create`）+ 下单成功页（无支付 SDK）
- [x] P2 继续：订单列表（`order/info/page` + 状态 Tab + 分页）/ 订单详情（`order/info/info`）+ 取消订单（`order/info/cancel`）/ 确认收货（`order/info/confirm`）
- [x] P2 继续：收货地址增删改（`user/address/add|update|delete|info|default`）+ 表单校验 + 设为默认
- [x] P2 继续：短信验证码登录（`user/login/smsCode` + `user/login/phone`）+ 注册（`user/login/register`）
- [x] P2 继续：订单物流（`order/info/logistics`）+ 退款（`order/info/refund` + 字典原因）+ 详情/列表入口
- [x] P2 继续：订单评价（`goods/comment/submit` + 星级/文案 + 多商品 + 配图 URL/上传底座）
- [x] P1：Room3 购物车持久化（`AppDatabase` / `CartDao` / `BundledSQLiteDriver`）
- [x] P2 继续：商品搜索（热词 + keyword 列表）/ 优惠券列表 / 重置密码 UI（后端 reset API 未确认，不伪造成功）
- [x] P2 继续：文件上传底座（OSS 凭证 + multipart）
- [x] P2 继续：浏览足迹（Room + 商详自动记录）/ 意见反馈列表与提交 / 关于我们
- [x] P2 继续：系统相册选图（Android Photo Picker / iOS PHPicker）+ 设置主题 + 用户协议/隐私政策
- [x] P2 继续：客服 Chat（会话 REST + Socket.IO WebSocket）
- [x] P2 继续：客服发图（选图 → OSS → WS image）
- [x] P2 继续：Android 支付宝支付（`alipayAppPay` + PayTask）；iOS 不提供
- [x] P2 继续：支付回流后订单列表/详情 resume 刷新
- [x] P2 继续：composeResources 文案扩大（Me/Chat/Pay/Settings/OrderList/Auth）
- [x] P2 继续：反馈配图（选图 → OSS → submit images）
- [x] P2 继续：商详/购物车/地址/订单操作/关于/优惠券/Home·Category 重试等文案迁 composeResources
- [x] P2 继续：Splash/Guide/Auth 全页 + 订单确认/评价/退款/物流/详情/结果 + 支付 toast 资源化
- [x] P2 继续：VM 校验/错误 `UiMessage`+composeResources；商详残留文案；关键 vector 图标迁 composeResources
- [x] P2 继续：统一空态 `EmptyNetwork`/`EmptyError`/`EmptyData`/`EmptyCart`/`EmptyUiMessage`；列表/订单/地址/足迹/优惠券/反馈/文档等 Error 态接入；feature VM 错误 `UiMessage` 化
- [x] P3 切片：Android QQ/微信 optional stub（`ThirdPartyLogin` + 能力开关；iOS 不暴露）
- [x] P3 切片：最小设计 token（品牌主色/危险色/间距）+ SpecSelectSheet 双 CTA/网格规格 + 订单 Tab 资源化 + 立即购买→确认订单
- [x] P3 切片：`PriceText`/`TitleWithLine` + 全站价格/区块标题视觉对齐；合计前缀等文案资源化
- [x] P3 切片：`AppButton` 渐变主按钮（`#465CFF→#6831FF`）+ 描边/固定宽 + 主购/登录/订单等 CTA 接入
- [x] P3 切片：Me 订单角标（`order/info/userCount`）+ 售后 Tab + 最近足迹 + resume 刷新
- [x] P3 切片：引导多页 Pager（跳过/下一步/立即体验）+ Splash 跳过记 `guide_shown`
- [x] P3 切片：搜索历史 DataStore + IME Search；首页分类可点进列表
- [x] P3 切片：共享 `CouponCard` / `GoodsGridItem` / `GoodsListItem`；规格网格↔列表切换
- [x] P3 切片：Coupon `endTime` 本地过期（`DateTimeUtils` epoch 桥接）+ Me 会员权益卡
- [ ] P3：Android 真 QQ/微信 SDK（当前 stub）；重置密码后端 API（未确认）；完整 designsystem 余量

### 主要源码位置

| 能力 | 路径 |
|------|------|
| 依赖容器（替代 Hilt） | `shared/src/commonMain/kotlin/com/joker/coolmall/core/di/AppContainer.kt` |
| Ktor 网络 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/network/` |
| DataStore 存储 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/data/local/` |
| Room3 数据库 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/database/` |
| 业务仓库 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/data/repository/` |
| 导航根 | `shared/src/commonMain/kotlin/com/joker/coolmall/ui/CoolMallApp.kt` |
| 设计 token / 主题 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/ui/theme/` |
| 价格 / 区块标题 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/ui/component/PriceText.kt`、`TitleWithLine.kt` |
| 主按钮 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/ui/component/AppButton.kt` |
| 空态组件 | `shared/src/commonMain/kotlin/com/joker/coolmall/core/ui/empty/` |
