package practice.spring.toby.chapter7.config;

import org.springframework.context.annotation.Import;

@Import(value = SqlServiceContextConfig.class)
public @interface EnableSqlService {

}
