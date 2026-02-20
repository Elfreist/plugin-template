/**
 * NOTE: This is entirely optional and basics can be done in `settings.gradle.kts`
 */

repositories {
    // Any external repositories besides: MavenLocal, MavenCentral, HytaleMaven, and CurseMaven
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
