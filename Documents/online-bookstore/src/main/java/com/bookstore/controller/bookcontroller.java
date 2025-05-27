package com.bookstore.controller;

import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @Autowired
    private BookRepository repo;

    @GetMapping("/books")
    public String list(Model model) {
        model.addAttribute("books", repo.findAll());
        return "books"; // Sẽ hiển thị trang books.html
    }
}
