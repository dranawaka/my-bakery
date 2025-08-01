# Schema Update Note

## Issue
The application was failing to start with the following error:
```
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing table [addresses]
```

## Solution
1. Changed Hibernate ddl-auto setting from `validate` to `update` in both:
   - application.properties
   - application-prod.properties

2. This change allows Hibernate to automatically create the missing `addresses` table based on the `Address` entity definition.

## Next Steps
After the application has been deployed and run at least once (allowing Hibernate to create the missing table), the ddl-auto setting should be changed back to `validate` for production safety.

To revert to validation mode, change:
```properties
spring.jpa.hibernate.ddl-auto=update
```
to:
```properties
spring.jpa.hibernate.ddl-auto=validate
```

in both property files.

## Date
This change was made on: 2025-08-01