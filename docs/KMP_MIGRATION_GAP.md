# CoolMallKotlin → CoolMallKMP 对齐差距与迁移计划

> 更新时间：2026-10-28  
> 参考样板：`/Applications/qingpengxia/development-playground/android/kmp/netcloudmusic`  
> 目标：Android + iOS 双端；**iOS 不做 QQ / 微信 / 支付宝**

## 1. 当前结论（先看这个）

| 维度 | 状态 | 说明 |
|------|------|------|
| Screen 文件名 | ✅ 已对齐 | CoolMallKotlin / CoolMallKMP 各 37 个 `*Screen*.kt`，名称一致 |
| feature / core 目录 | ⚠️ dump 隔离 | 未改造源码在 `shared/migration/android-dump/`，不参与编译 |
| 真正 KMP 化 | 🔄 进行中 | 底座 + 主链路已 KMP；其余 feature 仍在 dump |
| 可运行双端业务 | ✅ 主链路 | Splash→Guide→Main + 登录/购物车/商详/确认订单 双端可编译 |
| iOS QQ/微信/支付宝 | 🚫 不做 | `ThirdPartyCapabilities`；iOS UI 不暴露入口 |

**一句话**：P0/P1 底座与主购链路（浏览→加购→登录→确认下单）已双端可跑；订单全链路/地址 CRUD/Room3 购物车/搜索/优惠券/重置密码 UI 已切片。

## 2. CoolMallKotlin 功能清单 vs KMP

### 2.1 已有同名 Screen（表面对齐）

- **main**：Home / Category / Cart / Me / Main  
- **auth**：Login / AccountLogin / Register / ResetPassword / SmsLogin  
- **user**：Profile / AddressList / AddressDetail / Footprint  
- **order**：OrderList / OrderConfirm / OrderDetail / OrderPay / OrderRefund / OrderComment / OrderLogistics  
- **goods**：GoodsSearch / GoodsDetail / GoodsComment / GoodsCategory  
- **market**：Coupon  
- **cs**：Chat  
- **feedback**：FeedbackList / FeedbackSubmit  
- **common**：About / Web / Settings / UserAgreement / PrivacyPolicy / Contributors  
- **launch**：Splash / Guide  

### 2.2 未真正跨平台对齐的能力（缺口）

| 能力 | CoolMallKotlin | CoolMallKMP 现状 | iOS 目标 |
|------|----------------|------------------|----------|
| 网络 | Retrofit + OkHttp | 原样在 commonMain | Ktor + Darwin |
| DI | Hilt | 118+ 处 Hilt/`@Inject` | 手动 `AppContainer`（同 netcloud） |
| 本地 KV | MMKV | Android-only | DataStore Preferences |
| DB | Room | Android Room | Room3 KMP + BundledSQLite |
| 图片 | Coil2 | 未接 KMP Coil | Coil3 + ktor3 |
| 导航 | Navigation3 / 模块导航 | 未接 CMP Navigation | Navigation Compose |
| 资源 | `R.string` / `painterResource(id=)` | Android R | composeResources |
| 支付支付宝 | PayTask SDK | 在 commonMain | **不做**（Android actual） |
| QQ 登录 | Tencent SDK | 在 commonMain | **不做** |
| 微信相关 UI/支付 | 有入口 | 在 commonMain | **不做** |
| Toast / 权限 / 通知 | Android API | 在 commonMain | expect/actual 或 Snackbar |
| 文件上传 / Uri | `android.net.Uri` | `FileUploadRepository` + `PickedImage`；Picker actual 空 | multiplatform 文件抽象 |

### 2.3 技术债量化（commonMain）

- `import android.*`：**39** 个文件  
- Hilt / `@Inject` / `hiltViewModel`：**118** 个文件  
- Retrofit / OkHttp：**21** 个文件  
- MMKV / Room：**21** 个文件  
- 支付 / QQ / Tencent 相关：**19** 个文件  
- 嵌套 `src/main/java` 目录：**21** 处  

### 2.4 相对 netcloudmusic 缺失的依赖

CoolMallKMP `libs.versions.toml` / `shared/build.gradle.kts` 目前几乎只有 Compose + Lifecycle，缺失：

