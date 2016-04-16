package be.pixxis.zoufzouf

import spock.lang.Specification


/**
 * @author Gert Leenders
 */
class LogSlurperThreadSpec extends Specification {

    LogSlurper slurper = Mock()
    LogSlurperThread slurperThread = new LogSlurperThread<>(slurper, "akey")

    def "Check uri stem extension needs processing"() {
        expect:
        slurperThread.checkUriStemExtension(input) == output

        where:

        input << ["test.mp4", "test.mp3", "test.flv", "test.ogg", "test.swf", "test.mov", "test.png", "test.jpg",
                  "test.gif", "test.pdf", "test.pptx", "test.svg", "test.key", "test.zip", "test.docx", "test.txt",
                  "test.eps", "test.psd", "test.m4v", "test.jpeg", "test.MP4", "test.MP3", "test.FLV", "test.OGG",
                  "test.SWF", "test.MOV", "test.PNG", "test.JPG", "test.GIF", "test.PDF", "test.PPTX", "test.SVG",
                  "test.KEY", "test.ZIP", "test.DOCX", "test.TXT", "test.EPS", "test.PSD", "test.M4V", "test.JPEG",
                  "test.bat", "test.sh", "test.flac", "test.xls", "test", "test.xml"]
        output << [true, true, true, true, true, true, true, true,
                   true, true, true, true, true, true, true, true,
                   true, true, true, true, true, true, true, true,
                   true, true, true, true, true, true, true, true,
                   true, true, true, true, true, true, true, true,
                   false, false, false, false, true, false]

    }
}
