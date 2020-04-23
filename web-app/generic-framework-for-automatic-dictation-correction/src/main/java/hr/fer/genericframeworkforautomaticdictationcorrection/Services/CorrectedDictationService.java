package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;

import java.util.List;

public interface CorrectedDictationService {

    CorrectedDictation findByName(String name);

    CorrectedDictation findByUsedOCRMethod(String usedOCRMethod);

    List<CorrectedDictation> findAll();

    List<CorrectedDictation> findByDictate(Dictate dictate);

    CorrectedDictation updateCorrectedDictation(CorrectedDictation correctedDictation);

    CorrectedDictation saveCorrectedDictation(CorrectedDictation correctedDictation);

    void deleteCorrectedDictation(CorrectedDictation correctedDictation);
}
