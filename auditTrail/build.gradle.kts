//plugins {
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
//    kotlin("kapt")
//    id("dagger.hilt.android.plugin")
//}
//
//
//android {
//    namespace =  "com.nudge.auditTrail"
//    compileSdk = 34
//
//    defaultConfig {
//        minSdk = 24
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.1"
//    }
//}


plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id ("kotlin-kapt")
//    kotlin("kapt")
}

android {
    namespace = "com.nudge.auditTrail"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
//    buildFeatures {
//        compose = true
//    }
}

dependencies {
    val retrofit = "2.9.0"
    val room_version = "2.5.2"
    implementation(project(":core"))
    implementation(project(":internetSpeedChecker"))
    implementation ("com.squareup.retrofit2:retrofit:$retrofit")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit")

    //Room
    implementation ("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    androidTestImplementation ("androidx.room:room-testing:$room_version")
   //hilt
    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    //work
    implementation ("androidx.hilt:hilt-work:1.0.0")
//    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}