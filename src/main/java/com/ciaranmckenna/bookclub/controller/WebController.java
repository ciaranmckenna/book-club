package com.ciaranmckenna.bookclub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.dto.ReadingListCreateDto;
import com.ciaranmckenna.bookclub.dto.ReadingListFormDto;
import com.ciaranmckenna.bookclub.dto.UserRegistrationDto;
import com.ciaranmckenna.bookclub.entity.User;
import com.ciaranmckenna.bookclub.repository.UserRepository;
import com.ciaranmckenna.bookclub.security.CustomUserDetails;
import com.ciaranmckenna.bookclub.service.BookService;
import com.ciaranmckenna.bookclub.service.ReadingListService;
import com.ciaranmckenna.bookclub.service.UserService;

import jakarta.validation.Valid;

/**
 * Controller for web pages using Thymeleaf templates
 */
@Controller
public class WebController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReadingListService readingListService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Home page
     * @param model Model
     * @return Home page template
     */
    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }
    
    /**
     * Login page
     * @return Login page template
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * Registration page
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
     * @param userRegistrationDto Registration data
     * @param bindingResult Validation result
     * @param model Model
     * @return Redirect to login or back to registration form
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute UserRegistrationDto userRegistrationDto,
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
     * Dashboard for authenticated users
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
            long readingListCount = readingListService.getReadingListsByUserId(user.getId(), Pageable.unpaged()).getTotalElements();
            model.addAttribute("readingListCount", readingListCount);
        }
        
        return "dashboard";
    }
    
    /**
     * Books page
     * @param pageable Pagination information
     * @param model Model
     * @return Books page template
     */
    @GetMapping("/books")
    public String books(@PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable, 
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model) {
        model.addAttribute("books", bookService.getAllBooks(pageable));
        
        // For authenticated users, add their reading lists for the "add to list" functionality
        if (userDetails != null) {
            model.addAttribute("userReadingLists", 
                    readingListService.getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged()).getContent());
        }
        
        return "books";
    }
    
    /**
     * Book details page
     * @param id Book ID
     * @param model Model
     * @return Book details page template
     */
    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable Long id, 
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        BookDto book = bookService.getBookById(id);
        model.addAttribute("book", book);
        
        // For authenticated users, add their reading lists
        if (userDetails != null) {
            model.addAttribute("userReadingLists", 
                    readingListService.getReadingListsByUserId(userDetails.getUser().getId(), Pageable.unpaged()).getContent());
            
            model.addAttribute("userReadingListsContainingBook", 
                    readingListService.findReadingListsByBookId(id).stream()
                            .filter(list -> list.userId().equals(userDetails.getUser().getId()))
                            .toList());
        }
        
        return "book-details";
    }
    
    /**
     * Reading lists page for authenticated users
     * @param userDetails Authenticated user details
     * @param pageable Pagination information
     * @param model Model
     * @return Reading lists page template
     */
    @GetMapping("/reading-lists")
    public String readingLists(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable, 
                              Model model) {
        model.addAttribute("readingLists", 
                readingListService.getReadingListsByUserId(userDetails.getUser().getId(), pageable));
        return "reading-lists";
    }
    
    /**
     * Reading list creation form
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
     * @param readingListForm Form data
     * @param bindingResult Validation result
     * @param userDetails Authenticated user details
     * @param redirectAttributes Redirect attributes
     * @return Redirect to reading lists
     */
    @PostMapping("/reading-lists/create")
    public String createReadingList(@Valid @ModelAttribute("readingListForm") ReadingListFormDto readingListForm,
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
     * @param id Reading list ID
     * @param model Model
     * @return Reading list form template
     */
    @GetMapping("/reading-lists/{id}/edit")
    public String editReadingListForm(@PathVariable Long id, Model model) {
        var readingList = readingListService.getReadingListById(id);
        model.addAttribute("readingList", readingList);
        model.addAttribute("readingListForm", new ReadingListFormDto(readingList.name(), readingList.description()));
        model.addAttribute("isEdit", true);
        return "reading-list-form";
    }
    
    /**
     * Process reading list edit
     * @param id Reading list ID
     * @param readingListForm Updated reading list data
     * @param bindingResult Validation result
     * @param redirectAttributes Redirect attributes
     * @return Redirect to reading list details
     */
    @PostMapping("/reading-lists/{id}/edit")
    public String updateReadingList(@PathVariable Long id,
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
     * @param bookId Book ID
     * @param readingListId Reading list ID (optional)
     * @param newListName New list name (optional)
     * @param userDetails Authenticated user details
     * @param redirectAttributes Redirect attributes
     * @param returnUrl Return URL after adding
     * @return Redirect to specified URL or books page
     */
    @PostMapping("/reading-lists/add-book")
    public String addBookToReadingList(@RequestParam Long bookId,
                                      @RequestParam(required = false) Long readingListId,
                                      @RequestParam(required = false) String newListName,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes,
                                      @RequestParam(required = false) String returnUrl) {
        try {
            if (readingListId != null) {
                // Add to existing list
                readingListService.addBookToReadingList(readingListId, bookId);
                redirectAttributes.addFlashAttribute("successMessage", "Book added to reading list successfully");
            } else if (newListName != null && !newListName.isBlank()) {
                // Create new list and add book
                var createDto = new ReadingListCreateDto(newListName, "");
                var newList = readingListService.createReadingList(userDetails.getUser().getId(), createDto);
                readingListService.addBookToReadingList(newList.id(), bookId);
                redirectAttributes.addFlashAttribute("successMessage", "New reading list created and book added successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a reading list or create a new one");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:" + (returnUrl != null ? returnUrl : "/books");
    }
    
    /**
     * Remove a book from a reading list
     * @param listId Reading list ID
     * @param bookId Book ID
     * @param redirectAttributes Redirect attributes
     * @return Redirect to reading list details
     */
    @PostMapping("/reading-lists/{listId}/remove-book/{bookId}")
    public String removeBookFromReadingList(@PathVariable Long listId,
                                           @PathVariable Long bookId,
                                           RedirectAttributes redirectAttributes) {
        try {
            readingListService.removeBookFromReadingList(listId, bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book removed from reading list successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/reading-lists/" + listId;
    }
    
    /**
     * Admin dashboard page
     * @param model Model
     * @return Admin dashboard page template
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("totalBooks", bookService.getAllBooks(Pageable.unpaged()).getTotalElements());
        model.addAttribute("totalReadingLists", readingListService.getAllReadingLists(Pageable.unpaged()).getTotalElements());
        return "admin/dashboard";
    }
} 