- kotlinSerialization / kotlinx-serialization-json  
- navigation-compose / runtime / common  
- ktor-client-core + content-negotiation + serialization；android okhttp / ios darwin  
- coil-compose + coil-network-ktor3  
- datastore + datastore-preferences  
- room3-runtime + compiler + sqlite-bundled + KSP  
- atomicfu / kotlinx-datetime / material-icons-extended  

## 3. netcloudmusic 可复用模式（照抄）

1. `expect fun platformHttpClient` → Android OkHttp / iOS Darwin  
2. `expect fun createDataStore` + `PreferenceDataStoreFactory.createWithPath`  
3. Room3 `RoomDatabaseFactory` expect/actual + `BundledSQLiteDriver`  
4. 手动 `AppContainer` / `AppContainerProvider`（替代 Hilt）  
5. 真正入口 `MyApp` / `CoolMallApp`；模板 `App.kt` 仅占位  
6. Android `MainActivity` / iOS `MainViewController` 里 `initialize` + 业务根 Composable  
7. Toast 优先 Snackbar；三方登录 iOS 直接「暂不支持」  
8. 图片用独立无 Auth 的 HttpClient，避免 CDN 403  

## 4. 迁移优先级 Checklist

### P0 — 能编译、能跑双端壳
- [x] 清理 `com/example/netcloudmusic` 残留空目录  
- [x] 对齐依赖（Ktor / Coil / Room3 / DataStore / Navigation / Serialization）  
- [x] 建立 `AppContainer` + 配置注入  
- [x] `CoolMallApp` 入口替换模板 `App()`  
- [x] Android / iOS 初始化链路打通（`assembleDebug` 已通过）  
- [x] 未改造 Android 源码移至 `shared/migration/android-dump/`（457 文件，不参与编译）  


### P1 — 网络 / 存储底座
- [x] Retrofit → Ktor NetworkDataSource（page / goods / auth 已接）  
- [x] Auth header / BaseUrl 配置跨平台化  
- [x] MMKV → DataStore（guide_shown + auth_info）  
- [x] Room → Room3 KMP（`AppDatabase` + `CartDao` + `BundledSQLiteDriver`；购物车已持久化）  
- [ ] 去掉 commonMain 中的 `android.content.Context`（若仍有残留）  

### P2 — UI 导航与业务切片
- [x] Navigation Compose：Splash → Guide? → Main Tabs（Home 已接 `GET page/home`）  
- [x] 分类 Tab：`POST goods/type/list` + 左右栏分类树  
- [x] 商品详情：`GET page/goodsDetail` + type-safe 导航  
- [x] 分类商品列表页（`POST goods/info/page` + 分页）  
- [x] 账号密码登录 + Auth DataStore 持久化 + Splash 恢复会话  
- [x] 我的 Tab：登录门禁（未登录入口 / 已登录退出） + 用户资料展示  
- [x] 购物车 Tab：Room3 `CartRepository` + 商详「加入购物车」（进程重启可恢复）  
- [x] 商详规格选择：`POST goods/spec/list` + BottomSheet + 按规格/数量加购  
- [x] 用户资料：`GET user/info/person` + UserPreferences  
- [x] 地址列表：`POST user/address/list`（只读列表）  
- [x] 订单确认：`GET page/confirmOrder` + `POST order/info/create` + CheckoutDraft + 结果页  
- [x] 购物车结算门禁：未登录跳转登录  
- [x] 订单列表：`POST order/info/page` + 状态 Tab（全部/待付款/待发货/待收货/售后/待评价/完成）+ 分页  
- [x] 订单详情：`GET order/info/info` + 金额明细 + 收货地址 + 商品列表  
- [x] 取消订单：`POST order/info/cancel`；确认收货：`GET order/info/confirm`  
- [x] 「我的」页订单入口：我的订单 + 待付款/待发货/待收货/待评价/售后 快捷跳转 + `userCount` 角标  
- [x] 「我的」最近足迹横滑 + 用户资料 resume 刷新（`MeViewModel`）  
- [x] 地址增删改：`user/address/add` / `update` / `delete` / `info` / `default` + 表单校验 + 设默认 + 删除确认  
- [x] 订单物流：`GET order/info/logistics` + 轨迹时间线 UI + 详情/列表入口  
- [x] 订单退款：`POST order/info/refund` + 字典原因（`dict/info/data` type=`orderRefundReason`）+ 手填兜底 + 详情/列表入口  
- [x] 订单评价：`POST goods/comment/submit` + 星级/文案 + 多商品选择 + 配图（`FileUploadRepository` + 系统相册选图）  
- [x] 商品搜索：热词 `goods/search/keyword` + 搜索页 → 分类列表 keyword  
- [x] 优惠券：领取/列表 API + 「我的」入口  
- [x] 重置密码：UI + 发短信/校验（原 CoolMallKotlin 与后端均无确认 reset API，**不伪造成功提交**）  
- [x] 上传底座：`POST base/comm/upload` 凭证 + multipart 腾讯云；`readImageBytes` expect/actual  


