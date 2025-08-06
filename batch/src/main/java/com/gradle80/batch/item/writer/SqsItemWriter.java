package com.gradle80.batch.item.writer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Item writer implementation for writing batch items to an AWS SQS queue.
 * Converts objects to JSON and sends them as SQS messages.
 */
public class SqsItemWriter implements ItemWriter<Object> {

    private static final Logger logger = LoggerFactory.getLogger(SqsItemWriter.class);
    private static final int MAX_BATCH_SIZE = 10; // SQS limits batch requests to 10 messages

    private final AmazonSQS sqsClient;
    private final String queueUrl;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new SQS item writer.
     *
     * @param sqsClient    AWS SQS client
     * @param queueUrl     URL of the SQS queue to write to
     * @param objectMapper JSON object mapper for serialization
     */
    public SqsItemWriter(AmazonSQS sqsClient, String queueUrl, ObjectMapper objectMapper) {
        Assert.notNull(sqsClient, "SQS client must not be null");
        Assert.hasText(queueUrl, "Queue URL must not be empty");
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
    }

    /**
     * Writes a list of items to an SQS queue, converting each item to JSON.
     * Messages are sent in batches of up to 10 items (SQS limit).
     *
     * @param items the items to be written to the SQS queue
     * @throws Exception if an error occurs during writing
     */
    @Override
    public void write(List<? extends Object> items) throws Exception {
        if (items == null || items.isEmpty()) {
            return;
        }

        logger.debug("Writing {} items to SQS queue: {}", items.size(), queueUrl);
        
        // Process items in batches of MAX_BATCH_SIZE
        List<List<? extends Object>> batches = splitIntoBatches(items);
        
        for (List<? extends Object> batch : batches) {
            sendBatch(batch);
        }
    }

    /**
     * Split items into batches of appropriate size for SQS batch requests.
     * 
     * @param items Items to be split into batches
     * @return List of batches
     */
    private List<List<? extends Object>> splitIntoBatches(List<? extends Object> items) {
        List<List<? extends Object>> batches = new ArrayList<>();
        
        for (int i = 0; i < items.size(); i += MAX_BATCH_SIZE) {
            int end = Math.min(items.size(), i + MAX_BATCH_SIZE);
            batches.add(items.subList(i, end));
        }
        
        return batches;
    }

    /**
     * Sends a batch of items to SQS.
     * 
     * @param batch Batch of items to send
     * @throws JsonProcessingException if JSON serialization fails
     */
    private void sendBatch(List<? extends Object> batch) throws JsonProcessingException {
        SendMessageBatchRequest batchRequest = new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(createBatchEntries(batch));
        
        try {
            sqsClient.sendMessageBatch(batchRequest);
            logger.debug("Successfully sent batch of {} messages to SQS", batch.size());
        } catch (Exception e) {
            logger.error("Failed to send message batch to SQS queue {}: {}", queueUrl, e.getMessage());
            throw e;
        }
    }

    /**
     * Creates batch entries for SQS batch request from a list of objects.
     * 
     * @param batch Objects to convert to batch entries
     * @return List of batch request entries
     * @throws JsonProcessingException if JSON serialization fails
     */
    private List<SendMessageBatchRequestEntry> createBatchEntries(List<? extends Object> batch) throws JsonProcessingException {
        List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
        
        for (Object item : batch) {
            String messageBody = objectMapper.writeValueAsString(item);
            String id = UUID.randomUUID().toString();
            
            SendMessageBatchRequestEntry entry = new SendMessageBatchRequestEntry()
                    .withId(id)
                    .withMessageBody(messageBody);
            
            entries.add(entry);
        }
        
        return entries;
    }
    
    // TODO: Add support for message attributes and delivery delay
    
    // FIXME: Add error handling for when queue doesn't exist
}