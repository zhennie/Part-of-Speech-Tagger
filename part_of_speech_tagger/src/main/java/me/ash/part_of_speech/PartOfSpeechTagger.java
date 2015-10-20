package me.ash.part_of_speech;

import me.ash.data_structure.Pair;
import me.ash.learning.HMM;
import me.ash.learning.inference.Viterbi;
import me.ash.part_of_speech.data_structure.Tag;
import me.ash.part_of_speech.data_structure.Word;
import me.ash.part_of_speech.io.POSTaggerUtilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by ash on 10/19/15.
 */
public class PartOfSpeechTagger {
    private HMM<Tag, Word> hmm;
    private Set<Word> vob;

    public void train(String path) throws IOException {
        List<List<Pair<Word,Tag>>> data = POSTaggerUtilities.readTrainingFile(new File(path));
        this.hmm = POSTaggerUtilities.buildHMM(data);
        this.vob = POSTaggerUtilities.getAllPossibleWords(data);
    }

    public Set<Word> getVob() {
        return vob;
    }

    public List<Tag> tag(List<Word> sentences){
        return Viterbi.inference(hmm, sentences);
    }


    public static void main(String[] args) throws IOException {

//        System.out.println(Math.log(0));

        PartOfSpeechTagger tagger = new PartOfSpeechTagger();

        tagger.train(args[0]);
        List<List<Word>> corpus = POSTaggerUtilities.readTestFile(new File(args[1]), tagger.getVob());//
        for( List<Word> sentence : corpus){
            List<Tag> tags = tagger.tag(sentence);
            System.out.println(
                    POSTaggerUtilities.stringfyTagAndWords(sentence,tags)
            );
        }

    }


}
