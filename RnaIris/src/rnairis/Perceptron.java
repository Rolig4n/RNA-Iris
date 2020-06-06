/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rnairis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hiago
 */
public class Perceptron {

    private double[] w; //pesos sinápticos
    private double NET; //atributo responsável pelo somatório (rede)
    private double alfa; //taxa de redução da velocidade de aprendizado
    private double bias; //valor bias
    private int maxIte; //valor máximo de iterações durante o processo de aprendizado.

    private final int epocasMax = 30;//número max de epocas
    private int count = 0; //contagem de epocas durante o treinamento
    private int[][] matrizAprendizado = new int[5][5];

    public void setW(double[] w) {
        this.w = w;
    }

    public void setNET(double NET) {
        this.NET = NET;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public void setMaxIte(int maxIte) {
        this.maxIte = maxIte;
    }

    //Executar com 3 neurônios
    public List<Iris> executarRNA(double[][] base, double[] w_c1, double[] w_c2, double[] w_c3) {
        //resultado
        List<Iris> lstResultado = new ArrayList<Iris>();
        double y = 0, d = 0;
        //inicializa o bias
        for (int x = 0; x < base.length; x++) {
            base[x][0] = this.bias;
            base[x][5] = d;
        }
        for (int x = 0; x < base.length; x++) {
            d = base[x][4]; //classe do vinho que veio classificada
            //pega amostra
            double[] amostra = new double[6];
            for (int j = 0; j < 6; j++) {
                amostra[j] = base[x][j];
            }
            //testar a amostra nos neuronios
            double rna1 = executar(amostra, w_c1);
            double rna2 = executar(amostra, w_c2);
            double rna3 = executar(amostra, w_c3);
            int resultado = 0;
            if (rna1 > rna2 && rna1 > rna3) {
                resultado = 1;
            } else if (rna2 > rna1 && rna2 > rna3) {
                resultado = 2;
            } else {
                resultado = 3;
            }
            Iris oIris = new Iris();
            oIris.setA1(amostra[0]);
            oIris.setA2(amostra[1]);
            oIris.setA3(amostra[2]);
            oIris.setA4(amostra[3]);
            oIris.setA5((int) amostra[4]);
            oIris.setClassificacao(resultado);
            lstResultado.add(oIris);
        }
        return lstResultado;
    }

    private double executar(double[] amostra, double[] w) {
        double y = 0, soma = 0;
        //calcula a saida da função de ativação e função soma
        soma = 0;
        for (int x = 0; x < w.length; x++) {
            soma = soma + (amostra[x] * w[x]);
        }
        //retorno da função soma
        y = soma;
        return y;
    }

    public double[] treinar(double[][] baseTreina) {
        //inicializa vetor de pesos w
        double[] w = new double[4];
        //iniciliza vetor w (pesos)
        for (int j = 0; j < w.length; j++) {
            //inicializa w com pesos igual a zero
            w[j] = 0;
        }
        //iniciliza X0 igual a Bias em todas as amostras da base de dados
        for (int x = 0; x < baseTreina.length; x++) {
            baseTreina[x][0] = this.bias;
        }
        //contador de iteracoes do aprendizado
        int iteracao = 1;
        //inicializa soma dos erros 
        double erro = 1;
        //faça enquanto iteracao for menor que o máximo de iterações e 
        //enquanto erro for diferente de 0;
        while (iteracao < this.maxIte && erro != 0) {
            erro = 0;
            //inicia loop de treinamento
            for (int i = 0; i < baseTreina.length; i++) {
                //inicializa fator de erro
                double y = 0, soma = 0;
                //variavel d --> resposta esperada
                double d = baseTreina[i][5];
                //calcula a saida da função soma
                soma = ((baseTreina[i][0] * w[0])
                        + (baseTreina[i][1] * w[1])
                        + (baseTreina[i][2] * w[2])
                        + (baseTreina[i][3] * w[3]));
                //função de ativação binária
                if (soma > 0) {
                    y = 1;
                } else {
                    y = 0;
                }
                //realiza o aprendizado supervisionado
                //verifica a resposta calculada com a esperada
                if (y != d) {
                    //atualizar o vetor de pesos w
                    for (int j = 0; j < w.length; j++) {
                        //formula aprendizado
                        w[j] = w[j] + this.alfa * (d - y) * baseTreina[i][j];
                    }
                    //acumula erro
                    erro = erro + Math.pow((d - y), 2);
                }
            }
            iteracao++;
        }
        return w;
    }

    //testar treinamento com a base teste
    public Estatistica testarTreinamento(double[][] baseTeste, double[] w) {
        List<Iris> lstResultado = new ArrayList<Iris>();
        int[] classificacao = new int[baseTeste.length];
        int vp = 0, vn = 0, fp = 0, fn = 0;
        //inicializa X0 igual ao Bias em todas as amostras da base
        for (int x = 0; x < baseTeste.length; x++) {
            baseTeste[x][0] = this.bias;
        }
        //inicia loop para classificação de dados
        for (int i = 0; i < baseTeste.length; i++) {
            //inicializa fator erro
            double y = 0, soma = 0;
            //inicializa variavel com resultado esperado
            double d = baseTeste[i][5];
            //calcula a saida da função de ativação e função soma
            soma = 0;
            for (int x = 0; x < w.length; x++) {
                soma = soma + (baseTeste[i][x] * w[x]);
            }
            //saida neuronio
            if (soma > 0) {
                y = 1;
            } else {
                y = 0;
            }
            if (y == d) {
                if (d == 0) {
                    vn++;
                } else {
                    vp++;
                }
            } else {
                if (d == 0) {
                    fn++;
                } else {
                    fp++;
                }
            }
        }
        Estatistica oEstatistica = new Estatistica(
                String.valueOf(vp),
                String.valueOf(vn),
                String.valueOf(fp),
                String.valueOf(fn));
        return oEstatistica;
    }
}
