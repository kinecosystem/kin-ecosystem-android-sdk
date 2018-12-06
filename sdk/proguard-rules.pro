# Preserve annotated Javascript interface methods. // just incase hosting app doesn't use getDefaultProguardFile
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Remove logs code in release
-assumenosideeffects class Logger {
    public static void log(...);
}
