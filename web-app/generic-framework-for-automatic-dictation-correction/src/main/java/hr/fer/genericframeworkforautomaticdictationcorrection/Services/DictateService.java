package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewDictateFrom;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Dictate;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Language;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;

import java.util.List;

public interface DictateService {
    Dictate findById(Long id);

    Dictate findByName(String name);

    List<Dictate> findAll();

    List<Dictate> findByUser(User user);

    List<Dictate> findByLanguage(Language language);

    Dictate updateDictate(Dictate dictate);

    Dictate saveDictate(Dictate dictate);

    void deleteDictate(Dictate dictate);

    Dictate addDictate(NewDictateFrom newDictateFrom, User user);

    Dictate editDictate(NewDictateFrom newDictateFrom, User user);
}