- [x] 支付：Android `order/pay/alipayAppPay` + `PayTask.payV2`；iOS 不提供  
- [x] 短信验证码登录：`user/login/smsCode` + `user/login/phone` + 60 秒倒计时  
- [x] 用户注册：`user/login/register` + 密码一致性校验 + 注册后直接登录  
- [x] 浏览足迹：Room `footprints` + 商详写入 + 列表/清空（「我的」入口）  
- [x] 意见反馈：`feedback/page` + `feedback/submit` + 字典类型 + 列表/提交页  
- [x] 关于我们：版本/平台/三方能力说明 + 协议入口  
- [x] 系统相册选图：`rememberPlatformImagePicker`（Android Photo Picker / iOS PHPicker）  
- [x] 设置：主题模式（SYSTEM/LIGHT/DARK，DataStore）+ 协议/关于入口  
- [x] 用户协议 / 隐私政策：`getParam` + 纯文本渲染（不做 WebView）  
- [x] 客服 Chat：`cs/session/create` + 消息分页 + Socket.IO WebSocket（Ktor）  
- [x] 客服发图：相册选图 + OSS 上传 + WS `type=image`  
- [x] 反馈配图：相册选图 + OSS 上传 + `feedback/submit` images  
- [x] 订单支付回流：详情/列表 `LifecycleResumeEffect` 刷新状态  
- [x] composeResources 字符串扩大（Me/Chat/Pay/Settings/Order/Auth/Cart/Goods/Address/Feedback/About/Coupon…）  
- [x] composeResources 本轮：Splash/Guide、Sms/Register/Reset、OrderConfirm/Comment/Logistics/Refund/Result/Detail、OrderList 空态、支付 toast（`PayToastCode` + UI `getString`）  
- [x] VM 校验/错误文案：`UiMessage`/`MessageCode` + `resolveUiMessage`（Auth/Address/GoodsDetail）；商详规格/优惠券/评价等 stringResource  
- [x] 图标：dump vector → `composeResources/drawable`（ic_qq/wechat/alipay/empty_* 等 22 个）；登录/支付/购物车空态已引用  
- [x] Android QQ / 微信登录 optional 骨架（`ThirdPartyLogin` stub + AuthRepository；能力开关默认 false，iOS 不暴露）  
- [x] 统一空态组件：`core/ui/empty/EmptyStates.kt`（`EmptyNetwork`/`EmptyError`/`EmptyData`/`EmptyCart`/`EmptyUiMessage`）  
- [x] 列表/订单/地址/足迹/优惠券/反馈/文档/搜索/分类/商详/客服等 Error·空列表接入空态；Snackbar `resolveUiMessageSuspend`  
- [x] feature VM 错误载体 `UiMessage`（含 Document/Coupon/Feedback/Chat messageEvent 等）  

