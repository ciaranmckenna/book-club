package com.ciaranmckenna.bookclub.controller;

import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.dto.BookFormDto;
import com.ciaranmckenna.bookclub.dto.ReadingListCreateDto;
import com.ciaranmckenna.bookclub.dto.ReadingListFormDto;
import com.ciaranmckenna.bookclub.dto.UserRegistrationDto;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.security.CustomUserDetails;
import com.ciaranmckenna.bookclub.service.BookService;
import com.ciaranmckenna.bookclub.service.PasswordResetService;
import com.ciaranmckenna.bookclub.service.ReadingListService;
import com.ciaranmckenna.bookclub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Controller for web pages using Thymeleaf templates */
@Controller
public class WebController {

  @Autowired private UserService userService;

  @Autowired private BookService bookService;

  @Autowired private ReadingListService readingListService;

  @Autowired private PasswordResetService passwordResetService;

  /**
   * Home page
   *
   * @param model Model
   * @return Home page template
   */
  @GetMapping("/")
  public String home(Model model) {
    return "home";
  }

  /**
   * Login page
   *
   * @return Login page template
   */
  @GetMapping("/login")
  public String login() {
    return "login";
  }

  /**
   * Registration page
   *
   * @param model Model
   * @return Registration page template
   */
  @GetMapping("/register")
  public String register(Model model) {
    // Use the static factory method to create an empty DTO without triggering validation
    UserRegistrationDto dto = UserRegistrationDto.createEmpty();
    model.addAttribute("userRegistrationDto", dto);

    return "register";
  }

  /**
   * Process user registration
   *
   * @param userRegistrationDto Registration data
   * @param bindingResult Validation result
   * @param model Model
   * @return Redirect to login or back to registration form
   */
  @PostMapping("/register")
  public String processRegistration(
      @Valid @ModelAttribute UserRegistrationDto userRegistrationDto,
      BindingResult bindingResult,
      Model model,
      RedirectAttributes redirectAttributes) {
    // Log incoming request for debugging
    System.out.println("POST /register received");
    System.out.println("POST /register - Form data: " + userRegistrationDto);
    System.out.println("POST /register - Has binding errors: " + bindingResult.hasErrors());
    if (bindingResult.hasErrors()) {
      System.out.println("POST /register - Validation errors: " + bindingResult.getAllErrors());
      return "register";
    }

    try {
      userService.registerUser(userRegistrationDto);
      redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
      return "redirect:/login";
    } catch (Exception e) {
      System.out.println("POST /register - Error: " + e.getMessage());
      model.addAttribute("error", e.getMessage());
      return "register";
    }
  }

  /**
   * Forgot password page
   *
   * @return Forgot password template
   */
  @GetMapping("/forgot-password")
  public String forgotPassword() {
    return "forgot-password";
  }

  /**
   * Process forgot password form
   *
   * @param email Email address
   * @param redirectAttributes Redirect attributes for flash messages
   * @return Redirect to login with success message
   */
  @PostMapping("/forgot-password")
  public String processForgotPassword(
      @RequestParam String email, RedirectAttributes redirectAttributes) {
    boolean emailSent = passwordResetService.initiatePasswordReset(email);

    if (emailSent) {
      redirectAttributes.addFlashAttribute("success", "Password reset email sent!");
    } else {
      redirectAttributes.addFlashAttribute(
          "info", "If that email exists, a reset link has been sent.");
    }

    return "redirect:/login";
  }

  /**
   * Password reset page
   *
   * @param token Reset token
   * @param model Model
   * @return Reset password template or redirect to login
   */
  @GetMapping("/reset-password")
  public String resetPassword(@RequestParam String token, Model model) {
    if (passwordResetService.validateResetToken(token)) {
      model.addAttribute("token", token);
      return "reset-password";
    } else {
      model.addAttribute("error", "Invalid or expired reset token");
      return "redirect:/login";
    }
  }

  /**
   * Process password reset
   *
   * @param token Reset token
   * @param newPassword New password
   * @param confirmPassword Confirm password
   * @param redirectAttributes Redirect attributes
   * @param model Model
   * @return Redirect to login or back to reset form
   */
  @PostMapping("/reset-password")
  public String processPasswordReset(
      @RequestParam String token,
      @RequestParam String newPassword,
      @RequestParam String confirmPassword,
      RedirectAttributes redirectAttributes,
      Model model) {

    if (!newPassword.equals(confirmPassword)) {
      model.addAttribute("error", "Passwords do not match");
      model.addAttribute("token", token);
      return "reset-password";
    }

    if (newPassword.length() < 8) {
      model.addAttribute("error", "Password must be at least 8 characters long");
      model.addAttribute("token", token);
      return "reset-password";
    }

    boolean resetSuccessful = passwordResetService.resetPassword(token, newPassword);

    if (resetSuccessful) {
      redirectAttributes.addFlashAttribute("success", "Password reset successful! Please log in.");
    } else {
      redirectAttributes.addFlashAttribute("error", "Invalid or expired reset token");
    }

    return "redirect:/login";
  }

