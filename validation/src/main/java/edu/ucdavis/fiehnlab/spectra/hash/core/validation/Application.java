package edu.ucdavis.fiehnlab.spectra.hash.core.validation;

import edu.ucdavis.fiehnlab.spectra.hash.core.Splash;
import edu.ucdavis.fiehnlab.spectra.hash.core.SplashFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Application {

    private Splash splash = SplashFactory.create();

    /**
     * main method to launch this applications
     *
     * @param args
     */
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}
