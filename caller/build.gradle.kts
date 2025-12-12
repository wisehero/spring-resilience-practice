dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // OpenFeign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Resilience with Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
}