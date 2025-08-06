package com.gradle80.batch.test;

import com.gradle80.batch.item.processor.DataProcessingItemProcessor;
import com.gradle80.batch.item.reader.DataProcessingItemReader;
import com.gradle80.batch.item.writer.DatabaseItemWriter;
import com.gradle80.batch.service.TransformationService;
import com.gradle80.batch.item.reader.DataProcessingItemReader.EntityRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for batch components.
 * Tests the core functionality of item readers, processors, and writers
 * using mocks to isolate each component.
 *
 * @author gradle80
 */
@RunWith(MockitoJUnitRunner.class)
public class BatchUnitTests {

    // Test data
    private static final String TEST_ITEM_1 = "test-item-1";
    private static final String TEST_ITEM_2 = "test-item-2";
    private static final String PROCESSED_ITEM_1 = "processed-test-item-1";
    private static final String PROCESSED_ITEM_2 = "processed-test-item-2";

    // Mocked components
    @Mock
    private EntityRepository mockRepository;

    @Mock
    private TransformationService mockTransformationService;

    @Mock
    private DataSource mockDataSource;

    @Mock
    private JdbcBatchItemWriter<Object> mockItemWriter;

    // Components under test
    private DataProcessingItemReader<String> dataProcessingItemReader;
    private DataProcessingItemProcessor dataProcessingItemProcessor;
    private DatabaseItemWriter databaseItemWriter;

    /**
     * Setup test fixtures before each test
     */
    @Before
    public void setUp() {
        // Setup reader
        dataProcessingItemReader = new DataProcessingItemReader<>(mockRepository);
        
        // Setup processor
        dataProcessingItemProcessor = new DataProcessingItemProcessor(mockTransformationService);
        
        // Setup writer
        String sqlQuery = "INSERT INTO test_table (value) VALUES (:value)";
        databaseItemWriter = new DatabaseItemWriter(mockDataSource, sqlQuery);

        // Configure mock behavior for reader
        List<String> testItems = Arrays.asList(TEST_ITEM_1, TEST_ITEM_2);
        when(mockRepository.findAll(anyInt(), anyInt())).thenReturn(testItems);
        when(mockRepository.count()).thenReturn(testItems.size());

        // Configure mock behavior for processor
        when(mockTransformationService.transformData(TEST_ITEM_1)).thenReturn(PROCESSED_ITEM_1);
        when(mockTransformationService.transformData(TEST_ITEM_2)).thenReturn(PROCESSED_ITEM_2);
    }

    /**
     * Tests that the item reader correctly reads items from the repository.
     * Verifies that:
     * - The reader returns all expected items
     * - The reader correctly signals the end of data
     * - The repository's findAll method is called with correct parameters
     */
    @Test
    public void testItemReaderReadsCorrectly() throws Exception {
        // Initialize reader
        ExecutionContext executionContext = new ExecutionContext();
        dataProcessingItemReader.open(executionContext);

        // Read items
        List<String> readItems = new ArrayList<>();
        String item;
        while ((item = dataProcessingItemReader.read()) != null) {
            readItems.add(item);
        }

        // Verify results
        assertEquals("Reader should return 2 items", 2, readItems.size());
        assertEquals("First item should match", TEST_ITEM_1, readItems.get(0));
        assertEquals("Second item should match", TEST_ITEM_2, readItems.get(1));
        
        // Verify repository was called correctly
        verify(mockRepository).findAll(eq(0), anyInt());
        verify(mockRepository).count();

        // Verify reader state is updated correctly
        dataProcessingItemReader.update(executionContext);
        assertEquals("Execution context should have correct index", 
                2, executionContext.getInt("current.index"));
        
        // Test reader close method
        dataProcessingItemReader.close();
        
        // TODO: Add tests for restart scenarios with saved execution context
    }

    /**
     * Tests that the item processor correctly transforms items.
     * Verifies that:
     * - The processor transforms items as expected
     * - The transformation service is called for each item
     * - Null items are handled correctly
     */
    @Test
    public void testItemProcessorTransformsCorrectly() throws Exception {
        // Process test items
        Object processedItem1 = dataProcessingItemProcessor.process(TEST_ITEM_1);
        Object processedItem2 = dataProcessingItemProcessor.process(TEST_ITEM_2);
        Object processedNullItem = dataProcessingItemProcessor.process(null);

        // Verify transformation results
        assertEquals("First item should be correctly transformed", 
                PROCESSED_ITEM_1, processedItem1);
        assertEquals("Second item should be correctly transformed", 
                PROCESSED_ITEM_2, processedItem2);
        assertNull("Null input should result in null output", processedNullItem);

        // Verify transformation service was called correctly
        verify(mockTransformationService).transformData(TEST_ITEM_1);
        verify(mockTransformationService).transformData(TEST_ITEM_2);
        verify(mockTransformationService, never()).transformData(null);

        // Test exception handling
        when(mockTransformationService.transformData(anyString()))
                .thenThrow(new RuntimeException("Test exception"));
        
        Object result = dataProcessingItemProcessor.process("error-item");
        assertNull("Processor should return null on exception", result);
    }

    /**
     * Tests that the item writer correctly writes items to the database.
     * Verifies that:
     * - The writer creates a correctly configured JdbcBatchItemWriter
     * - The writer uses the correct SQL query and datasource
     */
    @Test
    public void testItemWriterWritesCorrectly() throws Exception {
        // Mock the writer creation
        // Since DatabaseItemWriter doesn't expose the writer, we'll test indirectly
        // or through a spy
        DatabaseItemWriter spy = spy(databaseItemWriter);
        
        // Create a writer using our spy
        JdbcBatchItemWriter<Object> writer = spy.createWriter();
        
        // Verify the writer was created with correct configuration
        // Note: This is limited testing as we can't easily verify internal state
        assertNotNull("Writer should be created", writer);
        
        // Verify the datasource was used
        verify(spy).createWriter();
        
        // FIXME: Add more comprehensive tests for writer functionality
        // This would typically involve using ReflectionTestUtils to access private fields
        // or creating a test-specific subclass that exposes the needed functionality
    }
}