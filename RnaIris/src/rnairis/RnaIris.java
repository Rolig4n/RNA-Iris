/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rnairis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static rnairis.GeraCSV.geraCSV;

/**
 *
 * @author hiago
 */
public class RnaIris {

    //atributos gerais
    static public List<Iris> baseDados = new ArrayList<Iris>();
    static public List<Iris> resultado = new ArrayList<Iris>();

    //base individuais para neuronio classe 1,2 e 3
    static public double[][] baseTreina;
    static public double[][] baseValida;
    static public double[][] baseTeste;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //lista resultado final
        List<Estatistica> lstResFinal = new ArrayList<Estatistica>();
        List<Estatistica> lstEstatisticaN1 = new ArrayList<Estatistica>();
        List<Estatistica> lstEstatisticaN2 = new ArrayList<Estatistica>();
        List<Estatistica> lstEstatisticaN3 = new ArrayList<Estatistica>();

        //definir os padrões de execução da rede neural
        double bias = 1;
        double taxaAprendizado = 0.001;
        double[] pesoW = {0, 0, 0, 0};
        int nroIteracoes = 20000;

        boolean geraBaseValidacao = true;
        boolean normalizaBase = true;
        //dados do arquivo -- C:\winebase
        String arquivo = "C:/Users/hiago/OneDrive/Documents/NetBeansProjects/RnaIris/Base Iris/iris.data";
        List<Iris> base = readFile(arquivo);
        if (normalizaBase) {
            baseDados = normalizaBase(base);
        } else {
            baseDados = base;
        }
        //monta laço para N testes
        for (int nroTestes = 0; nroTestes <= 999; nroTestes++) {
            double[] wC1;
            double[] wC2;
            double[] wC3;
            /*----------Treinamento dos neuronios -----------*/
            //definir o neuronio para identificação de vinhos de classe 1
            Perceptron oRnaC1 = new Perceptron();
            oRnaC1.setAlfa(taxaAprendizado);
            oRnaC1.setBias(bias);
            oRnaC1.setNET(bias);
            oRnaC1.setW(pesoW);
            oRnaC1.setMaxIte(nroIteracoes);
            sorteiaBases(baseDados, geraBaseValidacao, 1);//sorteia bases para classe 1
            wC1 = oRnaC1.treinar(baseTreina);
            //testando neuronio 1
            Estatistica oEstatisticaN1 = oRnaC1.testarTreinamento(baseTeste, wC1);
            lstEstatisticaN1.add(oEstatisticaN1);
            
            //definir o neuronio para identificação de vinhos de classe 2
            Perceptron oRnaC2 = new Perceptron();
            oRnaC2.setAlfa(taxaAprendizado);
            oRnaC2.setBias(bias);
            oRnaC2.setNET(bias);
            oRnaC2.setW(pesoW);
            oRnaC2.setMaxIte(nroIteracoes);
            atualizaBases(2);
            wC2 = oRnaC2.treinar(baseTreina);
            //testando neuronio 2
            Estatistica oEstatisticaN2 = oRnaC1.testarTreinamento(baseTeste, wC2);
            lstEstatisticaN2.add(oEstatisticaN2);
            
            //definir o neuronio para identificação de vinhos de classe 3
            Perceptron oRnaC3 = new Perceptron();
            oRnaC3.setAlfa(taxaAprendizado);
            oRnaC3.setBias(bias);
            oRnaC3.setNET(bias);
            oRnaC3.setW(pesoW);
            oRnaC3.setMaxIte(nroIteracoes);
            atualizaBases(3);
            wC3 = oRnaC3.treinar(baseTreina);
            //testando neuronio 3
            Estatistica oEstatisticaN3 = oRnaC1.testarTreinamento(baseTeste, wC3);
            lstEstatisticaN3.add(oEstatisticaN3);
            
            Perceptron oRnaSaida = new Perceptron();
            oRnaSaida.setAlfa(taxaAprendizado);
            oRnaSaida.setBias(bias);
            oRnaSaida.setNET(bias);
            oRnaSaida.setW(pesoW);
            oRnaSaida.setMaxIte(nroIteracoes);

            List<Iris> baseClassificada = oRnaSaida.executarRNA(baseTeste, wC1, wC2, wC3);

            int acertos = 0, erros = 0;
            for (Iris oIris : baseClassificada) {
                if (oIris.getA5() == oIris.getClassificacao()) {
                    acertos++;
                } else {
                    erros++;
                }
            }
            Estatistica oEst = new Estatistica(String.valueOf(acertos), String.valueOf(erros), "", "");
            lstResFinal.add(oEst);
        }
        geraCSV(lstEstatisticaN1, "neuronio1.csv");
        geraCSV(lstEstatisticaN2, "neuronio2.csv");
        geraCSV(lstEstatisticaN3, "neuronio3.csv");
        geraCSV(lstResFinal, "resultadoFinal.csv");
    }

    static private List<Iris> readFile(String arquivo) {
        List<Iris> lstBase = new ArrayList<Iris>();
        try {
            BufferedReader base = new BufferedReader(new FileReader(arquivo));
            while (base.ready()) {
                //realiza a leitura linha a linha
                String linhaBase = base.readLine();
                //os valores na linha são definidos pelo separador ","
                String[] valueFields = linhaBase.split(",");
                Iris oIris = new Iris();
                oIris.setA1(Double.parseDouble(valueFields[0]));
                oIris.setA2(Double.parseDouble(valueFields[1]));
                oIris.setA3(Double.parseDouble(valueFields[2]));
                oIris.setA4(Double.parseDouble(valueFields[3]));
                int classe = 0;
                if (valueFields[4].equals("Iris-setosa")) {
                    classe = 1;
                } else if (valueFields[4].equals("Iris-versicolor")) {
                    classe = 2;
                } else if (valueFields[4].equals("Iris-virginica")) {
                    classe = 3;
                }
                oIris.setA5(classe);
                lstBase.add(oIris);
            }
            base.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstBase;
    }

    static private List<Iris> normalizaBase(List<Iris> lstIris) {
        List<Iris> listaNormalizada = new ArrayList<Iris>();
        double[] a1 = new double[lstIris.size()];
        double[] a2 = new double[lstIris.size()];
        double[] a3 = new double[lstIris.size()];
        double[] a4 = new double[lstIris.size()];
        //gerar vetores
        int contaElementos = 0;
        for (Iris oIris : lstIris) {
            a1[contaElementos] = oIris.getA1();
            a2[contaElementos] = oIris.getA2();
            a3[contaElementos] = oIris.getA3();
            a4[contaElementos] = oIris.getA4();
            contaElementos++;
        }
        //calcular o desvio padrão do vetores e sua média
        double dvA1 = calcularDesvioPadrao(a1);
        double mediaA1 = calcularMedia(a1);
        double dvA2 = calcularDesvioPadrao(a2);
        double mediaA2 = calcularMedia(a2);
        double dvA3 = calcularDesvioPadrao(a3);
        double mediaA3 = calcularMedia(a3);
        double dvA4 = calcularDesvioPadrao(a4);
        double mediaA4 = calcularMedia(a4);
        for (Iris oIris : lstIris) {
            Iris oIrisNorm = new Iris();
            oIrisNorm.setA1((oIris.getA1() - mediaA1) / dvA1);
            oIrisNorm.setA2((oIris.getA2() - mediaA2) / dvA2);
            oIrisNorm.setA3((oIris.getA3() - mediaA3) / dvA3);
            oIrisNorm.setA4((oIris.getA4() - mediaA4) / dvA4);
            oIrisNorm.setA5(oIris.getA5());
            listaNormalizada.add(oIrisNorm);
        }
        return listaNormalizada;
    }

    static private double calcularMedia(double[] listaValores) {
        double resultadoMedia = 0;
        double somatorio = 0;
        int tamanhoVetor = listaValores.length;
        for (int i = 0; i < tamanhoVetor; i++) {
            somatorio = somatorio + listaValores[i];
        }
        resultadoMedia = somatorio / tamanhoVetor;
        return resultadoMedia;
    }

    static private double calcularDesvioPadrao(double[] listaValores) {
        double media = calcularMedia(listaValores);
        double somatorio = 0;
        int tamanhoVetor = listaValores.length;
        for (int i = 0; i < tamanhoVetor; i++) {
            somatorio = somatorio + Math.pow((listaValores[i] - media), 2);
        }
        //soma dos quadrados da diferença entre cada valor e sua média
        //aritmética, dividida pela quantidade de elementos no vetor
        double valorVariancia = somatorio / tamanhoVetor;
        //desvio padrao é raiz quadrada da variância
        double valorDesvioPadrao = Math.sqrt(valorVariancia);
        return valorDesvioPadrao;
    }

    static private void sorteiaBases(List<Iris> lstIris, boolean valida, int classe) {
        //definir percentual do rateio das bases
        double percTreina = 0, percValida = 0, percTeste = 0;
        if (valida) {
            percTreina = 0.5;
            percValida = 0.2;
            percTeste = 0.3;
        } else {
            percTreina = 0.7;
            percValida = 0;
            percTeste = 0.3;
        }
        //conta nro de casos
        int nroC1 = 0, nroC2 = 0, nroC3 = 0;
        int nroBaseTreinaC1 = 0, nroBaseTreinaC2 = 0, nroBaseTreinaC3 = 0;
        int nroBaseValidaC1 = 0, nroBaseValidaC2 = 0, nroBaseValidaC3 = 0;
        int nroBaseTesteC1 = 0, nroBaseTesteC2 = 0, nroBaseTesteC3 = 0;
        //contar nro de ocorrencias de cada classe na base de dados
        for (Iris oIris : lstIris) {
            if (oIris.getA5() == 1) {
                nroC1++;
            } else if (oIris.getA5() == 2) {
                nroC2++;
            } else if (oIris.getA5() == 3) {
                nroC3++;
            }
        }
        //define tamanho de cada tipo de base para cada classe
        nroBaseTreinaC1 = (int) (nroC1 * percTreina);
        nroBaseTreinaC2 = (int) (nroC2 * percTreina);
        nroBaseTreinaC3 = (int) (nroC3 * percTreina);

        nroBaseValidaC1 = (int) (nroC1 * percValida);
        nroBaseValidaC2 = (int) (nroC2 * percValida);
        nroBaseValidaC3 = (int) (nroC3 * percValida);

        nroBaseTesteC1 = (nroC1 - (nroBaseTreinaC1 + nroBaseValidaC1));
        nroBaseTesteC2 = (nroC2 - (nroBaseTreinaC2 + nroBaseValidaC2));
        nroBaseTesteC3 = (nroC3 - (nroBaseTreinaC3 + nroBaseValidaC3));
        //declara o tamanho de cada base
        int tamBaseTreina = 0, tamBaseValida = 0, tamBaseTeste = 0;
        if (nroBaseValidaC1 > 0) {
            //se houver validação
            tamBaseTreina = nroBaseTreinaC1 + nroBaseTreinaC2 + nroBaseTreinaC3;
            tamBaseValida = nroBaseValidaC1 + nroBaseValidaC2 + nroBaseValidaC3;
            tamBaseTeste = lstIris.size() - (tamBaseTreina + tamBaseValida);
            baseTreina = new double[tamBaseTreina][6];
            baseValida = new double[tamBaseValida][6];
            baseTeste = new double[tamBaseTeste][6];
        } else {
            //se não houver validação
            tamBaseTreina = nroBaseTreinaC1 + nroBaseTreinaC2 + nroBaseTreinaC3;
            tamBaseValida = 0;
            tamBaseTeste = lstIris.size() - (tamBaseTreina + tamBaseValida);
            baseTreina = new double[tamBaseTreina][6];
            baseTeste = new double[tamBaseTeste][6];
        }
        int sorteia = 0;
        int ctaBase1Classe1 = 0, ctaBase1Classe2 = 0, ctaBase1Classe3 = 0;
        int ctaBase2Classe1 = 0, ctaBase2Classe2 = 0, ctaBase2Classe3 = 0;
        int ctaBase3Classe1 = 0, ctaBase3Classe2 = 0, ctaBase3Classe3 = 0;
        int linhaBase1 = 0, linhaBase2 = 0, linhaBase3 = 0;
        //
        Random randBase = new Random();
        int contaelemento = -1;
        for (Iris oSepara : lstIris) {
            contaelemento++;
            System.out.println(contaelemento);
            //seleciona 0 ou 1 se for a classe de interesse
            int valorD = 0;
            if (oSepara.getA5() == classe) {
                valorD = 1;
            }
            /*
              Sorteia qual base vai pertencer o elemento proporcional
              tamanho da base respeitando o tamanho de cada base especifica
             */
            boolean passa;
            do {
                passa = true;
                int nroRandomico = randBase.nextInt(99);
                if (tamBaseValida > 0) {
                    if (nroRandomico <= 69) {
                        sorteia = 1;
                    } else if (nroRandomico > 69 && nroRandomico <= 84) {
                        sorteia = 2;
                    } else if (nroRandomico > 84 && nroRandomico <= 99) {
                        sorteia = 3;
                    }
                } else {
                    if (nroRandomico <= 69) {
                        sorteia = 1;
                    } else if (nroRandomico > 69 && nroRandomico <= 99) {
                        sorteia = 2;
                    }
                }
                int classeElemento = oSepara.getA5();
                //validar meu sorteio
                if (sorteia == 1 && linhaBase1 < tamBaseTreina
                        && ((oSepara.getA5() == 1 && ctaBase1Classe1 < nroBaseTreinaC1)
                        || (oSepara.getA5() == 2 && ctaBase1Classe2 < nroBaseTreinaC2)
                        || (oSepara.getA5() == 3 && ctaBase1Classe3 < nroBaseTreinaC3))) {
                    passa = false;
                } else if (sorteia == 2 && linhaBase2 < tamBaseTeste
                        && ((oSepara.getA5() == 1 && ctaBase2Classe1 < nroBaseTesteC1)
                        || (oSepara.getA5() == 2 && ctaBase2Classe2 < nroBaseTesteC2)
                        || (oSepara.getA5() == 3 && ctaBase2Classe3 < nroBaseTesteC3))) {
                    passa = false;
                } else if (sorteia == 3 && linhaBase3 < tamBaseValida
                        && ((oSepara.getA5() == 1 && ctaBase3Classe1 < nroBaseValidaC1)
                        || (oSepara.getA5() == 2 && ctaBase3Classe2 < nroBaseValidaC2)
                        || (oSepara.getA5() == 3 && ctaBase3Classe3 < nroBaseValidaC3))) {
                    passa = false;
                }
            } while (passa);

            if (sorteia == 1) {
                //base treinamento
                baseTreina[linhaBase1][0] = oSepara.getA1();
                baseTreina[linhaBase1][1] = oSepara.getA2();
                baseTreina[linhaBase1][2] = oSepara.getA3();
                baseTreina[linhaBase1][3] = oSepara.getA4();
                baseTreina[linhaBase1][4] = oSepara.getA5();
                baseTreina[linhaBase1][5] = valorD;
                linhaBase1++;
                //acumula tipo de classe inserida na base
                if (oSepara.getA5() == 1) {
                    ctaBase1Classe1++;
                } else if (oSepara.getA5() == 2) {
                    ctaBase1Classe2++;
                } else if (oSepara.getA5() == 3) {
                    ctaBase1Classe3++;
                }
            } else if (sorteia == 2) {
                //base teste
                baseTeste[linhaBase2][0] = oSepara.getA1();
                baseTeste[linhaBase2][1] = oSepara.getA2();
                baseTeste[linhaBase2][2] = oSepara.getA3();
                baseTeste[linhaBase2][3] = oSepara.getA4();
                baseTeste[linhaBase2][4] = oSepara.getA5();
                baseTeste[linhaBase2][5] = valorD;
                linhaBase2++;
                //acumula tipo de classe inserida na base
                if (oSepara.getA5() == 1) {
                    ctaBase2Classe1++;
                } else if (oSepara.getA5() == 2) {
                    ctaBase2Classe2++;
                } else if (oSepara.getA5() == 3) {
                    ctaBase2Classe3++;
                }
            } else if (sorteia == 3) {
                //base validação
                baseValida[linhaBase3][0] = oSepara.getA1();
                baseValida[linhaBase3][1] = oSepara.getA2();
                baseValida[linhaBase3][2] = oSepara.getA3();
                baseValida[linhaBase3][3] = oSepara.getA4();
                baseValida[linhaBase3][4] = oSepara.getA5();
                baseValida[linhaBase3][5] = valorD;
                linhaBase3++;
                //acumula tipo de classe inserida na base
                if (oSepara.getA5() == 1) {
                    ctaBase3Classe1++;
                } else if (oSepara.getA5() == 2) {
                    ctaBase3Classe2++;
                } else if (oSepara.getA5() == 3) {
                    ctaBase3Classe3++;
                }
            }
        }
    }

    static private void atualizaBases(int classe) {
        int tamBaseTreina = baseTreina.length;
        int tamBaseTeste = baseTeste.length;
        int tamBaseValida = 0;
        if (!(baseValida == null)) {
            tamBaseValida = baseValida.length;
        }
        int valorD = 1;
        for (int x = 0; x < tamBaseTreina; x++) {
            if (baseTreina[x][4] == classe) {
                baseTreina[x][5] = valorD;
            } else {
                baseTreina[x][5] = 0;
            }
        }
        for (int x = 0; x < tamBaseTeste; x++) {
            if (baseTeste[x][4] == classe) {
                baseTeste[x][5] = valorD;
            } else {
                baseTeste[x][5] = 0;
            }
        }
        for (int x = 0; x < tamBaseValida; x++) {
            if (baseValida[x][4] == classe) {
                baseValida[x][5] = valorD;
            } else {
                baseValida[x][5] = 0;
            }
        }
    }
}
