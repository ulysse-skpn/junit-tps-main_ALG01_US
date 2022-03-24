package eu.fr.indyli.formation.tdd.junit.dbunit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import eu.fr.indyli.formation.tdd.junit.dbunit.dao.IBookRepository;
import eu.fr.indyli.formation.tdd.junit.dbunit.entity.Adherent;
import eu.fr.indyli.formation.tdd.junit.dbunit.entity.Book;
import eu.fr.indyli.formation.tdd.junit.dbunit.entity.RequestAdherent;
import eu.fr.indyli.formation.tdd.junit.dbunit.service.IBookService;
import eu.fr.indyli.formation.tdd.junit.dbunit.service.exception.BookException;

@Service("bookService")
public class BookServiceImpl extends java.util.Observable implements IBookService {

  private static Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);
  @Resource(name = "bookRepository")
  private IBookRepository bookRepository = null;


  /**
   * Map des postulants à un bouquin
   */
  private Map<String, List<Adherent>> mapPretendantsBook;

  public BookServiceImpl() {
    mapPretendantsBook = new HashMap<String, List<Adherent>>();
  }

  public void addObserver(Observer o) {
    RequestAdherent a = (RequestAdherent) o;
    mapPretendantsBook.putIfAbsent(a.getRequestISBN(), new ArrayList<Adherent>());
    // On ajoute un nouveau pretendant
    mapPretendantsBook.get(a.getRequestISBN()).add(a);
  }

  public void deleteObserver(Observer o) {}

  public Map<String, List<Adherent>> getMapPretendantsBook() {
    return mapPretendantsBook;
  }

  public void setMapPretendantsBook(Map<String, List<Adherent>> mapPretendantsBook) {
    this.mapPretendantsBook = mapPretendantsBook;
  }

  public Boolean rentBook(Adherent a, String isbnRent) throws BookException {
    LOGGER.info("Mr {} a demande le booquin d'ISBN : {}", a.getLastname(), isbnRent);
    Book book = this.bookRepository.findByIsbn(isbnRent);
    if (book == null) {
      throw new BookException("AUCUN BOUQUIN PORTANT CET ISBN");
    }
    if (book.getQuantity() <= 0) {
      RequestAdherent reqAd = new RequestAdherent(a.getId(), a.getLastname(), isbnRent);
      this.addObserver(reqAd);
      return Boolean.FALSE;
    } else {// Quantityé disponible, alors on effectue la sortie
      Integer qty = book.getQuantity();
      qty--;
      book.setQuantity(qty);
      this.bookRepository.save(book);
      return Boolean.TRUE;
    }
  }

  public void turnBackBook(Adherent a, String isbn) throws BookException {
    Book book = this.bookRepository.findByIsbn(isbn);
    if (book == null) {
      throw new BookException("AUCUN BOUQUIN PORTANT CET ISBN");
    }
    Integer qty = book.getQuantity();
    qty++;
    book.setQuantity(qty);
    this.bookRepository.save(book);
    // On teste s'il y'a les pretendants pour une diffusion
    List<Adherent> requestClients = this.getMapPretendantsBook().get(isbn);
    if (!CollectionUtils.isEmpty(requestClients)) {
      requestClients.stream().peek(reqCustomer -> reqCustomer.update((Observable) this, isbn))
          .collect(Collectors.toList());
    }
  }

  @Override
  public Boolean isBookAvalaible(String isbn) {
    Book book = this.bookRepository.findByIsbn(isbn);
    Integer qty = book.getQuantity();
    return qty > 0;
  }

  @Override
  public Integer getNbRequestCustomerByISBN(String isbn) {
    if (!this.mapPretendantsBook.containsKey(isbn)) {
      return 0;
    }
    return this.mapPretendantsBook.get(isbn).size();
  }

  @Override
  public Book findByIsbn(String isbn) {
    return this.bookRepository.findByIsbn(isbn);
  }

  @Override
  public List<Book> findAll() {
    return this.bookRepository.findAll();
  }

  @Override
  public Book findById(Long id) {
    Book book = this.bookRepository.findById(id).orElse(null);
    return book;
  }

  @Override
  public Integer countAvailableBookByParutionDate(Date date) {
    return this.bookRepository.countAvailableBookByParutionDate(date);
  }

}
