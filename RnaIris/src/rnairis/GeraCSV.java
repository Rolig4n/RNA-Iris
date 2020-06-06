/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rnairis;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author hiago
 */
public class GeraCSV {

    static public void geraCSV(List<Estatistica> lstEstatistica, String nomeArquivo) throws IOException {
        String[] cabecalho = {"VP", "VN", "FP", "FN"};
        Writer oWriter = new FileWriter(nomeArquivo);
        CSVWriter csvWriter = new CSVWriter(oWriter);
        csvWriter.writeNext(cabecalho);
        for (Estatistica oEst : lstEstatistica) {
            String[] linha = {oEst.getVP(), oEst.getVN(), oEst.getFP(), oEst.getFN()};
            csvWriter.writeNext(linha);
        }
        oWriter.flush();
        oWriter.close();
    }
}