### P3 — 平台专有 & 收敛
- [x] Android：支付宝 actual；QQ/微信 optional stub（真 SDK 待接）  
- [x] iOS：支付与三方登录隐藏 / NotConfigured stub  
- [ ] Android 真 QQ / 微信 SDK 接入（当前 `ThirdPartyLogin` stub，能力开关默认关）  
- [ ] 重置密码后端 API 确认并接线（UI 已有，不伪造成功）  
- [x] 最小设计 token：`core/ui/theme`（Primary `#465CFF` / Danger `#FF2B2B` / 间距 4·8·12·16）+ `CoolMallApp` MaterialTheme  
- [x] SpecSelectSheet 视觉对齐 dump（100dp header、网格卡片、选中后数量、加购+立即购买）+ `buyNow`→CheckoutDraft  
- [x] OrderStatus Tab 文案资源化；售后筛选 `[5,6]`；选中 Tab 主色强调  
- [x] 协议页标题 `user_agreement` / `privacy_policy` 资源化  
- [x] `PriceText` / `PriceTextSmall` / `PriceTextLarge`（`ColorDanger` + ¥ 基线对齐）  
- [x] `TitleWithLine` 区块标题（左侧 4dp 主色竖线）  
- [x] 价格/标题接入：Home / GoodsDetail / SpecSelect / Cart / OrderConfirm / OrderList / OrderDetail / OrderPay / OrderRefund / Coupon / Footprint / Category 等  
- [x] `AppButton` / `AppButtonFixed` / `AppButtonBordered`（FILLED / OUTLINED / GRADIENT，`#465CFF→#6831FF`）  
- [x] 主 CTA 接入：商详/规格弹层/购物车/确认订单/支付/登录/注册/重置/地址保存/退款/评价/反馈/引导/订单结果/列表·详情操作  
- [x] Me：`MeViewModel` + `GET order/info/userCount` 角标 + 售后 tab(4) + 最近足迹横滑 + `LifecycleResumeEffect` 刷新  
- [x] 引导：3 页 `HorizontalPager` + 跳过/下一步/立即体验；Splash `skipToMain` 写 `guide_shown`  
- [x] 搜索：`SearchHistoryPreferences`（DataStore）+ IME Search + 历史 chip；首页分类点击 → `navigateToGoodsCategory`  
- [x] 共享组件：`CouponCard`（RECEIVE/SELECT/VIEW）/ `GoodsGridItem` / `GoodsListItem`；Home/Category/CouponList 接入  
- [x] SpecSelectSheet：网格 ↔ 列表切换（`isGridMode` + `AnimatedContent`）  
- [x] 订单状态展示文案 `Order.statusLabel()` 资源化（列表/详情）  
- [x] Coupon 本地 endTime 二次过期判定：`DateTimeUtils.isDateExpired`（epoch 桥接 `kotlin.time` ↔ `kotlinx.datetime`）+ `useStatus`  
- [x] 会员权益卡：`MembershipCard`（深底 `#242424` + 金色 `#E0A472` + `ic_vip_fill`）  
- [x] Me 页视觉/结构对齐 CoolMallKotlin：72dp 头像 → 会员卡 → 五态图标+角标订单区 → 100dp 足迹横滑 → 彩色菜单双 Card（无标题/无调试卡/无底部大按钮）  
- [x] Me 专用 dump 矢量迁入 composeResources（order/pay/receipt/logistics/message/footprint/location/service/creative/tip/set/coupon_fill）并接入 MeScreen  
- [x] Profile 个人资料页（只读头像/昵称/手机/账号 + 退出登录；iOS 无 QQ/微信绑定区）+ Me 头像区已登录跳转  
- [x] Home 页视觉/结构对齐 CoolMallKotlin：顶栏 Logo+搜索+GitHub → Banner 轮播 → 优惠券横条+领取弹层 → 分类 5 列网格 → 限时精选横滑 → 推荐列表 → 全部商品双列网格+分页；去掉大标题/简易 LazyRow  
- [ ] 完整 designsystem 模块 / 其余视觉余量  




- [ ] 权限 / 外链 / 分享 / 缓存清理 expect/actual  
- [ ] 统一日志；删除无效 appleMain/nativeMain 空壳  

## 5. iOS 明确不做的模块

- QQ 登录（`QQLoginManager` / Tencent SDK）  
- 微信登录 / 微信支付入口  
- 支付宝 App 支付（`PayTask`）  

Android 端可通过 `expect/actual` 或 `androidMain` 保留；iOS UI 隐藏对应按钮，或点击提示「当前平台暂不支持」。

## 6. 建议落地顺序（本仓库执行）

1. 先补 **P0 底座**（依赖 + Network/DataStore expect/actual + AppContainer + CoolMallApp）  
2. 再迁 **model + network API**（纯 Kotlin 优先）  
3. 再迁 **launch/main** 最小闭环  
4. 其余 feature 按依赖顺序切片，避免一次改 400+ 文件导致长期不可编译  
