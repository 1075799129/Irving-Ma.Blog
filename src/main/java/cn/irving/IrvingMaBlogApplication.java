package cn.irving;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("cn.luischen.dao")
@EnableCaching
public class IrvingMaBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(IrvingMaBlogApplication.class, args);
	}
}
