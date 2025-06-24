package com.ciaranmckenna.bookclub.service.impl;

import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.dto.ReadingListCreateDto;
import com.ciaranmckenna.bookclub.dto.ReadingListDto;
import com.ciaranmckenna.bookclub.entity.Book;
import com.ciaranmckenna.bookclub.entity.ReadingList;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.BookRepository;
import com.ciaranmckenna.bookclub.repository.ReadingListRepository;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import com.ciaranmckenna.bookclub.service.ReadingListService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementation of the ReadingListService interface */
@Service
public class ReadingListServiceImpl implements ReadingListService {

  @Autowired private ReadingListRepository readingListRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private BookRepository bookRepository;

  /**
   * Create a new reading list for a user
   *
   * @param userId User ID
   * @param createDto Reading list data
   * @return Newly created reading list
   */
  @Override
  @Transactional
  public ReadingListDto createReadingList(Long userId, ReadingListCreateDto createDto) {
    // Validate the DTO
    createDto.validateForProcessing();

    // Find user
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

    // Create new reading list
    ReadingList readingList = new ReadingList();
    readingList.setName(createDto.name());
    readingList.setDescription(createDto.description());
    readingList.setUser(user);

    // Save reading list
    ReadingList savedReadingList = readingListRepository.save(readingList);

    // Convert to DTO and return
    return convertToDto(savedReadingList);
  }

  /**
   * Get a reading list by ID
   *
   * @param id Reading list ID
   * @return Reading list with the specified ID
   * @throws EntityNotFoundException if reading list not found
   */
  @Override
  public ReadingListDto getReadingListById(Long id) {
    ReadingList readingList =
        readingListRepository
            .findWithBooksById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Reading list not found with id: " + id));
    return convertToDto(readingList);
  }

  /**
   * Update a reading list
   *
   * @param id Reading list ID
   * @param createDto Updated reading list data
   * @return Updated reading list
   * @throws EntityNotFoundException if reading list not found
   */
  @Override
  @Transactional
  public ReadingListDto updateReadingList(Long id, ReadingListCreateDto createDto) {
    // Validate the DTO
    createDto.validateForProcessing();

    ReadingList readingList =
        readingListRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityNotFoundException("Reading list not found with id: " + id));

    // Update reading list fields
    readingList.setName(createDto.name());
    readingList.setDescription(createDto.description());

    // Save updated reading list
    ReadingList updatedReadingList = readingListRepository.save(readingList);

