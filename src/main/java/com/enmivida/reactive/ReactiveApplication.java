package com.enmivida.reactive;

import com.enmivida.reactive.flux.FluxAndMono;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ReactiveApplication implements CommandLineRunner {

    private final FluxAndMono fluxAndMono;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
		//  fluxAndMono.namesFlux();
		// log.info("--------------------------------------------------");
		// fluxAndMono.namesMono();
		//fluxAndMono.namesFluxMap();
        //fluxAndMono.namesFluxImmutability();
       //fluxAndMono.namesFluxMap(3);
        //fluxAndMono.namesFluxFlatMapAsync(3);
        //fluxAndMono.namesFluxConcatMap(3);
        //fluxAndMono.namesFluxTransformSwitchIfEmpty(6);
        fluxAndMono.exploreZipAgain();
    }
}
