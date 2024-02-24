plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.nathanfremont.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "IS_LOGGABLE", "false")
        }
        debug {
            buildConfigField("boolean", "IS_LOGGABLE", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.hilt)
    implementation(libs.timber)
    kapt(libs.hiltCompiler)
    kapt(libs.hiltAndroidCompiler)

    releaseImplementation(libs.bundles.retrofitAndOkHttpProd)
    debugImplementation(libs.bundles.retrofitAndOkHttpPreprod)

    testImplementation(project(":coreTest"))
    testImplementation(libs.bundles.commonTest)
    testImplementation(libs.testingJunitJupiterApi)
    kaptTest(libs.testingHilt)
    testRuntimeOnly(libs.testingJunitJupiterEngine)
}