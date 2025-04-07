package com.example.delivery.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Delivery Application API",
                description = "API для доставки еды,"
                        + " позволяющий выбирать рестораны и блюда для доставки.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Yarik",
                        email = "morozovarik72@gmail.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
public class SwaggerConfig {
}