    // Convert to DTO and return
    return convertToDto(updatedReadingList);
  }

  /**
   * Delete a reading list
   *
   * @param id Reading list ID
   * @throws EntityNotFoundException if reading list not found
   */
  @Override
  @Transactional
  public void deleteReadingList(Long id) {
    if (!readingListRepository.existsById(id)) {
      throw new EntityNotFoundException("Reading list not found with id: " + id);
    }
    readingListRepository.deleteById(id);
  }

  /**
   * Get reading lists for a user
   *
   * @param userId User ID
   * @param pageable Pagination information
   * @return Page of reading lists
   */
  @Override
  public Page<ReadingListDto> getReadingListsByUserId(Long userId, Pageable pageable) {
    return readingListRepository.findByUserId(userId, pageable).map(this::convertToDto);
  }

  /**
   * Get all reading lists
   *
   * @param pageable Pagination information
   * @return Page of reading lists
   */
  @Override
  public Page<ReadingListDto> getAllReadingLists(Pageable pageable) {
    return readingListRepository.findAll(pageable).map(this::convertToDto);
  }

  /**
   * Search reading lists by name
   *
   * @param name Name to search for
   * @param pageable Pagination information
   * @return Page of reading lists matching the name
   */
  @Override
  public Page<ReadingListDto> searchReadingListsByName(String name, Pageable pageable) {
    return readingListRepository
        .findByNameContainingIgnoreCase(name, pageable)
        .map(this::convertToDto);
  }

  /**
   * Search reading lists by name for a specific user
   *
   * @param name Name to search for
   * @param userId User ID
   * @param pageable Pagination information
   * @return Page of reading lists matching the name for the user
   */
  @Override
  public Page<ReadingListDto> searchReadingListsByNameAndUserId(
      String name, Long userId, Pageable pageable) {
    return readingListRepository
        .findByNameContainingIgnoreCaseAndUserId(name, userId, pageable)
        .map(this::convertToDto);
  }

  /**
   * Add a book to a reading list
   *
   * @param readingListId Reading list ID
   * @param bookId Book ID
   * @return Updated reading list
   * @throws EntityNotFoundException if reading list or book not found
   */
  @Override
  @Transactional
  public ReadingListDto addBookToReadingList(Long readingListId, Long bookId) {
    ReadingList readingList =
        readingListRepository
            .findById(readingListId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Reading list not found with id: " + readingListId));

    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

    // Add book to reading list
    readingList.addBook(book);

    // Save updated reading list
    ReadingList updatedReadingList = readingListRepository.save(readingList);

    // Convert to DTO and return
    return convertToDto(updatedReadingList);
  }

  /**
   * Remove a book from a reading list
   *
   * @param readingListId Reading list ID
   * @param bookId Book ID
   * @return Updated reading list
   * @throws EntityNotFoundException if reading list or book not found
   */
  @Override
  @Transactional
  public ReadingListDto removeBookFromReadingList(Long readingListId, Long bookId) {
    ReadingList readingList =
        readingListRepository
            .findById(readingListId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Reading list not found with id: " + readingListId));

    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

    // Remove book from reading list
    readingList.removeBook(book);

    // Save updated reading list
    ReadingList updatedReadingList = readingListRepository.save(readingList);

    // Convert to DTO and return
    return convertToDto(updatedReadingList);
  }

  /**
   * Find reading lists containing a specific book
   *
   * @param bookId Book ID
   * @return List of reading lists containing the book
   */
  @Override
  public List<ReadingListDto> findReadingListsByBookId(Long bookId) {
    return readingListRepository.findReadingListsByBookId(bookId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  /**
   * Check if a book is already in a reading list
   *
   * @param readingListId Reading list ID
   * @param bookId Book ID
   * @return true if the book is in the reading list, false otherwise
   */
  @Override
  public boolean isBookInReadingList(Long readingListId, Long bookId) {
    ReadingList readingList =
        readingListRepository
            .findWithBooksById(readingListId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Reading list not found with id: " + readingListId));

    return readingList.getBooks().stream().anyMatch(book -> book.getId().equals(bookId));
  }

  /**
   * Add multiple books to a reading list in a single operation
   *
   * @param readingListId Reading list ID
   * @param bookIds List of book IDs to add
   * @return Updated reading list
   * @throws EntityNotFoundException if reading list or any book not found
   */
  @Override
  @Transactional
  public ReadingListDto addBooksToReadingList(Long readingListId, List<Long> bookIds) {
    ReadingList readingList =
        readingListRepository
            .findById(readingListId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Reading list not found with id: " + readingListId));

    // Find all books
    List<Book> books = bookRepository.findAllById(bookIds);

    // Check if all books were found
    if (books.size() != bookIds.size()) {
      List<Long> foundBookIds = books.stream().map(Book::getId).toList();
      List<Long> missingBookIds =
          bookIds.stream().filter(id -> !foundBookIds.contains(id)).toList();
      throw new EntityNotFoundException("Books not found with ids: " + missingBookIds);
    }

    // Add each book to the reading list
    for (Book book : books) {
      if (!readingList.getBooks().contains(book)) {
        readingList.addBook(book);
      }
    }

    // Save updated reading list
    ReadingList updatedReadingList = readingListRepository.save(readingList);

    // Convert to DTO and return
    return convertToDto(updatedReadingList);
  }

  /**
   * Convert a ReadingList entity to a ReadingListDto
   *
   * @param readingList ReadingList entity
   * @return ReadingListDto
   */
  private ReadingListDto convertToDto(ReadingList readingList) {
    Set<BookDto> bookDtos = new HashSet<>();

    if (readingList.getBooks() != null) {
      bookDtos =
          readingList.getBooks().stream().map(this::convertBookToDto).collect(Collectors.toSet());
    }

    return new ReadingListDto(
        readingList.getId(),
        readingList.getName(),
        readingList.getDescription(),
        readingList.getCreatedAt(),
        readingList.getUser().getId(),
        readingList.getUser().getUsername(),
        bookDtos);
  }

  /**
   * Convert a Book entity to a BookDto
   *
   * @param book Book entity
   * @return BookDto
   */
  private BookDto convertBookToDto(Book book) {
    return new BookDto(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getPublicationDate(),
        book.getDescription(),
        book.getIsbn(),
        book.getCoverImageUrl());
  }
}
