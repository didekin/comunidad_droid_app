# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/pedro/Documents/software/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

-dontwarn javax.annotation.**
# Guava
-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.Unsafe
-keep class com.google.j2objc.annotations.** { *; }
-dontwarn   com.google.j2objc.annotations.**
-keep class java.lang.ClassValue { *; }
-dontwarn   java.lang.ClassValue
-keep class org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement { *; }
-dontwarn   org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement