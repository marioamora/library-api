package com.cursodsousa.libraryapi.api.resource;

import com.cursodsousa.libraryapi.api.dto.BookDTO;
import com.cursodsousa.libraryapi.api.model.entity.Book;
import com.cursodsousa.libraryapi.exception.BusinessException;
import com.cursodsousa.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Should create a book with success")
    public void createBookTest() throws Exception {

        BookDTO dto = createNewBook();
        Book savedBook = Book.builder().id(10l).author("Mario").title("My first book").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( BOOK_API )
                .contentType( APPLICATION_JSON )
                .accept( APPLICATION_JSON )
                .content( json );

        mvc.perform( request )
            .andExpect( status().isCreated() )
            .andExpect( jsonPath("id").value(10l) )
            .andExpect( jsonPath("title").value(dto.getTitle()) )
            .andExpect( jsonPath("author").value(dto.getAuthor()) )
            .andExpect( jsonPath("isbn").value(dto.getIsbn()) )
        ;
    }

    @Test
    @DisplayName("Should throw validation error when hasn't sufficient data for book creation")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( BOOK_API )
                .contentType( APPLICATION_JSON )
                .accept( APPLICATION_JSON )
                .content( json );

        mvc.perform( request )
            .andExpect( status().isBadRequest() )
            .andExpect( jsonPath("errors", hasSize(3)) );
    }

    @Test
    @DisplayName("Should throw error when trying to register a book with isbn already registered")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String errorMessage = "Isbn already registered.";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post( BOOK_API )
                .contentType( APPLICATION_JSON )
                .accept( APPLICATION_JSON )
                .content( json );

        mvc.perform( request )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(1)) )
                .andExpect( jsonPath( "errors[0]").value(errorMessage));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Mario").title("My first book").isbn("001").build();
    }
}
