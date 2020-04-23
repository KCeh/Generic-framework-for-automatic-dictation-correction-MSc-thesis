package hr.fer.genericframeworkforautomaticdictationcorrection.Repositories;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    //Language findById(Long id);
    Language findByName(String name);

    Language findByCode(String code);

    List<Language> findAll();
}
