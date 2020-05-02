package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.CorrectedDictation;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.CorrectedDictationRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.CorrectedDictationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CorrectedDictationServiceImplementation implements CorrectedDictationService {
    @Autowired
    CorrectedDictationRepository correctedDictationRepository;

    @Override
    public CorrectedDictation findById(Long id) {
        Optional<CorrectedDictation> correctedDictation = correctedDictationRepository.findById(id);
        if(!correctedDictation.isPresent()) return null;
        return correctedDictation.get();
    }

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
    @Transactional
    public CorrectedDictation updateCorrectedDictation(CorrectedDictation correctedDictation) {
        return correctedDictationRepository.save(correctedDictation);
    }

    @Override
    @Transactional
    public CorrectedDictation saveCorrectedDictation(CorrectedDictation correctedDictation) {
        return correctedDictationRepository.save(correctedDictation);
    }

    @Override
    @Transactional
    public void deleteCorrectedDictation(CorrectedDictation correctedDictation) {
        correctedDictationRepository.delete(correctedDictation);
    }

    @Override
    public List<CorrectedDictation> findByUser(User user) {
        return correctedDictationRepository.findByUser(user);
    }
}
