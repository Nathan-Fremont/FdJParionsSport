plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.nathanfremont.data"
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
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_URL", "\"https://www.thesportsdb.com/api/v1/json/\"")
            buildConfigField("String", "API_KEY", "\"50130162\"")
        }

        debug {
            isMinifyEnabled = false
            buildConfigField("String", "API_URL", "\"https://www.thesportsdb.com/api/v1/json/\"")
            buildConfigField("String", "API_KEY", "\"50130162\"")
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
    implementation(project(":domain"))
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