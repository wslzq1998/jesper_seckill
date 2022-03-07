package com.lzq.jesper_seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.lzq.jesper_seckill.*")
public class JesperSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(JesperSeckillApplication.class, args);
    }

}
