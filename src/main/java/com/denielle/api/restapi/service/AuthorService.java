package com.denielle.api.restapi.service;

import com.denielle.api.restapi.dto.AuthorDTO;
import com.denielle.api.restapi.exception.FieldAlreadyExistsException;
import com.denielle.api.restapi.exception.NotFoundException;
import com.denielle.api.restapi.mapper.AuthorMapper;
import com.denielle.api.restapi.model.Author;
import com.denielle.api.restapi.model.Book;
import com.denielle.api.restapi.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorDTO getById(int id) throws NotFoundException {
        Author author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Author with id of " + id + " does not exists"));
        return authorMapper.toDTO(author);
    }

    public AuthorDTO getByName(String name) throws NotFoundException {
        Author author = authorRepository.fetchByName(name).orElseThrow(() -> new NotFoundException("Author with name of " + name + " does not exists"));
        return authorMapper.toDTO(author);
    }

    public List<String> getAllBooks(int id) throws NotFoundException {
        Author author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Author with id of " + id + " does not exists"));
        return author.getBookList()
                .stream()
                .map(Book::getTitle)
                .toList();
    }

    public int getBookCount(int id) throws NotFoundException {
        Author author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Author with id of " + id + " does not exists"));
        return author.getBookList().size();
    }

    public List<String> searchByFirstLetter(char firstLetter) {
        return authorRepository.searchByFirstLetter(firstLetter);
    }

    public List<AuthorDTO> getAll() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toDTO)
                .toList();
    }

    public List<AuthorDTO> getAllById(List<Integer> ids) {
        return ids.stream()
                .map(this::getById)
                .toList();
    }

    public List<AuthorDTO> getAll(int pageNumber, int pageSize) {
        Pageable pageable = PageSorter.getPage(pageNumber, pageSize);

        return authorRepository.findAll(pageable)
                .stream()
                .map(authorMapper::toDTO)
                .toList();
    }

    public List<AuthorDTO> getAll(int pageNumber, int pageSize, String sortDirection, String sortProperty) {
        Pageable pageable = PageSorter.getPage(pageNumber, pageSize, sortDirection, sortProperty);

        return authorRepository.findAll(pageable)
                .stream()
                .map(authorMapper::toDTO)
                .toList();
    }

    public List<Integer> saveAll(List<AuthorDTO> authors) {
        return authors.stream()
                .map(this::save)
                .toList();
    }

    public int save(AuthorDTO authorDTO) throws FieldAlreadyExistsException {
        if (isNameAlreadyExists(authorDTO.getName())) throw new FieldAlreadyExistsException("Author with name of " + authorDTO.getName() + " already exists");
        Author author = authorMapper.toEntity(authorDTO);
        authorRepository.save(author);
        log.debug("Author saved successfully {}", author.getName());
        return author.getId();
    }

    public void delete(int id) {
        authorRepository.deleteById(id);
        log.debug("Author with id of {} deleted successfully", id);
    }

    public void update(int id, AuthorDTO authorDTO) throws NotFoundException, FieldAlreadyExistsException {
        if (isNameAlreadyExists(authorDTO.getName())) throw new FieldAlreadyExistsException("Author with name of " + authorDTO.getName() + " already exists");

        Author author = authorRepository.findById(id).orElseThrow(() -> new NotFoundException("Author with id of " + id + " does not exists"));

        authorMapper.updateEntity(author, authorDTO);
        authorRepository.save(author);

        log.debug("Author with id of {} updated successfully", id);
    }

    public boolean isNameAlreadyExists(String authorName) {
        return authorRepository.findAll()
                .stream()
                .map(Author::getName)
                .anyMatch(authorName::equalsIgnoreCase);
    }
}
