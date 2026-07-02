package uk.org.pentlandscouts.events.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Autowired
    AwsProperties awsProperties;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey())
                ));

        if (awsProperties.getRegion() != null && !awsProperties.getRegion().isEmpty()) {
            builder.region(Region.of(awsProperties.getRegion()));
        }

        if (awsProperties.getEndPointURL() != null && !awsProperties.getEndPointURL().isEmpty()) {
            builder.endpointOverride(URI.create(awsProperties.getEndPointURL()));
        }

        return builder.build();
    }

    @Bean
    public io.awspring.cloud.dynamodb.DynamoDbTableNameResolver dynamoDbTableNameResolver(AwsProperties awsProperties) {
        return new io.awspring.cloud.dynamodb.DynamoDbTableNameResolver() {
            @Override
            public <T> String resolve(Class<T> clazz) {
                String prefix = awsProperties.getTablePrefix() != null ? awsProperties.getTablePrefix() : "";
                return prefix + clazz.getSimpleName();
            }
        };
    }

    @Bean
    public software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public io.awspring.cloud.dynamodb.DynamoDbTemplate dynamoDbTemplate(
            software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient dynamoDbEnhancedClient,
            io.awspring.cloud.dynamodb.DynamoDbTableNameResolver dynamoDbTableNameResolver) {
        return new io.awspring.cloud.dynamodb.DynamoDbTemplate(
                dynamoDbEnhancedClient,
                new io.awspring.cloud.dynamodb.DefaultDynamoDbTableSchemaResolver(),
                dynamoDbTableNameResolver
        );
    }
}
