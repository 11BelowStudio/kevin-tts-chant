package packij;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class KevinChanter implements AutoCloseable
{

    private final Voice kevin = VoiceManager.getInstance().getVoice("kevin");
    private final Voice kevin16 = VoiceManager.getInstance().getVoice("kevin16");

    private final int MAXIMUM_CONCURRENT_SINGLE_VOICE = 10;

    private Voice[] voice_array_maker(int how_many, String voiceName){
        final Voice[] voices = new Voice[how_many];
        final VoiceManager vm = VoiceManager.getInstance();
        for (int i = 0; i < how_many; i++) {
            final Voice v = vm.getVoice(voiceName);
            v.allocate();
            voices[i] = v;
        }
        return voices;
    }

    private final Voice[] kevins = voice_array_maker(MAXIMUM_CONCURRENT_SINGLE_VOICE, "kevin");
    private final Voice[] kevin16s = voice_array_maker(MAXIMUM_CONCURRENT_SINGLE_VOICE, "kevin16");



    private final Random rng = new Random();

    @Override
    public void close() throws Exception {
        kevin.deallocate();
        kevin16.deallocate();
        for (Voice v: kevins){
            v.deallocate();
        }
        for (Voice v: kevin16s){
            v.deallocate();
        }
    }


    private enum KEVIN_SPEECH_TYPE_ENUM{
        ONE_LETTER_EACH,
        SAY_MULTIPLE_TIMES_IN_SERIES,
        SAY_MULTIPLE_TIMES_ALTERNATING,
        SAY_MULTIPLE_TIMES_RANDOM,
        RANDOM_IN_SERIES,
        SAY_TWICE_AT_ONCE,
        SAY_DIFFERENT_THINGS_AT_ONCE,
        KEVIN_SAY_ONCE,
        KEVIN16_SAY_ONCE,
        RANDOM_SAY_ONCE
    }

    private final KEVIN_SPEECH_TYPE_ENUM[] kevin_speech_types = KEVIN_SPEECH_TYPE_ENUM.values();
    private final int kevin_speech_types_len = kevin_speech_types.length;

    private KEVIN_SPEECH_TYPE_ENUM next_speech_type(){
        return kevin_speech_types[rng.nextInt(kevin_speech_types_len)];
    }


    private final String kevString = "Kevin";

    private final String[] other_ways_of_saying_kevin = new String[]{
            "Kevin","Kevo","Kev in", "Kayvin", "I love Kevin", "Kevin!",
            "Gimme a K! Gimme an E! Gimme a V! Gimme an I! Gimme an N! What do you get? Kevin!",
            "Kevin is love, Kevin is life.", "We love Kevin", "Thank you, Kevin.", "Kevin number 1!",
            "All Hail Kevin!", "Kevin is based.", "Based and Kevinpilled.",
            "Who needs social credit when you have Kevin?",
            "All glory to Kevin!", "All praise Kevin!", "Kevin is truly based.", "I love Kevin!", "k e v i n",
            "K, as in 'Kevin'. E as in 'The second letter of Kevin'. V as in 'The third letter of Kevin'. I as in" +
                    " 'the fourth letter of Kevin'. N as in 'The last letter of Kevin'." +
                    " Put those together, you get Kevin.",
            "I love Kevin", "Kevin is great", "We all love Kevin", "Kevin is watching you.", "UwU I love Kevin",
            "Kevin is great", "Kevin is the best!", "Kevin for President!", "Kevin, our glorious benevolent dictator!",
            "Kevin cares about you!", "Kevin is the greatest!", "I love Kevin!", "Kevin!", "Kevin", "Kevo", "Kevin!",
            "Kev!", "Kovan? No wait cancel that, I meant 'Kevin'!", "Kevin Kevin Kevin Kevin Kevin!", "Kehvihn",
            "Kevin!","We love Kevin a lot we truly do he is the best person to ever exist", "I adore Kevin",
            "everything about Kevin is my favourite thing about Kevin!", "k.e.v.i.n.", "k! e! v! i! n!",
            "Glorious Dear Leader Kevin!", "Thank you Kevin for allowing us in r/shitposting!", "kevin number 1",
            "I want Kevin to fuck my dad!", "Kevin is great!", "Kevin!", "k e v i n", "Hooray for Kevin!",
            "Kevin kevin kevin kevin kevin kevin kevin kevin kevin kevin kevin kevin kevin", "great work, Kevin!",
            "Kevin is number 1", "Kevin it to win it!", "Kevin great!", "KEVIN.\nTAP TO EDIT TEXT.", "Vote for Kevin!"
    };

    private final int other_len = other_ways_of_saying_kevin.length;


    /**
     * Picks which Kevin word to say next.
     * 50% chance it's just Kevin, 50% chance it'll be something from the other ways of saying kevin
     * @return next thing to say.
     */
    private String pick_next_kevin_word(){

        if (rng.nextBoolean()){
            return kevString;
        } else {
            return other_ways_of_saying_kevin[rng.nextInt(other_len)];
        }
    }


    /**
     * Picks a random way of saying Kevin and (attempts) to say it, returns the count of times that Kevin was said.
     * @return how many times was Kevin said?
     */
    private int kevin_sayer(){
        switch (next_speech_type()){
            case KEVIN_SAY_ONCE:
                kevin.speak(pick_next_kevin_word());
                return 1;
            case KEVIN16_SAY_ONCE:
                kevin16.speak(pick_next_kevin_word());
                return 1;
            case ONE_LETTER_EACH:
                if (sayKevinOneLetterEach()){
                    return 1;
                }
                break;
            case SAY_DIFFERENT_THINGS_AT_ONCE:
                if (sayDifferentThings(pick_next_kevin_word(), pick_next_kevin_word())){
                    return 2;
                }
                break;
            case SAY_TWICE_AT_ONCE:
                if (sayTwice(pick_next_kevin_word())){
                    return 1;
                }
                break;
            case SAY_MULTIPLE_TIMES_IN_SERIES:
                final int times_to_say = 3 + rng.nextInt(MAXIMUM_CONCURRENT_SINGLE_VOICE-3);
                if (sayWordsMultipleTimesInSeries(times_to_say, pick_next_kevin_word())){
                    return times_to_say/2;
                }
                break;
            case SAY_MULTIPLE_TIMES_ALTERNATING:
                final int times_to_say_2 = 3 + rng.nextInt(MAXIMUM_CONCURRENT_SINGLE_VOICE-3);
                if (sayWordsMultipleTimesInSeriesAlternating(times_to_say_2, pick_next_kevin_word(), false)){
                    return times_to_say_2/2;
                }
                break;
            case SAY_MULTIPLE_TIMES_RANDOM:
                final int times_to_say_3 = 3 + rng.nextInt(MAXIMUM_CONCURRENT_SINGLE_VOICE-3);
                if (sayWordsMultipleTimesInSeriesAlternating(times_to_say_3, pick_next_kevin_word(), true)){
                    return times_to_say_3/2;
                }
                break;
            case RANDOM_SAY_ONCE:
            default:
                if (random_say(pick_next_kevin_word())){
                    return 1;
                }
        }
        return 0;
    }


    private long random_long(int max){
        return (long)(rng.nextFloat() * max);
    }

    private long delay_picker(){
        long delay = 10L;
        for (int i = rng.nextInt(4); i < 4; i++) {
            delay += random_long(640);
        }
        return delay;
    }

    /**
     * Starts saying Kevin the specified number of times.
     * @param timesToSayKevin how many times does 'Kevin' need to be said?
     */
    public void handle_saying_kevin_x_times(int timesToSayKevin){

        int kevin_counter = 0;

        while (kevin_counter < timesToSayKevin && control.keepGoing()){
            kevin_counter += handle_an_iteration_of_kevin_being_said();
            control.logInfo(
                    String.format(
                            "%1d of %2d, %3f%% done",
                            kevin_counter, timesToSayKevin, 100*(kevin_counter/(double)timesToSayKevin)
                    )
            );
        }
    }


    private int handle_an_iteration_of_kevin_being_said(){
        try{
            Thread.sleep(delay_picker());
        }catch (InterruptedException ignored){
        }
        return kevin_sayer();
    }

    /**
     * Call this to say 'Kevin' infinitely many times.
     */
    public void say_kevin_infinitely(){
        while (control.keepGoing()){
            handle_an_iteration_of_kevin_being_said();
        }
    }
    
    
    private boolean sayKevinOneLetterEach(){
        
        final String[] kevinLetters = new String[]{"k","e","v","i","n"};
        
        boolean kevin16Next = rng.nextBoolean();
        
        for(String s: kevinLetters){
            if (kevin16Next){
                kevin16.speak(s);
            } else{
                kevin.speak(s);
            }
            kevin16Next = !kevin16Next;
        }
        return true;
    }


    private boolean random_say(String words_to_say){
        if (rng.nextBoolean()){
            kevin.speak(words_to_say);
        } else {
            kevin16.speak(words_to_say);
        }
        return true;
    }

    private Thread SYNCHRONIZED_VOICE_THREAD_FACTORY(CyclicBarrier cb, Voice voiceToUse, String words, long delay){
        return new Thread(
                () -> {
                    try{
                       cb.await();
                    }  catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException | IllegalArgumentException e2){
                        e2.printStackTrace();
                    }
                    voiceToUse.speak(words);
                }
        );
    }

    private boolean sayWordsMultipleTimesInSeries(int timesToSayKevin, String wordsToSay){

        try {

            Thread[] sayThreads = new Thread[timesToSayKevin];

            final CyclicBarrier cbm = new CyclicBarrier(timesToSayKevin);

            final boolean usingKevin16 = rng.nextBoolean();

            final long delay = 200L + random_long(100);

            for (int i = 0; i < timesToSayKevin; i++) {

                if (usingKevin16) {
                    sayThreads[i] = SYNCHRONIZED_VOICE_THREAD_FACTORY(
                            cbm, kevin16s[i], wordsToSay, i * delay
                    );
                } else {
                    sayThreads[i] = SYNCHRONIZED_VOICE_THREAD_FACTORY(
                            cbm, kevins[i], wordsToSay, i * delay
                    );
                }

            }

            for (Thread t : sayThreads) {
                t.start();
            }
            for (Thread t : sayThreads) {
                t.join();
            }
            return true;
        } catch (InterruptedException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean sayWordsMultipleTimesInSeriesAlternating(int times, String words, boolean randomize){

        try {
            Thread[] sayThreads = new Thread[times];

            final CyclicBarrier cbm = new CyclicBarrier(times);

            boolean usingKevin16 = rng.nextBoolean();

            final long delay = 200L + random_long(100);

            for (int i = 0; i < times; i++) {

                if (usingKevin16) {
                    sayThreads[i] = SYNCHRONIZED_VOICE_THREAD_FACTORY(
                            cbm, kevin16s[i], words, i * delay
                    );
                } else {
                    sayThreads[i] = SYNCHRONIZED_VOICE_THREAD_FACTORY(
                            cbm, kevins[i], words, i * delay
                    );
                }

                if (randomize){
                    usingKevin16 = rng.nextBoolean();
                } else {
                    usingKevin16 = !usingKevin16;
                }

            }

            for (Thread t : sayThreads) {
                t.start();
            }
            for (Thread t : sayThreads) {
                t.join();
            }
            return true;
        } catch (InterruptedException e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean sayDifferentThings(String kevinWords, String kevin16words){
        try {
            CyclicBarrier cb2 = new CyclicBarrier(2);

            Thread kevThread = SYNCHRONIZED_VOICE_THREAD_FACTORY(cb2, kevin, kevinWords, 0);

            Thread kev16thread = SYNCHRONIZED_VOICE_THREAD_FACTORY(cb2, kevin16, kevin16words, 0);

            kevThread.start();
            kev16thread.start();

            kevThread.join();
            kev16thread.join();
            return true;
        } catch (InterruptedException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean sayTwice(String sayThis){
        return sayDifferentThings(sayThis, sayThis);
    }

    private final ChantControlStopperLoggerInterface control;

    KevinChanter(ChantControlStopperLoggerInterface control){
        this.control = control;
        Voice[] voices = VoiceManager.getInstance().getVoices();
        for(Voice v: voices){
            System.out.println(v.getName());
        }
        if (kevin != null)
        {
            //the Voice class allocate() method allocates this voice
            kevin.allocate();
        }
        if (kevin16 != null){
            kevin16.allocate();
        }
    }


    public static void main(String[] args)
    {

        final ChantControlPanel ccp = new ChantControlPanel();
        final JFrame theFrame = ccp.getTheFrame();

        theFrame.pack();
        theFrame.revalidate();
        theFrame.setVisible(true);

        /*
        for (String s: args) {
            System.out.println(s);
        }

        int timesToSayKevin = 1000000;

        boolean infinite_kevins = false;

        if (args.length > 0){
            try{
                timesToSayKevin = Integer.parseInt(args[0]);
                if (timesToSayKevin < 0){
                    throw new IllegalArgumentException();
                } else if (timesToSayKevin == 0){
                    System.out.println("0 specified in command line; I'm assuming you want infinite kevins.");
                    infinite_kevins = true;
                }
            } catch (NumberFormatException e){
                System.out.printf("%s is not an integer!",args[0]);
                System.exit(1);
            } catch (IllegalArgumentException e2){
                System.out.printf("%d must be an integer greater than 0!", timesToSayKevin);
                System.exit(1);
            }
        } else {
            System.out.println("No command line argument specified!");
        }

        JFrame jf = new JFrame("KevinFrame");
        JButton jb = new JButton("STOP");

        try (KevinChanter kc = new KevinChanter(new ChantControlStopperLoggerInterface() {})){


            if (infinite_kevins){

                kc.say_kevin_infinitely();
            } else {

                System.out.printf("Saying Kevin %d times!", timesToSayKevin);
                System.out.println();

                //kc.testSpeaking();

                //kc.one_voice_say_twice_at_once("kevin");
                //kc.one_voice_say_twice_at_once("kevin", kc.kevin16);

                kc.handle_saying_kevin_x_times(timesToSayKevin);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

         */

    }
}

interface ChantControlStopperLoggerInterface{

    default boolean keepGoing(){
        return true;
    }

    default void logInfo(String infoToLog){
        System.out.println(infoToLog);
    }
}

/**
 * A totes legit GUI for controlling this stuff
 */
class ChantControlPanel implements ChantControlStopperLoggerInterface {

    private boolean keepGoing = true;

    private final JFrame theFrame = new JFrame("Kevin Chanter Control Window");

    private final JTextArea textArea = new JTextArea(20, 60);

    private boolean canStartTheChanting = true;

    private final KevinChanter kc;

    private final String[] infoStrings = new String[20];
    private int infoStringsCurrentCursor = -1;

    private long start_time = 0L;

    ChantControlPanel(){

        Arrays.fill(infoStrings, "");

        theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final Container contentPane = theFrame.getContentPane();
        final BoxLayout boxLayout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
        contentPane.setLayout(boxLayout);

        contentPane.add(new JLabel("Some sort of control panel thing."));

        final Border etchedBorder = BorderFactory.createEtchedBorder();

        final JPanel startButtonPanel = new JPanel();
        startButtonPanel.setLayout(new GridLayout(3,1));
        startButtonPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder, "Start it!"));

        final JButton startMillionButton = new JButton("Start saying Kevin 1 million times");
        startMillionButton.addActionListener(
                e -> this.startSayingKevin(1000000)
        );

        final JButton startInfiniteButton = new JButton("Start saying Kevin infinitely many times");
        startInfiniteButton.addActionListener(
                e -> this.startSayingInfiniteKevins()
        );
        final JButton startChoiceButton = new JButton("Pick how many times to say Kevin");
        startChoiceButton.addActionListener(
                e -> this.startSayingKevinUserInputTimes()
        );
        startButtonPanel.add(startMillionButton);
        startButtonPanel.add(startInfiniteButton);
        startButtonPanel.add(startChoiceButton);

        contentPane.add(startButtonPanel);

        final JPanel stopButtonPanel = new JPanel(new BorderLayout());
        stopButtonPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder, "EMERGENCY STOP"));

        final JButton stopButton = new JButton("STOP EVERYTHING!");
        stopButton.addActionListener(
                e -> this.stopEverything()
        );
        stopButtonPanel.add(stopButton, BorderLayout.CENTER);

        contentPane.add(stopButtonPanel);

        final JPanel logPanel = new JPanel();

        logPanel.setLayout(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder(etchedBorder, "LOGGED INFO"));

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        //textArea.setText(String.join("\n",infoStrings));
        final JScrollPane scrollPane = new JScrollPane(
                textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );


        logPanel.add(scrollPane);

        contentPane.add(logPanel);

        kc = new KevinChanter(this);


        theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        theFrame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        stopEverything();
                        try {
                            kc.close();
                        } catch (Exception ignored) {
                        }
                    }
                }
        );

    }

    public JFrame getTheFrame() {
        return theFrame;
    }

    void stopEverything(){
        keepGoing = false;
        logInfo("WAIT!! [$!?!] THE PRESSES!");
    }

    private void chantingIsOver(){
        final long end_time = System.currentTimeMillis();
        canStartTheChanting = true;
        logInfo("Chanting has ended!");
        logInfo(String.format("Chanting ended after %f seconds", (end_time-start_time)/1000d));
    }

    private void startSayingInfiniteKevins(){
        if (canStartTheChanting) {

            canStartTheChanting = false;
            keepGoing = true;

            Thread kc_runner = new Thread(
                    () -> {

                        Thread kc_thread = new Thread(
                                kc::say_kevin_infinitely
                        );
                        kc_thread.start();
                        try {
                            kc_thread.join();
                        }  catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        chantingIsOver();
                    }
            );
            start_time = System.currentTimeMillis();
            kc_runner.start();
            logInfo("The infinite Kevin chant has started!");

        } else {
            JOptionPane.showMessageDialog(
                    theFrame,
                    "Please stop the current chant before attempting to start a new one.",
                    "Can't chant twice at once!",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    }


    private void startSayingKevinUserInputTimes(){

        final String input = JOptionPane.showInputDialog(
                theFrame,
                "Please enter how many times you want to say Kevin.",
                "How many Kevins?",
                JOptionPane.QUESTION_MESSAGE
        );

        try{
            final int times = Integer.parseInt(input);
            startSayingKevin(times);
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(
                    theFrame,
                    "Please enter an integer instead.",
                    "Invalid number of Kevins!",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }

    private void startSayingKevin(int times){
        if (canStartTheChanting) {
            if (times < 1){
                JOptionPane.showMessageDialog(
                        theFrame,
                        "Please enter a number greater than 0.",
                        "Invalid number of Kevins!",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            canStartTheChanting = false;
            keepGoing= true;

            Thread kc_runner = new Thread(
                    () -> {

                        Thread kc_thread = new Thread(
                                () -> kc.handle_saying_kevin_x_times(times)
                        );
                        kc_thread.start();
                        try {
                            kc_thread.join();
                        }  catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        chantingIsOver();
                    }
            );
            start_time = System.currentTimeMillis();
            kc_runner.start();
            logInfo(String.format("Now chanting %d times!",times));

        } else {
            JOptionPane.showMessageDialog(
                    theFrame,
                    "Please stop the current chant before attempting to start a new one.",
                    "Can't chant twice at once!",
                    JOptionPane.WARNING_MESSAGE
                    );
        }
    }


    @Override
    public boolean keepGoing() {
        return keepGoing;
    }

    @Override
    public void logInfo(String infoToLog) {
        infoStringsCurrentCursor += 1;
        if (infoStringsCurrentCursor >= infoStrings.length){
            infoStringsCurrentCursor = 0;
        }
        infoStrings[infoStringsCurrentCursor] = infoToLog + "\n";

        StringBuilder infoBuilder = new StringBuilder(infoStrings.length);
        for (int i = infoStringsCurrentCursor; i >= 0; i--) {
            infoBuilder.append(infoStrings[i]);
        }
        for (int i = infoStrings.length-1; i > infoStringsCurrentCursor; i--){
            infoBuilder.append(infoStrings[i]);
        }
        textArea.setText(infoBuilder.toString());

    }
}