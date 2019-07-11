package com.example.customvalidation;

import com.example.customvalidation.entity.ContactInfoExpression;
import com.example.customvalidation.entity.repository.ContactInfoExpressionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
public class ContactInfoValidator implements ConstraintValidator<ContactInfo, String> {

    private static final Logger LOG = LogManager.getLogger(ContactInfoValidator.class);

    @Value("${contactInfoType}")
    private String expressionType;

    private String pattern;

    @Autowired
    private ContactInfoExpressionRepository contactInfoExpressionRepository;

    @Override
    public void initialize(ContactInfo contactInfo) {
        if (StringUtils.isEmptyOrWhitespace(expressionType)) {
            LOG.error("Contact info type missing!");
        } else {
            pattern = contactInfoExpressionRepository.findById(expressionType)
                    .map(ContactInfoExpression::getPattern).get();
        }

        List<String> expressions = new ArrayList<>();
        for (ContactInfoExpression expression : contactInfoExpressionRepository.findAll()) {
            System.out.println("Expression = " + expression.getType());
            expressions.add(expression.getType());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.isEmptyOrWhitespace(pattern)) {
            return Pattern.matches(pattern, value);
        }
        LOG.error("Contact info pattern missing!");
        return false;
    }
}
