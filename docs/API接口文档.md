# ZFX 支付中心系统 - API 接口文档

## 1. 接口概述

### 1.1 基础信息
- **系统名称**: ZFX 支付中心系统
- **API 版本**: v1.0
- **基础路径**: `/api/v1`
- **数据格式**: JSON
- **字符编码**: UTF-8

### 1.2 认证方式
- **认证类型**: Token 认证
- **请求头**: `Authorization: Bearer {token}`
- **Token 获取**: 通过登录接口获取

### 1.3 响应格式
```json
{
  "code": 1,
  "msg": "success",
  "data": {},
  "ok": true
}
```

### 1.4 错误码说明
| 错误码 | 说明 |
|--------|------|
| 1 | 成功 |
| 30007 | 未登录或登录失效 |
| 30008 | 账号已在别处登录 |
| 40001 | 参数错误 |
| 50001 | 系统内部错误 |

## 2. 认证接口

### 2.1 用户登录
- **接口地址**: `/login`
- **请求方式**: `POST`
- **接口描述**: 用户登录获取访问令牌

#### 请求参数
```json
{
  "loginName": "admin",
  "password": "123456",
  "captcha": "1234",
  "captchaKey": "uuid-key"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| loginName | String | 是 | 登录用户名 |
| password | String | 是 | 登录密码 |
| captcha | String | 是 | 验证码 |
| captchaKey | String | 是 | 验证码密钥 |

#### 响应参数
```json
{
  "code": 1,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": 1,
      "loginName": "admin",
      "actualName": "管理员",
      "departmentId": 1,
      "roleId": 1
    }
  }
}
```

### 2.2 用户登出
- **接口地址**: `/logout`
- **请求方式**: `POST`
- **接口描述**: 用户登出，清除登录状态

#### 请求参数
无

#### 响应参数
```json
{
  "code": 1,
  "msg": "登出成功",
  "data": null
}
```

## 3. 订单管理接口

### 3.1 分页查询订单
- **接口地址**: `/orderInfo/queryPage`
- **请求方式**: `POST`
- **接口描述**: 分页查询支付订单信息

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "platformId": 1,
  "merNo": "MER001",
  "payStatus": 2,
  "startTime": 1696003200000,
  "endTime": 1696089600000,
  "keyword": "订单号或商户名称",
  "paymentMethod": "bank",
  "currency": "USD"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 是 | 页码，从1开始 |
| pageSize | Integer | 是 | 每页大小 |
| platformId | Long | 否 | 平台ID |
| merNo | String | 否 | 商户编码 |
| payStatus | Integer | 否 | 支付状态：1-待付款，2-已付款，3-已取消，4-已过期 |
| startTime | Long | 否 | 开始时间戳 |
| endTime | Long | 否 | 结束时间戳 |
| keyword | String | 否 | 关键字搜索 |
| paymentMethod | String | 否 | 付款方式 |
| currency | String | 否 | 币种 |

#### 响应参数
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "platformId": 1,
        "platformName": "ZFX平台",
        "orderNo": "ORDER202409290001",
        "amount": 1000.00,
        "merName": "测试商户",
        "merNo": "MER001",
        "currency": "USD",
        "country": "US",
        "subMerName": "子商户1",
        "paymentMethod": "bank",
        "accountName": "张三",
        "bankAccount": "6222****1234",
        "payStatus": 2,
        "payStatusStr": "已付款",
        "status": 2,
        "statusStr": "已确认",
        "payTime": 1696089600000,
        "payTimeLocal": "2024-09-29 10:00:00",
        "finishedTime": 1696089660000,
        "finishedTimeLocal": "2024-09-29 10:01:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

### 3.2 导出订单
- **接口地址**: `/orderInfo/exportExcel`
- **请求方式**: `POST`
- **接口描述**: 导出订单信息到Excel文件

#### 请求参数
```json
{
  "platformId": 1,
  "merNo": "MER001",
  "payStatus": 2,
  "startTime": 1696003200000,
  "endTime": 1696089600000,
  "keyword": "订单号或商户名称",
  "paymentMethod": "bank",
  "currency": "USD"
}
```

#### 响应参数
返回Excel文件流，文件名：`订单列表.xlsx`

### 3.3 上传回单
- **接口地址**: `/orderInfo/receipt/upload`
- **请求方式**: `POST`
- **接口描述**: 上传支付回单凭证

#### 请求参数
```json
{
  "orderId": 1,
  "receiptFileId": 123,
  "remark": "支付凭证上传"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | Long | 是 | 订单ID |
| receiptFileId | Long | 是 | 回单文件ID |
| remark | String | 否 | 备注信息 |

#### 响应参数
```json
{
  "code": 1,
  "msg": "回单上传成功",
  "data": "上传成功"
}
```

### 3.4 查询失败订单记录
- **接口地址**: `/orderInfo/fail/record`
- **请求方式**: `POST`
- **接口描述**: 查询失败订单记录

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "orderNo": "ORDER202409290001",
  "startTime": 1696003200000,
  "endTime": 1696089600000
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "orderNo": "ORDER202409290001",
        "failReason": "银行卡信息错误",
        "failTime": 1696089600000,
        "retryCount": 3,
        "status": 1
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

## 4. 商户管理接口

### 4.1 分页查询商户
- **接口地址**: `/merchant/queryPage`
- **请求方式**: `POST`
- **接口描述**: 分页查询商户信息

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "merchantName": "测试商户",
  "merchantCode": "MER001",
  "status": 1,
  "startTime": 1696003200000,
  "endTime": 1696089600000
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |
| merchantName | String | 否 | 商户名称 |
| merchantCode | String | 否 | 商户编码 |
| status | Integer | 否 | 商户状态：1-正常，0-禁用 |
| startTime | Long | 否 | 开始时间 |
| endTime | Long | 否 | 结束时间 |

#### 响应参数
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "merchantName": "测试商户",
        "merchantCode": "MER001",
        "contactName": "张三",
        "contactPhone": "138****1234",
        "contactEmail": "test@example.com",
        "status": 1,
        "statusStr": "正常",
        "createTime": "2024-09-29 10:00:00"
      }
    ],
    "total": 50,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

