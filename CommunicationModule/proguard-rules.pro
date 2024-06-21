# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#........

#-keep public class MyClass
-keepattributes *Annotation*

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Top-level functions that can only be used by Kotlin.
#-dontwarn retrofit2.-KotlinExtensions

-keep class retrofit2.** { *; }

#ExoPlayer
-keep class com.google.android.exoplayer.** { *; }

#Glide removed after enabling R8 https://github.com/bumptech/glide#r8--proguard
#-keep public class * implements com.bumptech.glide.module.GlideModule

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement #TODO check

 #Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { <fields>; }
-keep class com.thenudge.uat.database.model.** { <fields>; }


##---------------End: proguard configuration for Gson  ----------


# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { *; }

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * { *; }
#-keepclassmembers enum com.your.package.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.google.** { *; }

# Room Database
-dontwarn androidx.room.paging.**
-keep class com.thenudge.uat.database.model.** {*; }

-dontwarn com.opencsv.**
-dontwarn org.apache.commons.beanutils.**
-dontwarn org.apache.commons.collections.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontoptimize