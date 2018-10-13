package com.shabha.anomalydetection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;


public class Utils {

   
    private Utils() {
    }
   
    public static Histogram createHistogram(String sentence) {
        Histogram.Builder<String> hist = new Histogram.Builder<>();
        List<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(sentence.split("[ (, )(\\. )]")));
        for (String w : words) {
            w = StringUtils.trimToEmpty(StringUtils.lowerCase(w));
            if (StringUtils.isEmpty(w)) {
                continue;
            }
            hist.addObservation(w);
        }
        return hist.build();
    }

}
