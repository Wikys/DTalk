plugins {
    id("com.android.application")
    id ("com.google.gms.google-services")

}

android {
    namespace = "com.example.dtalk"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.dtalk"
        minSdk = 33
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.gms:play-services-auth:19.2.0")
    implementation ("io.github.ParkSangGwon:tedpermission-normal:3.3.0")
    implementation ("com.google.android.gms:play-services-auth:20.1.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:29.2.0"))

    // Add the dependency for the Firebase SDK for Google Analytics
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation ("com.google.firebase:firebase-analytics")

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // Declare the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation ("com.google.firebase:firebase-auth")
    implementation ("androidx.activity:activity:1.3.0-alpha08")
    implementation ("androidx.fragment:fragment:1.4.0-alpha01")
    // Retrofit2
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Gson
    implementation ("com.google.code.gson:gson:2.10.1")
    //glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")

}