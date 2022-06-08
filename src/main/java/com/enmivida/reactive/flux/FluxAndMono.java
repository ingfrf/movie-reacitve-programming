package com.enmivida.reactive.flux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class FluxAndMono {

    private static final List<String> list = Stream.of("alex", "ben", "chloe").collect(Collectors.toList());

    public void namesFlux() {
        Flux<String> f = Flux.fromIterable(list).log(); // behind the scenes
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesMono() {
        Mono<String> m = Mono.just("alex").log();
        m.subscribe(name -> log.info("Mono Name is {}", name));
    }

    public void namesFluxMap(int length) {
        Flux<String> f = Flux.fromIterable(list)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                //.log()
                ;
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesFluxFlatMap(int length) {
        Flux<String> f = Flux.fromIterable(list)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .flatMap(s -> splitString(s))
                //.log()
                ;
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesFluxFlatMapAsync(int length) {
        Flux<String> f = Flux.fromIterable(list)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .flatMap(s -> splitStringDelayed(s))
                //.log()
                ;
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesFluxConcatMap(int length) {
        Flux<String> f = Flux.fromIterable(list)
                .filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .concatMap(s -> splitStringDelayed(s))
                //.log()
                ;
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesFluxImmutability() {
        Flux<String> f = Flux.fromIterable(list);
        f.map(String::toUpperCase);
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesMonoFlatMap(int length) {
        Mono<List<String>> m = Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > length)
                .flatMap(this::splitStringMono)// Mono <List of A, L, E, X>
                .log();
        m.subscribe(name -> log.info("Mono Name is {}", name));
    }

    public void namesMonoFlatMapMany(int length) {
        Flux<String> m = Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > length)
                .flatMapMany(this::splitString)// Mono <List of A, L, E, X>
                .log();
        m.subscribe(name -> log.info("Mono Name is {}", name));
    }

    public void namesFluxTransform(int length) {

        Function<Flux<String>, Flux<String>> filterFunction = name -> name.
                filter(s -> s.length() > length)
                .map(String::toUpperCase);

        Flux<String> f = Flux.fromIterable(list)
                .transform(filterFunction)
                .flatMap(s -> splitString(s))
                .defaultIfEmpty("default")
                .log();
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void namesFluxTransformSwitchIfEmpty(int length) {

        Function<Flux<String>, Flux<String>> filterFunction = name -> name.
                filter(s -> s.length() > length)
                .map(String::toUpperCase)
                .flatMap(s -> splitString(s));

        Flux<String> defaultFlux = Flux.just("default").transform(filterFunction);

        Flux<String> f = Flux.fromIterable(list)
                .transform(filterFunction)
                .switchIfEmpty(defaultFlux) // "D","E","F","A","U","L","T"
                .log();
        f.subscribe(name -> log.info("Name is {}", name));
    }

    public void exploreConcat() {
        Flux<String> abc = Flux.just("A", "B", "C");
        Flux<String> def = Flux.just("D", "E", "F");
        // es un método estático
        Flux<String> f = Flux.concat(abc, def);
        // Flux<String> f = abc.concatWith(def);
        f.subscribe(name -> log.info("The result is {}", name));
        // A, B, C, D, E, F
    }

    public void exploreConcatWith() {
        Mono<String> aMono = Mono.just("A");
        Flux<String> bMono = Flux.just("B", "C");
        // es un método estático
        Flux<String> f = aMono.concatWith(bMono);
        f.subscribe(name -> log.info("The result is {}", name));
        // A, B, C
    }

    public void exploreMerge() {
        Flux<String> abc = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        Flux<String> def = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));
        // es un método estático
        //Flux<String> f = Flux.merge(abc, def);
        Flux<String> f = abc.mergeWith(def);
        f.subscribe(name -> log.info("The result is {}", name));
        // A, D, B, E, C, F
    }

    public void exploreMergeSequential() {
        Flux<String> abc = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        Flux<String> def = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));
        // se suscriben los 2 Flux al mismo tiempo
        // emiten los valores al mismo tiempo
        // y retornan en secuencia
        Flux<String> f = Flux.mergeSequential(abc, def).log();
        f.subscribe(name -> log.info("The result is {}", name));
        // A, B, C, D, E, F
    }

    public void exploreZip() {
        Flux<String> abc = Flux.just("A", "B", "C");
        Flux<String> def = Flux.just("D", "E", "F", "G");
        //Flux<String> f = Flux.zip(abc, def, (first, second)-> first+second);
        Flux<String> f = abc.zipWith(def, (first, second)-> first+second);
        f.subscribe(name -> log.info("The result is {}", name));
        //AD, BE, CF
    }

    public void exploreZipAgain() {
        Flux<String> abc = Flux.just("A", "B", "C");
        Flux<String> def = Flux.just("D", "E", "F");
        Flux<String> _123 = Flux.just("1", "2", "3");
        Flux<String> _456 = Flux.just("4", "5", "6");
        Flux<String> f = Flux.zip(abc, def, _123,_456)
                // esto se conoce como un Tuple4, un elemento con 4 valores
                // que se pueden acceder con un get
                // existe hasta un Tuple8
                .map(t4 -> t4.getT1()+t4.getT2()+t4.getT3()+t4.getT4());
        f.subscribe(name -> log.info("The result is {}", name));
        //AD14, BE25, CF36
    }

    private Mono<List<String>> splitStringMono(String name) {
        String[] chars = name.split("");
        List<String> l = Stream.of(chars).collect(Collectors.toList());
        return Mono.just(l);
    }

    private Flux<String> splitString(String name) {
        String[] chars = name.split("");
        return Flux.fromArray(chars);
    }

    private Flux<String> splitStringDelayed(String name) {
        String[] chars = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(chars)
                .delayElements(Duration.ofMillis(delay));
    }
}
