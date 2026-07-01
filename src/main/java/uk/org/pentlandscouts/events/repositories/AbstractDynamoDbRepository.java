package uk.org.pentlandscouts.events.repositories;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractDynamoDbRepository<T, ID> {

    protected final DynamoDbTemplate dynamoDbTemplate;
    protected final DynamoDbEnhancedClient enhancedClient;
    protected final Class<T> entityClass;
    protected final Function<ID, Key> keyMapper;
    protected final String tableName;

    protected AbstractDynamoDbRepository(
            DynamoDbTemplate dynamoDbTemplate,
            DynamoDbEnhancedClient enhancedClient,
            Class<T> entityClass,
            Function<ID, Key> keyMapper,
            String tableName
    ) {
        this.dynamoDbTemplate = dynamoDbTemplate;
        this.enhancedClient = enhancedClient;
        this.entityClass = entityClass;
        this.keyMapper = keyMapper;
        this.tableName = tableName;
    }

    public T save(T entity) {
        dynamoDbTemplate.save(entity);
        return entity;
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        entities.forEach(list::add);
        
        DynamoDbTable<T> table = enhancedClient.table(tableName, TableSchema.fromBean(entityClass));
        
        for (int i = 0; i < list.size(); i += 25) {
            List<S> batch = list.subList(i, Math.min(i + 25, list.size()));
            WriteBatch.Builder<T> batchBuilder = WriteBatch.builder(entityClass)
                    .mappedTableResource(table);
            for (S entity : batch) {
                batchBuilder.addPutItem(entity);
            }
            enhancedClient.batchWriteItem(BatchWriteItemEnhancedRequest.builder()
                    .addWriteBatch(batchBuilder.build())
                    .build());
        }
        return entities;
    }

    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        Key key = keyMapper.apply(id);
        if (key == null) {
            return Optional.empty();
        }
        T entity = dynamoDbTemplate.load(key, entityClass);
        return Optional.ofNullable(entity);
    }

    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    public List<T> findAll() {
        return dynamoDbTemplate.scanAll(entityClass).items().stream().toList();
    }

    public Iterable<T> findAllById(Iterable<ID> ids) {
        List<T> list = new ArrayList<>();
        for (ID id : ids) {
            findById(id).ifPresent(list::add);
        }
        return list;
    }

    public long count() {
        return findAll().size();
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    public void delete(T entity) {
        dynamoDbTemplate.delete(entity);
    }

    public void deleteAllById(Iterable<? extends ID> ids) {
        for (ID id : ids) {
            deleteById(id);
        }
    }

    public void deleteAll(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    public void deleteAll() {
        deleteAll(findAll());
    }
}
