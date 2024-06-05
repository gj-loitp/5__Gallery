import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
}

//val keystorePropertiesFile: File = rootProject.file("keystore.properties")
//val keystoreProperties = Properties()
//if (keystorePropertiesFile.exists()) {
//    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
//}

android {
    namespace = "com.mckimquyen.gallery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mckimquyen.gallery"
        minSdk = 23
        targetSdk = 34
        versionName = "2024.06.05"
        versionCode = 20240605
        setProperty("archivesBaseName", "Cat Gallery-$versionCode")
    }

//    signingConfigs {
//        if (keystorePropertiesFile.exists()) {
//            register("release") {
//                keyAlias = keystoreProperties.getProperty("keyAlias")
//                keyPassword = keystoreProperties.getProperty("keyPassword")
//                storeFile = file(keystoreProperties.getProperty("storeFile"))
//                storePassword = keystoreProperties.getProperty("storePassword")
//            }
//        }
//    }
    signingConfigs {
        register("release") {
            storeFile = file("keystores.jks")
            storePassword = "27072000"
            keyAlias = "mckimquyen"
            keyPassword = "27072000"
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
//        debug {
//            applicationIdSuffix = ".debug"
//        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            if (keystorePropertiesFile.exists()) {
//                signingConfig = signingConfigs.getByName("release")
//            }
            signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions.add("licensing")
    productFlavors {
        register("dev")
        register("prod")
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }

    compileOptions {
        val currentJavaVersionFromLibs = JavaVersion.valueOf(libs.versions.app.build.javaVersion.get())
        sourceCompatibility = currentJavaVersionFromLibs
        targetCompatibility = currentJavaVersionFromLibs
    }

    dependenciesInfo {
        includeInApk = false
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    packaging {
        resources {
            excludes += "META-INF/library_release.kotlin_module"
        }
    }
}

dependencies {
//    api(libs.fossify.commons)
    api("com.github.gj-loitp:lib_Commons:1.0.0")
    api(libs.android.image.cropper)
    api(libs.exif)
    api(libs.android.gif.drawable)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.media3.exoplayer)
    api(libs.sanselan)
    api(libs.imagefilters)
    api(libs.androidsvg.aar)
    api(libs.gestureviews)
    api(libs.subsamplingscaleimageview)
    api(libs.androidx.swiperefreshlayout)
    api(libs.awebp)
    api(libs.apng)
    api(libs.avif.integration)
    api(libs.okio)
    api(libs.picasso) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    compileOnly(libs.okhttp)
    ksp(libs.glide.compiler)
    api(libs.zjupure.webpdecoder)
    api(libs.bundles.room)
    //noinspection UseTomlInstead
    api("com.applovin:applovin-sdk:12.4.2")
    ksp(libs.androidx.room.compiler)
    //noinspection UseTomlInstead
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}
