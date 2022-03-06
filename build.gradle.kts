// Top-level build file where you can add configuration options common to all sub-projects/modules.

ext {
    set("butterKnifeVersion", "8.5.1")
    set("state_version", "1.2.0")
    set("lombokVersion", "1.16.20")
    set("supportVersion", "27.1.0")
    set("gms", "12.0.0")
    set("thirtyinchVersion", "0.8.0")
    set("retrofit", "2.3.0")
    set("junitVersion", "4.12")
    set("mockitoVersion", "1.10.19")
    set("assertjVersion", "2.5.0")
    set("espresseVersion", "2.2.2")
    set("requery", "1.3.2")
    set("kotlin_version", "1.6.10")
    set("commonmark", "0.10.0")
    set("glideVersion", "3.7.0")
}
buildscript {
    repositories {
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/jcenter")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.0-alpha04")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.novoda:gradle-build-properties-plugin:0.3")
        classpath("com.dicedmelon.gradle:jacoco-android:0.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20-RC")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter")
    }
}
