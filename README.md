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
    implementation 'com.qpg.aop:superaop:1.0'
    ...
}
```

注解预览
---


| 注解名称        | 作用          | 备注          |
| ------------- |:-------------:| :-------------:|
| @CacheTrace    |Spring Cache风格的Cache注解,将结果放于缓存中|只适用于android4.0以后|
| @CheckLoginTrace    |用于检查登陆|       |
| @CheckNetTrace    |用于检查是否连接网络|       |
| @HookMethodTrace   |可以在调用某个方法之前、以及之后进行hook|比较适合埋点的场景，可以单独使用也可以跟任何自定义注解配合使用。也支持在匿名内部类中使用|
| @PermissionTrace   |可用于运行时动态地申请权限|
| @PrefsTrace        |将方法返回的结果放入SharedPreferences中|只适用于android4.0以后|
| @SafeTrace         |可以安全地执行方法,而无需考虑是否会抛出运行时异常|支持在捕获异常的时候进行监听|
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
 - 将方法返回的结果放于缓存中
```java
 //  @CacheTrace(key = "user",expiry = 2000)//自定义缓存过期时间
    @CacheTrace(key = "user")
    private User saveUser() {
        User userInfo = new User();
        userInfo.setUid("111");
        userInfo.setToken("sfsdgwefc");
        userInfo.setName("乔少");
        return userInfo;
    }

    void getUser() {
        Cache cache = Cache.get(this);
        User user = (User) cache.getObject("user");
        System.out.println(user);
    }
    
```
 缓存注解2
 ---
 - 将方法返回的结果放入SharedPreferences中
```java
  @PrefsTrace(key = "user")
     private User saveUserByPrefs() {
         User userInfo = new User();
         userInfo.setUid("111");
         userInfo.setToken("sfsdgwefc");
         userInfo.setName("乔少");
         return userInfo;
     }
 
     void getUserByByPrefs() {
         AppPrefs appPrefs = AppPrefs.get(this);
         User user = (User) appPrefs.getObject("user");
         System.out.println(user);
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