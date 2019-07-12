package com.example.customvalidation;

import com.example.customvalidation.entity.ContactInfoExpression;
import com.example.customvalidation.entity.Customer;
import com.example.customvalidation.entity.repository.ContactInfoExpressionRepository;
import com.example.customvalidation.entity.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    ContactInfoExpressionRepository contactInfoExpressionRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        String pattern = "[a-z0-9!#$%&*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        ContactInfoExpression email = new ContactInfoExpression("email", pattern);
        contactInfoExpressionRepository.save(email);

        pattern = "^([0-9]( |-)?)?(\\(?[0-9]{3}\\)?|[0-9]{3})( |-)?([0-9]{3}( |-)?[0-9]{4}|[a-zA-Z0-9]{7})$";
        ContactInfoExpression phone = new ContactInfoExpression("phone", pattern);
        contactInfoExpressionRepository.save(phone);

        pattern = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
        ContactInfoExpression website = new ContactInfoExpression("website", pattern);
        contactInfoExpressionRepository.save(website);

        Customer customer1 = new Customer("mhussainshah79@hotmail.com");
        customerRepository.save(customer1);
    }
}