### 4.2 添加商户
- **接口地址**: `/merchant/add`
- **请求方式**: `POST`
- **接口描述**: 添加新商户

#### 请求参数
```json
{
  "merchantName": "新商户",
  "merchantCode": "MER002",
  "contactName": "李四",
  "contactPhone": "139****5678",
  "contactEmail": "new@example.com",
  "businessLicense": "营业执照号",
  "remark": "备注信息"
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "商户添加成功",
  "data": {
    "merchantId": 2
  }
}
```

## 5. 支付渠道管理接口

### 5.1 分页查询支付渠道
- **接口地址**: `/paymentChannel/queryPage`
- **请求方式**: `POST`
- **接口描述**: 分页查询支付渠道信息

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "channelName": "银行渠道",
  "channelCode": "BANK001",
  "status": 1
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "channelName": "银行渠道",
        "channelCode": "BANK001",
        "channelType": "bank",
        "status": 1,
        "statusStr": "启用",
        "feeRate": 0.006,
        "createTime": "2024-09-29 10:00:00"
      }
    ],
    "total": 20,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

### 5.2 添加支付渠道
- **接口地址**: `/paymentChannel/add`
- **请求方式**: `POST`
- **接口描述**: 添加新的支付渠道

#### 请求参数
```json
{
  "merName": "新支付渠道",
  "merCode": "NEW001",
  "merAk": "商户号",
  "merSk": "商户秘钥",
  "domain": "https://example.com",
  "ext": "{\"appId\":\"123\",\"appSecret\":\"456\"}",
  "merUsername": "渠道持有人",
  "phone": "13800138000",
  "idNumber": "身份证号",
  "paymentFlag": 1,
  "paymentScale": 100,
  "paymentLimit": 10000.00,
  "paymentCount": 1000,
  "cashFlag": 1,
  "cashScale": 90,
  "cashLimit": 5000.00,
  "cashCount": 500,
  "workTime": "09:00-18:00",
  "interfaceType": 1,
  "implCode": "BANK_001",
  "xjFlag": 0,
  "status": 1
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| merName | String | 是 | 商户名称 |
| merCode | String | 是 | 商户编码 |
| merAk | String | 是 | 商户号 |
| merSk | String | 是 | 商户秘钥 |
| domain | String | 否 | 域名 |
| ext | String | 否 | 额外配置信息JSON |
| merUsername | String | 否 | 商户持有人姓名 |
| phone | String | 否 | 绑定手机号 |
| idNumber | String | 否 | 证件号 |
| paymentFlag | Integer | 否 | 支付标志 |
| paymentScale | Integer | 否 | 支付比例 |
| paymentLimit | Decimal | 否 | 支付限额 |
| paymentCount | Integer | 否 | 支付笔数限制 |
| cashFlag | Integer | 否 | 兑付标志 |
| cashScale | Integer | 否 | 兑付比例 |
| cashLimit | Decimal | 否 | 兑付限额 |
| cashCount | Integer | 否 | 兑付笔数限制 |
| workTime | String | 否 | 工作时间 |
| interfaceType | Integer | 否 | 接口类型(1-API方式，2-非API方式) |
| implCode | String | 否 | 实现类关联值 |
| xjFlag | Integer | 否 | 是否支持新疆用户(0-不支持 1-支持) |
| status | Integer | 否 | 状态(0-禁用；1-启用) |

#### 响应参数
```json
{
  "code": 1,
  "msg": "支付渠道添加成功",
  "data": {
    "channelId": 2
  }
}
```

### 5.3 更新支付渠道
- **接口地址**: `/paymentChannel/update`
- **请求方式**: `POST`
- **接口描述**: 更新支付渠道信息

#### 请求参数
```json
{
  "id": 1,
  "merName": "更新后的渠道名称",
  "merCode": "UPD001",
  "status": 1
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "支付渠道更新成功",
  "data": null
}
```

### 5.4 删除支付渠道
- **接口地址**: `/paymentChannel/delete/{channelId}`
- **请求方式**: `DELETE`
- **接口描述**: 删除支付渠道

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| channelId | Long | 是 | 渠道ID |

#### 响应参数
```json
{
  "code": 1,
  "msg": "支付渠道删除成功",
  "data": null
}
```

### 5.5 获取渠道选项列表
- **接口地址**: `/paymentChannel/options`
- **请求方式**: `GET`
- **接口描述**: 获取渠道选项列表，用于下拉选择

#### 请求参数
无

#### 响应参数
```json
{
  "code": 1,
  "msg": "获取成功",
  "data": [
    {
      "channelId": 1,
      "channelName": "银行渠道1",
      "merCode": "BANK001"
    },
    {
      "channelId": 2,
      "channelName": "二维码渠道1", 
      "merCode": "QR001"
    }
  ]
}
```

## 6. 结算管理接口

### 6.1 分页查询结算信息
- **接口地址**: `/settlement/queryPage`
- **请求方式**: `POST`
- **接口描述**: 分页查询结算信息

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "merchantId": 1,
  "settlementStatus": 1,
  "startTime": 1696003200000,
  "endTime": 1696089600000
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页大小 |
| merchantId | Long | 否 | 商户ID |
| settlementStatus | Integer | 否 | 结算状态：1-待结算，2-已结算，3-结算失败 |
| startTime | Long | 否 | 开始时间 |
| endTime | Long | 否 | 结束时间 |

#### 响应参数
```json
{
  "code": 1,
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "merchantId": 1,
        "merchantName": "测试商户",
        "settlementNo": "SETTLE202409290001",
        "settlementAmount": 50000.00,
        "feeAmount": 300.00,
        "actualAmount": 49700.00,
        "settlementStatus": 2,
        "settlementStatusStr": "已结算",
        "settlementTime": "2024-09-29 18:00:00",
        "createTime": "2024-09-29 10:00:00"
      }
    ],
    "total": 30,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

