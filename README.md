## Dynamic DTO Validation Config Retrieved from the Database
by baeldung PersistenceSpring Boot DTO


### 1. Overview
In this tutorial, we’re going to take a look at how we can create a custom validation annotation that uses a regular expression retrieved from a database to match against the field value.

We will use Hibernate Validator as a base implementation.

### 2. Maven Dependencies
For development, we will need the following dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
The latest versions of spring-boot-starter-thymeleaf, spring-boot-starter-data-jpa can be downloaded from Maven Central.

### 3. Custom Validation Annotation
For our example, we will create a custom annotation called `@ContactInfo` that will validate a value against a regular expression retrieved from a database. We will then apply this validation on the contactInfo field of a POJO class called Customer.

To retrieve regular expressions from a database, we will model these as a ContactInfoExpression entity class.

### 3.1. Data Models and Repository
Let’s create the Customer class with id and contactInfo fields:

```java
@Entity
public class Customer {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
 
    private String contactInfo;
 
    // standard constructor, getters, setters
}
```
Next, let’s take a look at the ContactInfoExpression class – which will hold the regular expression values in a property called pattern:

```java
@Entity
public class ContactInfoExpression {
 
    @Id
    @Column(name="expression_type")
    private String type;
  
    private String pattern;
 
    //standard constructor, getters, setters
}
```
Next, let’s add a repository interface based on Spring Data to manipulate the ContactInfoExpression entities:

```java
public interface ContactInfoExpressionRepository 
  extends CrudRepository<ContactInfoExpression, String> {
}
```
### 3.2. Database Setup
For storing regular expressions, we will use an H2 in-memory database with the following persistence configuration:

```properties
contactInfoType=email
spring.h2.console.enabled=true
spring.h2.console.path=/h2
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.show-sql=true
```
The two scripts mentioned are used for creating the schema and inserting the data into the contact_info_expression table:
```sql
CREATE TABLE contact_info_expression(
  expression_type varchar(50) not null,
  pattern varchar(500) not null,
  PRIMARY KEY ( expression_type )
);
```
The data-expressions.sql script will add three records to represent the types email, phone, and website. These represent regular expressions for validating that value is a valid email address, a valid US phone number, or a valid URL:

```sql
insert into contact_info_expression values ('email',
  '[a-z0-9!#$%&*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?')
insert into contact_info_expression values ('phone',
  '^([0-9]( |-)?)?(\(?[0-9]{3}\)?|[0-9]{3})( |-)?([0-9]{3}( |-)?[0-9]{4}|[a-zA-Z0-9]{7})$')
insert into contact_info_expression values ('website',
  '^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$')
```

or use DataLoader

```java
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

        Customer customer1 = new Customer("m_hussain_shah@hotmail.com");
        customerRepository.save(customer1);
    }
}
```
### 3.3. Creating the Custom Validator
Let’s create the ContactInfoValidator class that contains the actual validation logic. Following Java Validation specification guidelines, the class implements the ConstraintValidator interface and overrides the isValid() method.

This class will obtain the value of the currently used type of contact info — email, phone, or website — which is set in a property called contactInfoType, then use it to retrieve the regular expression’s value from the database:

```java
public class ContactInfoValidator implements ConstraintValidator<ContactInfo, String> {
     
    private static final Logger LOG = Logger.getLogger(ContactInfoValidator.class);
 
    @Value("${contactInfoType}")
    private String expressionType;
 
    private String pattern;
  
    @Autowired
    private ContactInfoExpressionRepository expressionRepository;
 
    @Override
    public void initialize(ContactInfo contactInfo) {
        if (StringUtils.isEmptyOrWhitespace(expressionType)) {
            LOG.error("Contact info type missing!");
        } else {
            pattern = expressionRepository.findById(expressionType)
              .map(ContactInfoExpression::getPattern).get();
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
```
The contactInfoType property can be set in the application.properties file to one of the values email, phone or website:

```properties
contactInfoType=email
```

#### 3.4. Creating the Custom Constraint Annotation
And now, let’s create the annotation interface for our custom constraint:

```java
@Constraint(validatedBy = { ContactInfoValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContactInfo {
    String message() default "Invalid value";
 
    Class<?>[] groups() default {};
 
    Class<? extends Payload>[] payload() default {};
}
```

#### 3.5. Applying the Custom Constraint
Finally, let’s add validation annotations to the contactInfo field of our Customer class:

```java
public class Customer {
     
    // ...
    @ContactInfo
    @NotNull
    private String contactInfo;
     
    // ...
}
```
### 4. Spring Controller and HTML Form
To test our validation annotation, we will create a Spring MVC request mapping that uses the `@Valid` annotation to trigger the validation of a Customer object:

```java
@PostMapping("/customer")
public String validateCustomer(@Valid Customer customer, BindingResult result, Model model) {
    if (result.hasErrors()) {
        model.addAttribute("message", "The information is invalid!");
    } else {
        model.addAttribute("message", "The information is valid!");
    }
    return "customer";
}
```

The Customer object is sent to the controller from an HTML form:
```html
<form action="customer" method="POST">
Contact Info: <input type="text" name="contactInfo" /> <br />
<input type="submit" value="Submit" />
</form>
<span th:text="${message}"></span>
```
To wrap it all up, we can run our application as a Spring Boot application:

```java
@SpringBootApplication
public class DynamicValidationApp {
    public static void main(String[] args) {
        SpringApplication.run(DynamicValidationApp.class, args);
    }
}
```
### 5. Conclusion
In this example, we have shown how we can create a custom validation annotation that retrieves a regular expression dynamically from a database and uses it to validate the annotated field.
