# Kevin TTS Chant

This is the git repo for the Kevin Text-To-Speech Chant thingy.

This uses the FreeTTS speech synthesizer for Java to chant
the word 'Kevin' (and variations of the word 'Kevin').

Using this requires Java 8. It will not work with a more
modern version of Java (due to some weirdness within FreeTTS
that more modern versions of Java don't really like).

To run this, please run the main method in
`src/packij/KevinChanter.java`.

Alternatively, download and unzip the `KevinTTSChant.zip` folder,
navigate to the `KevinTTSChant` subfolder of that zip folder,
and run the `kevin tts chant.jar` jar file in there.

Further information about FreeTTS can be found here: https://freetts.sourceforge.io/

Some of the code was adapted from https://www.javatpoint.com/convert-text-to-speech-in-java
