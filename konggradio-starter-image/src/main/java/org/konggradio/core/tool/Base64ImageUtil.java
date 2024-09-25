/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Base64ImageUtil.java
 *  <p>
 *  Licensed under the Apache License Version 2.0;
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.konggradio.core.tool;

import org.konggradio.core.image.Base64Image;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 在线图片转Base64编码: https://www.bejson.com/ui/image2base64/#563
 * Base64字符串转图片: https://tool.chinaz.com/tools/imgtobase/
 */
public class Base64ImageUtil {
    public static String FULL_STOP = ".";
    // 支持的图片格式
    public static String IMAGE_REGEX = "(png|jpeg|jpg|gif|bmp)";
	/*
    public static void main(String[] args) throws IOException {
        Base64ImageUtil.convertBase64ToImageByFile(new File("demo-jpeg.txt"),"demo-jpeg");
        Base64ImageUtil.convertBase64ToImageByFile(new File("demo-png.txt"),"demo");

    }
    */


    /**
     * 判断图片base64字符串的文件格式
     *
     * @param base64ImgData
     * @return
     */
    public static Base64Image checkImageBase64Format(String base64ImgData) {
		Base64Image imageBase64 = new Base64Image();
        String start = "data:image/";
        String end = ";base64,";
        Pattern pattern = Pattern.compile(start + IMAGE_REGEX + end);
        Matcher match = pattern.matcher(base64ImgData);
        String type = "";
        if (match.find()) {
            String imageHeader = match.group();
            imageBase64.setBase64Header(imageHeader);
            type = imageHeader.replaceAll(start, "").replaceAll(end, "");
            String dataContent = base64ImgData.replace(imageHeader, "");
            imageBase64.setBase64Content(dataContent);
        }
        Assert.hasText(type, "Image type not support!");
        imageBase64.setImagePostfix(type);
        return imageBase64;
    }

    /**
     * base64转图片
     *
     * @param base64ImgData base64码
     */
    public static Base64Image convertBase64StringToImage(String base64ImgData, String savePath) {
		Assert.hasText(base64ImgData, "Image data error!");
		Base64Image imageBase64 = checkImageBase64Format(base64ImgData);
        BufferedImage image = null;
        byte[] imageByte = null;
        try {
            imageByte = DatatypeConverter.parseBase64Binary(imageBase64.getBase64Content());
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(new ByteArrayInputStream(imageByte));
            bis.close();
            File outputFile = new File(savePath + FULL_STOP + imageBase64.getImagePostfix());
            ImageIO.write(image, imageBase64.getImagePostfix(), outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBase64;
    }

    public static Base64Image convertBase64ToImageByFile(File dataFile, String savePath) {
        String content = "";
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(streamReader);
            while ((content = bufferedReader.readLine()) != null)
                builder.append(content);
        }catch (IOException e){
            e.printStackTrace();
        }finally {

        }
        return convertBase64StringToImage(builder.toString(), savePath);
    }
}