### 6.2 确认结算
- **接口地址**: `/settlement/confirm`
- **请求方式**: `POST`
- **接口描述**: 确认结算操作

#### 请求参数
```json
{
  "settlementId": 1,
  "confirmAmount": 49700.00,
  "remark": "确认结算"
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "结算确认成功",
  "data": null
}
```

## 7. 多因子认证接口

### 7.1 获取OTP二维码
- **接口地址**: `/employee/otp/qrcode`
- **请求方式**: `GET`
- **接口描述**: 获取OTP绑定二维码

#### 请求参数
无

#### 响应参数
```json
{
  "code": 1,
  "msg": "获取成功",
  "data": {
    "qrCodeUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "secretKey": "JBSWY3DPEHPK3PXP",
    "manualKey": "JBSWY3DPEHPK3PXP"
  }
}
```

### 7.2 绑定OTP
- **接口地址**: `/employee/otp/bind`
- **请求方式**: `POST`
- **接口描述**: 绑定OTP验证器

#### 请求参数
```json
{
  "secretKey": "JBSWY3DPEHPK3PXP",
  "verifyCode": "123456"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| secretKey | String | 是 | 秘钥 |
| verifyCode | String | 是 | 验证码 |

#### 响应参数
```json
{
  "code": 1,
  "msg": "OTP绑定成功",
  "data": null
}
```

### 7.3 验证OTP
- **接口地址**: `/employee/otp/verify`
- **请求方式**: `POST`
- **接口描述**: 验证OTP验证码

#### 请求参数
```json
{
  "verifyCode": "123456"
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "验证成功",
  "data": null
}
```

### 7.4 解绑OTP
- **接口地址**: `/employee/otp/unbind`
- **请求方式**: `POST`
- **接口描述**: 解绑OTP验证器

#### 请求参数
```json
{
  "verifyCode": "123456"
}
```

#### 响应参数
```json
{
  "code": 1,
  "msg": "OTP解绑成功",
  "data": null
}
```

## 8. 系统管理接口

### 8.1 获取用户信息
- **接口地址**: `/user/info`
- **请求方式**: `GET`
- **接口描述**: 获取当前登录用户信息

#### 请求参数
无

#### 响应参数
```json
{
  "code": 1,
  "msg": "获取成功",
  "data": {
    "userId": 1,
    "loginName": "admin",
    "actualName": "管理员",
    "email": "admin@example.com",
    "phone": "138****1234",
    "departmentId": 1,
    "departmentName": "技术部",
    "roleId": 1,
    "roleName": "超级管理员",
    "permissions": ["order:list", "order:export", "merchant:manage"]
  }
}
```

### 8.2 获取菜单列表
- **接口地址**: `/menu/list`
- **请求方式**: `GET`
- **接口描述**: 获取用户可访问的菜单列表

#### 请求参数
无

#### 响应参数
```json
{
  "code": 1,
  "msg": "获取成功",
  "data": [
    {
      "menuId": 1,
      "menuName": "订单管理",
      "menuType": 1,
      "parentId": 0,
      "path": "/order",
      "icon": "order",
      "children": [
        {
          "menuId": 11,
          "menuName": "订单列表",
          "menuType": 2,
          "parentId": 1,
          "path": "/order/list",
          "icon": "list"
        }
      ]
    }
  ]
}
```

## 9. 文件管理接口

### 9.1 文件上传
- **接口地址**: `/file/upload`
- **请求方式**: `POST`
- **接口描述**: 上传文件

#### 请求参数
- **Content-Type**: `multipart/form-data`
- **file**: 文件流

#### 响应参数
```json
{
  "code": 1,
  "msg": "上传成功",
  "data": {
    "fileId": 123,
    "fileName": "receipt.jpg",
    "fileSize": 1024000,
    "fileUrl": "/files/receipt_123.jpg"
  }
}
```

### 9.2 文件下载
- **接口地址**: `/file/download/{fileId}`
- **请求方式**: `GET`
- **接口描述**: 下载文件

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

#### 响应参数
返回文件流

## 10. 接口调用示例

### 10.1 JavaScript 调用示例
```javascript
// 设置请求头
const headers = {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer ' + token
};

