package me.ash.part_of_speech.io;

import me.ash.data_structure.Pair;
import me.ash.learning.HMM;
import me.ash.learning.prob.CountingProbabilityDistribution;
import me.ash.part_of_speech.data_structure.Tag;
import me.ash.part_of_speech.data_structure.Word;
import org.apache.commons.io.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by ash on 10/19/15.
 */
public class POSTaggerUtilities {

    public static final String UNK = "UNK";


    public static List<List<Pair<Word, Tag>>> readTrainingFile(File file) throws IOException {

        List<String> lines = FileUtils.readLines(file, "UTF-8");

        List<List<Pair<Word, Tag>>> returnList = new ArrayList<>();

        for (String line : lines) {

            String[] stringArray = line.split(" ");
            List<Pair<Word, Tag>> pairs = new ArrayList<>();

            for (String word_and_tag : stringArray) {
                String[] orgPair = word_and_tag.split("_");
                Word word = new Word(orgPair[0].toLowerCase());
                Tag tag = new Tag(orgPair[1]);
                pairs.add(new Pair(word, tag));
            }


            returnList.add(pairs);
        }

        return returnList;
    }

    public static List<List<Word>> readTestFile(File file, Set<Word> trainedWords) throws IOException {
        //      Handle rare word in reading test file.              --freqword???
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        List<List<Word>> returnList = new ArrayList<>();


        for (String line : lines) {
            line = line.toLowerCase();
            String[] stringArray = line.split(" ");
            List<Word> words = new ArrayList<>();

            for (String word : stringArray) {
                Word newWord = new Word(word);
                if (trainedWords!=null && !trainedWords.contains(newWord)) newWord.setWordValue(UNK);
                words.add(newWord);
            }

            returnList.add(words);
        }

        return returnList;
    }



    public static String stringfyTagAndWords(List<Word> words, List<Tag> tags){
        if (words == null || tags == null || words.size() != tags.size()) return "";

        StringBuilder builder = new StringBuilder();
        for (int i=0; i<words.size(); i++){
            builder.append(words.get(i).getWordValue() + "_" + tags.get(i).getTagValue() + " ");
        }
        builder.deleteCharAt(builder.length()-1);

        return builder.toString();
    }


    public static List<List<Pair<Word, Tag>>> normalizeRareWordInData(List<List<Pair<Word, Tag>>> data, int cutoff) {

        if (cutoff <= 0) return data;

        HashMap<Word, Integer> map = new HashMap<>();

        for (List<Pair<Word, Tag>> l : data){
            for (Pair<Word, Tag> pair : l){

                pair.getL().setWordValue(pair.getL().getWordValue().toLowerCase());
                Word w = pair.getL();
                int count = map.getOrDefault(w, 0) + 1;
                map.put(w, count);
            }
        }

        HashSet<Word> rareWords = new HashSet<>();
        for (Word w : map.keySet()){
            if (map.get(w) < cutoff) rareWords.add(w);
        }

        for (List<Pair<Word, Tag>> l : data){
            for (Pair<Word, Tag> pair : l){
                if (rareWords.contains(pair.getL())) {
                    pair.getL().setWordValue(UNK);
                }
            }
        }

        return data;
    }

    public static Map<Tag, Integer> getCountingTableOfInitTag(List<List<Pair<Word, Tag>>> data) {
        Map<Tag, Integer> map = new HashMap<>();

        for (List<Pair<Word, Tag>> l : data){
            Pair<Word, Tag> pair0 = l.get(0);
            Tag t = pair0.getR();
            int count = map.getOrDefault(t, 0) + 1;
            map.put(t, count);
        }

        return map;

    }

    public static Map<Pair<Word, Tag>, Integer> getCountingTableOfEmit(List<List<Pair<Word, Tag>>> data) {
        Map<Pair<Word, Tag>, Integer> map = new HashMap<>();

        for (List<Pair<Word, Tag>> l : data){
            for (Pair<Word, Tag> pair : l){
                pair.getL().setWordValue(pair.getL().getWordValue().toLowerCase());
                int count = map.getOrDefault(pair, 0) + 1;
                map.put(pair, count);
            }
        }

        return map;
    }

    public static Map<Pair<Tag, Tag>, Integer> getCountingTableOfTrans(List<List<Pair<Word, Tag>>> data) {
        Map<Pair<Tag, Tag>, Integer> map = new HashMap<>();

        for (List<Pair<Word, Tag>> l : data){
            if (l.size()<=1) continue;

            for (int i=0; i<l.size()-1; i++){
                Tag t1 = l.get(i).getR();
                Tag t2 = l.get(i+1).getR();
                Pair<Tag, Tag> newPair = new Pair<>(t1, t2);

                int count = map.getOrDefault(newPair, 0) + 1;
                map.put(newPair, count);
            }
        }

        return map;

    }

    public static Set<Tag> getAllPossibleTags(List<List<Pair<Word, Tag>>> data) {

        Set<Tag> set = new HashSet<>();

        for (List<Pair<Word, Tag>> l : data){
            for (Pair<Word, Tag> pair : l) {
                if (!set.contains(pair.getR())) set.add(pair.getR());
            }
        }

        return  set;
     }

    public static Set<Word> getAllPossibleWords(List<List<Pair<Word, Tag>>> data) {

        Set<Word> set = new HashSet<>();

        for (List<Pair<Word, Tag>> l : data){
            for (Pair<Word, Tag> pair : l){
                if (!set.contains(pair.getL())) set.add(pair.getL());
            }
        }

        return  set;
    }

    public static HMM<Tag, Word> buildHMM(List<List<Pair<Word, Tag>>> data) {
        data = normalizeRareWordInData(data, 5);
        return new HMM<>(
                new CountingProbabilityDistribution<Tag>(getCountingTableOfInitTag(data)),
                new CountingProbabilityDistribution<Pair<Tag, Tag>>(getCountingTableOfTrans(data)),
                new CountingProbabilityDistribution<Pair<Word, Tag>>(getCountingTableOfEmit(data)),
                getAllPossibleTags(data)
        );

    }

}
