package com.volante.tools;

import com.tplus.transform.design.*;
import com.tplus.transform.design.app.ConsoleDesigner;
import com.tplus.transform.design.composer.VolanteComposer;
import com.tplus.transform.design.serial.SerialException;
import com.tplus.transform.design.ui.ConsoleCartridge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyValidations extends ConsoleCartridge {
    private static String DESIGER_HOME = "/home/administrator/Installations/Volante_622_RC";
    private static String CARTPATH = "/home/administrator/Desktop/WFH/03SwiftIIdentifier_02Apr2020/04 20May2020";
    VolanteComposer volanteComposer;
    Map<String,FieldValidationRule> validationNameVsfieldValidationRuleMap = new HashMap<>();

    @Override
    public int main(String[] strings) throws Throwable {
        init();
        listFiles(new File(CARTPATH));
        return 0;
    }

    private void init() throws Exception {
        System.setProperty("designer.home", DESIGER_HOME);
        volanteComposer = (VolanteComposer) ConsoleDesigner.createComposer();
    }

    private void listFiles(File file) throws IOException, SerialException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                listFiles(f);
            }
        } else {
            String path = file.getPath();
            if (path.endsWith(".car")) {
                disableVals(path);
            }
        }
    }

    private void disableVals(String cartPath) throws IOException, SerialException {
        Cartridge cartridge = volanteComposer.openCartridge(cartPath);
        List<ExternalMessage> externalMessages = cartridge.getExternalMessages();
        for (ExternalMessage externalMessage : externalMessages) {
            List<FieldsValidationRules> validationRulesList = externalMessage.getValidationRulesList();
            int count = 0;
            for (FieldsValidationRules fieldsValidationRules : validationRulesList) {
                ValidationRules dataValidationRules = fieldsValidationRules.getDataValidationRules();
                List<FieldValidationRule> allValidationRules = dataValidationRules.getAllValidationRules();
                for (FieldValidationRule fieldValidationRule : allValidationRules) {
                    ValidationType ruleType = fieldValidationRule.getRuleType();
                    if (ruleType == ValidationType.FORMULA) {
                        String name = fieldValidationRule.getName();
                        if (validationNameVsfieldValidationRuleMap.containsKey(name)) {
                            FieldValidationRule fieldValidationRule_Map = validationNameVsfieldValidationRuleMap.get(name);

                            if(!verifyVals(fieldValidationRule, fieldValidationRule_Map)){
                                System.out.println(cartridge.getName() + "\t" + name);
                            }

                        } else {
                            validationNameVsfieldValidationRuleMap.put(name, fieldValidationRule);
                        }
                    }
                }
            }
        }
        volanteComposer.saveCartridge(cartridge, cartPath);
    }

    private boolean verifyVals(FieldValidationRule f1, FieldValidationRule f2){
        if(!f1.getActionMessage().equals(f2.getActionMessage())){
            return false;
        }
        if(!f1.getValidationRule().equals(f2.getValidationRule())){
            return false;
        }
            return true;
    }
}
