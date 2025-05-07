package kr.money.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class CategoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CategoryApplication.class, args);
		System.out.println("Category Service Started.");
	}
}
