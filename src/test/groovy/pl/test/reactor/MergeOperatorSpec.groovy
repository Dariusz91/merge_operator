package pl.test.reactor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class MergeOperatorSpec extends Specification {

    private static final Logger logger = LoggerFactory.getLogger(MergeOperatorSpec.class)

    CountDownLatch latch
    AtomicInteger errorCounter

    def setup() {
        errorCounter = new AtomicInteger()
    }

    @Unroll
    def "merge operator should emit error"() {
        given:
            def iterations = 5

        when:
            def first = monoWithError()
                    .doOnError { logger.error("First Mono Error", it) }
                    .doOnSuccess { logger.info("First Mono Success: $it") }
            def second = Mono.just("abc")
                    .delayElement(Duration.ofMillis(10))
                    .doOnError { logger.error("Second Mono Error", it) }
                    .doOnSuccess { logger.info("Second Mono Success: $it") }

            for (i in 1..iterations) {
                waitForCompletion {
                    Flux.merge(first, second)
                            .then(Mono.empty())
                            .doOnError {
                                logger.error("Flux Error")
                                errorCounter.incrementAndGet()
                            }
                            .doOnSuccess {
                                logger.info("Flux Success")
                            }
                            .doOnTerminate { latch.countDown() }
                            .subscribe()
                }
            }
        then:
            errorCounter.get() == iterations

        where:
            i << (1..50)
    }

    private static Mono<String> monoWithError() {
        return Mono.just("Text going to fail anyway")
                .delayElement(Duration.ofMillis(5))
                .map() { throw new RuntimeException("X") }
    }

    protected def waitForCompletion(Runnable runnable) {
        latch = new CountDownLatch(1)
        runnable.run()
        latch.await(1, TimeUnit.SECONDS)
    }

}
