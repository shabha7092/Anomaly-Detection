package com.shabha.anomalydetection;

import com.enterprisemath.math.algebra.Interval;
import com.enterprisemath.math.probability.NormalDistributionMixture;
import com.enterprisemath.math.probability.ProbabilityDistribution;
import com.enterprisemath.math.statistics.NormalDistributionMixtureEstimator;
import com.enterprisemath.math.statistics.observation.ListObservationProvider;
import com.enterprisemath.utils.DomainUtils;
import com.enterprisemath.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AnomalyDetector {

    
    private List<Histogram> histograms;
    private ProbabilityDistribution<Double> probability;
    private Double trigger;
    private AnomalyDetector() {

    }

    public boolean isAnomal(String sentence) {
        Histogram<String> histogram = Utils.createHistogram(sentence);
        double score = 0;
        for (Histogram<String> hst : histograms) {
            score = score + Histogram.getDistance(histogram, hst);
        }
        score = score / histograms.size();
        double prob = probability.getValue(score);
        System.out.println("Average score and probability: ");
        System.out.println(score + " - " + prob);
        if (prob < trigger) {
            return true;
        }
        else {
            return false;
        }
    }

    public static AnomalyDetector performTraining(List<String> sentences, double sensitivity) {
        ValidationUtils.guardPositiveInt(sentences.size() - 2, "at least 3 sentences are required");
        List<Histogram> histograms = new ArrayList<>(sentences.size());
        for (String sentence : sentences) {
            Histogram<String> hst = Utils.createHistogram(sentence);
            histograms.add(hst);
        }
        List<Double> scores = new ArrayList<>(sentences.size());
        for (Histogram th : histograms) {
            double score = 0;
            for (Histogram h : histograms) {
                score = score + Histogram.getDistance(th, h);
            }
            score = score / histograms.size();
            scores.add(score);
        }
        System.out.println("- Average scores:");
        System.out.println(scores);
        NormalDistributionMixtureEstimator estimator = new NormalDistributionMixtureEstimator.Builder().
                setMaxComponents(10).
                setMinSigma(0.5).
                setMinWeight(0.01).
                build();
        NormalDistributionMixture mixture = estimator.estimate(ListObservationProvider.create(scores));
        Interval interval = mixture.getProbableInterval(0.99);
        List<List<Double>> points = new ArrayList<>();
        for (int i = (int) interval.getMin(); i <= interval.getMax(); ++i) {
            points.add(Arrays.asList((double) i, mixture.getValue((double) i)));
        }
        System.out.println("Probability density function:");
        System.out.println(points);

        double prob = 0;
        List<Double> refProbabilities = new ArrayList<>();
        for (double sc : scores) {
            double px = mixture.getValue(sc);
            refProbabilities.add(px);
            prob = prob + px;
        }
        prob = prob / scores.size();
        double trigger = prob * sensitivity;
        System.out.println("P(x) for average scores:");
        System.out.println(refProbabilities);
        System.out.println("THRESHOLD:");
        System.out.println(trigger);
        AnomalyDetector res = new AnomalyDetector();
        res.histograms = DomainUtils.softCopyUnmodifiableList(histograms);
        res.probability = mixture;
        res.trigger = trigger;
        return res;
    }

}
