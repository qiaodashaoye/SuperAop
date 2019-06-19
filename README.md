# SuperAop

AOP在安卓端的实现，让程序员只关注于业务

- 项目地址：[https://github.com/qiaodashaoye/SuperAop](https://github.com/qiaodashaoye/SuperAop)

集成方法
---
在app 模块目录下的build.gradle中添加
```groovy
apply plugin: 'android-aspectjx'

...

dependencies {
    implementation 'com.qpg.aop:superaop:1.0.1'
    ...
}
```

注解预览
---


| 注解名称        | 作用          | 备注          |
| ------------- |:-------------:| :-------------:|
| @AsyncTrace    |异步操作|      |
| @CacheTrace    |Spring Cache风格的Cache注解,将结果放于缓存中|只适用于android4.0以后|
| @DeleteCacheTrace    |用于清除缓存|         |
| @CheckLoginTrace    |用于检查登陆|       |
| @CheckNetTrace    |用于检查是否连接网络|       |
| @DelayTrace    |延迟操作|       |
| @DelayAwayTrace    |移除延迟任务|       |
| @HookMethodTrace   |可以在调用某个方法之前、以及之后进行hook|比较适合埋点的场景，可以单独使用也可以跟任何自定义注解配合使用。也支持在匿名内部类中使用|
| @PermissionTrace   |可用于运行时动态地申请权限|
| @PrefsTrace        |将方法返回的结果放入SharedPreferences中|只适用于android4.0以后|
| @DeletePrefsTrace        |用于删除sp缓存|        |
| @RetryTrace         |重试操作|      |
| @SafeTrace         |可以安全地执行方法,而无需考虑是否会抛出运行时异常|支持在捕获异常的时候进行监听|
| @ScheduledTrace         |定时任务|   |
| @SingleClickTrace        |用于防止按钮的重复点击（事件防抖）       |


 用法实例：
 ---

第一步需要在application中进行全局初始化以及添加全局相关配置，具体使用如下：
> 简单初始化
```
SuperAop.getInstance().init(this);
        
```
> 如果使用本库中的登陆检测注解，需用以下配置
```
  SuperAop.getInstance().init(this, new ILogin() {
              @Override
              public void login(Context context, int actionDefine) {
                  switch (actionDefine) {
                      case 0:
                      //    context.startActivity(new Intent(context,LoginActivity.class));
                          break;
                      case 1:
                          ToastUtil.showCustomToast("请登录后操作");
                          break;
                  }
              }
  
              @Override
              public boolean isLogin(Context context) {
                  if(false){
                      return true;
                  }
                  return false;
              }
          });

```
异步操作
---

```
    @Async
    public void asyn() {
        Log.e(TAG, "useAync: "+Thread.currentThread().getName());
    }
```
 网络判断注解
 ---

在需要网络检测的方法上添加@CheckNetTrace即可

 按钮防抖注解
 ---

在按钮点击所要执行的方法上添加@SingleClickTrace，默认防抖时间是500毫秒，若要自定义时间应@SingleClickTrace(1000)
或@SingleClickTrace(value=1000)

 登陆检测注解
 ---

在按钮点击所要执行的方法上添加@CheckLoginTrace，登陆检测注解可以自定义事件，具体用法可看demo或添加群进行交流

 缓存注解1
 ---
 - 将方法返回的结果放于缓存中(可存任意类型数据)
```java
 
   1、插入缓存
   //  @CacheTrace(key = "user",expiry = 2000)//自定义缓存过期时间
       @CacheTrace(key = "user")
          private User saveUserByCache() {
              User userInfo = new User();
              userInfo.setUid("111");
              userInfo.setToken("sfsdgwefc");
              userInfo.setName("乔少");
              return userInfo;
          }
       
   2、获取缓存
       void getUserByCache() {
              ACache cache = ACache.get(this);
              User user = (User) cache.getAsObject("user");
              System.out.println(user);
          }
   3、移除缓存
         @DeleteCacheTrace(key = "user")
          private void removeUser() {
      
          }
```
 缓存注解2
 ---
 - 将方法返回的结果放入SharedPreferences中
```java
 1、保存key到sp
      @PrefsTrace(key = "user")
         private User saveUserByPrefs() {
             User userInfo = new User();
             userInfo.setUid("111");
             userInfo.setToken("sfsdgwefc");
             userInfo.setName("乔少");
             return userInfo;
         }
     
 2、从sp中获取值
     void getUserByByPrefs() {
            User user = SPUtil.get(this, "user", null);
            System.out.println(user);
        }
     
    3、从sp中移除key
    @DeleteSpTrace(key = "user")
        public void removeUserBySP() {
        }

```
延时任务
---
```java
       //开启延迟任务（10s后执行该方法）
       @DelayTrace(key = "test", delay = 10000L)
       public void delay() {
       }
   
       //移除延迟任务
       @DelayAwayTrace(key = "test")
       public void cancelDelay() {
       }
```
定时任务
---
```java
     /**
     * @param interval 初始化延迟
     * @param interval 时间间隔
     * @param timeUnit 时间单位
     * @param count 执行次数
     * @param taskExpiredCallback 定时任务到期回调，其值要保持和自定义的回调方法一致
     */
    @ScheduledTrace(interval = 1000L, count = 10, taskExpiredCallback = "taskExpiredCallback")
    public void scheduled() {
        Log.e(TAG, "scheduled: >>>>");
    }
    
    private void taskExpiredCallback(){
        Log.e(TAG, "taskExpiredCallback: >>>>");
    }
```
重试机制
---
```java
     /**
     * @param count 重试次数
     * @param delay 每次重试的间隔
     * @param asyn 是否异步执行
     * @param retryCallback 自定义重试结果回调
     * @return 当前方法是否执行成功
     */
    @RetryTrace(count = 3, delay = 1000, asyn = true, retryCallback = "retryCallback")
    public boolean retry() {
        Log.e(TAG, "retryDo: >>>>>>"+Thread.currentThread().getName());
        return false;
    }
    
    private void retryCallback(boolean result){
        Log.e(TAG, "retryCallback: >>>>"+result);
    }
```
try-catch安全处理
---
```java
    //自动帮你try-catch   允许你定义回调方法
    @SafeTrace(callBack = "throwMethod")
    public void safe() {
        String str = null;
        str.toString();
    }
    
    //自定义回调方法（注意要和callBack的值保持一致）
    private void throwMethod(Throwable throwable){
        Log.e(TAG, "throwMethod: >>>>>"+throwable.toString());
    }
```

Hook注解
---
- 在调用某个方法之前、以及之后进行hook
不写beforeMethod和afterMethod，则相当于没有使用@HookMethod<br>
beforeMethod和afterMethod对应的都是方法名，分别表示在调用doSomething()之前执行和之后执行。目前还不支持在beforeMethod和afterMethod中传递参数。

```Java
   @HookMethodTrace(beforeMethod="dosthbeforeMethod",afterMethod="dosthafterMethod")
   void doSomething() {

   }
```

@HookMethodTrace 同样支持在匿名内部类中使用

```java
    @HookMethodTrace(beforeMethod = "method1",afterMethod = "method2")
    private void initData() {

        L.i("initData()");
    }

    private void method1() {
        L.i("method1() is called before initData()");
    }

    private void method2() {
        L.i("method2() is called after initData()");
    }

    private void testRx() {

        Observable.just("asdf")
                .subscribe(new Consumer<String>() {

                    @HookMethodTrace(beforeMethod = "testRxBefore")
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        System.out.println("s="+s);
                    }

                    private void testRxBefore() {
                        L.i("testRxBefore() is called before accept()");
                    }
                });
```

### 1、申请单个权限
申请单个权限：
```
btn_click.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        callMap();
    }
});

/**
 * 申请权限
 */
@PermissionTrace(value = {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode = 0)
private void callMap() {
    Toast.makeText(this, "定位权限申请通过", Toast.LENGTH_SHORT).show();
}
```
@PermissionTrace后面的value代表需要申请的权限，是一个String[]数组；requestCode是请求码，是为了区别开同一个Activity中有多个不同的权限请求，默认是0，如果同一个Activity中只有一个权限申请，requestCode可以忽略不写。

```
/**
 * 权限被取消
 *
 * @param bean CancelBean
 */
@PermissionCanceledTrace
public void dealCancelPermission(CancelBean bean) {
    Toast.makeText(this, "requestCode:" + bean.getRequestCode(), Toast.LENGTH_SHORT).show();
}
```
声明一个public方法接收权限被取消的回调，**方法必须有一个CancelBean类型的参数**，这点类似于EventBus，CancelBean中有requestCode变量，即是我们请求权限时的请求码。
```
/**
 * 权限被拒绝
 *
 * @param bean DenyBean
 */
@PermissionDeniedTrace
public void dealPermission(DenyBean bean) {
        Toast.makeText(this,
        "requestCode:" + bean.getRequestCode()+ ",Permissions: " + Arrays.toString(bean.getDenyList().toArray()), Toast.LENGTH_SHORT).show();
  }
```
声明一个public方法接收权限被取消的回调，**方法必须有一个DenyBean类型的参数**，DenyBean中有一个requestCode变量，即是我们请求权限时的请求码，另外还可以通过denyBean.getDenyList()来拿到被权限被拒绝的List。

### 2、申请多个权限

基本用法同上，区别是@PermissionTrace后面声明的权限是多个，如下：
```
/**
 * 申请多个权限
 */
@PermissionTrace(value = {Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA}, requestCode = 10)
public void callPhone() {
    Toast.makeText(this, "电话、相机权限申请通过", Toast.LENGTH_SHORT).show();
}
```
value中声明了两个权限，一个电话权限，一个相机权限
## Proguard
```
 -keep class com.qpg.aop.** { *; }
```
## 交流方式
 * QQ: 1451449315
 * QQ群: 122619758
 
 ## Licenses
 ```
  Copyright 2017 qpg
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 ```