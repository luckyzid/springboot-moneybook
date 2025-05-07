package kr.money.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class AnalyzeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyzeApplication.class, args);
		System.out.println("Analyze Service Started.");
	}
}
