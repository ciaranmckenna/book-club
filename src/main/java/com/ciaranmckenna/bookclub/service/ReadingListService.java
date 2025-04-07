package com.ciaranmckenna.bookclub.service;

import com.ciaranmckenna.bookclub.dto.ReadingListCreateDto;
import com.ciaranmckenna.bookclub.dto.ReadingListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing reading lists
 */
public interface ReadingListService {
    
    /**
     * Create a new reading list for a user
     * @param userId User ID
     * @param createDto Reading list data
     * @return Newly created reading list
     */
    ReadingListDto createReadingList(Long userId, ReadingListCreateDto createDto);
    
    /**
     * Get a reading list by ID
     * @param id Reading list ID
     * @return Reading list with the specified ID
     * @throws jakarta.persistence.EntityNotFoundException if reading list not found
     */
    ReadingListDto getReadingListById(Long id);
    
    /**
     * Update a reading list
     * @param id Reading list ID
     * @param createDto Updated reading list data
     * @return Updated reading list
     * @throws jakarta.persistence.EntityNotFoundException if reading list not found
     */
    ReadingListDto updateReadingList(Long id, ReadingListCreateDto createDto);
    
    /**
     * Delete a reading list
     * @param id Reading list ID
     * @throws jakarta.persistence.EntityNotFoundException if reading list not found
     */
    void deleteReadingList(Long id);
    
    /**
     * Get reading lists for a user
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of reading lists
     */
    Page<ReadingListDto> getReadingListsByUserId(Long userId, Pageable pageable);
    
    /**
     * Get all reading lists
     * @param pageable Pagination information
     * @return Page of reading lists
     */
    Page<ReadingListDto> getAllReadingLists(Pageable pageable);
    
    /**
     * Search reading lists by name
     * @param name Name to search for
     * @param pageable Pagination information
     * @return Page of reading lists matching the name
     */
    Page<ReadingListDto> searchReadingListsByName(String name, Pageable pageable);
    
    /**
     * Search reading lists by name for a specific user
     * @param name Name to search for
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of reading lists matching the name for the user
     */
    Page<ReadingListDto> searchReadingListsByNameAndUserId(String name, Long userId, Pageable pageable);
    
    /**
     * Add a book to a reading list
     * @param readingListId Reading list ID
     * @param bookId Book ID
     * @return Updated reading list
     * @throws jakarta.persistence.EntityNotFoundException if reading list or book not found
     */
    ReadingListDto addBookToReadingList(Long readingListId, Long bookId);
    
    /**
     * Remove a book from a reading list
     * @param readingListId Reading list ID
     * @param bookId Book ID
     * @return Updated reading list
     * @throws jakarta.persistence.EntityNotFoundException if reading list or book not found
     */
    ReadingListDto removeBookFromReadingList(Long readingListId, Long bookId);
    
    /**
     * Find reading lists containing a specific book
     * @param bookId Book ID
     * @return List of reading lists containing the book
     */
    List<ReadingListDto> findReadingListsByBookId(Long bookId);
} 