package hr.fer.genericframeworkforautomaticdictationcorrection.Repositories;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectedDictationRepository extends JpaRepository<CorrectedDictation, Long> {

    //CorrectedDictation findById(Long id);

    CorrectedDictation findByName(String name);

    CorrectedDictation findByUsedOCRMethod(String usedOCRMethod);

    List<CorrectedDictation> findAll();

    List<CorrectedDictation> findByDictate(Dictate dictate);
}
