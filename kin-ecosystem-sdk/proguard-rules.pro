#keep JSON schema genrated BI Enums and models
-keepclassmembers enum com.kin.ecosystem.** { *; }

# Preserve annotated Javascript interface methods. // just incase hosting app doesn't use getDefaultProguardFile
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
