plugins {
    alias(libs.plugins.ucb.android.library)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.framework"
}

dependencies {
    implementation(libs.retrofit)
    //networking bundle
    implementation(libs.bundles.networking)

    //local bundle room
    implementation(libs.bundles.local)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    testImplementation(libs.room.testing)

    implementation(project(":domain"))
    implementation(project(":data"))
}
