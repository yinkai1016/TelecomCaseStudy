## 1. 缘起
* Google 推荐使用 `eccdata.txt` 配置 ecc，并且根据 `eccdata.txt` 生成 `eccdata` 来使用
* 但是有时候拿到一个 `eccdata` 这个二进制文件，怎么知道它里面具体的配置呢？

## 2. 启发
* 受 `EccDataTest.java` 的启发，可以用 Java 代码的方式将每一个国家的 ecc 打印出来
* 另外也可以直接改 `EccDataTest.java`，然后编译 test APP，push 到手机后 run
  ```
  adb shell am instrument -e class com.android.phone.ecc.EccDataTest -w com.android.phone.tests/androidx.test.runner.AndroidJUnitRunner
  ```

## 3. 解析

### 3.1 生成 ProtobufEccData
```
cd packages/services/Telephony/ecc/proto
mkdir gen
protoc --javanano_out=store_unknown_fields=true,enum_style=java:./gen protobuf_ecc_data.proto

# 生成 packages\services\Telephony\ecc\proto\gen\com\android\phone\ecc\nano\ProtobufEccData.java
```
* 附件 [ProtobufEccData.java](../file/007_001_ProtobufEccData.java)

### 3.2 获取 protobuf 库
* `ProtobufEccData` 用到了 Google 的 `com.google.protobuf.nano` 这个库
* 可以通过以下链接下载
  * https://mvnrepository.com/artifact/com.google.protobuf.nano/protobuf-javanano/3.0.0-alpha-7
  * 附件也有 [protobuf-javanano-3.0.0-alpha-7.jar](../file/007_002_protobuf-javanano-3.0.0-alpha-7.jar)

### 3.3 解析代码
```
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
```
* 附件 [TestEcc.java](../file/007_003_TestEcc.java)
* 附件 [eccdata](../file/007_004_eccdata)

### 3.4 执行结果
```
...
* isoCode = YE
  eccs = 199
* isoCode = ZA
  eccs = 112
* isoCode = ZM
  eccs = 991
  eccs = 993
* isoCode = ZW
  eccs = 995
  eccs = 999
  eccs = 993
```