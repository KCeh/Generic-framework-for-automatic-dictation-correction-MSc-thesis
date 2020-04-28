package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewDictateFrom;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.DictateRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.DictateService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.LanguageService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DictateServiceImplementation implements DictateService {
    @Autowired
    DictateRepository dictateRepository;

    @Autowired
    LanguageService languageService;

    @Override
    public Dictate findById(Long id) {
        Optional<Dictate> dictate = dictateRepository.findById(id);
        if(!dictate.isPresent()) return null;
        return dictate.get();
    }

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

    @Override
    public Dictate addDictate(NewDictateFrom newDictateFrom, User user) {
        Dictate dictate  = new Dictate();
        dictate.setUser(user);
        dictate.setName(newDictateFrom.getName());
        dictate.setText(newDictateFrom.getText());
        dictate.setAudioUrl(newDictateFrom.getAudioUrl());
        Language language  = languageService.findByCode(newDictateFrom.getLanguage());
        dictate.setLanguage(language);
        return saveDictate(dictate);
    }

    @Override
    public Dictate editDictate(NewDictateFrom newDictateFrom, User user) {
        Dictate dictate = findById(newDictateFrom.getId());
        if (ObjectUtils.isEmpty(dictate)){
            return addDictate(newDictateFrom, user);
        }

        dictate.setName(newDictateFrom.getName());
        dictate.setText(newDictateFrom.getText());
        dictate.setAudioUrl(newDictateFrom.getAudioUrl());
        Language language  = languageService.findByCode(newDictateFrom.getLanguage());
        dictate.setLanguage(language);
        return updateDictate(dictate);
    }
}