  /**
   * Dashboard for authenticated users
   *
   * @param userDetails Currently authenticated user
   * @param model Model
   * @return Dashboard page template
   */
  @GetMapping("/dashboard")
  public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    if (userDetails != null) {
      // Add user data
      User user = userDetails.getUser();
      model.addAttribute("user", user);

      // Add reading lists
      var pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
      var readingLists = readingListService.getReadingListsByUserId(user.getId(), pageable);
      model.addAttribute("recentReadingLists", readingLists.getContent());

      // Add recently added books
      var books = bookService.getAllBooks(pageable);
      model.addAttribute("recentBooks", books.getContent());

      // Add statistics
      long readingListCount =
          readingListService
              .getReadingListsByUserId(user.getId(), Pageable.unpaged())
              .getTotalElements();
      model.addAttribute("readingListCount", readingListCount);
    }

    return "dashboard";
  }

  /**
   * Books page
   *
   * @param pageable Pagination information
   * @param model Model
   * @return Books page template
   */
  @GetMapping("/books")
  public String books(
      @PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {
    model.addAttribute("books", bookService.getAllBooks(pageable));

    // For authenticated users, add their reading lists for the "add to list" functionality
    if (userDetails != null) {
      model.addAttribute(
          "userReadingLists",
          readingListService
              .getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged())
              .getContent());
    }

    return "books";
  }

  /**
   * Book details page
   *
   * @param id Book ID
   * @param model Model
   * @return Book details page template
   */
  @GetMapping("/books/{id}")
  public String bookDetails(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
    BookDto book = bookService.getBookById(id);
    model.addAttribute("book", book);

    // For authenticated users, add their reading lists
    if (userDetails != null) {
      model.addAttribute(
          "userReadingLists",
          readingListService
              .getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged())
              .getContent());

      model.addAttribute(
          "userReadingListsContainingBook",
          readingListService.findReadingListsByBookId(id).stream()
              .filter(list -> list.userId().equals(userDetails.getUser().getId()))
              .toList());
    }

    return "book-details";
  }

  /**
   * Reading lists page for authenticated users
   *
   * @param userDetails Authenticated user details
   * @param pageable Pagination information
   * @param model Model
   * @return Reading lists page template
   */
  @GetMapping("/reading-lists")
  public String readingLists(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
      Model model) {
    model.addAttribute(
        "readingLists",
        readingListService.getReadingListsByUserId(userDetails.getUser().getId(), pageable));
    return "reading-lists";
  }

  /**
   * Reading list creation form
   *
   * @param model Model
   * @return Reading list form template
   */
  @GetMapping("/reading-lists/create")
  public String createReadingListForm(Model model) {
    // Use the form DTO which doesn't have constructor validation
    model.addAttribute("readingListForm", new ReadingListFormDto());
    model.addAttribute("isEdit", false);
    return "reading-list-form";
  }

  /**
   * Process reading list creation
   *
   * @param readingListForm Form data
   * @param bindingResult Validation result
   * @param userDetails Authenticated user details
   * @param redirectAttributes Redirect attributes
   * @return Redirect to reading lists
   */
  @PostMapping("/reading-lists/create")
  public String createReadingList(
      @Valid @ModelAttribute("readingListForm") ReadingListFormDto readingListForm,
      BindingResult bindingResult,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "reading-list-form";
    }

    try {
      // Convert form DTO to business DTO
      ReadingListCreateDto createDto = readingListForm.toCreateDto();
      readingListService.createReadingList(userDetails.getUser().getId(), createDto);
      redirectAttributes.addFlashAttribute("successMessage", "Reading list created successfully");
      return "redirect:/reading-lists";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/reading-lists";
    }
  }

  /**
   * Reading list edit form
   *
   * @param id Reading list ID
   * @param model Model
   * @return Reading list form template
   */
  @GetMapping("/reading-lists/{id}/edit")
  public String editReadingListForm(@PathVariable Long id, Model model) {
    var readingList = readingListService.getReadingListById(id);
    model.addAttribute("readingList", readingList);
    model.addAttribute(
        "readingListForm", new ReadingListFormDto(readingList.name(), readingList.description()));
    model.addAttribute("isEdit", true);
    return "reading-list-form";
  }

  /**
   * Process reading list edit
   *
   * @param id Reading list ID
   * @param readingListForm Updated reading list data
   * @param bindingResult Validation result
   * @param redirectAttributes Redirect attributes
   * @return Redirect to reading list details
   */
  @PostMapping("/reading-lists/{id}/edit")
  public String updateReadingList(
      @PathVariable Long id,
      @Valid @ModelAttribute("readingListForm") ReadingListFormDto readingListForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      return "reading-list-form";
    }

    try {
      // Convert form DTO to business DTO
      ReadingListCreateDto createDto = readingListForm.toCreateDto();
      readingListService.updateReadingList(id, createDto);
      redirectAttributes.addFlashAttribute("successMessage", "Reading list updated successfully");
      return "redirect:/reading-lists/" + id;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/reading-lists/" + id;
    }
  }

  /**
   * Delete a reading list
   *
   * @param id Reading list ID
   * @param redirectAttributes Redirect attributes
   * @return Redirect to reading lists
   */
  @PostMapping("/reading-lists/{id}/delete")
  public String deleteReadingList(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      readingListService.deleteReadingList(id);
      redirectAttributes.addFlashAttribute("successMessage", "Reading list deleted successfully");
      return "redirect:/reading-lists";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/reading-lists";
    }
  }

  /**
   * Reading list details page
   *
   * @param id Reading list ID
   * @param model Model
   * @return Reading list details page template
   */
  @GetMapping("/reading-lists/{id}")
  public String readingListDetails(@PathVariable Long id, Model model) {
    model.addAttribute("readingList", readingListService.getReadingListById(id));
    return "reading-list-details";
  }

  /**
   * Add a book to a reading list
   *
   * @param bookId Book ID
   * @param readingListId Reading list ID (optional)
   * @param newListName New list name (optional)
   * @param userDetails Authenticated user details
   * @param redirectAttributes Redirect attributes
   * @param returnUrl Return URL after adding
   * @return Redirect to specified URL or books page
   */
  @PostMapping("/reading-lists/add-book")
  public String addBookToReadingList(
      @RequestParam Long bookId,
      @RequestParam(required = false) Long readingListId,
      @RequestParam(required = false) String newListName,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      RedirectAttributes redirectAttributes,
      @RequestParam(required = false) String returnUrl) {
    try {
      if (readingListId != null) {
        // Add to existing list
        readingListService.addBookToReadingList(readingListId, bookId);
        redirectAttributes.addFlashAttribute(
            "successMessage", "Book added to reading list successfully");
      } else if (newListName != null && !newListName.isBlank()) {
        // Create new list and add book
        var createDto = new ReadingListCreateDto(newListName, "");
        var newList =
            readingListService.createReadingList(userDetails.getUser().getId(), createDto);
        readingListService.addBookToReadingList(newList.id(), bookId);
        redirectAttributes.addFlashAttribute(
            "successMessage", "New reading list created and book added successfully");
      } else {
        redirectAttributes.addFlashAttribute(
            "errorMessage", "Please select a reading list or create a new one");
      }
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }

    return "redirect:" + (returnUrl != null ? returnUrl : "/books");
  }

  /**
   * Remove a book from a reading list
   *
   * @param listId Reading list ID
   * @param bookId Book ID
   * @param redirectAttributes Redirect attributes
   * @return Redirect to reading list details
   */
  @PostMapping("/reading-lists/{listId}/remove-book/{bookId}")
  public String removeBookFromReadingList(
      @PathVariable Long listId, @PathVariable Long bookId, RedirectAttributes redirectAttributes) {
    try {
      readingListService.removeBookFromReadingList(listId, bookId);
      redirectAttributes.addFlashAttribute(
          "successMessage", "Book removed from reading list successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    }

    return "redirect:/reading-lists/" + listId;
  }

  /**
   * Admin dashboard page
   *
   * @param model Model
   * @return Admin dashboard page template
   */
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminDashboard(Model model) {
    model.addAttribute("users", userService.getAllUsers());
    model.addAttribute(
        "totalBooks", bookService.getAllBooks(Pageable.unpaged()).getTotalElements());
    model.addAttribute(
        "totalReadingLists",
        readingListService.getAllReadingLists(Pageable.unpaged()).getTotalElements());
    return "admin/dashboard";
  }

  /**
   * Book creation form
   *
   * @param model Model
   * @param userDetails Authenticated user details
   * @return Book form template
   */
  @GetMapping("/books/add")
  public String createBookForm(
      Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
    model.addAttribute("bookForm", new BookFormDto());
    model.addAttribute("isEdit", false);

    // Add reading lists for the user
    if (userDetails != null) {
      model.addAttribute(
          "userReadingLists",
          readingListService
              .getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged())
              .getContent());
    }

    return "book-form";
  }

  /**
   * Process book creation
   *
   * @param bookForm Form data
   * @param bindingResult Validation result
   * @param readingListId Reading list ID to add book to (optional)
   * @param newListName New reading list name (optional)
   * @param userDetails Authenticated user details
   * @param redirectAttributes Redirect attributes
   * @return Redirect to books or reading list
   */
  @PostMapping("/books/add")
  public String createBook(
      @Valid @ModelAttribute("bookForm") BookFormDto bookForm,
      BindingResult bindingResult,
      @RequestParam(required = false) Long readingListId,
      @RequestParam(required = false) String newListName,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      // Add reading lists for the user
      if (userDetails != null) {
        model.addAttribute(
            "userReadingLists",
            readingListService
                .getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged())
                .getContent());
      }
      model.addAttribute("isEdit", false);
      return "book-form";
    }

    try {
      // Ensure userDetails is not null (should always be present for authenticated users)
      if (userDetails == null || userDetails.getUser() == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "User authentication required.");
        return "redirect:/books";
      }
      // Create the book with the authenticated user's ID
      BookDto createdBook = bookService.createBook(bookForm.toBookDto(), userDetails.getUser().getId());

      // Add to reading list if specified
      if (userDetails != null) {
        if (readingListId != null) {
          // Add to existing list
          readingListService.addBookToReadingList(readingListId, createdBook.id());
          redirectAttributes.addFlashAttribute(
              "successMessage", "Book created and added to reading list successfully");
          return "redirect:/reading-lists/" + readingListId;
        } else if (newListName != null && !newListName.isBlank()) {
          // Create new list and add book
          var createDto = new ReadingListCreateDto(newListName, "");
          var newList =
              readingListService.createReadingList(userDetails.getUser().getId(), createDto);
          readingListService.addBookToReadingList(newList.id(), createdBook.id());
          redirectAttributes.addFlashAttribute(
              "successMessage", "Book created and added to new reading list successfully");
          return "redirect:/reading-lists/" + newList.id();
        } else {
          // Just create the book
          redirectAttributes.addFlashAttribute("successMessage", "Book created successfully");
        }
      } else {
        redirectAttributes.addFlashAttribute("successMessage", "Book created successfully");
      }

      return "redirect:/books";
    } catch (Exception e) {
      // Add reading lists for the user
      if (userDetails != null) {
        model.addAttribute(
            "userReadingLists",
            readingListService
                .getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged())
                .getContent());
      }
      model.addAttribute("isEdit", false);
      model.addAttribute("errorMessage", e.getMessage());
      return "book-form";
    }
  }

  /**
   * Book edit form
   *
   * @param id Book ID
   * @param model Model
   * @param userDetails Currently authenticated user
   * @return Book form template
   */
  @GetMapping("/books/{id}/edit")
  public String editBookForm(
      @PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      BookDto book = bookService.getBookById(id);

      // Check if user is admin or the book owner
      boolean canEdit =
          userDetails != null
              && (userDetails.getAuthorities().stream()
                      .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                  || bookService.isBookOwner(id, userDetails.getUser().getId()));

      if (!canEdit) {
        return "redirect:/books/" + id;
      }

      model.addAttribute("bookForm", BookFormDto.fromBookDto(book));
      model.addAttribute("bookId", id);
      model.addAttribute("isEdit", true);
      return "book-form";
    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "redirect:/books";
    }
  }

  /**
   * Process book edit
   *
   * @param id Book ID
   * @param bookForm Updated book data
   * @param bindingResult Validation result
   * @param redirectAttributes Redirect attributes
   * @return Redirect to book details
   */
  @PostMapping("/books/{id}/edit")
  public String processBookEdit(
      @PathVariable Long id,
      @Valid @ModelAttribute BookFormDto bookForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute(
          "errorMessage", "Invalid book data. Please check your input.");
      return "redirect:/books/" + id + "/edit";
    }

    try {
      // Convert form data to DTO and perform partial update
      BookDto bookDto = bookForm.toBookDto();
      bookService.partialUpdateBook(id, bookDto);

      redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
      return "redirect:/books/" + id;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/books/" + id + "/edit";
    }
  }
}
