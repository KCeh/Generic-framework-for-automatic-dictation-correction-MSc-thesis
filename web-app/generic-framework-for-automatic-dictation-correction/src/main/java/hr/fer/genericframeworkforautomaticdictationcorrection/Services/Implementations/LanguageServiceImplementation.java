package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.LanguageRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageServiceImplementation implements LanguageService {
    @Autowired
    LanguageRepository languageRepository;

    @Override
    public Language findByName(String name) {
        return languageRepository.findByName(name);
    }

    @Override
    public Language findByCode(String code) {
        return languageRepository.findByCode(code);
    }

    @Override
    public List<Language> findAll() {
        return languageRepository.findAll();
    }

    @Override
    public Language updateLanguage(Language language) {
        return languageRepository.save(language);
    }

    @Override
    public Language saveLanguage(Language language) {
        return languageRepository.save(language);
    }

    @Override
    public void deleteLanguage(Language language) {
        languageRepository.delete(language);
    }
}
