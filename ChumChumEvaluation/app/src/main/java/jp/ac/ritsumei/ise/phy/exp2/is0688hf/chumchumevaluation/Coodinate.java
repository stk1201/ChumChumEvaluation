package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

public class Coodinate {
    //TensorFlowで取得した座標を保存する場所。
    //ロード画面で保存し、スコア表示のところでスコア計算のために抽出する。
    //番号でパーツを識別する。
    private double[][] nose= new double[2][];//0
    private double[][] leftEye= new double[2][];//1
    private double[][] rightEye= new double[2][];//2
    private double[][] leftEar= new double[2][];//3
    private double[][] rightEar= new double[2][];//4
    private double[][] leftShoulder= new double[2][];//5
    private double[][] rightShoulder= new double[2][];//6
    private double[][] leftElbow= new double[2][];//7
    private double[][] rightElbow= new double[2][];//8
    private double[][] leftWrist= new double[2][];//9
    private double[][] rightWrist= new double[2][];//10
    private double[][] leftWaist= new double[2][];//11
    private double[][] rightWaist= new double[2][];//12
    private double[][] leftKnee= new double[2][];//13
    private double[][] rightKnee= new double[2][];//14
    private double[][] leftAnkle= new double[2][];//15
    private double[][] rightAnkle= new double[2][];//16

    public void coordinate(){

    }

    public void addCoordinate(int num, double[] x, double[] y){
       switch(num){
           case 0:
               nose[0] = new double[x.length];
               nose[0] = x;

               nose[1] = new double[y.length];
               nose[1] = y;
           break;

           case 1:
               leftEye[0] = new double[x.length];
               leftEye[0] = x;

               leftEye[1] = new double[y.length];
               leftEye[1] = y;
           break;

           case 2:
               rightEye[0] = new double[x.length];
               rightEye[0] = x;

               rightEye[1] = new double[y.length];
               rightEye[1] = y;
           break;

           case 3:
               leftEar[0] = new double[x.length];
               leftEar[0] = x;

               leftEar[1] = new double[y.length];
               leftEar[1] = y;
           break;

           case 4:
               rightEar[0] = new double[x.length];
               rightEar[0] = x;

               rightEar[1] = new double[y.length];
               rightEar[1] = y;
           break;

           case 5:
               leftShoulder[0] = new double[x.length];
               leftShoulder[0] = x;

               leftShoulder[1] = new double[y.length];
               leftShoulder[1] = y;
           break;

           case 6:
               rightShoulder[0] = new double[x.length];
               rightShoulder[0] = x;

               rightShoulder[1] = new double[y.length];
               rightShoulder[1] = y;
           break;

           case 7:
               leftElbow[0] = new double[x.length];
               leftElbow[0] = x;

               leftElbow[1] = new double[y.length];
               leftElbow[1] = y;
           break;

           case 8:
               rightElbow[0] = new double[x.length];
               rightElbow[0] = x;

               rightElbow[1] = new double[y.length];
               rightElbow[1] = y;
           break;

           case 9:
               leftWrist[0] = new double[x.length];
               leftWrist[0] = x;

               leftWrist[1] = new double[y.length];
               leftWrist[1] = y;
           break;

           case 10:
               rightWrist[0] = new double[x.length];
               rightWrist[0] = x;

               rightWrist[1] = new double[y.length];
               rightWrist[1] = y;
           break;

           case 11:
               leftWaist[0] = new double[x.length];
               leftWaist[0] = x;

               leftWaist[1] = new double[y.length];
               leftWaist[1] = y;
           break;

           case 12:
               rightWaist[0] = new double[x.length];
               rightWaist[0] = x;

               rightWaist[1] = new double[y.length];
               rightWaist[1] = y;
           break;

           case 13:
               leftKnee[0] = new double[x.length];
               leftKnee[0] = x;

               leftKnee[1] = new double[y.length];
               leftKnee[1] = y;
           break;

           case 14:
               rightKnee[0] = new double[x.length];
               rightKnee[0] = x;

               rightKnee[1] = new double[y.length];
               rightKnee[1] = y;
           break;

           case 15:
               leftAnkle[0] = new double[x.length];
               leftAnkle[0] = x;

               leftAnkle[1] = new double[y.length];
               leftAnkle[1] = y;
           break;

           case 16:
               rightAnkle[0] = new double[x.length];
               rightAnkle[0] = x;

               rightAnkle[1] = new double[y.length];
               rightAnkle[1] = y;
           break;

       }
    }

    public double[][] outCoordinate(int num) {
        switch (num) {
            case 0:
                return nose;
            case 1:
                return leftEye;
            case 2:
                return rightEye;
            case 3:
                return leftEar;
            case 4:
                return rightEar;
            case 5:
                return leftShoulder;
            case 6:
                return rightShoulder;
            case 7:
                return leftElbow;
            case 8:
                return rightElbow;
            case 9:
                return leftWrist;
            case 10:
                return rightWrist;
            case 11:
                return leftWaist;
            case 12:
                return rightWaist;
            case 13:
                return leftKnee;
            case 14:
                return rightKnee;
            case 15:
                return leftAnkle;
            case 16:
                return rightAnkle;
            default:
                return null;

        }
    }

    //３次元構造にすれば短くできる気がする。ここでパーツごとに分ける必要はない（？）。
}
