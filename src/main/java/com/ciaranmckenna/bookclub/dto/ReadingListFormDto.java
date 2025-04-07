package com.ciaranmckenna.bookclub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for form binding when creating or editing reading lists
 * This class is separate from the business logic DTO to avoid
 * validation conflicts during form display.
 */
public class ReadingListFormDto {
    
    @NotBlank(message = "Name cannot be empty")
    private String name;
    
    private String description;
    
    /**
     * Default constructor for form binding
     */
    public ReadingListFormDto() {
        // Empty constructor for form binding
    }
    
    /**
     * Constructor with all fields
     * @param name Reading list name
     * @param description Reading list description
     */
    public ReadingListFormDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * Convert this form DTO to a business DTO
     * @return ReadingListCreateDto with this form's data
     */
    public ReadingListCreateDto toCreateDto() {
        return new ReadingListCreateDto(this.name, this.description);
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 