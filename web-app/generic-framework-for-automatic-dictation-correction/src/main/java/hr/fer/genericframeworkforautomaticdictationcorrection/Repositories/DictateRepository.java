package hr.fer.genericframeworkforautomaticdictationcorrection.Repositories;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DictateRepository  extends JpaRepository<Dictate, Long> {
    Optional<Dictate> findById(Long id);

    Dictate findByName(String name);

    List<Dictate> findAll();

    List<Dictate> findByUser(User user);

    List<Dictate> findByLanguage(Language language);
}
