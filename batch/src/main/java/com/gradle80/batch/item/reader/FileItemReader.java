package com.gradle80.batch.item.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Item reader implementation for file-based input in batch jobs.
 * This class provides a reusable way to configure file readers for Spring Batch,
 * supporting various file formats through the LineMapper abstraction.
 * 
 * @author gradle80
 * @param <T> The type of item being read from the file
 */
@Component
public class FileItemReader<T> {

    /**
     * The file resource to read data from
     */
    private Resource fileResource;
    
    /**
     * The line mapper that converts each line to a domain object
     */
    private LineMapper<T> lineMapper;
    
    /**
     * Name of the resource being read (for identification in logs)
     */
    private String resourceName;
    
    /**
     * Number of lines to skip at the beginning of the file (e.g., for headers)
     */
    private int linesToSkip = 0;
    
    /**
     * Encoding of the input file
     */
    private String encoding = "UTF-8";
    
    /**
     * Whether to parse input as strict CSV (default constructor)
     */
    public FileItemReader() {
        // Default constructor
    }
    
    /**
     * Constructor with required dependencies
     * 
     * @param fileResource the input file resource
     * @param lineMapper the mapper to convert lines to domain objects
     */
    @Autowired
    public FileItemReader(Resource fileResource, LineMapper<T> lineMapper) {
        this.fileResource = fileResource;
        this.lineMapper = lineMapper;
    }
    
    /**
     * Creates and configures a FlatFileItemReader based on the current settings.
     * This method builds the reader with appropriate configuration for line mapping,
     * encoding, and resource handling.
     * 
     * @return a fully configured FlatFileItemReader ready for batch processing
     */
    public FlatFileItemReader<T> createReader() {
        // Create the reader
        FlatFileItemReader<T> reader = new FlatFileItemReader<>();
        
        // Configure the reader
        reader.setResource(fileResource);
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(linesToSkip);
        reader.setEncoding(encoding);
        
        // Set a name for the reader (useful for logging and metrics)
        if (resourceName != null) {
            reader.setName(resourceName);
        } else {
            // If no resource name is set, use the file name
            try {
                reader.setName("FileItemReader: " + fileResource.getFilename());
            } catch (Exception e) {
                reader.setName("FileItemReader");
                // FIXME: Consider better exception handling here
            }
        }
        
        // Set strict mode (for checking all lines are parsed correctly)
        reader.setStrict(true);
        
        return reader;
    }
    
    /**
     * Set the resource to read from
     * 
     * @param fileResource the input file resource
     * @return this instance for fluent API usage
     */
    public FileItemReader<T> withResource(Resource fileResource) {
        this.fileResource = fileResource;
        return this;
    }
    
    /**
     * Set the line mapper to use
     * 
     * @param lineMapper the mapper to convert lines to domain objects
     * @return this instance for fluent API usage
     */
    public FileItemReader<T> withLineMapper(LineMapper<T> lineMapper) {
        this.lineMapper = lineMapper;
        return this;
    }
    
    /**
     * Set the number of lines to skip at the beginning of the file
     * 
     * @param linesToSkip number of lines to skip (e.g., header rows)
     * @return this instance for fluent API usage
     */
    public FileItemReader<T> withLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    
    /**
     * Set the encoding of the input file
     * 
     * @param encoding the file encoding (default is UTF-8)
     * @return this instance for fluent API usage
     */
    public FileItemReader<T> withEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }
    
    /**
     * Set a descriptive name for this reader
     * 
     * @param resourceName name to identify this reader
     * @return this instance for fluent API usage
     */
    public FileItemReader<T> withResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }
    
    /**
     * Helper method to create a default CSV line mapper
     * This is useful for common CSV processing scenarios
     * 
     * @param <T> the type to map to
     * @param targetType class of the target object
     * @param delimiter the delimiter character (e.g., comma)
     * @param columns names of the columns
     * @return a configured line mapper for the specified format
     */
    public static <T> LineMapper<T> createDefaultCsvLineMapper(
            Class<T> targetType, String delimiter, String... columns) {
        // TODO: Implement factory method for common CSV formats
        // This would create a DefaultLineMapper with BeanWrapperFieldSetMapper
        throw new UnsupportedOperationException("CSV mapper creation not yet implemented");
    }
}