package postgres.xlog.reader;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class XLogReaderTest {

    @Test
    public void readXLogHeader() throws IOException {
        File file = new File("/home/unmesh/pgwaltestground/data/pg_xlog/000000010000000000000001");
        InputStream ios = new FileInputStream(file);
        DataInputStream d = new DataInputStream(ios);
        long i = readBytesLittle2Big(d, 4);
        long transactionIf = readBytesLittle2Big(d, 4);
        long nextRecordPointer = readBytesLittle2Big(d, 8);
//        long flags = readBytesLittle2Big(d, 1);
        System.out.println(i);
        System.out.println("transactionIf = " + transactionIf);
        System.out.println("nextRecordPointer = " + nextRecordPointer);
//        System.out.println("flags = " + flags);

    }


    public static long readBytesLittle2Big(DataInputStream d, int iHowmany) throws IOException {
        byte[] b = new byte[iHowmany];
        ByteBuffer bb = ByteBuffer.allocate(iHowmany);
        for (int x=0;x<iHowmany;x++)
        {
            b[x] = d.readByte();
        }
	/*Here I read stuff from disc which is saved in little endian order which I have to convert into big endian*/
        for (int x=0;x<iHowmany;x++)
        {
            bb.put(iHowmany-1-x, b[x]);
        }
        bb.rewind();
        return bb.getInt();
    }
}