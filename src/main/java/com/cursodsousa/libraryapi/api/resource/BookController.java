package com.cursodsousa.libraryapi.api.resource;

import com.cursodsousa.libraryapi.api.dto.BookDTO;
import com.cursodsousa.libraryapi.api.model.entity.Book;
import com.cursodsousa.libraryapi.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;

    public BookController(BookService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto ){
        Book entity = service.save(toEntity(dto));
        return toDto(entity);
    }

    private Book toEntity(BookDTO dto){
        return Book.builder()
                    .id(dto.getId())
                    .author(dto.getAuthor())
                    .title(dto.getTitle())
                    .isbn(dto.getIsbn())
                    .build();
    }

    private BookDTO toDto(Book entity){
        return BookDTO.builder()
                .id(entity.getId())
                .author(entity.getAuthor())
                .title(entity.getTitle())
                .isbn(entity.getIsbn())
                .build();
    }
}