// 查询订单列表
const queryOrders = async () => {
  const response = await fetch('/api/v1/orderInfo/queryPage', {
    method: 'POST',
    headers: headers,
    body: JSON.stringify({
      pageNum: 1,
      pageSize: 20,
      payStatus: 2
    })
  });
  const result = await response.json();
  return result;
};

// 上传回单
const uploadReceipt = async (orderId, fileId) => {
  const response = await fetch('/api/v1/orderInfo/receipt/upload', {
    method: 'POST',
    headers: headers,
    body: JSON.stringify({
      orderId: orderId,
      receiptFileId: fileId,
      remark: '支付凭证'
    })
  });
  const result = await response.json();
  return result;
};
```

### 10.2 Java 调用示例
```java
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public PageResult<PaymentOrderInfoListVO> queryOrders(PaymentOrderInfoQueryForm form) {
        String url = "http://localhost:1681/orderInfo/queryPage";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<PaymentOrderInfoQueryForm> request = new HttpEntity<>(form, headers);
        
        ResponseEntity<ResponseDTO<PageResult<PaymentOrderInfoListVO>>> response = 
            restTemplate.postForEntity(url, request, 
                new ParameterizedTypeReference<ResponseDTO<PageResult<PaymentOrderInfoListVO>>>() {});
        
        return response.getBody().getData();
    }
}
```

## 11. 错误处理

### 11.1 常见错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 30007 | 未登录或登录失效 | 重新登录获取token |
| 30008 | 账号已在别处登录 | 检查账号是否在其他地方登录 |
| 40001 | 参数错误 | 检查请求参数格式和必填项 |
| 40003 | 权限不足 | 联系管理员分配权限 |
| 50001 | 系统内部错误 | 联系技术支持 |

### 11.2 错误响应示例
```json
{
  "code": 40001,
  "msg": "参数错误：订单ID不能为空",
  "data": null,
  "ok": false
}
```

---

**文档版本**: v1.0  
**创建日期**: 2024-09-29  
**更新日期**: 2024-09-29  
**创建人**: AI Assistant  
**审核人**: 待定
