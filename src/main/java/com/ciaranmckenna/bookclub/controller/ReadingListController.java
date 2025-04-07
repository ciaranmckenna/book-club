package com.ciaranmckenna.bookclub.controller;

import com.ciaranmckenna.bookclub.common.ApiResponse;
import com.ciaranmckenna.bookclub.dto.ReadingListCreateDto;
import com.ciaranmckenna.bookclub.dto.ReadingListDto;
import com.ciaranmckenna.bookclub.security.CustomUserDetails;
import com.ciaranmckenna.bookclub.service.ReadingListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for reading list endpoints
 */
@RestController
@RequestMapping("/api/readinglists")
public class ReadingListController {

    @Autowired
    private ReadingListService readingListService;
    
    /**
     * Create a new reading list for the authenticated user
     * @param createDto Reading list data
     * @param userDetails Authenticated user details
     * @return ResponseEntity with the newly created reading list
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReadingListDto>> createReadingList(
            @Valid @RequestBody ReadingListCreateDto createDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            ReadingListDto readingListDto = readingListService.createReadingList(
                    userDetails.getUser().getId(), createDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Reading list created successfully", readingListDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all reading lists with pagination and sorting (admin only)
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @param direction Sort direction
     * @return ResponseEntity with page of reading lists
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReadingListDto>>> getAllReadingLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<ReadingListDto> readingLists = readingListService.getAllReadingLists(pageable);
            return ResponseEntity.ok(ApiResponse.success("Reading lists retrieved successfully", readingLists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get reading lists for the authenticated user with pagination
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @param direction Sort direction
     * @param userDetails Authenticated user details
     * @return ResponseEntity with page of reading lists
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReadingListDto>>> getUserReadingLists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<ReadingListDto> readingLists = readingListService.getReadingListsByUserId(
                    userDetails.getUser().getId(), pageable);
            return ResponseEntity.ok(ApiResponse.success("Reading lists retrieved successfully", readingLists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get reading lists for a specific user with pagination (admin only)
     * @param userId User ID
     * @param page Page number
     * @param size Page size
     * @return ResponseEntity with page of reading lists
     */
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReadingListDto>>> getReadingListsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReadingListDto> readingLists = readingListService.getReadingListsByUserId(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Reading lists retrieved successfully", readingLists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get a reading list by ID
     * @param id Reading list ID
     * @return ResponseEntity with reading list
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingListDto>> getReadingListById(@PathVariable Long id) {
        try {
            ReadingListDto readingList = readingListService.getReadingListById(id);
            return ResponseEntity.ok(ApiResponse.success("Reading list retrieved successfully", readingList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update a reading list
     * @param id Reading list ID
     * @param createDto Updated reading list data
     * @return ResponseEntity with updated reading list
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReadingListDto>> updateReadingList(
            @PathVariable Long id, 
            @Valid @RequestBody ReadingListCreateDto createDto) {
        try {
            ReadingListDto updatedReadingList = readingListService.updateReadingList(id, createDto);
            return ResponseEntity.ok(ApiResponse.success("Reading list updated successfully", updatedReadingList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete a reading list
     * @param id Reading list ID
     * @return ResponseEntity with deletion result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReadingList(@PathVariable Long id) {
        try {
            readingListService.deleteReadingList(id);
            return ResponseEntity.ok(ApiResponse.success("Reading list deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Search reading lists by name
     * @param name Name to search for
     * @param page Page number
     * @param size Page size
     * @return ResponseEntity with page of reading lists
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReadingListDto>>> searchReadingListsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReadingListDto> readingLists = readingListService.searchReadingListsByName(name, pageable);
            return ResponseEntity.ok(ApiResponse.success("Search results", readingLists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Search reading lists by name for the authenticated user
     * @param name Name to search for
     * @param page Page number
     * @param size Page size
     * @param userDetails Authenticated user details
     * @return ResponseEntity with page of reading lists
     */
    @GetMapping("/search/my")
    public ResponseEntity<ApiResponse<Page<ReadingListDto>>> searchMyReadingListsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReadingListDto> readingLists = readingListService.searchReadingListsByNameAndUserId(
                    name, userDetails.getUser().getId(), pageable);
            return ResponseEntity.ok(ApiResponse.success("Search results", readingLists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Add a book to a reading list
     * @param readingListId Reading list ID
     * @param bookId Book ID
     * @return ResponseEntity with updated reading list
     */
    @PostMapping("/{readingListId}/books/{bookId}")
    public ResponseEntity<ApiResponse<ReadingListDto>> addBookToReadingList(
            @PathVariable Long readingListId, 
            @PathVariable Long bookId) {
        try {
            ReadingListDto updatedReadingList = readingListService.addBookToReadingList(readingListId, bookId);
            return ResponseEntity.ok(ApiResponse.success("Book added to reading list successfully", updatedReadingList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Remove a book from a reading list
     * @param readingListId Reading list ID
     * @param bookId Book ID
     * @return ResponseEntity with updated reading list
     */
    @DeleteMapping("/{readingListId}/books/{bookId}")
    public ResponseEntity<ApiResponse<ReadingListDto>> removeBookFromReadingList(
            @PathVariable Long readingListId, 
            @PathVariable Long bookId) {
        try {
            ReadingListDto updatedReadingList = readingListService.removeBookFromReadingList(readingListId, bookId);
            return ResponseEntity.ok(ApiResponse.success("Book removed from reading list successfully", updatedReadingList));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Find reading lists containing a specific book
     * @param bookId Book ID
     * @return ResponseEntity with list of reading lists
     */
    @GetMapping("/containing-book/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReadingListDto>>> findReadingListsByBookId(@PathVariable Long bookId) {
        try {
            List<ReadingListDto> readingLists = readingListService.findReadingListsByBookId(bookId);
            return ResponseEntity.ok(ApiResponse.success("Reading lists containing book", readingLists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 