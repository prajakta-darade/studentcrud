package com.studentcrudoperation.studentCRUD;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentRepository repo;

    @GetMapping
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable int id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createStudent(@RequestBody Student student) {
        repo.save(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable int id, @RequestBody Student data) {
        Optional<Student> existingStudent = repo.findById(id);
        if (existingStudent.isPresent()) {
            Student updatedStudent = existingStudent.get();
            updatedStudent.setName(data.getName());
            updatedStudent.setAge(data.getAge());
            updatedStudent.setCourse(data.getCourse());
            repo.save(updatedStudent);
            return ResponseEntity.ok("Student with ID " + id + " updated successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student with ID " + id + " not found.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> removeStudent(@PathVariable int id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pagination/{pageNo}/{pageSize}")
    public List<Student> getPaginatedStudents(@PathVariable int pageNo, @PathVariable int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Student> pagedResult = repo.findAll(pageable);
        return pagedResult.hasContent() ? pagedResult.getContent() : List.of();
    }

    @GetMapping("/sorting")
    public List<Student> getSortedStudents(@RequestParam String field, @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return repo.findAll(Sort.by(sortDirection, field));
    }

    @GetMapping("/pagination-and-sorting/{pageNo}/{pageSize}")
    public List<Student> getPaginatedAndSortedStudents(@PathVariable int pageNo, @PathVariable int pageSize,
                                                        @RequestParam String sortField, @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Student> pagedResult = repo.findAll(pageable);
        return pagedResult.hasContent() ? pagedResult.getContent() : List.of();
    }
}
