# 主卡通讯SDK
**使用主卡通讯SDK之前，需要先集成 IpuSDK(https://github.com/ipulian/ipusdk)** 
有一些公共方法，在IpuSDK中已经做了说明，这里不再赘述。
首先可以通过 https://github.com/ipulian/sim-module.git 把该项目在Android Studio中直接运行。
## Description
集成主卡通讯的SDK后，在您的app内通过SDK外呼后，SDK会主动检查系统的外呼记录，并把该记录同步到SDK内。如果不通过SDK外呼，则SDK不会读取该记录。
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
 <uses-permission android:name="android.permission.READ_CALL_LOG" />

 <application android:requestLegacyExternalStorage="true"></application>
```
## Usage
- 1.主卡外呼(需要先申请android.permission.CALL_PHONE权限)
```java
    //方式1，通过回调返回结果。有关SimRiskControlBean的定义，参考SimRiskControlBean.class
    SimHttp.getInstance()
           .callPhoneBySim(String phone, OnSimCallPhoneResultListener<SimRiskControlBean> listener);
    //方式2，由SDK处理相关返回值。包括三种可能结果，1.直接外呼，2.Dialog提示msg,并禁止外呼，3.Dialog提示msg,并给出选项是否继续外呼。
    SimHttp.getInstance().callPhoneBySim(String phone)
```
- 2.主卡发送短信
```java
SimPhoneManager.sendSms(String phone);
//OR
SimPhoneManager.sendSms(String phone,String content);
```
- 3.查询记录(数据存储和查询采用的是Room和RxJava的方式，当数据库中的数据发生变化时，会自动回调IObserver方法)
```
    /**
     * 查询等待上传的记录
     */
    public static void queryWaitingList(int page, IObserver<List<SysRecording>> observer);
    /**
     * 查询上传成功的记录
     */
    public static void querySucceedList(int page, IObserver<List<SysRecording>> observer);
    /**
     * 查询上传失败的记录
     */
    public static void queryFailedList(int page, IObserver<List<SysRecording>> observer);
    /**
     * 查询正在上传的记录
     */
    public static void queryUploadingList(int page, IObserver<List<SysRecording>> observer);
    /**
     * 根据状态和页数查询记录
     */
    public static void queryByStatusForListPage(List<Integer> uploadStatus, int page,
                                                IObserver<List<SysRecording>> observer);
    
```
- 3.获取通话状态,可以参考 IpuSDK(https://github.com/ipulian/ipusdk) 中的说明。
- 4.展示通话弹屏,可以参考 IpuSDK(https://github.com/ipulian/ipusdk) 中的说明。
- 5.查看通话记录，使用拨号键盘，查看电话统计，客户统计，综合排名等功能，可以跳转到内部的H5页面
```java
startActivity(new Intent(this, IpuWebViewActivity.class));
```
## ProGuard rules
```
-keep class com.ipusoft.sim.bean.** { *;}
-keep class com.ipusoft.sim.constant.** { *;}
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
