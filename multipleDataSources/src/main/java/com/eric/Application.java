package com.eric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println(" 　　　　　　　　┏┓　　　┏┓+ +\n" +
                " 　　　　　　　┏┛┻━━━┛┻┓ + +\n" +
                " 　　　　　　　┃　　　　　　　┃ 　\n" +
                " 　　　　　　　┃　　　━　　　┃ ++ + + +\n" +
                " 　　　　         ██ ━██  ┃+\n" +
                " 　　　　　　　┃　　　　　　　┃ +\n" +
                " 　　　　　　　┃　　　┻　　　┃\n" +
                " 　　　　　　　┃　　　　　　　┃ + +\n" +
                " 　　　　　　　┗━┓　　　┏━┛\n" +
                " 　　　　　　　　　┃　　　┃　　　　　　　　　　　\n" +
                " 　　　　　　　　　┃　　　┃ + + + +\n" +
                " 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting　　　　　　　\n" +
                " 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug　　\n" +
                " 　　　　　　　　　┃　　　┃\n" +
                " 　　　　　　　　　┃　　　┃　　+　　　　　　　　　\n" +
                " 　　　　　　　　　┃　 　　┗━━━┓ + +\n" +
                " 　　　　　　　　　┃ 　　　　　　　┣┓\n" +
                " 　　　　　　　　　┃ 　　　　　　　┏┛\n" +
                " 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +\n" +
                " 　　　　　　　　　　┃┫┫　┃┫┫\n" +
                " 　　　　　　　　　　┗┻┛　┗┻┛+ + + +");
    }
}
