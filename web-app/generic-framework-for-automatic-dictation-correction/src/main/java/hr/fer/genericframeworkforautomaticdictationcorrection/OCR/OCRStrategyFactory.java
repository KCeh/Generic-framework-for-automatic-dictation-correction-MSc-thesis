package hr.fer.genericframeworkforautomaticdictationcorrection.OCR;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OCRStrategyFactory {
    private Map<String, OCR> strategies;

    public OCRStrategyFactory(Set<OCR> setOfAvailableMethods){
        strategies = new HashMap<>();
        setOfAvailableMethods.forEach(s->strategies.put(s.getName(),s));
    }

    public OCR getMethod(String methodName){
        return strategies.get(methodName);
    }

    public List<String> getAllMethodesNames(){
        Set<String> allNamesSet = strategies.keySet();
        List<String> list = new ArrayList<>(allNamesSet);
        Collections.sort(list);
        return list;
    }
}
