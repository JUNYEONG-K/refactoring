package chapter_4;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;

public class FileReaderTest extends TestCase {
    FileReader input;
    FileReader empty;

    public FileReaderTest(String name) {
        super(name);
    }

    protected void setUp() {
        try {
            input = new FileReader("src/chapter_4/data.txt");
            empty = newEmptyFile();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("테스트 파일을 열 수 없음.");
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private FileReader newEmptyFile() throws IOException {
        File empty = new File("empty.txt");
        FileOutputStream output = new FileOutputStream(empty);
        output.close();
        return new FileReader(empty);
    }

    protected void tearDown() {
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException("테스트 파일을 닫는 중 에러 발생");
        }
    }

    public void testRead() throws IOException {
        char ch = '&';
        for (int i = 0; i < 4; i++) {
            ch = (char) input.read();
        }
        assert('d' == ch);
    }

    public void testReadAtEnd() throws IOException {
        int ch = -1234;
        for (int i = 0; i < 141; i++) {
            ch = input.read();
        }
        assertEquals("read at end", -1, input.read());
    }

    public void testReadBoundaries() throws IOException {
        assertEquals("read first char", 'B', input.read());
        int ch;
        for (int i = 1; i < 140; i++) {
            ch = input.read();
        }
//        assertEquals("read last char", '6', input.read());
        assertEquals("read at end", -1, input.read());
        assertEquals("read past end", -1, input.read());
    }

    public void testEmptyRead() throws IOException {
        assertEquals(-1, empty.read());
    }

    public void testReadAfterClose() throws IOException {
        input.close();
        try {
            input.read();
            fail("read past end에 예외가 발생하지 않음.");
        } catch (IOException io) {}
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(FileReaderTest.class));
    }
}
