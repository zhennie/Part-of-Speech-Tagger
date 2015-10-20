package me.ash.part_of_speech.io;

import me.ash.data_structure.Pair;
import me.ash.learning.HMM;
import me.ash.learning.prob.CountingProbabilityDistribution;
import me.ash.part_of_speech.data_structure.Tag;
import me.ash.part_of_speech.data_structure.Word;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static org.junit.Assert.*;

public class POSTaggerUtilitiesTest {

    @Test
    public void testReadTrainingFile() throws Exception {
        File temp = File.createTempFile("temp-file-name", ".tmp");

        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write("The_DT move_NN is_VBZ");
        bw.close();

        List<List<Pair<Word, Tag>>> testResult = POSTaggerUtilities.readTrainingFile(temp);
        List<List<Pair<Word, Tag>>> rightAns = new ArrayList<>();
        List<Pair<Word, Tag>> sentence = new ArrayList<>();

        sentence.add(new Pair<>(new Word("The"), new Tag("DT")));
        sentence.add(new Pair<>(new Word("move"), new Tag("NN")));
        sentence.add(new Pair<>(new Word("is"), new Tag("VBZ")));

        rightAns.add(sentence);

        assertEquals(rightAns, testResult);
    }

    @Test
    public void testReadTestFile() throws Exception {
        File temp = File.createTempFile("temp-file-name", ".tmp");

        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write("The move is");
        bw.close();

        Set<Word> vob = new HashSet<>();
        vob.add(new Word("The"));

        List<List<Word>> testResult = POSTaggerUtilities.readTestFile(temp, vob);

        List<List<Word>> rightAns = new ArrayList<>();
        List<Word> sentence = new ArrayList<>();

        sentence.add(new Word("The"));
        sentence.add(new Word(POSTaggerUtilities.UNK));
        sentence.add(new Word(POSTaggerUtilities.UNK));
        rightAns.add(sentence);

        assertEquals(rightAns, testResult);

    }


    @Test
    public void testStringfyTagAndWords() throws Exception {
        List<Word> words = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();

        words.add(new Word("The"));
        words.add(new Word("move"));
        words.add(new Word("is"));

        tags.add(new Tag("DT"));
        tags.add(new Tag("NN"));
        tags.add(new Tag("VBZ"));

        String rightAns = "The_DT move_NN is_VBZ";
        assertEquals(rightAns, POSTaggerUtilities.stringfyTagAndWords(words, tags));
    }

    @Test
    public void testNormalizeRareWordInData() throws Exception {
        File temp = File.createTempFile("temp-file-name", ".tmp");

        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        //        1      2       3      4      5           6        7      8     9
        bw.write("The_DT move_NN is_VBZ the_DT biggest_JJS salvo_NN yet_RB in_IN the_DT");
        bw.close();

        List<List<Pair<Word, Tag>>> testResult = POSTaggerUtilities.readTrainingFile(temp);

        List<List<Pair<Word, Tag>>> rightAns = new ArrayList<>();
        List<Pair<Word, Tag>> sentence = new ArrayList<>();

        sentence.add(new Pair<>(new Word("the"), new Tag("DT")));// 1
        sentence.add(new Pair<>(new Word("UNK"), new Tag("NN")));// 2
        sentence.add(new Pair<>(new Word("UNK"), new Tag("VBZ")));// 3
        sentence.add(new Pair<>(new Word("the"), new Tag("DT")));// 4
        sentence.add(new Pair<>(new Word("UNK"), new Tag("JJS")));// 5
        sentence.add(new Pair<>(new Word("UNK"), new Tag("NN")));// 6
        sentence.add(new Pair<>(new Word("UNK"), new Tag("RB")));// 7
        sentence.add(new Pair<>(new Word("UNK"), new Tag("IN")));// 8
        sentence.add(new Pair<>(new Word("the"), new Tag("DT")));// 9

        rightAns.add(sentence);

        assertEquals(rightAns, POSTaggerUtilities.normalizeRareWordInData(testResult, 2));
    }

    @Test
    public void testGetCountingTableOfInitTag() throws Exception {
        File temp = File.createTempFile("temp-file-name", ".tmp");

        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write("Kemper_NNP Financial_NNP \n" +
                "The_DT move_NN is_VBZ \n" +
                "The_DT Kemper_NNP Corp._NNP unit_NN");
        bw.close();

        List<List<Pair<Word, Tag>>> data = POSTaggerUtilities.readTrainingFile(temp);

        Map<Tag, Integer> map = new HashMap<>();
        map.put(new Tag("DT"), 2);
        map.put(new Tag("NNP"), 1);

        assertEquals(map, POSTaggerUtilities.getCountingTableOfInitTag(data));
    }

    @Test
    public void testGetCountingTableOfEmit() throws Exception {
        File temp = File.createTempFile("temp-file-name", ".tmp");

        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write("The_DT move_NN is_VBZ the_DT biggest_JJS salvo_NN yet_RB in_IN the_DT");
        bw.close();

        List<List<Pair<Word, Tag>>> data = POSTaggerUtilities.readTrainingFile(temp);

        Map<Pair<Word, Tag>, Integer> map = new HashMap<>();

        map.put((new Pair<>(new Word("the"), new Tag("DT"))), 3);
        map.put((new Pair<>(new Word("move"), new Tag("NN"))),1);
        map.put((new Pair<>(new Word("is"), new Tag("VBZ"))),1);
        map.put((new Pair<>(new Word("biggest"), new Tag("JJS"))),1);
        map.put((new Pair<>(new Word("salvo"), new Tag("NN"))),1);
        map.put((new Pair<>(new Word("yet"), new Tag("RB"))),1);
        map.put((new Pair<>(new Word("in"), new Tag("IN"))), 1);

        assertEquals(map, POSTaggerUtilities.getCountingTableOfEmit(data));
    }

    @Test
    public void testGetCountingTableOfTrans() throws Exception {
        File temp = File.createTempFile("temp-file-name", ".tmp");

        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write("The_DT move_NN is_VBZ the_DT biggest_JJS salvo_NN yet_RB in_IN the_DT");
        bw.close();

        List<List<Pair<Word, Tag>>> data = POSTaggerUtilities.readTrainingFile(temp);

        Map<Pair<Tag, Tag>, Integer> map = new HashMap<>();

        map.put((new Pair<>(new Tag("DT"), new Tag("NN"))),1);
        map.put((new Pair<>(new Tag("NN"), new Tag("VBZ"))),1);
        map.put((new Pair<>(new Tag("VBZ"), new Tag("DT"))),1);
        map.put((new Pair<>(new Tag("DT"), new Tag("JJS"))),1);
        map.put((new Pair<>(new Tag("JJS"), new Tag("NN"))),1);
        map.put((new Pair<>(new Tag("NN"), new Tag("RB"))),1);
        map.put((new Pair<>(new Tag("RB"), new Tag("IN"))),1);
        map.put((new Pair<>(new Tag("IN"), new Tag("DT"))),1);

        assertEquals(map, POSTaggerUtilities.getCountingTableOfTrans(data));
    }


}