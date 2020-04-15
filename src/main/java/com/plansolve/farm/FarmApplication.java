package com.plansolve.farm;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.boot.web.servlet.ServletComponentScan;
        import org.springframework.cache.annotation.EnableCaching;
        import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
@EnableScheduling
public class FarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmApplication.class, args);
    }
}
