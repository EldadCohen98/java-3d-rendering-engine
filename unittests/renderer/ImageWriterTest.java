package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;


class ImageWriterTest {

    @Test
    void testWritePixel() {
        ImageWriter imageWriter = new ImageWriter(800, 500);
        for(int i=0; i<imageWriter.nX(); i++)
            for (int j=0; j<imageWriter.nY(); j++)
                imageWriter.writePixel(i,j,new Color(java.awt.Color.yellow));


        for(int x=0; x<imageWriter.nX(); x++)
            for (int y=0; y<imageWriter.nY(); y++)
                if(x%50==0 || y%50==0)
                    imageWriter.writePixel(x,y,new Color(java.awt.Color.black));

        imageWriter.writeToImage("yellow");
    }
}