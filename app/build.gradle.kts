plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("jacoco")
}

android {
    namespace = "com.example.coworkingfinds"
    compileSdk = 34
    testOptions {
        managedDevices {
            devices {
                create("pixel2api30", com.android.build.api.dsl.ManagedVirtualDevice::class.java) {
                    device = "Pixel 2"
                    apiLevel = 30
                    systemImageSource = "aosp" // or "google" for Play Store access
                }
            }
        }
    }
    defaultConfig {
        applicationId = "com.example.coworkingfinds"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["coverage"] = "true"
        testInstrumentationRunnerArguments["coverageFile"] = "/sdcard/coverage.ec"    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            enableUnitTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // 🔥 Firebase BoM manages all versions
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    implementation("com.google.firebase:firebase-auth") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    implementation("com.google.firebase:firebase-firestore") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    implementation("com.google.firebase:firebase-analytics")

    // 🧱 Protobuf version expected by Firestore 25.1.1
    implementation("com.google.protobuf:protobuf-javalite:3.25.1")
    testImplementation("com.google.protobuf:protobuf-javalite:3.25.1")
    androidTestImplementation("com.google.protobuf:protobuf-javalite:3.25.1")

    // 🗺 Maps & Places
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // UI, Glide, and Networking
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.support.annotations)

    // 🧪 Espresso UI Tests (exclude protobuf conflicts)
    implementation(libs.espresso.intents)
    androidTestImplementation("androidx.test:core:1.5.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    androidTestImplementation("androidx.test:rules:1.5.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    androidTestImplementation("androidx.test.ext:junit:1.1.5") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }

    // 🧪 JUnit & Mockito
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-inline:4.6.1")

    // 🧪 Robolectric (optional: exclude proto-lite too)
    testImplementation("org.robolectric:robolectric:4.11") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
}


configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.google.protobuf" && requested.name.contains("protobuf")) {
            useVersion("3.25.1")
        }
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.register<JacocoReport>("jacocoInstrumentedReport") {
    dependsOn("connectedDebugAndroidTest")

    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*"
    )

    val debugTree = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
        exclude(fileFilter)
    }

    val mainSrc = "$projectDir/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(layout.buildDirectory.dir("outputs/code-coverage/connected").map {
        fileTree(it) {
            include("**/*.ec")
        }
    })

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}



