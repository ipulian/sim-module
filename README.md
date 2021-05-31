# 主卡通讯SDK
**使用主卡通讯SDK之前，需要先集成 IpuSDK(https://github.com/ipulian/ipusdk)** 
有一些公共方法，在IpuSDK中已经做了说明，这里不再赘述。
首先可以通过 https://github.com/ipulian/sim-module.git 把该项目在Android Studio中直接运行。
## Setup
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.ipulian:sim-module:latest-version'//使用时把 latest-version 替换成最新release版本
}
```
在AndroidManifest.xml中注册需要的权限
```xml
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.CALL_PHONE" />
 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

 <application android:requestLegacyExternalStorage="true></application>
```
## Usage
- 1. 主卡外呼(需要先申请android.permission.CALL_PHONE权限)
```java
    
````
或者通过RxJava3 的Observer 返回bindingInfo

```java
XPhoneHttp.queryXPhone(string, new Observer<BindingInfo>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                
            }

            @Override
            public void onNext(@NonNull BindingInfo bindingInfo) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
````
- 2. 获取通话状态,可以参考 IpuSDK(https://github.com/ipulian/ipusdk) 中的说明。
- 3. 展示通话弹屏,可以参考 IpuSDK(https://github.com/ipulian/ipusdk) 中的说明。
- 4. 查看通话记录，使用拨号键盘，查看电话统计，客户统计，综合排名等功能，可以跳转到内部的H5页面
```java
startActivity(new Intent(this, IpuWebViewActivity.class));
```
## ProGuard rules
```pro
-keep class com.ipusoft.xlibrary.** { *;}
```
# License
```
MIT License

Copyright (c) 2021 ipulian

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
