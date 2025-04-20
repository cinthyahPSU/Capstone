plugins {
    alias(libs.plugins.android.application)
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.example.coworkingfinds"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.coworkingfinds"
        minSdk = 26
        targetSdk = 34
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
}



dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation("com.google.firebase:firebase-auth:22.3.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }

    implementation("com.google.firebase:firebase-firestore:25.1.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }

    implementation ("com.google.protobuf:protobuf-javalite:3.21.12")

    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.espresso.intents)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.android.volley:volley:1.2.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-inline:4.6.1")
    testImplementation("org.robolectric:robolectric:4.11")

    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
}




apply(plugin = "com.google.gms.google-services")
configurations.all {
    resolutionStrategy {
        force ("com.google.protobuf:protobuf-javalite:3.24.0")
    }
}