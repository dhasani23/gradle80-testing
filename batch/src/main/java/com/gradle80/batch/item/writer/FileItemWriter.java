package com.gradle80.batch.item.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.core.io.Resource;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Item writer for file output.
 * This implementation serves as a factory for creating Spring Batch {@link FlatFileItemWriter}
 * instances to write data to files.
 */
public class FileItemWriter<T> {

    private Resource outputResource;
    private LineAggregator<T> lineAggregator;

    /**
     * Constructor with required dependencies.
     *
     * @param outputResource the output file resource
     * @param lineAggregator the formatter to convert items to lines
     */
    public FileItemWriter(Resource outputResource, LineAggregator<T> lineAggregator) {
        this.outputResource = outputResource;
        this.lineAggregator = lineAggregator;
    }

    /**
     * Create a configured {@link FlatFileItemWriter} for writing to files.
     * 
     * @return a fully configured {@link FlatFileItemWriter} instance
     */
    public ItemWriter<T> createWriter() {
        // Validate required fields
        Assert.notNull(outputResource, "outputResource must not be null");
        Assert.notNull(lineAggregator, "lineAggregator must not be null");
        
        // Build and configure the writer
        FlatFileItemWriter<T> writer = new FlatFileItemWriterBuilder<T>()
            .name("fileItemWriter")
            .resource(outputResource)
            .lineAggregator(lineAggregator)
            .shouldDeleteIfExists(true)  // Default behavior to overwrite existing files
            .append(false)
            .build();
        
        // FIXME: Consider adding encoding option for non-English character support
        
        try {
            // Initialize the writer - this opens the output file
            writer.afterPropertiesSet();
            
            // Return the writer cast to ItemWriter interface to maintain compatibility
            return writer;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize file writer", e);
        }
    }

    /**
     * Sets the output resource to write to.
     *
     * @param outputResource the output file resource
     */
    public void setOutputResource(Resource outputResource) {
        this.outputResource = outputResource;
    }

    /**
     * Sets the line aggregator used to format items into lines.
     *
     * @param lineAggregator the formatter to convert items to lines
     */
    public void setLineAggregator(LineAggregator<T> lineAggregator) {
        this.lineAggregator = lineAggregator;
    }
}