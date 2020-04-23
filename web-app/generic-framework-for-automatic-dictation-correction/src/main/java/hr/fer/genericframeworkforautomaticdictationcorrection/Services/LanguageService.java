package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;

import java.util.List;

public interface LanguageService {
    Language findByName(String name);

    Language findByCode(String code);

    List<Language> findAll();

    Language updateLanguage(Language language);

    Language saveLanguage(Language language);

    void deleteLanguage(Language language);
}
