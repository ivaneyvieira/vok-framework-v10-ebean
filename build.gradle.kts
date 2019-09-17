plugins {
    `build-scan`
    kotlin("jvm") version "1.3.50"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("eu.vaadinonkotlin:vok-framework:0.7.1")
    compile("eu.vaadinonkotlin:vok-util-vaadin10:0.7.1")
    compile("io.ebean:ebean:11.45.1")
}