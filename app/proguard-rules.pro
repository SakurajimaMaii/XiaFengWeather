#  排除 okhttp
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

#  排除 QWeather
-dontwarn com.qweather.sdk.**
-keep class com.qweather.sdk.** { *;}

# 对于 ViewBinding 的处理
-keep public interface androidx.viewbinding.ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding{
    *;
}

# 排除 VastActivity 的子类
-keep class * extends com.ave.vastgui.tools.activity.VastActivity { *;}

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder