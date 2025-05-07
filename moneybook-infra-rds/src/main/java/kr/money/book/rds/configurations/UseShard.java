package kr.money.book.rds.configurations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@UseShard(value = id)
//public Account findAccountById(Long id) {
//	return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Found"));
//}
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseShard {

    String shardKey() default "id";
}
