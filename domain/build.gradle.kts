plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.nathanfremont.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    productFlavors {
        flavorDimensions += "env"

        create("preprod") {
            dimension = "env"
        }

        create("prod") {
            dimension = "env"
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
    implementation(project(":common"))
    implementation(libs.hilt)
    implementation(libs.timber)
    kapt(libs.hiltCompiler)
    kapt(libs.hiltAndroidCompiler)

    testImplementation(project(":coreTest"))
    testImplementation(libs.bundles.commonTest)
    testImplementation(libs.testingJunitJupiterApi)
    kaptTest(libs.testingHilt)
    testRuntimeOnly(libs.testingJunitJupiterEngine)
}