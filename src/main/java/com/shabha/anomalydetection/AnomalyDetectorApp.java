package com.shabha.anomalydetection;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;


public class AnomalyDetectorApp {

    private AnomalyDetectorApp() {
    }

    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 2) {
            return;
        }
        double sensitivity = Double.valueOf(args[0]);
        List<String> trainingSentences = FileUtils.readLines(new File(args[1]));
        List<String> testSentences = FileUtils.readLines(new File(args[args.length > 2 ? 2 : 1]));
        int cnt = 0;
        System.out.println(" Executing Training");
        AnomalyDetector detector = AnomalyDetector.performTraining(trainingSentences, sensitivity);
        System.out.println("Executing prediction");
        for (String sentence : testSentences) {
            if (detector.isAnomal(sentence)) {
                System.out.println("Anomaly sentence: ");
                System.out.println("- " + sentence);
                cnt = cnt + 1;
            }
        }
        System.out.println("Number of anomal sentences: " + cnt);
    }

}
