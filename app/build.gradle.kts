import java.util.Properties

val secretsFile = rootProject.file("secret.properties")
val properties = Properties().apply {
    load(secretsFile.inputStream())
}

plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

            buildConfigField("String", "CREATE_ACCOUNT_API", "\"${properties["CREATE_ACCOUNT_API"]}\"")
            buildConfigField("String", "LOGIN_API", "\"${properties["LOGIN_API"]}\"")
            buildConfigField("String", "GET_HISTORYLIST_API", "\"${properties["GET_HISTORYLIST_API"]}\"")
            buildConfigField("String", "SAVE_RESULT_API", "\"${properties["SAVE_RESULT_API"]}\"")
            buildConfigField("String", "S3_BUCKET_NAME", "\"${properties["S3_BUCKET_NAME"]}\"")
            buildConfigField("String", "S3_IDENTITY_POOL_ID", "\"${properties["S3_IDENTITY_POOL_ID"]}\"")
        }

        debug{
            buildConfigField("String", "CREATE_ACCOUNT_API", "\"${properties["CREATE_ACCOUNT_API"]}\"")
            buildConfigField("String", "LOGIN_API", "\"${properties["LOGIN_API"]}\"")
            buildConfigField("String", "GET_HISTORYLIST_API", "\"${properties["GET_HISTORYLIST_API"]}\"")
            buildConfigField("String", "SAVE_RESULT_API", "\"${properties["SAVE_RESULT_API"]}\"")
            buildConfigField("String", "S3_BUCKET_NAME", "\"${properties["S3_BUCKET_NAME"]}\"")
            buildConfigField("String", "S3_IDENTITY_POOL_ID", "\"${properties["S3_IDENTITY_POOL_ID"]}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        implementation("com.squareup.okhttp3:okhttp:4.9.1")

    }
}

dependencies {
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    implementation ("org.json:json:20210307")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.google.mediapipe:tasks-vision:latest.release")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.amazonaws:aws-android-sdk-core:2.57.0")
    implementation ("com.amazonaws:aws-android-sdk-s3:2.57.0")
    implementation ("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.57.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}

