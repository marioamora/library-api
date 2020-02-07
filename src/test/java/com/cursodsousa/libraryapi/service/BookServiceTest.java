package com.cursodsousa.libraryapi.service;

import com.cursodsousa.libraryapi.api.model.entity.Book;
import com.cursodsousa.libraryapi.exception.BusinessException;
import com.cursodsousa.libraryapi.model.repository.BookRepository;
import com.cursodsousa.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl( repository );
    }

    @Test
    @DisplayName("Should save a book")
    public void saveBookTest(){
        Book book = createValidBook();
        when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);

        Book expectedBook = Book.builder().id(1l).author("Myself").title("New book").isbn("123123").build();
        when( service.save(book) ).thenReturn( expectedBook ) ;

        Book savedBook = service.save(book);

        assertThat( savedBook.getId() ).isNotNull();
        assertThat( savedBook.getAuthor() ).isEqualTo( expectedBook.getAuthor() );
        assertThat( savedBook.getTitle() ).isEqualTo( expectedBook.getTitle() );
        assertThat( savedBook.getIsbn() ).isEqualTo( expectedBook.getIsbn() );
    }

    @Test
    @DisplayName("Should throw business error when trying registry a book with isbn duplicated")
    public void shouldNotSaveABookWithDuplicatedISBN(){

        Book actualBook = createValidBook();
        when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        Throwable exception = Assertions.catchThrowable( () -> service.save(actualBook) );
        assertThat( exception )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already registered.");

        Mockito.verify( repository, Mockito.never() ).save(actualBook);
    }

    private Book createValidBook() {
        return Book.builder().author("Myself").title("New book").isbn("123123").build();
    }
}
