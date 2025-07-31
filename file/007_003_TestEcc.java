import com.android.phone.ecc.nano.ProtobufEccData;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class TestEcc {
    private static final String ECC_DATA_PATH = "D:\\Download\\eccdata";

    public static void main(String[] args) throws IOException {
        InputStream eccData = new GZIPInputStream(new FileInputStream(ECC_DATA_PATH));
        ProtobufEccData.AllInfo allEccMessages = ProtobufEccData.AllInfo.parseFrom(
                readInputStreamToByteArray(eccData));
        eccData.close();

        for (ProtobufEccData.CountryInfo countryInfo : allEccMessages.countries) {
            System.out.println("* isoCode = " + countryInfo.isoCode);

            for (ProtobufEccData.EccInfo eccInfo : countryInfo.eccs) {
                System.out.println("  eccs = " + eccInfo.phoneNumber);
            }
        }
    }

    /**
     * Util function to convert inputStream to byte array before parsing proto data.
     */
    private static byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16 * 1024]; // Read 16k chunks
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
