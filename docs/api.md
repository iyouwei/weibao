# Weibao 情侣点餐系统接口文档 v0.1

> 基础地址：`/api`。除登录接口外，其余请求需在 Header 中附带 `Authorization: Bearer <token>`。返回统一使用 `ApiResponse` 包装：`{ "code": 0, "message": "OK", "data": ... }`

## 1. 鉴权 & 用户
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| POST | `/auth/weapp-login` | 微信小程序登录 |
| POST | `/auth/sms-login` | H5 手机号登录 |
| GET | `/auth/profile` | 查询当前登录用户、家庭、钱包信息 |
| POST | `/auth/logout` | 退出登录 |

### 1.1 POST `/auth/weapp-login`
请求体：
```json
{ "code": "wx-code", "avatar": "https://...", "nickname": "小王" }
```
响应体（`token` 为 JWT，调用其他接口时放入 `Authorization: Bearer <token>`）：
```json
{
  "token": "jwt-token",
  "user": { "id": 1, "nickname": "小王", "avatar": "https://..." },
  "family": { "id": 99, "name": "小王的专属餐厅", "type": "couple" },
  "wallet": { "balance": 520, "frozen": 0 }
}
```

## 2. 家庭 / 情侣
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| POST | `/family/invite` | 创建邀请码 |
| POST | `/family/join` | 使用邀请码加入 |
| GET | `/family/info` | 查询家庭详情与成员 |
| POST | `/family/unbind` | 解除绑定 |

示例：`POST /family/invite` → `{ "code": "FD5K8A", "qrUrl": "...", "expireAt": "2025-01-01T00:00:00Z" }`

## 3. 菜单 & 分类
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/menu/categories` | 分类列表 |
| GET | `/menu/dishes` | 菜品分页（`categoryId`, `keyword`, `page`, `size`） |
| GET | `/menu/dish/{id}` | 菜品详情 |
| POST/PUT/DELETE | `/menu/admin/category[/{id}]` | 分类增删改（管理员） |
| POST/PUT/DELETE | `/menu/admin/dish[/{id}]` | 菜品增删改（管理员） |
| GET | `/menu/admin/dashboard` | 菜品热点指标 |

示例：
```json
POST /menu/admin/category
{ "name": "无肉不欢", "icon": "🥩", "description": "大口吃肉", "sortOrder": 1, "visible": true }

POST /menu/admin/dish
{
  "categoryId": 1,
  "name": "牛排",
  "cover": "https://...",
  "description": "情侣必点",
  "price": 108,
  "rating": 4.9,
  "tags": ["情侣必点","高蛋白"],
  "spicyLevel": 1
}
```

## 4. 购物车 & 订单
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/cart` | 查询家庭购物车 |
| PUT | `/cart/item` | 新增/修改购物车项 |
| DELETE | `/cart/item/{dishId}` | 移除菜品 |
| POST | `/order/preview` | 下单前预览（根据传入 cartItems 计算总价/推荐抵扣） |
| POST | `/order/submit` | 提交订单（body 同 preview，额外 `coinUse`、`remark`） |
| GET | `/order/{id}` | 订单详情 |
| GET | `/order/family` | 家庭订单列表 |

`OrderPreviewResponse` 字段：
```json
{
  "totalAmount": 266,
  "discountAmount": 20,
  "coinAvailable": 520,
  "coinRecommended": 200,
  "payableAmount": 246,
  "items": [{ "dishId": 1, "name": "牛排", "quantity": 2, "note": "5 成熟", "price": 108 }]
}
```

## 5. 虚拟币
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/coin/balance` | 查询余额 |
| GET | `/coin/tasks` | 待领取任务 |
| POST | `/coin/claim` | 领取任务，Body：`{ "taskCode": "daily_sign" }` |
| GET | `/coin/history` | 流水记录 |

## 6. 管理端
| 方法 | 路径 | 描述 |
| --- | --- | --- |
| GET | `/admin/statistics` | 订单/营收/虚拟币统计 |
| POST | `/admin/upload` | 素材上传（`multipart/form-data`，返回 `url` 与 `fileName`） |

## 7. 错误码
| code | message | 场景 |
| --- | --- | --- |
| 401 | 未登录 | token 失效 |
| 403 | 权限不足 | 管理接口需要管理员角色 |
| 404 | 资源不存在 | 菜品/订单 |
| 1001 | 邀请码无效 | 加入家庭失败 |
| 2001 | 菜品库存不足 | 下单失败 |
| 3001 | 虚拟币不足 | 折抵失败 |

## 8. 本地调试
1. `./mvnw spring-boot:run`
2. 访问 `http://localhost:8080/api/auth/profile`
3. 可配合前端 Taro 项目联调


