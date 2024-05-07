package com.example.test_2.service;

import org.springframework.stereotype.Service;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ItextService {

    public void addImageToPdf(String srcPdf, String destPdf, String imagePath) {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(srcPdf);
            stamper = new PdfStamper(reader, new FileOutputStream(destPdf));
            Image img = Image.getInstance(imagePath);
            img.setAbsolutePosition(400, 120); // Adjust position as per your requirement
            img.scaleToFit(100, 100); // Adjust size as per your requirement
            stamper.getOverContent(1).addImage(img); // Adding image to the first page
        } catch (DocumentException | IOException e) {
            e.printStackTrace(); // Consider proper logging in a production environment
        } finally {
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (DocumentException | IOException e) {
                    e.printStackTrace(); // Consider proper logging in a production environment
                }
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}