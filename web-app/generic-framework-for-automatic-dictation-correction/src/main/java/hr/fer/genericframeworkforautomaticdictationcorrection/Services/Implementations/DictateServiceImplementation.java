package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.DictateRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.DictateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DictateServiceImplementation implements DictateService {
    @Autowired
    DictateRepository dictateRepository;

    @Override
    public Dictate findByName(String name) {
        return dictateRepository.findByName(name);
    }

    @Override
    public List<Dictate> findAll() {
        return dictateRepository.findAll();
    }

    @Override
    public List<Dictate> findByUser(User user) {
        return dictateRepository.findByUser(user);
    }

    @Override
    public List<Dictate> findByLanguage(Language language) {
        return dictateRepository.findByLanguage(language);
    }

    @Override
    @Transactional
    public Dictate updateDictate(Dictate dictate) {
        return dictateRepository.save(dictate);
    }

    @Override
    @Transactional
    public Dictate saveDictate(Dictate dictate) {
        return dictateRepository.save(dictate);
    }

    @Override
    @Transactional
    public void deleteDictate(Dictate dictate) {
        dictateRepository.delete(dictate);
    }
}
