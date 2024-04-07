plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kaptPlugins)
    alias(libs.plugins.hiltAndroid)
//    alias(libs.plugins.kspPlugins)
}

android {
    namespace = "com.example.basecomposeapplication"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.basecomposeapplication"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://api.agify.io/\"")
        }
        release {
            isMinifyEnabled = false
//            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*aar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.security)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.window)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.compose.material.core)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.test)

    implementation(libs.timber)

    implementation(libs.dimension.sdp)
    implementation(libs.dimension.ssp)

    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.paging.runtime)
    implementation(libs.paging.rx)

//    implementation(libs.ml.face.detection)
//    implementation(libs.ml.barcode.scanning)
//    implementation(libs.ml.image.labeling)
//    implementation(libs.ml.image.labeling.custom)
//    implementation(libs.ml.language.id)
//    implementation(libs.ml.text.recognition)
//    implementation(libs.ml.segmentation.selfie)
//    implementation(libs.ml.pose.detection)
//    implementation(libs.ml.pose.detection.accurate)
//    implementation(libs.ml.camera)
//    implementation(libs.ml.face.detection.gms)
//    implementation(libs.ml.barcode.scanning.gms)
//    implementation(libs.ml.image.labeling.gms)
//    implementation(libs.ml.language.id.gms)
//    implementation(libs.ml.text.recognition.gms)

    implementation(libs.square.retrofit)
    implementation(libs.square.gson)
    implementation(libs.square.rx.adapter)
    implementation(libs.square.okhttp)
    implementation(libs.square.okhttp.log)
    implementation(libs.square.okhttp.url)

//    implementation(libs.koin.core)
//    implementation(libs.koin.android)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.hardware.biometric)

    implementation(libs.google.gson)
    implementation(libs.google.material)
    implementation(libs.google.accompanist.appcompat.theme)
    implementation(libs.google.accompanist.swiperefresh)
    implementation(libs.google.accompanist.systemuicontroller)
    implementation(libs.google.accompanist.flowlayout)
    implementation(libs.google.location)

    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.extensions)
    implementation(libs.camera.exif)

    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}