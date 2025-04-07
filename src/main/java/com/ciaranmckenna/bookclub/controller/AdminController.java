package com.ciaranmckenna.bookclub.controller;

import com.ciaranmckenna.bookclub.dto.BookDto;
import com.ciaranmckenna.bookclub.dto.ReadingListCreateDto;
import com.ciaranmckenna.bookclub.service.BookService;
import com.ciaranmckenna.bookclub.service.ReadingListService;
import com.ciaranmckenna.bookclub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for admin-only web pages
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReadingListService readingListService;
    
    /**
     * User management page
     * @param model Model
     * @return User management page template
     */
    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user-management";
    }
    
    /**
     * Book management page
     * @param pageable Pagination information
     * @param search Search query (optional)
     * @param model Model
     * @return Book management page template
     */
    @GetMapping("/books")
    public String bookManagement(
            @PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String search,
            Model model) {
        
        if (search != null && !search.isBlank()) {
            model.addAttribute("books", bookService.searchBooks(search, pageable));
        } else {
            model.addAttribute("books", bookService.getAllBooks(pageable));
        }
        
        return "admin/book-management";
    }
    
    /**
     * Process book creation
     * @param bookDto Book data
     * @param bindingResult Validation result
     * @param redirectAttributes Redirect attributes
     * @return Redirect to book management
     */
    @PostMapping("/books/create")
    public String createBook(@Valid @ModelAttribute BookDto bookDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid book data. Please check your input.");
            return "redirect:/admin/books";
        }
        
        try {
            bookService.createBook(bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book created successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/books";
        }
    }
    
    /**
     * Process book edit
     * @param id Book ID
     * @param bookDto Updated book data
     * @param bindingResult Validation result
     * @param redirectAttributes Redirect attributes
     * @return Redirect to book management
     */
    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute BookDto bookDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid book data. Please check your input.");
            return "redirect:/admin/books";
        }
        
        try {
            bookService.updateBook(id, bookDto);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/books";
        }
    }
    
    /**
     * Process book deletion
     * @param id Book ID
     * @param redirectAttributes Redirect attributes
     * @return Redirect to book management
     */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully");
            return "redirect:/admin/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/books";
        }
    }
    
    /**
     * Reading list management page
     * @param pageable Pagination information
     * @param search Search query (optional)
     * @param model Model
     * @return Reading list management page template
     */
    @GetMapping("/reading-lists")
    public String readingListManagement(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String search,
            Model model) {
        
        if (search != null && !search.isBlank()) {
            model.addAttribute("readingLists", readingListService.searchReadingListsByName(search, pageable));
        } else {
            model.addAttribute("readingLists", readingListService.getAllReadingLists(pageable));
        }
        
        // Add users for the dropdown
        model.addAttribute("users", userService.getAllUsers());
        
        return "admin/reading-list-management";
    }
    
    /**
     * Process reading list creation by admin
     * @param userId User ID
     * @param readingListCreateDto Reading list data
     * @param bindingResult Validation result
     * @param redirectAttributes Redirect attributes
     * @return Redirect to reading list management
     */
    @PostMapping("/reading-lists/create")
    public String createReadingList(
            @RequestParam Long userId,
            @Valid @ModelAttribute ReadingListCreateDto readingListCreateDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid reading list data. Please check your input.");
            return "redirect:/admin/reading-lists";
        }
        
        try {
            readingListService.createReadingList(userId, readingListCreateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Reading list created successfully");
            return "redirect:/admin/reading-lists";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/reading-lists";
        }
    }
    
    /**
     * Process reading list deletion
     * @param id Reading list ID
     * @param redirectAttributes Redirect attributes
     * @return Redirect to reading list management
     */
    @PostMapping("/reading-lists/{id}/delete")
    public String deleteReadingList(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            readingListService.deleteReadingList(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reading list deleted successfully");
            return "redirect:/admin/reading-lists";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/reading-lists";
        }
    }
} 