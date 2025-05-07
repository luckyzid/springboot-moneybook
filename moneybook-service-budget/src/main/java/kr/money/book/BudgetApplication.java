package kr.money.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class BudgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetApplication.class, args);
		System.out.println("Budget Service Started.");
	}
}
