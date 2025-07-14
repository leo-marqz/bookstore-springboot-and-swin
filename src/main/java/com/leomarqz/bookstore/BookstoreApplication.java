package com.leomarqz.bookstore;

import com.leomarqz.bookstore.views.BookForm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class BookstoreApplication {

	public static void main(String[] args) {
//		SpringApplication.run(BookstoreApplication.class, args);

		ConfigurableApplicationContext context =
				new SpringApplicationBuilder(BookstoreApplication.class)
						.headless(false) // Disable headless mode for GUI applications
						.web(WebApplicationType.NONE)
						.run(args);

		// Launch the BookForm GUI
		EventQueue.invokeLater(()->{
			try {
				context.getBean(BookForm.class)
						.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
