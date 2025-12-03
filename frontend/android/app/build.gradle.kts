plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.example.frontend"
    compileSdk = 35 //flutter.compileSdkVersion
    ndkVersion = "27.0.12077973"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.example.frontend"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = 23
        targetSdk = 35 //flutter.targetSdkVersion
        versionCode = 1
        versionName = "1.0"
    }
}

    /*  
    buildTypes {
        release {
            // TODO: Add your own signing config for the release build.
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")

            // 프로가드/난독화
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        debug {
            // 디버그에서도 사이즈 줄이고 싶으면 활성화 가능
            // minifyEnabled false
        }
    }
    */
    /*
    // Kotlin/JVM 설정
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }

    // NDK나 packaging 충돌 피하기(필요 시)
    packagingOptions {
        resources {
            excludes += [
                "META-INF/*",
                "kotlin/**"
            ]
        }
    }
    */
//}

repositories {
    google()
    mavenCentral()
    // FoodLens SDK가 별도 레포를 쓰면 아래 활성화
    // maven { url "https://<foodlens-maven-url>" }
}

/* 
dependencies {
    // Flutter 의존성은 flutter.gradle이 주입
    //implementation "org.jetbrains.kotlin:kotlin-stdlib:1.9.24"

    // CameraX
    //implementation "androidx.camera:camera-core:1.3.4"
    //implementation "androidx.camera:camera-camera2:1.3.4"
    //implementation "androidx.camera:camera-lifecycle:1.3.4"
    //implementation "androidx.camera:camera-view:1.3.4"

    // EXIF 회전 보정 시 유용
    implementation "androidx.exifinterface:exifinterface:1.3.7"

    // JSON 변환 시(Gson 또는 kotlinx.serialization 중 택1)
    implementation "com.google.code.gson:gson:2.11.0"

    // FoodLens Network SDK (실제 좌표/버전으로 교체)
    // implementation "com.foodlens:network-sdk:<version>"
}
*/

flutter {
    source = "../.."
}
