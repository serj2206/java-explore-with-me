package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.model.compilation.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {


}
