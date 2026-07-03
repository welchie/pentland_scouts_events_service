package uk.org.pentlandscouts.events.repositories;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractDynamoDbRepositoryTest {

    @Mock
    private DynamoDbTemplate dynamoDbTemplate;

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    private TestRepository repository;

    @software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
    public static class TestEntity {
        private String id;
        public TestEntity() {}
        @software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    static class TestRepository extends AbstractDynamoDbRepository<TestEntity, String> {
        public TestRepository(DynamoDbTemplate dynamoDbTemplate, DynamoDbEnhancedClient enhancedClient) {
            super(dynamoDbTemplate, enhancedClient, TestEntity.class,
                  id -> Key.builder().partitionValue(id).build(), "TestTable");
        }
    }

    @BeforeEach
    public void setUp() {
        repository = new TestRepository(dynamoDbTemplate, enhancedClient);
    }

    @Test
    public void testSave() {
        TestEntity entity = new TestEntity();
        entity.setId("1");

        TestEntity result = repository.save(entity);
        assertEquals(entity, result);
        verify(dynamoDbTemplate).save(entity);
    }

    @Test
    public void testSaveAll() {
        TestEntity entity = new TestEntity();
        entity.setId("1");

        DynamoDbTable<TestEntity> tableMock = mock(DynamoDbTable.class);
        TableSchema<TestEntity> schema = TableSchema.fromBean(TestEntity.class);
        when(tableMock.tableSchema()).thenReturn(schema);
        when(enhancedClient.table(anyString(), any(TableSchema.class))).thenReturn(tableMock);

        Iterable<TestEntity> result = repository.saveAll(Collections.singletonList(entity));
        assertNotNull(result);
        verify(enhancedClient).batchWriteItem(any(BatchWriteItemEnhancedRequest.class));
    }

    @Test
    public void testFindByIdNull() {
        Optional<TestEntity> result = repository.findById(null);
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdSuccess() {
        TestEntity entity = new TestEntity();
        entity.setId("1");

        when(dynamoDbTemplate.load(any(Key.class), eq(TestEntity.class))).thenReturn(entity);

        Optional<TestEntity> result = repository.findById("1");
        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    public void testExistsById() {
        TestEntity entity = new TestEntity();
        entity.setId("1");
        when(dynamoDbTemplate.load(any(Key.class), eq(TestEntity.class))).thenReturn(entity);

        assertTrue(repository.existsById("1"));
    }

    @Test
    public void testFindAll() {
        TestEntity entity = new TestEntity();
        PageIterable<TestEntity> pageIterable = mock(PageIterable.class);
        SdkIterable<TestEntity> sdkIterable = mock(SdkIterable.class);

        when(dynamoDbTemplate.scanAll(TestEntity.class)).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(java.util.stream.Stream.of(entity));

        List<TestEntity> result = repository.findAll();
        assertEquals(1, result.size());
        assertEquals(entity, result.get(0));
    }

    @Test
    public void testFindAllById() {
        TestEntity entity = new TestEntity();
        entity.setId("1");
        when(dynamoDbTemplate.load(any(Key.class), eq(TestEntity.class))).thenReturn(entity);

        Iterable<TestEntity> result = repository.findAllById(Collections.singletonList("1"));
        assertNotNull(result);
        assertEquals(entity, result.iterator().next());
    }

    @Test
    public void testDelete() {
        TestEntity entity = new TestEntity();
        repository.delete(entity);
        verify(dynamoDbTemplate).delete(entity);
    }

    @Test
    public void testDeleteById() {
        TestEntity entity = new TestEntity();
        entity.setId("1");
        when(dynamoDbTemplate.load(any(Key.class), eq(TestEntity.class))).thenReturn(entity);

        repository.deleteById("1");
        verify(dynamoDbTemplate).delete(entity);
    }

    @Test
    public void testDeleteAllById() {
        TestEntity entity = new TestEntity();
        entity.setId("1");
        when(dynamoDbTemplate.load(any(Key.class), eq(TestEntity.class))).thenReturn(entity);

        repository.deleteAllById(Collections.singletonList("1"));
        verify(dynamoDbTemplate).delete(entity);
    }

    @Test
    public void testDeleteAll() {
        TestEntity entity = new TestEntity();
        repository.deleteAll(Collections.singletonList(entity));
        verify(dynamoDbTemplate).delete(entity);
    }
}
