package net.koko.kaspar;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;


class KasparApplicationTests {

	@Test
	void contextLoads() {

		Mono<Object> err = Mono.error(new IllegalArgumentException())
				//.onErrorResume(e -> Mono.just("onErrorResume"))
				//.onErrorMap(Throwable::getCause)
				.doOnError(e -> System.out.println("doOnError"))

				.onErrorReturn("onErrorReturn")
				.log();
		System.out.println(err.block());
	}

}
