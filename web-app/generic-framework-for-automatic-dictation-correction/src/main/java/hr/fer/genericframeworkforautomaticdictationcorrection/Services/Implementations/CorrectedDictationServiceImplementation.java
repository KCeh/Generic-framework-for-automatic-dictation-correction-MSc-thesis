package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.CorrectedDictationRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.CorrectedDictationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrectedDictationServiceImplementation implements CorrectedDictationService {
    @Autowired
    CorrectedDictationRepository correctedDictationRepository;

    @Override
    public CorrectedDictation findByName(String name) {
        return correctedDictationRepository.findByName(name);
    }

    @Override
    public CorrectedDictation findByUsedOCRMethod(String usedOCRMethod) {
        return correctedDictationRepository.findByUsedOCRMethod(usedOCRMethod);
    }

    @Override
    public List<CorrectedDictation> findAll() {
        return correctedDictationRepository.findAll();
    }

    @Override
    public List<CorrectedDictation> findByDictate(Dictate dictate) {
        return correctedDictationRepository.findByDictate(dictate);
    }

    @Override
    public CorrectedDictation updateCorrectedDictation(CorrectedDictation correctedDictation) {
        return correctedDictationRepository.save(correctedDictation);
    }

    @Override
    public CorrectedDictation saveCorrectedDictation(CorrectedDictation correctedDictation) {
        return correctedDictationRepository.save(correctedDictation);
    }

    @Override
    public void deleteCorrectedDictation(CorrectedDictation correctedDictation) {
        correctedDictationRepository.delete(correctedDictation);
    }

    @Override
    public List<CorrectedDictation> findByUser(User user) {
        return correctedDictationRepository.findByUser(user);
    }
}
