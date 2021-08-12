plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.sky.medialib"
        minSdk = 21
        targetSdk = 27
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.blankj:utilcodex:1.30.6")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.guolindev.permissionx:permissionx:1.5.0")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.6")
    implementation("com.github.gzu-liyujiang.AndroidPicker:ImagePicker:3.1.0")
    implementation("tv.danmaku.ijk.media:ijkplayer-java:0.8.8")
    implementation("tv.danmaku.ijk.media:ijkplayer-armv7a:0.8.8")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.15")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.airbnb.android:lottie:3.3.0")

    implementation(project(mapOf("path" to ":media")))
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}