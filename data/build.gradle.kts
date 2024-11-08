plugins {
    alias(libs.plugins.ucb.jvm.library)
}
dependencies {
    implementation(project(":domain"))
}
