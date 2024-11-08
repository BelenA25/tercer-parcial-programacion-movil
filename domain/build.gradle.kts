plugins {
    alias(libs.plugins.ucb.jvm.library)
    alias(libs.plugins.kotlinSerialization)
}
dependencies {
    //serialization
    implementation(libs.kotlinx.serialization.json)
}
