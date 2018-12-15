package com.eric;

import com.eric.util.FTPClientTemplate;
import com.eric.util.FTPStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;

/**
 * Created by eric on 2017/9/28.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FtpClientTemplateTest {

    @Autowired
    FTPClientTemplate ftpClientTemplate;

    @Test
    public void testDataSource() throws Exception {
        try {
            String remotePath = "imgDir/2017/09/28/88_9d6ef69c52ca4f3c9c1726005dd2cbe1.jpg";
            FTPStatus result = ftpClientTemplate.upload("C:\\Users\\eric\\Pictures\\1.jpg", remotePath, true);
            System.out.println(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
