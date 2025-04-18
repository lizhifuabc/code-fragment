
## 签名

- `请求参数` + `时间戳` + `密钥`拼接成一个字符串，然后通过`md5`等hash算法，生成sign。

- 设置一个合理的过期时间，比如：15分钟，一次请求，在15分钟之内是有效的，超过15分钟，API接口的网关服务会返回超过有效期的异常提示。


## ip白名单

只有在白名单中的ip地址，才能成功请求API接口，否则直接返回无访问权限。

ip白名单也可以加在API网关服务上。

## 限流

如果你的API接口被第三方平台调用了，这就意味着着，调用频率是没法控制的。

第三方平台调用你的API接口时，如果并发量一下子太高，可能会导致你的API服务不可用，接口直接挂掉。

由此，必须要对API接口做`限流`。

限流方法有三种：

1. 对请求ip做限流：比如同一个ip，在一分钟内，对`API接口总的请求次数`，不能超过10000次。
2. 对请求接口做限流：比如同一个ip，在一分钟内，对`指定的API接口`，请求次数不能超过2000次。
3. 对请求用户做限流：比如同一个`AK/SK用户`，在一分钟内，对API接口总的请求次数，不能超过10000次。

我们在实际工作中，可以通过`nginx`，`redis`或者`gateway`实现限流的功能。

## 参数校验

我们需要对API接口做`参数校验`，比如：校验必填字段是否为空，校验字段类型，校验字段长度，校验枚举值等等。

这样做可以拦截一些无效的请求。

比如在新增数据时，字段长度超过了数据字段的最大长度，数据库会直接报错。

但这种异常的请求，我们完全可以在API接口的前期进行识别，没有必要走到数据库保存数据那一步，浪费系统资源。

有些金额字段，本来是正数，但如果用户传入了负数，万一接口没做校验，可能会导致一些没必要的损失。

还有些状态字段，如果不做校验，用户如果传入了系统中不存在的枚举值，就会导致保存的数据异常。

由此可见，做参数校验是非常有必要的。

在Java中校验数据使用最多的是`hiberate`的`Validator`框架，它里面包含了@Null、@NotEmpty、@Size、@Max、@Min等注解。

用它们校验数据非常方便。

当然有些日期字段和枚举字段，可能需要通过自定义注解的方式实现参数校验。

## 统一返回值

业务系统在出现异常时，抛出业务异常的RuntimeException，其中有个message字段定义异常信息。

所有的API接口都必须经过API网关，API网关捕获该业务异常，然后转换成统一的异常结构返回，这样能统一返回值结构。

## 统一封装异常

我们的API接口需要对`异常`进行统一处理。

不知道你有没有遇到过这种场景：有时候在API接口中，需要访问数据库，但表不存在，或者sql语句异常，就会直接把sql信息在API接口中直接返回。

返回值中包含了`异常堆栈信息`、`数据库信息`、`错误代码和行数`等信息。

如果直接把这些内容暴露给第三方平台，是很危险的事情。

有些不法分子，利用接口返回值中的这些信息，有可能会进行sql注入或者直接脱库，而对我们系统造成一定的损失。

因此非常有必要对API接口中的异常做统一处理，把异常转换成这样：

```json
json 代码解读复制代码{
    "code":500,
    "message":"服务器内部错误",
    "data":null
}
```

返回码`code`是`500`，返回信息`message`是`服务器内部异常`。

这样第三方平台就知道是API接口出现了内部问题，但不知道具体原因，他们可以找我们排查问题。

我们可以在内部的日志文件中，把堆栈信息、数据库信息、错误代码行数等信息，打印出来。

我们可以在`gateway`中对异常进行拦截，做统一封装，然后给第三方平台的是处理后没有敏感信息的错误信息。

## 请求日志

在第三方平台请求你的API接口时，接口的请求日志非常重要，通过它可以快速的分析和定位问题。

我们需要把API接口的请求url、请求参数、请求头、请求方式、响应数据和响应时间等，记录到日志文件中。

最好有`traceId`，可以通过它串联整个请求的日志，过滤多余的日志。

当然有些时候，请求日志不光是你们公司开发人员需要查看，第三方平台的用户也需要能查看接口的请求日志。

这时就需要把日志落地到数据库，比如：`mongodb`或者`elastic search`，然后做一个UI页面，给第三方平台的用户开通查看权限。这样他们就能在外网查看请求日志了，他们自己也能定位一部分问题。

## 幂等设计

极短的时间内，第三方平台用相同的参数请求API接口多次，第一次请求数据库会新增数据，但第二次请求以后就不会新增数据，但也会返回成功。

这样做的目的是不会产生错误数据。

可以通过在`数据库`中增加`唯一索引`，或者在`redis`保存`requestId`和请求参来保证接口幂等性。


## 限制记录条数

对于对我提供的批量接口，一定要`限制请求的记录条数`。

通常情况下，建议一次请求中的参数，最多支持传入500条记录。

## 压测

`jmeter`或者`apache benc`对API接口做压力测试。

## 异步处理

一般的API接口的逻辑都是同步处理的，请求完之后立刻返回结果。

但有时候，我们的API接口里面的业务逻辑非常复杂，特别是有些批量接口，如果同步处理业务，耗时会非常长。

这种情况下，为了提升API接口的性能，我们可以改成`异步处理`。

在API接口中可以发送一条`mq消息`，然后直接返回成功。之后，有个专门的`mq消费者`去异步消费该消息，做业务逻辑处理。

直接异步处理的接口，第三方平台有两种方式获取到。

第一种方式是：我们`回调`第三方平台的接口，告知他们API接口的处理结果，很多支付接口就是这么玩的。

第二种方式是：第三方平台通过`轮询`调用我们另外一个查询状态的API接口，每隔一段时间查询一次状态，传入的参数是之前的那个API接口中的id集合。

## 数据脱敏

已用户手机号为例：`182****887`。

## 接口文档

说实话，一份完整的API接口文档，在双方做接口对接时，可以减少很多沟通成本，让对方少走很多弯路。

接口文档中需要包含如下信息：

1. 接口地址
2. 请求方式，比如：post或get
3. 请求参数和字段介绍
4. 返回值和字段介绍
5. 返回码和错误信息
6. 加密或签名示例
7. 完整的请求demo
8. 额外的说明，比如：开通ip白名单。

接口文档中最好能够统一接口和字段名称的命名风格，比如都用`驼峰标识`命名。

接口地址中可以加一个版本号v1，比如：v1/query/getCategory，这样以后接口有很大的变动，可以非常方便升级版本。

统一字段的类型和长度，比如：id字段用Long类型，长度规定20。status字段用int类型，长度固定2等。

统一时间格式字段，比如：time用String类型，格式为：yyyy-MM-dd HH:mm:ss。

接口文档中写明AK/SK和域名，找某某单独提供等。